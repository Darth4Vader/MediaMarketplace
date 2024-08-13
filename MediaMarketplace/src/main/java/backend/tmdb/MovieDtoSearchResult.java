package backend.tmdb;

import java.util.List;

import backend.dto.mediaProduct.CreateMovieDto;
import backend.dto.mediaProduct.MovieDto;

public class MovieDtoSearchResult {
	
	private String searchText;
	private List<CreateMovieDto> resultList;
	private int currentPage;
	private int totalPages;
	
	public List<CreateMovieDto> getResultList() {
		return resultList;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public void setResultList(List<CreateMovieDto> resultList) {
		this.resultList = resultList;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
}