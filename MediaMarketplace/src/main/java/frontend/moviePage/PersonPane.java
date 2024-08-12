package frontend.moviePage;

import backend.DataUtils;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Person;
import frontend.AppImageUtils;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

class PersonPane extends HBox {
	 private Label actorName;
	 private ImageView actorImage;
	
	public PersonPane(Region mainPane) {
		VBox mainBox = new VBox();
		actorName = new Label();
		actorName.setCenterShape(false);
		actorName.setWrapText(true);
		actorName.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
		actorName.setTextAlignment(TextAlignment.CENTER);
		actorName.setStyle("-fx-alignment: center");
		actorImage = new ImageView();
		actorImage.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.2));
		actorImage.fitHeightProperty().bind(mainPane.heightProperty().multiply(0.3).subtract(50));
		mainBox.getChildren().addAll(actorImage, actorName);
		mainBox.setStyle("-fx-border-color: blue");
		getChildren().addAll(mainBox);
	}
	
	public <T> void set(T item) {
		if(item instanceof Director)
			set((Director) item);
		else if(item instanceof Actor)
			set((Actor) item);
	}
	
	public void set(Director director) {
		Person person = director.getPerson();
		set(person, person.getName());
	}
	
	public void set(Actor actor) {
		Person person = actor.getPerson();
		set(person, person.getName() + "/" + actor.getRoleName());
	}
	
	private void set(Person person, String name) {
		actorName.setText(name);
		String personImagePath = person.getImagePath();
		if(DataUtils.isNotBlank(personImagePath)) {
			actorImage.setImage(AppImageUtils.loadImageFromClass(personImagePath));
		}
	}
	
	public void reset() {
		actorName.setText(null);
		actorImage.setImage(null);
	}
}
