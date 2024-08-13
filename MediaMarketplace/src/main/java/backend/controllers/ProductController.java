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
import backend.dto.mediaProduct.ProductReference;
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
    public List<ProductDto> getAllProducts() {
        List<ProductDto> body = productService.getAllProducts();
        return body;
    }
	
	@GetMapping("/add")
    public Long addProduct(@Valid @RequestBody ProductReference productDto) throws EntityNotFoundException {
		return productService.addProduct(productDto);
        //return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
	
	@GetMapping("/update")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody ProductReference productDto) throws EntityNotFoundException {
		productService.updateProduct(productDto);
        return new ResponseEntity<>("Created Successfully", HttpStatus.OK);
    }
	
	@GetMapping("/get/{movieId}")
    public ProductDto getProductOfMovie(Long movieId) throws EntityNotFoundException {
		return productService.getProductOfMovie(movieId);
    }
	
	@GetMapping("/get_reference/{movieId}")
    public ProductReference getProductReferenceOfMovie(Long movieId) throws EntityNotFoundException {
		return productService.getProductReferenceOfMovie(movieId);
    }
}
