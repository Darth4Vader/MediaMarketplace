package backend.services;

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

/**
 * Service class for managing person entities.
 * <p>
 * This class handles business logic related to persons, including adding, removing, and retrieving
 * person records. It ensures that administrative actions are performed correctly and provides
 * methods to convert between Person entities and PersonDto objects.
 * </p>
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DirectorRepository directorRepository;

    /**
     * Adds a new person to the database.
     * <p>
     * This method is restricted to admin users and adds a new Person entity based on the provided
     * PersonDto. If a person with the same ID already exists, an EntityAlreadyExistsException is thrown.
     * </p>
     * 
     * @param personDto The PersonDto containing information about the person to be added.
     * @throws EntityAlreadyExistsException if a person with the same ID already exists in the database.
     */
    @AuthenticateAdmin
    @Transactional
    public void addPerson(PersonDto personDto) throws EntityAlreadyExistsException {
        try {
        	//load the person
            Person person = getPersonByNameID(personDto.getPersonMediaID());
            //if found, then we can't add him to database, because he is already there, then we notify the user.
            throw new EntityAlreadyExistsException("The Person \"" + person.getPersonImdbId() + "\" already exists");
        } catch (EntityNotFoundException e) {
            // Expected exception if the person does not already exist
        }
        //save the person
        Person person = getPersonFromDto(personDto);
        personRepository.save(person);
    }

    /**
     * Removes a person from the database.
     * <p>
     * This method is restricted to admin users and deletes the Person entity with the specified ID.
     * It also removes all associated actor roles and directed media. If the person is not found,
     * an EntityNotFoundException is thrown.
     * </p>
     * 
     * @param id The ID of the person to be removed.
     * @throws EntityNotFoundException if the person with the specified ID does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void removePerson(Long id) throws EntityNotFoundException {
        Person person = getPersonByID(id);
        //first delete all connections
        actorRepository.deleteAllInBatch(person.getActorRoles());
        directorRepository.deleteAllInBatch(person.getDirectedMedia());
        //then delete the person.
        personRepository.delete(person);
    }
    
    /**
     * Retrieves a person by their media ID.
     * 
     * @param personID The media ID of the person to retrieve.
     * @return The Person entity with the specified media ID.
     * @throws EntityNotFoundException if no person with the specified media ID exists.
     */
    public Person getPersonByNameID(String personID) throws EntityNotFoundException {
        return personRepository.findByPersonImdbId(personID)
                .orElseThrow(() -> new EntityNotFoundException("The Person with ID: (" + personID + ") does not exist"));
    }

    /**
     * Retrieves a person by their database ID.
     * 
     * @param id The ID of the person to retrieve.
     * @return The Person entity with the specified ID.
     * @throws EntityNotFoundException if no person with the specified ID exists.
     */
    public Person getPersonByID(Long id) throws EntityNotFoundException {
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The Person with ID: \"" + id + "\" does not exist"));
    }

    /**
     * Converts a PersonDto to a Person entity.
     * 
     * @param personDto The PersonDto to convert.
     * @return A Person entity populated with the details from the PersonDto.
     */
    public static Person getPersonFromDto(PersonDto personDto) {
        Person person = new Person();
        person.setPersonImdbId(personDto.getPersonMediaID());
        person.setImagePath(personDto.getImagePath());
        person.setName(personDto.getName());
        person.setBirthDate(personDto.getBirthDate());
        return person;
    }
    
    /**
     * Converts a Person entity to a PersonDto.
     * 
     * @param person The Person entity to convert.
     * @return A PersonDto containing the details of the person.
     */
    public static PersonDto convertPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();
        personDto.setPersonMediaID(person.getPersonImdbId());
        personDto.setImagePath(person.getImagePath());
        personDto.setName(person.getName());
        personDto.setBirthDate(person.getBirthDate());
        return personDto;
    }
}