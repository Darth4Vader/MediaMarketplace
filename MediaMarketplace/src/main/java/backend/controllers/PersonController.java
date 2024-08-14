package backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.PersonDto;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.services.PersonService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/people")
public class PersonController {

	@Autowired
	private PersonService personService; 
	
	@GetMapping("/add")
    public ResponseEntity<String> addPerson(@Valid @RequestBody PersonDto personDto) throws EntityAlreadyExistsException {
		try {
			personService.addPerson(personDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the person with the media id: \"" + personDto.getPersonMediaID() + "\"", e);
		}
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
	
	@GetMapping("/remove")
    public ResponseEntity<String> removePerson(Long id) throws EntityNotFoundException {
		try {
			personService.removePerson(id);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityRemovalException("Unable to remove the person with the id: \"" + id + "\"", e);
		}
        return new ResponseEntity<>("Removed Successfully", HttpStatus.OK);
	}
}
