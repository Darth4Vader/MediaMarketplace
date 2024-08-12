package frontend.searchPage;

import backend.DataUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;

public class SearchPageUtils {

	
	public static ChangeListener<String> textPropertyChangeListener(TextInputControl control, int maxCharacters) {
		return new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if (!newValue.matches("\\d*"))
		        	newValue = newValue.replaceAll("[^\\d]", "");
	        	if(newValue.length() > maxCharacters)
	        		newValue = newValue.substring(0, maxCharacters);
		        control.setText(newValue);
		    }
	    };
	}
	
	public static ChangeListener<String> ratingChangeListener(TextInputControl control) {
		return new ChangeListener<String>() {
		    
			private String prev;
			
			@Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, 
		        String newValue) {
		        if(DataUtils.equalsIgnoreCase(prev, newValue))
		        	return;
				System.out.println("Start: " + newValue);
		    	if (!newValue.matches("\\d*"))
		        	newValue = newValue.replaceAll("[^\\d]", "");
	        	if(newValue.startsWith("00") && newValue.length() == 3) {
	        		newValue = newValue.substring(1,3);
	        		System.out.println("chocho: " + newValue);
	        	}
	        	else if(!newValue.equals("0") && !newValue.equals("00")) {
			    	newValue = newValue.replaceFirst("^0+(?!$)", "");
			    	if(newValue.length() > 3) {
		        		newValue = newValue.substring(0, 3);
		        	}
			    	Integer num = DataUtils.getIntegerNumber(newValue);
			    	if(num != null) {
			    		System.out.println("Chohooh");
			    		if(num > 100 && newValue.length() >= 3) {
			    			System.out.println("boibo");
			    			if(oldValue != null && oldValue.length() < 3)
			    				newValue = newValue.substring(0, 2);
			    			else
			    				newValue = "100";//newValue.substring(0, 2);
			    		}
			    		else {
			    			newValue = ""+num;
			    		}
			    	}
	        	}
		    	System.out.println("End: " + newValue);
		    	/*if(num != null && num >= 0 && num <= 100) {
		        	if(newValue.length() == 3)
		        		newValue = newValue.substring(0, 3);
		    		control.setText(newValue);
		    	}
		    	else {
		    		
		    	}*/
		    	String text = newValue;
		        this.prev = newValue;
		    	control.setText(text);
		    }
	    };
	}

}
