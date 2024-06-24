package frontend.help;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.MovieReviewController;
import backend.entities.Actor;
import backend.entities.Movie;
import backend.entities.MovieReview;
import backend.entities.Person;
import backend.entities.User;
import backend.exceptions.EntityNotFoundException;
import frontend.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

@Component
public class MoviePageController {
	
	public static final String PATH = "/frontend/help/MoviePanel.fxml";
	
	@FXML
	private ImageView posterView;
	
	@FXML
	private TextArea synopsisArea;
	
	@FXML
	private Label yearLbl;
	
	@FXML
	private Label rutimeLbl;
	
	@FXML
	private HBox watchOptions;
	
	@FXML
	private HBox directorsBox;
	
	@FXML
	private HBox actorsBox;
	
	@FXML
	private VBox reviewsBox;
	
	/*@FXML
	private void initialize(Movie movie) throws MalformedURLException {
		posterView.setImage(AppUtils.loadImageFromClass(movie.getImagePath()));
		List<Actor> actors = movie.getActorsRoles();
		if(actors != null) for(Actor actor : actors) {
			Person person = actor.getActor();
			BorderPane actorPane = new BorderPane();
			ImageView actorImage = new ImageView(AppUtils.loadImageFromClass(person.getImagePath()));
			Label actorName = new Label(person.getName() + "/" + actor.getRoleName());
			actorPane.setCenter(actorImage);
			actorPane.setBottom(actorName);
			actorsBox.getChildren().add(actorPane);
		}
	}*/
	
	 @Autowired
	 private MovieReviewController movieReviewController;
	
	public void initializeMovie(Movie movie) throws MalformedURLException {
		posterView.setImage(AppUtils.loadImageFromClass(movie.getPosterPath()));
		
		String synopsis = movie.getSynopsis();
		if(DataUtils.isBlank(synopsis))
			synopsis = "To Be Determined";
		synopsisArea.setText(movie.getSynopsis());
		
		List<Actor> actors = movie.getActorsRoles();
		System.out.println(actors);
		if(actors != null) for(Actor actor : actors) {
			Person person = actor.getPerson();
			BorderPane actorPane = new BorderPane();
			String personImagePath = person.getImagePath();
			if(DataUtils.isNotBlank(personImagePath)) {
				ImageView actorImage;
				try {
					actorImage = new ImageView(AppUtils.loadImageFromClass(personImagePath));
					actorPane.setCenter(actorImage);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(person.getName() + "/" + actor.getRoleName());
			Label actorName = new Label(person.getName() + "/" + actor.getRoleName());
			actorPane.setBottom(actorName);
			actorsBox.getChildren().add(actorPane);
		}
		try {
			List<MovieReview> reviews = movieReviewController.getAllReviewOfMovie(movie.getId());
			if(reviews != null) for(MovieReview review : reviews) {
				VBox box = new VBox();
				TextFlow textFlowPane = new TextFlow();
				Text star = new Text("★");
				star.setFill(Color.GOLD);
				star.setFont(Font.font(star.getFont().getSize()+3));
				Text rating = new Text(" "+review.getRating());
				textFlowPane.getChildren().addAll(star, rating);
				
				Label title = new Label(review.getReviewTitle());
				title.setStyle("-fx-font-weight: bold;");
				
				User user = review.getUser();
				Label userName = new Label(user.getUsername());
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				Label createdDate = new Label(format.format(review.getCreatedDate()));
				HBox userInfo = new HBox();
				userInfo.getChildren().addAll(userName, createdDate);
				
				TextArea reviewText = new TextArea(review.getReview());
				
				box.getChildren().addAll(textFlowPane, title, userInfo, reviewText);
				reviewsBox.getChildren().add(box);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(actors);
	}

}
