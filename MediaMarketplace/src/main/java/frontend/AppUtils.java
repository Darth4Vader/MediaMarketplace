package frontend;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import backend.dto.cart.CartProductDto;
import backend.entities.Movie;
import backend.entities.Product;
import backend.exceptions.EntityAlreadyExistsException;
import backend.exceptions.EntityNotFoundException;
import frontend.moviePage.MoviePageController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class AppUtils {

	public AppUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static Image loadImageFromClass(String path) {
		try {
			URL url;
			try {
				url = new URL(path);
			}
			catch (MalformedURLException e) {
				url = new File(path).toURI().toURL();
			}
			
			String fullPath = url.toExternalForm();
			System.out.println(fullPath);
			return new Image(fullPath, true);
		}
		catch (Exception e) {
			//if there is an exception with loading the image, or it is missing then return a null
			return null;
		}
	}
	
	public static ImageView loadImageViewFromClass(String path) {
		return new ImageView(loadImageFromClass(path));
	}
	
	/*public static Pane getMoviePane(Movie movie) throws MalformedURLException, IOException {
		ImageView view = AppUtils.loadImageViewFromClass(movie.getPosterPath());
		view.setPreserveRatio(true);
		BorderPane b = new BorderPane();
		view.fitWidthProperty().bind(b.widthProperty());
		view.fitHeightProperty().bind(b.heightProperty());
		b.setCenter(view);
		Label name = new Label(movie.getName());
		b.setBottom(name);
		b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		b.setOnMouseClicked(evt -> {
			FXMLLoader loader = App.getApplicationInstance().getFXMLLoader(MoviePageController.PATH);
			try {
				Parent root = loader.load();
				MoviePageController controller = loader.getController();
				controller.initializeMovie(movie);
				enterPanel(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return b;
	}*/
	
	public static BorderPane getMoviePane(Movie movie, Region sizePane) {
		BorderPane b = new BorderPane();
		ImageView view = null;
		view = loadImageViewFromClass(movie.getPosterPath());
		if(view != null) {
			view.setPreserveRatio(true);
			view.fitWidthProperty().bind(sizePane.widthProperty().multiply(0.2));
			view.fitHeightProperty().bind(sizePane.heightProperty().multiply(0.4));
			b.setCenter(view);
		}
		Label name = new Label(movie.getName());
		name.setWrapText(true);
		b.setBottom(name);
		b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		b.setOnMouseClicked(evt -> {
			App.getApplicationInstance().enterMoviePage(movie);
		});
		return b;
	}
	
	public static void loadProductsToGridPane(List<Product> products, GridPane moviePane, Region sizePane) {
		final int cols = moviePane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		moviePane.getChildren().clear();
		for(Product product : products) {
			Movie movie = product.getMovie();
			BorderPane pane = getMoviePane(movie, sizePane);
			moviePane.add(pane, currentCols, row);
			if(currentCols < cols-1)
				currentCols++;
			else {
				currentCols = 0;
				row++;
			}
		}
	}
	
	public static void loadMoviesToGridPane(List<Movie> movies, GridPane moviePane, Region sizePane) {
		final int cols = moviePane.getColumnCount();
		int row = 0;
		int currentCols = 0;
		moviePane.getChildren().clear();
		for(Movie movie : movies) {
			BorderPane pane = getMoviePane(movie, sizePane);
			moviePane.add(pane, currentCols, row);
			if(currentCols < cols-1)
				currentCols++;
			else {
				currentCols = 0;
				row++;
			}
		}
	}
	
	public static Alert alertOfError(String title, String bodyText) {
		return alertOfType(AlertType.ERROR, title, bodyText);
	}
	
	public static Alert alertOfInformation(String title, String bodyText) {
		return alertOfType(AlertType.INFORMATION, title, bodyText);
	}
	
	public static Alert alertOfType(AlertType type, String title, String bodyText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(bodyText);
        alert.show();
        return alert;
	}
	
	public static void enterPanel(Node node) throws IOException {
		App.getApplicationInstance().changeAppPanel(node);
	}
	
	public static void addToGridPane(GridPane gridPane, Node comp) {
		int size = gridPane.getChildren().size();
		int r = size / 3;
		int c = size - r*3;
		gridPane.add(comp, c, r);
	}
	
	
	public static void FullListViewAsGridPage(ListView<MovieRow> movieListView, List<?> list) {
		InitiatelistViewAsGridPage(movieListView);
		UpdatelistViewAsGridPage(movieListView, list);
	}
	
	public static void InitiatelistViewAsGridPage(ListView<MovieRow> movieListView) {
		final int MAX = 5;
		movieListView.setCellFactory(new Callback<ListView<MovieRow>, ListCell<MovieRow>>() {
			
			@Override
			public ListCell<MovieRow> call(ListView<MovieRow> param) {
				// TODO Auto-generated method stub
				return new MovieTableCellEditor(movieListView, MAX);
			}
		});
		movieListView.setSelectionModel(null);
		//productsPane.addEventHandler(MouseEvent.MOUSE_CLICKED, click -> click.consume());
	}
	
	public static void UpdatelistViewAsGridPage(ListView<MovieRow> movieListView, List<?> list) {
		ObservableList<MovieRow> movies = FXCollections.observableArrayList();
		int i = 0;
		final int MAX = 5;
		MovieRow movieRow = new MovieRow();
		for(Object object : list) {
			Movie movie = null;
			if(object instanceof Product)
				movie = ((Product)object).getMovie();
			else if(object instanceof Movie)
				movie = (Movie) object;
			if(movie != null) {
				movieRow.add(movie);
				i++;
				if(i == MAX) {
					movies.add(movieRow);
					movieRow = new MovieRow();
					i = 0;
				}
			}
		}
		if(!movieRow.getMovies().isEmpty()) {
			movies.add(movieRow);
		}
		movieListView.setItems(movies);
	}

}

