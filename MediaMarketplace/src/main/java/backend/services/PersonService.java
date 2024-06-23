package backend.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.PersonDto;
import backend.entities.Actor;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Person;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ActorRepository;
import backend.repositories.GenreRepository;
import backend.repositories.PersonRepository;

@Service
public class PersonService {
	
    @Autowired
    private PersonRepository personRepository;
    
    public List<Person> getAllPeople() {
    	return personRepository.findAll();
    }
    
    public void addPerson(PersonDto personDto) throws EntityAlreadyExistsException {
    	try {
			getPersonByNameID(personDto.getPersonMediaID());
			throw new EntityAlreadyExistsException("The Person already exists");
		} catch (EntityNotFoundException e) {
		}
    	Person person = getPersonFromDto(personDto);
    	personRepository.save(person);
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
    			orElseThrow(() -> new EntityNotFoundException("The Person with id: ("+id+") does not exists"));
    }
    
}
