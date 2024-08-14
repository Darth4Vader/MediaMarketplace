package backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import backend.dto.mediaProduct.ProductDto;
import backend.dto.mediaProduct.ProductReference;
import backend.exceptions.EntityAdditionException;
import backend.exceptions.EntityNotFoundException;
import backend.services.ProductService;
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
		try {
			return productService.addProduct(productDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the product for the movie with id \"" + productDto.getMovieId() + "\"", e);
		}
    }
	
	@GetMapping("/update/{id}")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody ProductReference productDto) throws EntityNotFoundException {
		try {
			productService.updateProduct(productDto);
		}
		catch (DataAccessException e) {//we will catch the Transactional runtime exception, and throw it as a custom exception
			throw new EntityAdditionException("Unable to add the product \"" + productDto.getId() + "\" for the movie with id \"" + productDto.getMovieId() + "\"", e);
		}
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
