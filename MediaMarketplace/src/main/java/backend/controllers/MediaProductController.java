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

import backend.dto.mediaProduct.MediaProductDto;
import backend.entities.MediaProduct;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MediaProductService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/media_products")
public class MediaProductController {

	@Autowired
	private MediaProductService mediaProductService; 
	
	@GetMapping("/")
	@ResponseStatus(code = HttpStatus.OK)
    public List<MediaProduct> getAllMediaProducts() {
        List<MediaProduct> body = mediaProductService.getAllMediaProducts();
        return body;
        //return new ResponseEntity<>(body, HttpStatus.OK);
    }
	
	@GetMapping("/create")
    public ResponseEntity<String> addMediaProduct(@Valid @RequestBody MediaProductDto mediaDto) throws EntityAlreadyExistsException, EntityNotFoundException {
		mediaProductService.addMediaProduct(mediaDto);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}