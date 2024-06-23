package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.MovieDto;
import backend.dto.mediaProduct.PersonDto;
import backend.entities.Movie;
import backend.entities.Person;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieService;
import backend.services.PersonService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/people")
public class PersonController {

	@Autowired
	private PersonService personService; 
	
	@GetMapping("/")
	@ResponseStatus(code = HttpStatus.OK)
    public List<Person> getAllPeople() {
        List<Person> body = personService.getAllPeople();
        return body;
        //return new ResponseEntity<>(body, HttpStatus.OK);
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addPerson(@Valid @RequestBody PersonDto personDto) throws EntityAlreadyExistsException {
		personService.addPerson(personDto);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}
