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

import backend.dto.mediaProduct.ProductDto;
import backend.entities.Movie;
import backend.entities.Product;
import backend.entities.User;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.services.MovieService;
import backend.services.ProductService;
import backend.services.UserServiceImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@GetMapping("/")
	@ResponseStatus(code = HttpStatus.OK)
    public List<Product> getAllProducts() {
        List<Product> body = productService.getAllProduct();
        return body;
    }
	
	@GetMapping("/add")
    public ResponseEntity<String> addProduct(@Valid @RequestBody ProductDto productDto) throws EntityNotFoundException {
		productService.addProduct(productDto);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
}
