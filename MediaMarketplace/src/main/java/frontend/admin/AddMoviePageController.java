package frontend.admin;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.MovieController;
import backend.dto.mediaProduct.MovieDto;
import backend.entities.Movie;
import backend.entities.Order;
import backend.tmdb.CreateMovie;
import backend.tmdb.CreateMovie.MovieDtoSearchResult;
import frontend.AppUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

@Component
public class AddMoviePageController {
	
	public static final String PATH = "/frontend/admin/AddMoviePage.fxml";
	
	@FXML
	private HBox mainPane;
	
	/*@FXML
	private ScrollPane scrollPane;*/
	
	@FXML
	private ListView<MovieDto> movieListView;
	
	@FXML
	private TextField searchField;
	
	/*@FXML
	private VBox moviesPanel;*/
	
	@FXML
	private Label pageLabel;
	
	@FXML
	private Label previousPageLbl;
	
	@FXML
	private Label nextPageLbl;
	
	@Autowired
	private CreateMovie createMovie;
	
	@Autowired
	private MovieController movieController;
	
	private MovieDtoSearchResult movieDtoSearchResult;
	
	private ObservableList<MovieDto> movieList;
	
	@FXML
	private void initialize() {
		this.movieList = FXCollections.observableArrayList();
		movieListView.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.6));
		previousPageLbl.setVisible(false);
		nextPageLbl.setVisible(false);
		/*ObjectProperty<MovieDtoSearchResult> city = new SimpleObjectProperty<>();
		previousPageLbl.visibleProperty().addListener(b -> {
			
		});*/
		movieListView.setCellFactory(x ->  new SearchMovieCell());
		movieListView.setItems(movieList);
		movieListView.setSelectionModel(null);
	}
	
	private class SearchMovieCell extends  ListCell<MovieDto> {
		
		private HBox moviePane;
		private ImageView posterView;
		private Label name;
		private Label date;
		private TextArea textArea;
		
		public SearchMovieCell() {
			setStyle("-fx-padding: 0px;");
			
			
			moviePane = new HBox();
			moviePane.setFillHeight(true);
			moviePane.maxHeightProperty().bind(movieListView.heightProperty().multiply(0.2));
			posterView = new ImageView();
			posterView.setPreserveRatio(true);
			posterView.fitWidthProperty().bind(movieListView.widthProperty().multiply(0.25));
			posterView.fitHeightProperty().bind(moviePane.maxHeightProperty());
			posterView.setCursor(Cursor.HAND);
			moviePane.setMinHeight(0);
			
			moviePane.getChildren().add(posterView);
			
			VBox infoBox = new VBox();
			name = new Label();
			name.setStyle("-fx-font-weight: bold");
			infoBox.getChildren().add(name);
			date = new Label();
			date.setTextFill(Color.LIGHTGRAY);
			infoBox.getChildren().add(date);
			textArea = new TextArea();
			textArea.setWrapText(true);
			textArea.setEditable(false);
			//textArea.setDisable(true);
			infoBox.getChildren().add(textArea);
			//textArea.setMinWidth(Region.USE_PREF_SIZE);
			VBox.setVgrow(textArea, Priority.ALWAYS);
			textArea.setMaxHeight(Double.MAX_VALUE);
			
			infoBox.prefHeightProperty().bind(moviePane.heightProperty());
			infoBox.prefHeightProperty().bind(moviePane.heightProperty());
			//infoBox.setStyle("-fx-border-color: blue");
			
			moviePane.getChildren().add(infoBox);
			HBox.setHgrow(infoBox, Priority.ALWAYS);
			
			moviePane.setBorder(Border.stroke(Color.BLUE));
			
			infoBox.setBorder(Border.stroke(Color.GREEN));
			
			//moviePane.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, BorderWidths.FULL)));
			
			//HBox.setVgrow(textArea, Priority.ALWAYS);
		}
		
		private void set(MovieDto movie) {
			try {
				posterView.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			posterView.setOnMouseClicked(e -> {
				try {
					createMovie.addMovieToDatabase(
							Integer.parseInt(movie.getMediaID()));
				} catch (NumberFormatException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			});
			name.setText(movie.getMediaName());
			LocalDate releaseDate = movie.getReleaseDate();
			if(releaseDate != null) {
				date.setText(releaseDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
			}
			textArea.setText(movie.getSynopsis());
		}
		
		private void reset() {
			posterView.setImage(null);
			posterView.setOnMouseClicked(null);
			name.setText(null);
			date.setText(null);
			textArea.setText(null);
		}
		
	    @Override
	    public void updateItem(MovieDto item, boolean empty) {
	        super.updateItem(item, empty);
	        if (item == null || empty) {
	        	System.out.println("Bye");
	            setGraphic(null);
	            setText(null);
	            reset();
	        }
	        else {
	            setGraphic(moviePane);
	            setText(null);
	            set(item);
	        }
	        setAlignment(Pos.CENTER_LEFT);
			setBorder(new Border(new BorderStroke(Color.PINK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
		            new BorderWidths(1))));
	        //setAlignment(Pos.CENTER);
	    }
	}
	
	private void validatePageMoveLabels() {
		if(movieDtoSearchResult != null) {
			int page = movieDtoSearchResult.getCurrentPage();
			int total = movieDtoSearchResult.getTotalPages();
			previousPageLbl.setVisible(page != 1);
			nextPageLbl.setVisible(page != total);
			pageLabel.setText(""+page);
		}
	}
	
	@FXML
	private void searchMovies() throws MalformedURLException {
		//Parent mainPane = moviesPanel.getParent();
		String search = searchField.getText();
		if(search.isEmpty()) return;
		try {
			movieDtoSearchResult = createMovie.searchMovie(search);
			validatePageMoveLabels();
			List<MovieDto> movies = movieDtoSearchResult.getResultList();
			viewMoviesSearched(movies);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void viewMoviesSearched(List<MovieDto> movies) {
		movieList.setAll(movies);
	}
	
	private HBox getMovieInfoPage(MovieDto movieDto) {
		HBox moviePane = new HBox();
		moviePane.setFillHeight(true);
		moviePane.maxHeightProperty().bind(movieListView.heightProperty().multiply(0.2));
		String posterPath = movieDto.getPosterPath();
		System.out.println(posterPath);
		ImageView posterView = new ImageView();
		Image poster = null;
		try {
			poster = AppUtils.loadImageFromClass(posterPath);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(poster != null) {
			posterView.setImage(poster);
		}
		posterView.setPreserveRatio(true);
		posterView.fitWidthProperty().bind(movieListView.widthProperty().multiply(0.25));
		posterView.fitHeightProperty().bind(moviePane.maxHeightProperty());
		posterView.setCursor(Cursor.HAND);
		posterView.setOnMouseClicked(e -> {
			try {
				createMovie.addMovieToDatabase(
						Integer.parseInt(movieDto.getMediaID()));
			} catch (NumberFormatException | ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		
		//poster.fitHeightProperty().bind(scrollPane.heightProperty().multiply(0.2));
		moviePane.setMinHeight(0);
		
		
		/*Platform.runLater(() -> {
			try {
				Image image = AppUtils.loadImageFromClass(posterPath);
				poster.setImage(image);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		//moviePane.setMinHeight(Region.USE_PREF_SIZE);
		//moviePane.setMaxHeight(Region.USE_PREF_SIZE);
		*/
		
		moviePane.getChildren().add(posterView);
		
		
		/*System.out.println(scrollPane.heightProperty());
		System.out.println(poster.fitHeightProperty());
		System.out.println(mainPane.heightProperty());
		//b.setCenter(poster);
		*/
		VBox infoBox = new VBox();
		//infoBox.setMaxHeight(Double.MAX_VALUE);
		Label name = new Label(movieDto.getMediaName());
		name.setStyle("-fx-font-weight: bold");
		infoBox.getChildren().add(name);
		LocalDate releaseDate = movieDto.getReleaseDate();
		if(releaseDate != null) {
			Label date = new Label(releaseDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
			date.setTextFill(Color.LIGHTGRAY);
			infoBox.getChildren().add(date);
		}
		TextArea textArea = new TextArea(movieDto.getSynopsis());
		//Label textArea = new Label(movieDto.getSynopsis());
		
		//Text textArea = new Text(movieDto.getSynopsis());
		
		/*textArea
		.setStyle(".text-area .scroll-pane { -fx-hbar-policy: never; -fx-vbar-policy: always;}");
		*/
		textArea.setWrapText(true);
		//textArea.setDisable(true);
		infoBox.getChildren().add(textArea);
		//textArea.setMinWidth(Region.USE_PREF_SIZE);
		VBox.setVgrow(textArea, Priority.ALWAYS);
		textArea.setMaxHeight(Double.MAX_VALUE);
		
		Platform.runLater(() -> {
			/*System.out.println(textArea.minHeightProperty());
			System.out.println(textArea.maxHeightProperty());
			System.out.println(textArea.prefHeightProperty());
			System.out.println(textArea.heightProperty());
			System.out.println(infoBox.heightProperty());
			System.out.println(moviePane.heightProperty());
			System.out.println(VBox.getVgrow(textArea));*/
			
			
			System.out.println(posterView.fitHeightProperty());
			System.out.println(moviePane.heightProperty());
			System.out.println(mainPane.heightProperty());
		});
		
		infoBox.prefHeightProperty().bind(moviePane.heightProperty());
		infoBox.prefHeightProperty().bind(moviePane.heightProperty());
		//infoBox.setStyle("-fx-border-color: blue");
		
		moviePane.getChildren().add(infoBox);
		HBox.setHgrow(infoBox, Priority.ALWAYS);
		
		moviePane.setBorder(Border.stroke(Color.BLUE));
		
		infoBox.setBorder(Border.stroke(Color.GREEN));
		
		//moviePane.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, BorderWidths.FULL)));
		
		//HBox.setVgrow(textArea, Priority.ALWAYS);
		return moviePane;
	}
	
	@FXML
	private void previousPage() {
		movePage(false);
	}
	
	@FXML
	private void nextPage() {
		movePage(true);
	}
	
	private void movePage(boolean forward) {
		if(movieDtoSearchResult != null) {
			int page = movieDtoSearchResult.getCurrentPage();
			int total = movieDtoSearchResult.getTotalPages();
			System.out.println(page);
			System.out.println(total);
			if(forward)
				page++;
			else
				page--;
			if(page >= 0 && page <= total) {
				String search = movieDtoSearchResult.getSearchText();
				try {
					movieDtoSearchResult = createMovie.searchMovie(search, page);
					validatePageMoveLabels();
					List<MovieDto> movies = movieDtoSearchResult.getResultList();
					viewMoviesSearched(movies);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
		
		
		
		
		/*if(!search.isEmpty()) {
			try {
				//MovieDtoSearchResult movieDtoSearchResult = createMovie.searchMovie(search);
				//List<MovieDto> movies = movieDtoSearchResult.getResultList();
				List<Movie> movies = movieController.getAllMovies();
				if(movies != null) for(Movie movieDto : movies) {
					BorderPane moviePane = new BorderPane();
					String posterPath = movieDto.getPosterPath();
					ImageView poster = AppUtils.loadImageViewFromClass(posterPath);
					poster.setPreserveRatio(true);
					BorderPane b = new BorderPane();
					poster.fitWidthProperty().bind(scrollPane.widthProperty().multiply(0.15));
					poster.fitHeightProperty().bind(scrollPane.heightProperty().multiply(0.2));
					b.setLeft(poster);
					
					VBox infoBox = new VBox();
					Label name = new Label(movieDto.getName());
					name.setStyle("-fx-font-weight: bold");
					infoBox.getChildren().add(name);
					LocalDate releaseDate = movieDto.getReleaseDate();
					if(releaseDate != null) {
						Label date = new Label(releaseDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
						date.setTextFill(Color.LIGHTGRAY);
						infoBox.getChildren().add(date);
					}
					TextArea textArea = new TextArea(movieDto.getSynopsis());
					infoBox.getChildren().add(textArea);
					moviePane.setRight(infoBox);
					moviesPanel.getChildren().add(moviePane);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

}
