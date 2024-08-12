package frontend.searchPage.utils;

import java.util.List;

import backend.DataUtils;

public class SortDto {
	
	private String name;
	private List<String> genres;
	private Double yearAbove;
	private Double yearBelow;
	private Double ratingAbove;
	private Double ratingBelow;

	public SortDto() {
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

	public Double getYearAbove() {
		return yearAbove;
	}

	public Double getYearBelow() {
		return yearBelow;
	}

	public Double getRatingAbove() {
		return ratingAbove;
	}

	public Double getRatingBelow() {
		return ratingBelow;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public void setYearAbove(Double yearAbove) {
		this.yearAbove = yearAbove;
	}

	public void setYearBelow(Double yearBelow) {
		this.yearBelow = yearBelow;
	}

	public void setRatingAbove(Double ratingAbove) {
		this.ratingAbove = ratingAbove;
	}

	public void setRatingBelow(Double ratingBelow) {
		this.ratingBelow = ratingBelow;
	}
	
	public boolean isSortEmpty() {
		return DataUtils.isBlank(name) && yearAbove == null && yearBelow == null
				&& ratingAbove == null && ratingBelow == null && DataUtils.isListEmpty(genres);
	}

}
