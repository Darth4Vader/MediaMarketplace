package frontend.homePage;

import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.controllers.ProductController;
import backend.entities.Movie;
import backend.entities.Product;
import frontend.App;
import frontend.AppUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Callback;

@Component
public class HomePageController {
	
	public static final String PATH = "/frontend/homePage/HomePage.fxml";
	
	@FXML
	private ListView<MovieRow> productsPane;
	
	@Autowired
	private ProductController productController;
	
	@FXML
	private void initialize() throws MalformedURLException {		
		List<Product> products = productController.getAllProducts();
		ObservableList<MovieRow> movies = FXCollections.observableArrayList();
		int i = 0;
		final int MAX = 5;
		MovieRow movieRow = new MovieRow();
		for(Product product : products) {
			movieRow.add(product.getMovie());
			i++;
			if(i == MAX) {
				movies.add(movieRow);
				movieRow = new MovieRow();
				i = 0;
			}
		}
		productsPane.setItems(movies);
		productsPane.setCellFactory(new Callback<ListView<MovieRow>, ListCell<MovieRow>>() {
			
			@Override
			public ListCell<MovieRow> call(ListView<MovieRow> param) {
				// TODO Auto-generated method stub
				return new MovieTableCellEditor(productsPane, MAX);
			}
		});
		productsPane.setSelectionModel(null);
		//productsPane.addEventHandler(MouseEvent.MOUSE_CLICKED, click -> click.consume());
	}

}

class MovieRow {
	
	private List<Movie> movies;
	
	public MovieRow() {
		this.movies = new ArrayList<>();
	}

	public MovieRow(List<Movie> movies) {
		super();
		this.movies = movies;
	}
	
	public void add(Movie movie) {
		this.movies.add(movie);
	}

	public List<Movie> getMovies() {
		return movies;
	}

	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
	
}

class MovieTableCellEditor extends ListCell<MovieRow> {
	
	private Region sizePane;
	
	private final HBox box;
	
	private final List<MovieCell> cells;
	
	private class MovieCell {
		private BorderPane b;
		private Label name;
		private ImageView view;
		
		public MovieCell(int number) {
			b = new BorderPane();
			view = new ImageView();
			view.setPreserveRatio(true);
			view.fitWidthProperty().bind(sizePane.widthProperty().multiply(1/number));
			view.fitHeightProperty().bind(sizePane.heightProperty().multiply(0.4));
			b.setCenter(view);
			name = new Label();
			name.setWrapText(true);
			b.setBottom(name);
			b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
		}
		
		public void set(Movie movie) {
    		try {
				view.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		name.setText(movie.getName());
    		b.setOnMouseClicked(evt -> {
    			App.getApplicationInstance().enterMoviePage(movie);
    		});
		}
		
		public void clean() {
        	view.setImage(null);
        	name.setText(null);
        	b.setOnMouseClicked(null);
		}
	}
	
	public MovieTableCellEditor(Region sizePane, int number) {
		this.sizePane = sizePane;
		this.box = new HBox();
		this.cells = new ArrayList<>();
		for(int i = 0; i < number; i++) {
			MovieCell cell = new MovieCell(number);
			this.cells.add(cell);
			this.box.getChildren().add(cell.b);
		}
    	System.out.println("Can See");
    	box.prefWidthProperty().bind(sizePane.widthProperty());
	}
	
    @Override
    public void updateItem(MovieRow item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
        	System.out.println("Bye");
            setGraphic(null);
            setText(null);
            for(MovieCell cell : cells)
            	cell.clean();
        }
        else {
        	List<Movie> movies = item.getMovies();
        	for(int i = 0; i < cells.size(); i++) {
        		Movie movie = movies.get(i);
        		MovieCell cell = cells.get(i);
        		if(i < movies.size()) {
        			cell.set(movie);
        		}
        		else {
        			cell.clean();
        		}
        	}
            setGraphic(box);
            setText(null);
        }
        setAlignment(Pos.CENTER);
    }
}
