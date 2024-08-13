package backend.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
import backend.dto.mediaProduct.MovieReference;
import backend.dto.mediaProduct.ProductDto;
import backend.dto.mediaProduct.ProductReference;
import backend.entities.CartProduct;
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
    public List<ProductDto> getAllProducts() {
    	List<Product> products = productRepository.findAll();
    	List<ProductDto> productDtos = new ArrayList<>();
    	for(Product product : products) {
    		ProductDto productDto = convertProductToDto(product);
    		productDtos.add(productDto);
    	}
    	return productDtos;
    }
    
    //only an admin can add a product to the database
    @AuthenticateAdmin
    public Long addProduct(ProductReference productDto) throws EntityNotFoundException {
    	Movie movie = movieService.getMovieByID(productDto.getMovieId());
    	Product product = getProductFromDto(productDto, movie);
    	productRepository.save(product);
    	return product.getId();
    }
    
    //only an admin can update a product to the database
    @AuthenticateAdmin
    public void updateProduct(ProductReference productDto) throws EntityNotFoundException {
    	Product product = getProductByID(productDto.getId());
    	updateProductFromReference(product, productDto);
    	productRepository.save(product);
    }
    
    private Product getProductByMovieId(Long movieId) throws EntityNotFoundException {
    	return productRepository.findByMovieId(movieId).
    			orElseThrow(() -> new EntityNotFoundException("The Movie does not have a product"));
    }
    
    public ProductDto getProductOfMovie(Long movieId) throws EntityNotFoundException {
    	return convertProductToDto(getProductByMovieId(movieId)); 
	}
    
    public ProductReference getProductReferenceOfMovie(Long movieId) throws EntityNotFoundException {
    	return getProductFromReference(getProductByMovieId(movieId)); 
	}
    
    public static Product getProductFromDto(ProductReference productDto, Movie movie) {
        Product product = new Product();
        updateProductFromReference(product, productDto);
        return product;
    }
    
    public static ProductReference getProductFromReference(Product product) {
    	ProductReference productReference = new ProductReference();
    	productReference.setId(product.getId());
    	productReference.setBuyPrice(product.getBuyPrice());
    	productReference.setRentPrice(product.getRentPrice());
    	productReference.setBuyDiscount(product.getBuyDiscount());
    	productReference.setRentDiscount(product.getRentDiscount());
    	return productReference;
    }
    
    public static void updateProductFromReference(Product product, ProductReference productDto) {
        product.setBuyPrice(productDto.getBuyPrice());
        product.setRentPrice(productDto.getRentPrice());
        product.setBuyDiscount(productDto.getBuyDiscount());
        product.setRentDiscount(productDto.getRentDiscount());
    }
    
    public Product getProductByID(Long id) throws EntityNotFoundException {
    	return productRepository.findById(id).
    			orElseThrow(() -> new EntityNotFoundException("The Product with id: ("+id+") does not exists"));
    }
    
    public static ProductDto convertProductToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setMovie(MovieService.convertMovieToReference(product.getMovie()));
        productDto.setFinalBuyPrice(calculateBuyPrice(product));
        productDto.setFinalRentPrice(calculateRentPrice(product));
        return productDto;
    }
    
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	public static double calculateBuyPrice(Product product) {
		return calculatePrice(product.getBuyPrice(), product.getBuyDiscount());
	}
	
	public static double calculateRentPrice(Product product) {
		return calculatePrice(product.getRentPrice(), product.getRentDiscount());
	}
	
	private static double calculatePrice(double price, BigDecimal discount) {
		if(discount == null || discount.compareTo(BigDecimal.ZERO) == 0)
			return price;
		return ONE_HUNDRED.subtract(discount)
				.divide(ONE_HUNDRED)
				.multiply(new BigDecimal(price, new MathContext(2, RoundingMode.HALF_EVEN)))
				.doubleValue();
	}
    
}
