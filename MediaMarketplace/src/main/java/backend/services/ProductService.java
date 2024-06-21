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
    
    public List<Product> getAllProduct() {
    	return productRepository.findAll();
    }
    
    public void addProduct(ProductDto productDto) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(productDto.getMovieId());
    	Product product = getProductFromDto(productDto, movie);
    	productRepository.save(product);
    }
    
    public static Product getProductFromDto(ProductDto productDto, Movie movie) {
        Product product = new Product();
        product.setBuyPrice(productDto.getBuyPrice());
        product.setRentPrice(productDto.getRentPrice());
        product.setBuyDiscount(productDto.getBuyDiscount());
        product.setRentDiscount(productDto.getRentDiscount());
        product.setMovie(movie);
        return product;
    }
    
    public Product getProductByID(Long id) throws EntityNotFoundException {
    	return productRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The Product with id: ("+id+") does not exists"));
    }
    
}
