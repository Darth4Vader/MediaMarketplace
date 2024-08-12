package frontend.searchPage.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import backend.DataUtils;
import backend.controllers.MovieController;
import backend.controllers.MovieReviewController;
import backend.entities.Actor;
import backend.entities.Director;
import backend.entities.Genre;
import backend.entities.Movie;

@Component
public class SearchUtils {
	
	private static MovieController movieController;
	
	private static MovieReviewController movieReviewController;
	
    public SearchUtils(MovieReviewController movieReviewController, MovieController movieController) {
        SearchUtils.movieController = movieController;
        SearchUtils.movieReviewController = movieReviewController;
    }
	
	public static List<Movie> searchMoviesSort(SortDto sortDto) {
		List<Movie> movieList = new ArrayList<>();
		if(!sortDto.isSortEmpty()) {
			List<Movie> list = movieController.getAllMovies();
	    	if(list != null) 
	    		for(Movie media : list) {
	        		if(searchMovie(media, sortDto))
	        			movieList.add(media);
	    		}
		}
        return movieList;
	}
	
	private static boolean searchMovie(Movie media, SortDto sortDto) {
		if(sortDto.isSortEmpty())
			return false;
		if(sortMovie(media, sortDto)) {
			String name = sortDto.getName();
			if(DataUtils.isBlank(name))
				return true;
    		String movieName = media.getName();
        	int cur = compare(movieName, name);
        	if(cur > 0)
        		return true;
        	List<Actor> actors = media.getActorsRoles();
        	if(searchActors(actors, name))
        		return true;
    		List<Director> directors = media.getDirectors();
    		if(searchDirectors(directors, name))
    			return true;
		}
		return false;
	}
	
	public static boolean searchActors(List<Actor> actors, String name) {
		for(Actor actor : actors) {
			if(compare(actor.getRoleName(), name) > 1)
				return true;
			if(compare(actor.getPerson().getName(), name) > 1)
				return true;
		}
		return false;
	}
	
	public static boolean searchDirectors(List<Director> directors, String name) {
		int cur1;
		for(Director director : directors) {
			cur1 = 0;
			cur1 = compare(director.getPerson().getName(), name);
			if(cur1 > 1)
				return true;
		}
		return false;
	}
	
	private static boolean sortMovie(Movie movie, SortDto sortDto) {
		boolean check = true;
		check &= checkBetween(movie.getYear(), sortDto.getYearAbove(), sortDto.getYearBelow());
		check &= checkGenres(movie, sortDto);
		return check
				&& checkBetween(movieReviewController.getMovieRatings(movie.getId()), sortDto.getRatingAbove(), sortDto.getRatingBelow());
	}
	
	private static boolean checkGenres(Movie movie, SortDto sortDto) {
		List<String> genres = sortDto.getGenres();
		List<Genre> movieGenres = movie.getGenres();
		if(movieGenres == null)
			return false;
		if(genres != null) for(String genre : genres) {
			if(movieGenres.stream().noneMatch(g -> DataUtils.equalsIgnoreCase(g.getName(), genre)))
				return false;
		}
		return true;
	}
	
	private static boolean checkBetween(Integer value, Double above, Double below) {
		boolean bol = true;
		if(above != null) {
			if(value != null)
				bol &= above <= value;
			else
				bol = false;
		}
		if(below != null) {
			if(value != null)
				bol &= value <= below;
			else
				bol = false;
		}
		return bol;
	}
	
	public static String getString(String str) {
		str = str.toLowerCase();
		str = str.replace(',', ' ');
		str = str.replace('/', ' ');
		str = str.replace(':', ' ');
		str = str.replace('-', ' ');
		str = str.replace('.', ' ');
		str = str.replace('!', ' ');
		return str;
	}
	
	public static int compare(String str1, String str2) {
		str1 = getString(str1);
		str2 = getString(str2);
		List<String> list1 = new ArrayList<>(Arrays.asList(str1.split("\\s+")));
		List<String> list2 = new ArrayList<>(Arrays.asList(str2.split("\\s+")));
		int mainCount = 0;
		for(int i = 0;i < list1.size(); i++)
			for(int j = 0;j < list2.size(); j++) {
				String st = getSubStringMatched(list1.get(i), list2.get(j));
				if(st.equals(list2.get(j))) {
					if(list1.get(i).startsWith(st))
						mainCount++;
					mainCount++;
					list2.remove(j);
					break;
				}
			}
		return mainCount;
	}
	
	private static String getSubStringMatched(String str1, String str2) {
		String st = "";
		char[] c1 = str1.toCharArray();
		char[] c2 = str2.toCharArray();
		for(int m = 0; m < c1.length; m++)
			for(int n = 0; n < c2.length; n++) {
				if(c1[m] == c2[n]) {
					st = st + c2[n];
					c2[n] = ' ';
					break;
				}
			}
		return st;
	}
	
	public static int compare2(String str1, String str2) {
		str1 = getString(str1);
		str2 = getString(str2);
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		String str = "";
		char c;
		for(int i = 0;i < str1.length(); i++) {
			c = str1.charAt(i);
			if(c == ' ') {
				if(str.equals("") != true)
					list1.add(str);
				str = "";
			}
			else
				str = str + c;
			if(str.isEmpty() != true && i == str1.length()-1)
				list1.add(str);
		}
		str = "";
		for(int i = 0;i < str2.length(); i++) {
			c = str2.charAt(i);
			if(c == ' ') {
				list2.add(str);
				str = "";
			}
			else
				str = str + c;
			if(c != ' ' && i == str2.length()-1)
				list2.add(str);
		}
		System.out.println(list1 + " " + list2);
		int mainCount = 0;
		String st = "";
		for(int i = 0;i < list1.size(); i++)
			for(int j = 0;j < list2.size(); j++) {
				st = "";
				char[] c1 = list1.get(i).toCharArray();
				char[] c2 = list2.get(j).toCharArray();
				for(int m = 0; m < c1.length; m++)
					for(int n = 0; n < c2.length; n++) {
						if(c1[m] == c2[n]) {
							st = st + c2[n];
							c2[n] = ' ';
							break;
						}
					}
				if(st.equals(list2.get(j))) {
					if(list1.get(i).startsWith(st))
						mainCount++;
					mainCount++;
					list2.remove(j);
					break;
				}
			}
		return mainCount;
	}

}
