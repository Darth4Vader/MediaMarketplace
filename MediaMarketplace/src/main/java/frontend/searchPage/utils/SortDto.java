package frontend.searchPage.utils;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SortDto {
	
	private String name;
	private List<String> genres;
	private Double yearUp;
	private Double yearDown;
	private Double ratingUp;
	private Double ratingDown;

	public SortDto() {
		// TODO Auto-generated constructor stub
	}
	
	public SortDto(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<String> getGenres() {
		return genres;
	}

	public Double getYearUp() {
		return yearUp;
	}

	public Double getYearDown() {
		return yearDown;
	}

	public Double getRatingUp() {
		return ratingUp;
	}

	public Double getRatingDown() {
		return ratingDown;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public void setYearUp(Double yearUp) {
		this.yearUp = yearUp;
	}

	public void setYearDown(Double yearDown) {
		this.yearDown = yearDown;
	}

	public void setRatingUp(Double ratingUp) {
		this.ratingUp = ratingUp;
	}

	public void setRatingDown(Double ratingDown) {
		this.ratingDown = ratingDown;
	}

}
