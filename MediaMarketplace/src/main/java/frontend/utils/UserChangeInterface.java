package frontend.utils;

/**
 * Interface for handling user information updates.
 * <p>
 * This interface defines a method for refreshing all user-related information in a given pane.
 * </p>
 */
public interface UserChangeInterface {
	
    /**
     * Refreshes all user information displayed in the associated pane.
     * <p>
     * Implementations should update the user information in the UI component to reflect the latest 
     * data from the user model or service.
     * </p>
     */
	public void refreshAllUserInformationInPane();
}
