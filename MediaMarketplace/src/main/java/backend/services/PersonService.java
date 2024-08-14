package backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dto.mediaProduct.PersonDto;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.DirectorRepository;
import backend.repositories.PersonRepository;

@Service
public class PersonService {
	
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private ActorRepository actorRepository;
    
    @Autowired
    private DirectorRepository directorRepository;
    
    //a non log user can get this information
    public List<Person> getAllPeople() {
    	return personRepository.findAll();
    }
    
    //only an admin can add a person to the database
    @AuthenticateAdmin
    @Transactional
    public void addPerson(PersonDto personDto) throws EntityAlreadyExistsException {
    	try {
			Person person = getPersonByNameID(personDto.getPersonMediaID());
			throw new EntityAlreadyExistsException("The Person \"" + person.getId() + "\" already exists");
		} catch (EntityNotFoundException e) {
		}
    	Person person = getPersonFromDto(personDto);
    	personRepository.save(person);
    }
    
    //only an admin can remove a person from the database
    @AuthenticateAdmin
    @Transactional
    public void removePerson(Long id) throws EntityNotFoundException {
    	Person person = getPersonByID(id);
    	actorRepository.deleteAllInBatch(person.getActorRoles());
    	directorRepository.deleteAllInBatch(person.getDirectedMedia());
    	personRepository.delete(person);
    }
    
    public static PersonDto convertPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();
        personDto.setPersonMediaID(person.getPersonImdbId());
        personDto.setImagePath(person.getImagePath());
        personDto.setName(person.getName());
        personDto.setBirthDate(person.getBirthDate());
        return personDto;
    }
    
    public static Person getPersonFromDto(PersonDto personDto) {
        Person person = new Person();
        person.setPersonImdbId(personDto.getPersonMediaID());
        person.setImagePath(personDto.getImagePath());
        person.setName(personDto.getName());
        person.setBirthDate(personDto.getBirthDate());
        return person;
    }
    
    public Person getPersonByNameID(String personID) throws EntityNotFoundException {
    	return personRepository.findByPersonImdbId(personID).
    			orElseThrow(() -> new EntityNotFoundException("The Person with id: ("+personID+") does not exists"));
    }
    
    public Person getPersonByID(Long id) throws EntityNotFoundException {
    	return personRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The Person with id: \"" + id + "\" does not exists"));
    }
    
}