class MovieTableCellEditor extends ListCell<MovieRow> {
	
	private Region sizePane;
	
	private final GridPane box;
	
	private final List<MovieCell> cells;
	
	public MovieTableCellEditor(Region sizePane, int number) {
		this.sizePane = sizePane;
		this.box = new GridPane();
		box.setAlignment(Pos.CENTER);
		this.cells = new ArrayList<>();
		for(int i = 0; i < number; i++) {
			ColumnConstraints cols = new ColumnConstraints();
			cols.setPercentWidth(100d / number);
			//cols.setHgrow(Priority.ALWAYS);
			cols.setHalignment(HPos.CENTER);
			MovieCell cell = new MovieCell(number);
			box.getColumnConstraints().add(cols);
			this.cells.add(cell);
			//this.box.getChildren().add(cell.b);
			this.box.add(cell.b, i, 0);
		}
    	System.out.println("Can See");
    	
    	ScrollBar vertScrollBar = (ScrollBar) sizePane.lookup(".scroll-bar:vertical");
    	System.out.println("\n\n\n" + vertScrollBar);
    	
    	box.prefWidthProperty().bind(sizePane.widthProperty().subtract(40));
    	//box.setMaxWidth(Control.USE_COMPUTED_SIZE);
    	
    	//box.prefWidthProperty().bind(sizePane.widthProperty());
		
    	//box.prefHeightProperty().bind(sizePane.heightProperty().multiply(0.4));
		
		box.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
		
		//Remove the gap
		setStyle("-fx-padding: 0px;");
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
        		MovieCell cell = cells.get(i);
        		if(i < movies.size()) {
        			cell.set(movies.get(i));
        		}
        		else {
        			cell.clean();
        		}
        	}
        	System.out.println("Box "+box.getPrefWidth()+ " List: " + sizePane.getWidth());
        	System.out.println(cells.get(0).b.prefWidthProperty());
        	System.out.println(cells.get(0).view.fitWidthProperty());
        	System.out.println(cells.get(0).name.widthProperty());
            setGraphic(box);
            setText(null);
        }
        setAlignment(Pos.CENTER_LEFT);
		setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
	            new BorderWidths(1))));
        //setAlignment(Pos.CENTER);
    }
    
	private class MovieCell {
		//private BorderPane b;
		private VBox b;
		private Label name;
		private ImageView view;
		
		public MovieCell(int number) {
			b = new VBox();
			b.setAlignment(Pos.TOP_CENTER);
			//b.prefWidthProperty().bind(sizePane.widthProperty().multiply((1/number)-0.01));
			view = new ImageView();
			view.setPreserveRatio(true);
			
			//view.fitWidthProperty().bind(sizePane.widthProperty().multiply((1/number)-0.01));
			//view.fitWidthProperty().bind(box.widthProperty().multiply(1/number));
			
			//view.fitWidthProperty().bind(box.prefWidthProperty());
			
			view.fitHeightProperty().bind(sizePane.heightProperty().multiply(0.4));
			//view.fitHeightProperty().bind(box.prefHeightProperty());
			
			b.getChildren().add(view);
			name = new Label();
			name.setWrapText(true);
			b.getChildren().add(name);
			b.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
		}
		
		public void set(Movie movie) {
			view.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
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
}
