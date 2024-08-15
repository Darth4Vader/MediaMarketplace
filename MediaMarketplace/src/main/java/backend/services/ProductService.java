package backend.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.auth.AuthenticateAdmin;
import backend.dto.mediaProduct.ProductDto;
import backend.dto.mediaProduct.ProductReference;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityNotFoundException;
import backend.repositories.ProductRepository;

/**
 * Service class for managing product entities related to movies.
 * <p>
 * This class handles the business logic for products, including operations to retrieve, add, and update
 * product records. It also provides methods to convert between Product entities and ProductDto/Reference objects.
 * </p>
 */
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MovieService movieService;

    /**
     * Retrieves a list of all products in the database.
     * <p>
     * This method returns a list of ProductDto objects representing
     * all products.
     * </p>
     * 
     * @return A list of ProductDto objects representing all products in the database.
     */
    public List<ProductDto> getAllProducts() {
        //load the products
    	List<Product> products = productRepository.findAll();
    	//then convert them to dtos
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = convertProductToDto(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    /**
     * Adds a new product to the database.
     * <p>
     * This method is restricted to admin users and adds a new Product entity based on the provided ProductReference.
     * If the movie associated with the product does not exist, an EntityNotFoundException is thrown.
     * </p>
     * 
     * @param productDto The ProductReference containing information about the product to be added.
     * @return The ID of the newly created product.
     * @throws EntityNotFoundException if the movie associated with the product does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public Long addProduct(ProductReference productDto) throws EntityNotFoundException {
        Movie movie = movieService.getMovieByID(productDto.getMovieId());
        Product product = getProductFromDto(productDto, movie);
        productRepository.save(product);
        return product.getId();
    }

    /**
     * Updates an existing product in the database.
     * <p>
     * This method is restricted to admin users and updates the Product entity with the information provided in
     * the ProductReference. If the product does not exist, an EntityNotFoundException is thrown.
     * </p>
     * 
     * @param productDto The ProductReference containing updated information about the product.
     * @throws EntityNotFoundException if the product to be updated does not exist.
     */
    @AuthenticateAdmin
    @Transactional
    public void updateProduct(ProductReference productDto) throws EntityNotFoundException {
        Product product = getProductByID(productDto.getId());
        updateProductFromReference(product, productDto);
        productRepository.save(product);
    }
    
    /**
     * Retrieves a product associated with a specific movie.
     * 
     * @param movieId The ID of the movie for which the product is to be retrieved.
     * @return The ProductDto representing the product associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    public ProductDto getProductOfMovie(Long movieId) throws EntityNotFoundException {
        return convertProductToDto(getProductByMovieId(movieId));
    }
    
    /**
     * Retrieves a product reference associated with a specific movie.
     * 
     * @param movieId The ID of the movie for which the product reference is to be retrieved.
     * @return The ProductReference representing the product associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    public ProductReference getProductReferenceOfMovie(Long movieId) throws EntityNotFoundException {
        return convertProductToReference(getProductByMovieId(movieId));
    }
    
    /**
     * Retrieves a product by its ID.
     * 
     * @param id The ID of the product to retrieve.
     * @return The Product entity with the specified ID.
     * @throws EntityNotFoundException if the product with the specified ID does not exist.
     */
    public Product getProductByID(Long id) throws EntityNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The Product with ID: \"" + id + "\" does not exist"));
    }
    
    /**
     * Retrieves a product associated with a specific movie by its movie ID.
     * 
     * @param movieId The ID of the movie for which the product is to be retrieved.
     * @return The Product entity associated with the specified movie.
     * @throws EntityNotFoundException if no product exists for the specified movie.
     */
    private Product getProductByMovieId(Long movieId) throws EntityNotFoundException {
        return productRepository.findByMovieId(movieId)
                .orElseThrow(() -> new EntityNotFoundException("The Movie with ID: \"" + movieId + "\" does not have a product"));
    }
    
    /**
     * Creates a Product entity from the provided ProductReference and associated Movie.
     * 
     * @param productDto The ProductReference containing product details.
     * @param movie The Movie associated with the product.
     * @return A Product entity populated with the provided details.
     */
    public static Product getProductFromDto(ProductReference productDto, Movie movie) {
        Product product = new Product();
        updateProductFromReference(product, productDto);
        return product;
    }
    
    /**
     * Updates a Product entity with details from a ProductReference.
     * 
     * @param product The Product entity to update.
     * @param productDto The ProductReference containing updated details.
     */
    public static void updateProductFromReference(Product product, ProductReference productDto) {
        product.setBuyPrice(productDto.getBuyPrice());
        product.setRentPrice(productDto.getRentPrice());
        product.setBuyDiscount(productDto.getBuyDiscount());
        product.setRentDiscount(productDto.getRentDiscount());
    }

    /**
     * Converts a Product entity to a ProductReference.
     * 
     * @param product The Product entity to convert.
     * @return A ProductReference containing the details of the product.
     */
    public static ProductReference convertProductToReference(Product product) {
        ProductReference productReference = new ProductReference();
        productReference.setId(product.getId());
        productReference.setBuyPrice(product.getBuyPrice());
        productReference.setRentPrice(product.getRentPrice());
        productReference.setBuyDiscount(product.getBuyDiscount());
        productReference.setRentDiscount(product.getRentDiscount());
        return productReference;
    }

    /**
     * Converts a Product entity to a ProductDto.
     * 
     * @param product The Product entity to convert.
     * @return A ProductDto containing the details of the product.
     */
    public static ProductDto convertProductToDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setMovie(MovieService.convertMovieToReference(product.getMovie()));
        productDto.setFinalBuyPrice(calculateBuyPrice(product));
        productDto.setFinalRentPrice(calculateRentPrice(product));
        return productDto;
    }

    /**
     * Calculates the final buy price of a product after applying the discount.
     * 
     * @param product The Product entity for which the buy price is calculated.
     * @return The final buy price of the product.
     */
    public static double calculateBuyPrice(Product product) {
        return calculatePrice(product.getBuyPrice(), product.getBuyDiscount());
    }

    /**
     * Calculates the final rent price of a product after applying the discount.
     * 
     * @param product The Product entity for which the rent price is calculated.
     * @return The final rent price of the product.
     */
    public static double calculateRentPrice(Product product) {
        return calculatePrice(product.getRentPrice(), product.getRentDiscount());
    }
    
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    /**
     * Calculates the final price after applying the discount.
     * 
     * @param price The original price of the product.
     * @param discount The discount to be applied.
     * @return The final price after applying the discount.
     */
    private static double calculatePrice(double price, BigDecimal discount) {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) == 0)
            return price;
        return ONE_HUNDRED.subtract(discount)
                .divide(ONE_HUNDRED)
                .multiply(new BigDecimal(price, new MathContext(2, RoundingMode.HALF_EVEN)))
                .doubleValue();
    }
}