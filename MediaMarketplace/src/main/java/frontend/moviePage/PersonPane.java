package frontend.moviePage;

import backend.DataUtils;
import backend.dtos.ActorDto;
import backend.dtos.DirectorDto;
import backend.dtos.PersonDto;
import frontend.AppImageUtils;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * A custom {@link HBox} layout used to display information about a person (actor or director) in the movie page.
 * <p>This class provides a layout that includes an image and a name for a person, accommodating both actors and directors.</p>
 */
class PersonPane extends HBox {
	
	/** Label to display the person's name. */
	private Label actorName;
	
	/** ImageView to display the person's image. */
	private ImageView actorImage;
	
	/**
	 * Constructs a new {@link PersonPane} with a layout that adapts to the given main pane's size.
	 * <p>This constructor initializes the layout with an image view and a label, binding their sizes to the main pane's dimensions.</p>
	 * 
	 * @param mainPane The {@link Region} used to bind the size of the image view and label.
	 */
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
	
	/**
	 * Sets the content of the {@link PersonPane} based on the provided object, which could be either a {@link DirectorDto} or {@link ActorDto}.
	 * <p>This method determines the type of the object and delegates the setting of properties accordingly.</p>
	 * 
	 * @param item The object to set, which could be either a {@link DirectorDto} or {@link ActorDto}.
	 */
	public <T> void set(T item) {
		if(item instanceof DirectorDto)
			set((DirectorDto) item);
		else if(item instanceof ActorDto)
			set((ActorDto) item);
	}
	/**
	 * Sets the content of the {@link PersonPane} using the provided {@link DirectorDto}.
	 * <p>This method updates the pane to display the director's name and image.</p>
	 * 
	 * @param director The {@link DirectorDto} containing the director's information.
	 */
	public void set(DirectorDto director) {
		PersonDto person = director.getPerson();
		set(person, person.getName());
	}
	
	/**
	 * Sets the content of the {@link PersonPane} using the provided {@link ActorDto}.
	 * <p>This method updates the pane to display the actor's name (including their role) and image.</p>
	 * 
	 * @param actor The {@link ActorDto} containing the actor's information.
	 */
	public void set(ActorDto actor) {
		PersonDto person = actor.getPerson();
		set(person, person.getName() + "/" + actor.getRoleName());
	}
	
	/**
	 * Updates the pane to display the provided person's name and image.
	 * <p>This method sets the name of the person in the label and updates the image view with the person's image.</p>
	 * 
	 * @param person The {@link PersonDto} containing the person's details.
	 * @param name The name to display in the label.
	 */
	private void set(PersonDto person, String name) {
		actorName.setText(name);
		String personImagePath = person.getImagePath();
		if(DataUtils.isNotBlank(personImagePath)) {
			actorImage.setImage(AppImageUtils.loadImageFromClass(personImagePath));
		}
	}
	
	/**
	 * Resets the content of the {@link PersonPane} to its default empty state.
	 * <p>This method clears the person's name and image.</p>
	 */
	public void reset() {
		actorName.setText(null);
		actorImage.setImage(null);
	}
}