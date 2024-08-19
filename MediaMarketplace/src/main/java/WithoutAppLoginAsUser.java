import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import backend.ActivateSpringApplication;
import backend.controllers.OrderController;
import backend.controllers.ProductController;
import backend.controllers.UserAuthenticateController;
import backend.dtos.references.CartProductReference;
import backend.dtos.references.ProductReference;
import backend.dtos.users.LogInDto;
import backend.entities.Genre;
import backend.entities.Movie;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import backend.exceptions.EntityRemovalException;
import backend.exceptions.PurchaseOrderException;
import backend.services.CartService;
import backend.services.GenreService;
import backend.services.MovieService;
import backend.services.OrderService;
import backend.services.ProductService;

public class WithoutAppLoginAsUser {

	private static ConfigurableApplicationContext appContext;
	
	public static void main(String[] args) {
		appContext = ActivateSpringApplication.create(args);
		UserAuthenticateController userAuth = appContext.getBean(UserAuthenticateController.class);
		
		//LogInDto dto = new LogInDto("frodo", "bag");
		LogInDto dto = new LogInDto("bilbo", "bag");
		try {
			userAuth.loginUser(dto);
			//userAuth.registerUser(new UserInformationDto("frodo", "", "bag", "bag"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long productId = 40l;
		
		ProductController productController = appContext.getBean(ProductController.class);
		
		
		ProductReference productReference2 = new ProductReference();
		productReference2.setId(productId);
		productReference2.setMovieId(32l);
		/*try {
			productController.addProduct(productReference2);
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
		
		CartService cartService = appContext.getBean(CartService.class);
		CartProductReference productReference = new CartProductReference();
		productReference.setBuying(false);
		productReference.setProductId(productId);
		/*try {
			cartService.addProductToCart(productReference);
		} catch (Exception e3) {
			e3.printStackTrace();
			// TODO: handle exception
		}*/
		
		
		OrderService orderService = appContext.getBean(OrderService.class);
		//TokenService tokenService = appContext.getBean(TokenService.class);
		
		OrderController orderController = appContext.getBean(OrderController.class);
		
		//User user = tokenService.getCurretUser();
		
		GenreService genreService = appContext.getBean(GenreService.class);
		
		
		MovieService movieService  =appContext.getBean(MovieService.class);
		
		try {
			productController.removeProduct(productId);
			
			//Movie movie = movieService.getMovieByID(9l);
			
			/*Genre genre1 = genreService.getGenreByName("Action");
			Genre genre2 = genreService.getGenreByName("Science fiction"); 
			Genre genre3 = genreService.getGenreByName("Adventure");*/
			
			Genre genre1 = genreService.getGenreByName("Comedy");
			
			//genreService.removeGenre(genre1.getName());
			//movieService.updateMovie(movie, genre1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		for(int i = 0; i < 8; i++) {
			int j = i;
			new Thread(() -> {
				try {
					userAuth.loginUser(dto);
					//cartService.removeProductFromCart(productReference, user);
					orderController.placeOrder();
					System.out.println("\u001B[32m" + "Purchased successfuly" + "\u001B[0m");
					//cartService.addProductToCart(productReference, user);
				} catch (Exception e) {
					System.err.println(e.getMessage());
					//e.printStackTrace();
				}
			}).start();
		}
		*/
		
	}

}
