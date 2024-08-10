package backend.services;

import java.time.Instant;
import java.util.ArrayList;
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

import backend.auth.AuthenticateAdmin;
import backend.dto.mediaProduct.ProductDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.MovieRepository;
import backend.repositories.ProductRepository;

@Service
public class ProductService {
	
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MovieService movieService;
    
    //a non log user can get this information
    
    @AuthenticateAdmin
    
    public List<Product> getAllProduct() {
    	return productRepository.findAll();
    }
    
    //only an admin can add a product to the database
    @AuthenticateAdmin
    public Long addProduct(ProductDto productDto) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(productDto.getMovieId());
    	Product product = getProductFromDto(productDto, movie);
    	productRepository.save(product);
    	return product.getId();
    }
    
    //only an admin can update a product to the database
    @AuthenticateAdmin
    public void updateProduct(ProductDto productDto) throws EntityNotFoundException {
    	Product product = getProductByID(productDto.getProductId());
    	updateProductFromDto(product, productDto);
    	productRepository.save(product);
    }
    
    public Product getProductByMovieId(Long movieId) throws EntityNotFoundException {
    	return productRepository.findByMovieId(movieId).
    			orElseThrow(() -> new EntityNotFoundException("The Movie does not have a product"));
    }
    
    public static Product getProductFromDto(ProductDto productDto, Movie movie) {
        Product product = new Product();
        updateProductFromDto(product, productDto);
        return product;
    }
    
    public static void updateProductFromDto(Product product, ProductDto productDto) {
        product.setBuyPrice(productDto.getBuyPrice());
        product.setRentPrice(productDto.getRentPrice());
        product.setBuyDiscount(productDto.getBuyDiscount());
        product.setRentDiscount(productDto.getRentDiscount());
    }
    
    public Product getProductByID(Long id) throws EntityNotFoundException {
    	return productRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The Product with id: ("+id+") does not exists"));
    }
    
}
