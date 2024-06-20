package frontend;

import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.controllers.ActorController;
import backend.controllers.MediaProductController;
import backend.entities.Actor;
import backend.entities.ActorRole;
import backend.entities.MediaProduct;
import backend.services.MediaProductService;
import jakarta.transaction.Transactional;

@Component
public class SearchUtils {
	
	/*public static void groupMovieBy(Object collectionObj, String conditionName, String conditionBy) {
		Comparator<Object> compare = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				int compare = 0;
				Object obj1 = o1, obj2 = o2;
				if(obj1 instanceof MapPanel && obj2 instanceof MapPanel) {
					obj1 = ((MapPanel)obj1).object;
					obj2 = ((MapPanel)obj2).object;
				}
				if(obj1 instanceof Container && obj2 instanceof Container) {
					obj1 = getComponents(MapPanel.class, (Container) obj1, true);
					obj2 = getComponents(MapPanel.class, (Container) obj2, true);
				}
				if(obj1 instanceof MapPanel && obj2 instanceof MapPanel) {
					obj1 = ((MapPanel)obj1).object;
					obj2 = ((MapPanel)obj2).object;
				}
				if(conditionName.equals(NAME) || conditionName.equals(YEAR) || conditionName.equals(IMDB_RATING)) {
					Object o1Obj = null;
					Object o2Obj = null;
					if(obj1 instanceof Media && obj2 instanceof Media) {
						o1Obj = ((Media) obj1).getDataByType(conditionName);
						o2Obj = ((Media) obj2).getDataByType(conditionName);
					}
					else if(obj1 instanceof Actor && obj2 instanceof Actor) {
						o1Obj = ((Actor) obj1).getDataByType(conditionName);
						o2Obj = ((Actor) obj2).getDataByType(conditionName);
					}
					if(o1Obj instanceof Comparable && o2Obj instanceof Comparable) {Double d;
						if(conditionBy.equals(ASCENDING)) {
							compare = ((Comparable<Object>)o1Obj).compareTo(o2Obj);
						}
						else if(conditionBy.equals(DESCENDING)) {
							compare = -((Comparable<Object>)o1Obj).compareTo(o2Obj);
						}
					}
				}
				return compare;
			}
		};
		if(collectionObj instanceof List) {
			List<?> list = (List<?>) collectionObj;
			Collections.sort(list, compare);
		}
		else if(collectionObj.getClass().isArray()) {
			Arrays.sort((Object[]) collectionObj, compare);
		}
	}*/
	
	
	/*public static final String ANY = "ANY", ALL = "ALL", BIGGER_THAN = "BIGGER_THAN", SMALLER_THAN = "SMALLER_THAN", NONE = "NONE";
	
	public static boolean sortMovieBy(SortType sortBy, MediaProduct media) {
		try {
			MediaProperty prop = sortBy.getInfo(); 
			if(prop == MediaProperty.GENRE || categoryName.equals(CONTENT_RATING)) return sortMovieByString(categoryName, collectionObj, conditionObj, media); else
			if(prop == MediaProperty.IMDB_RATING) return sortMovieByRatings(categoryName, collectionObj, conditionObj, media);
		}
		catch(Exception exp) {System.out.println("searchMovie.sortMovieBy: " + exp.getMessage());}
		return false;
	}
	
	public static boolean sortMovieByRatings(String categoryName, Object collectionObj, Object conditionObj,  MediaProduct media) {
		if(conditionObj instanceof Map) try {
			Map condition = (Map) conditionObj;
			Object object = null;
			object = media.getDataByType(categoryName);
			if(object instanceof Double) {
				double d = (Double) object; 
				d = d * 10;
				int val = (int) d;
				if(collectionObj instanceof Map) {
					Map<String, Integer> collectionMap = (Map<String, Integer>) collectionObj;
					boolean b = true;
					for(String key : collectionMap.keySet())
						b = b && sortCheck(key, collectionMap.get(key), 0, val);
					if(condition.containsKey(NONE) && !b) return true;
					else return b;
				} else
				if(collectionObj instanceof String) {
					return sortCheck((String) collectionObj, 0, 0, val);
				}
				else return true;
			}
		}
		catch(Exception exp) {System.out.println("searchMovie.sortMovieByRatings: " + exp.getMessage());}
		else return true;
		return false;
	}
	
	
	public static boolean sortMovieByString(String categoryName, Object collectionObj, Object conditionObj, Media media) {
		if(collectionObj instanceof Collection) try {
			Collection collection = (Collection) collectionObj;
			final int size = collection.size();
			if(size != 0) {
				Object object = media.getDataByType(categoryName);
				int count = 0;
				if(object instanceof List) {
					List<String> list = (List<String>) object;
					if(list != null) for(String str : list)
						if(collection.contains(str))
							count++;
					if(conditionObj instanceof String) {
						return sortCheck((String) conditionObj, 0, size, count);
					} else
					if(conditionObj instanceof Map) {
						Map<String, Integer> map = (Map<String, Integer>) conditionObj;
						boolean b = true;
						if(map.keySet() != null && map.keySet().size() != 0) for(String str2 : map.keySet()) {
							b = b && sortCheck(str2, map.get(str2), size, count);
						}
						else return sortCheck(null, size, count);
						return b;
					} else
					if(conditionObj instanceof List) {
						List<Object> listC = (List<Object>) conditionObj;
						boolean b = true;
						for(Object obj : listC) {
							if(obj instanceof String) {
								b = b && sortCheck((String) obj, 0, size, count);
							} else
							if(obj instanceof Map) {
								Map mapO = (Map) obj;
								for(Object ob : mapO.keySet()) {
									if(ob instanceof String) {
										String st = (String) ob;
										int n = (Integer) mapO.get(st);
										b = b && sortCheck(st, n, size, count);
									}
								}
							}
						}
						return b;
					}
				}
			}
			else return true;
		}
		catch(Exception exp) {System.out.println("searchMovie.sortMovieByString: " + exp.getMessage());}
		else return true;
		return false;
	}
	
	
	
	
	
	
	
	
	private static boolean sortCheck(String conditionName, int conditionValue, int collectionLength, int mainValue) {
		try {
			conditionValue = sortCheckNum(conditionName, conditionValue, collectionLength);
			return sortCheck(conditionName, conditionValue, mainValue);
		}
		catch(Exception exp) {}
		return false;
	}
	
	private static int sortCheckNum(String conditionName, int conditionValue, int collectionLength) {
		try {
			if(conditionName.equals(ALL))
				return collectionLength;
			else if(conditionName.equals(NONE) || conditionName.equals(ANY))
				return 0;
		}
		catch(Exception exp) {}
		return conditionValue;
	}
	private static boolean sortCheck(String conditionName, int conditionValue, int mainValue) {
		try {
			if(conditionName.equals(ALL))
				return mainValue == conditionValue;
			else if(conditionName.equals(NONE))
				return mainValue == 0;
			else if(conditionName.equals(ANY))
				return mainValue != 0;
			else if(conditionName.equals(BIGGER_THAN))
				return mainValue > conditionValue;
			else if(conditionName.equals(SMALLER_THAN))
				return mainValue < conditionValue;
		}
		catch(Exception exp) {System.out.println("searchMovie.sortCheck : " + exp.getMessage());}
		return mainValue == conditionValue;
	}
	
	public static boolean sortMovieQualify(MediaProduct media, List<SortType> sortByList) {
		boolean b = true;
		for(SortType sortBy : sortByList) {
			b = b && sortMovieBy(sortBy, media);
			if(!b)
				break;
		}
		return b;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	*/
	
	
	/*
	public static Object getValueObject(Map mainMap, String mainName, String subName) {
		try {
			Object object = sortMap.get(mapName);
			if(object instanceof Map) {
				Map<Object, Object> mapKey = (Map<Object, Object>) object;
				Object obj = mapKey.get("Key");
				return obj;
			}
		}
		catch(Exception exp) {}
		return null;
	}
	
	public static boolean contains() {
		try {
			Object object = getValueObject();
			if(object instanceof Collection) {
				Collection collection = (Collection) object;
				return collection.contains(mapValue);
			}
		}
		catch(Exception exp) {}
		return false;
	}*/
	
	/*public static Map<String, List<Map<Object, Object>>> searchMovies(String name) {
		return searchMoviesSort(name, null);
	}*/
	
	/*private static Map<Object, Object> createInformationMap(Map<Object, Object> map) {
		Map<Object, Object> newMap = new HashMap<>();
		try {
			for(Object object : map.keySet()) {
				if(object.equals(searchMovie.ACTORS) || object.equals(searchMovie.SOUNDTRACK) || object.equals(searchMovie.SYNOPSIS)
						|| object.equals(searchMovie.MAIN_CAST) || object.equals(searchMovie.DIRECTOR));
				else
					newMap.put(object, map.get(object));
			}
		}
		catch(Exception exp) {exp.printStackTrace();}
		return newMap;
	}*/
	
	/*public Object getDataByType(String type) {
		switch (type) {
		case DataInformation.NAME:
			return getName();
		case DataInformation.IMDB_TITLE_ID:
			return getImdbID();
		default:
			return null;
		}
	}*/
	
	private static MediaProductController mediaProductController;
	
    @Autowired
    public SearchUtils(MediaProductController mediaProductController) {
        SearchUtils.mediaProductController = mediaProductController;
    }
	
	@Autowired
	private static ActorController actorController;
	
	public static Map<String, List<?>> searchMoviesSort(String name) {
		List<MediaProduct> movieList = new ArrayList<>();
		List<MediaProduct> roleList = new ArrayList<>();
		Set<Object> roleKeys = new HashSet<>();
		Set<Object> actorKeys = new HashSet<>();
		List<Actor> actorList = new ArrayList<>();
    	List<MediaProduct> list = mediaProductController.getAllMediaProducts();
    	for(MediaProduct media : list) { 
    		String movieName = media.getMediaName();
        	String movieId = ""+media.getId();
        	//if(sortMovieQualify(media, sortByList)) {
        	//movie name
        	boolean bol = false;
        	int cur = compare(movieName, name);
        	if(cur > 0) {
        		bol = true;
        		movieList.add(media);
        	}
        	List<ActorRole> actors = media.getActorsRoles();
        	List<ActorRole> movieRoles = null;//getActorsList(actors, name, true);
        	if(!bol)for(ActorRole actor : movieRoles) {
        		Long keyId = actor.getActor().getId();
        		if(!actorKeys.contains(keyId) && !roleKeys.contains(movieId)) {
        			roleKeys.add(movieId);
        			actorKeys.add(keyId);
        			roleList.add(media);
        			//actorList.add(actor);
	        	}
        	}
    	}
        Map<String, List<?>> mainMap = new HashMap<>();
        mainMap.put("Movies", movieList);
        mainMap.put("Roles", roleList);
        mainMap.put("Actors", actorList);
        return mainMap;
	}
	
	
	
	/*@Transactional
	public static List<ActorRole> getActorsList(List<ActorRole> actors, String name, boolean b) {
		List<ActorRole> list = new ArrayList<>();
		int cur1, cur2;
		String actorName, actorRole, str;
		for(ActorRole actor : actors) {
			cur1 = 0; cur2 = 0;
			actorRole = actor.getRoleName();
			actorName = actor.getActor().getName();
			if(b)
				str = actorRole;
			else
				str = actorName;
			int idx = str.indexOf("/");
			if(idx != -1) {
				cur1 = compare(str.substring(0, idx), name);
				if(cur1 <= 1) 
					cur2 = compare(str.substring(idx+1), name);
			}
			else 
				cur1 = compare(str, name);
			if(cur1 > 1 || cur2 > 1) {
				list.add(actor);
			}
		}
		return list;
	}*/
	
	
	
	
	/*public static JsonNode loadFile(String path) {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File("C:\\Users\\itay5\\OneDrive\\Desktop\\New Eclipse Projects\\mainGroove\\movies\\h.json");
		JsonNode node = null;
		try {
			node = mapper.readTree(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return node;
	}*/
	
	public static String getString(String str) {
		//System.out.println(str);
		//System.out.println("batman / bruce wayne".replace('/', ' '));
		str = str.toLowerCase();
		str = str.replace(',', ' ');
		str = str.replace('/', ' ');
		str = str.replace('-', ' ');
		//str.replace('', ' ');
		str = str.replace('.', ' ');
		str = str.replace('!', ' ');
		//System.out.println(str);
		return str;
	}
	
	public static int compare(String str1, String str2) {
		str1 = getString(str1);
		str2 = getString(str2);
		List<String> list1 = new ArrayList<>();
		List<String> list2 = new ArrayList<>();
		//int k = 0;
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
		int len = list1.size()+1;
		//System.out.println(list1);
		int count = 0, mainCount = 0;
		String st = "";
		for(int i = 0;i < list1.size(); i++)
			for(int j = 0;j < list2.size(); j++) {
				st = "";
				count = 0;
				char[] c1 = list1.get(i).toCharArray();
				char[] c2 = list2.get(j).toCharArray();
				for(int m = 0; m < c1.length; m++)
					for(int n = 0; n < c2.length; n++) {
						if(c1[m] == c2[n]) {
							st = st + c2[n];
							c2[n] = ' ';
							count++;
							break;
						}
						//if(n == c2.length-1)
							
					}
				//System.out.println(st);
				//System.out.println(count);
				//if(((list2.get(j).length()-2 <= 2) && 2 <= count) || ((list2.get(j).length()-2 >= 2) && list2.get(j).length()-2 <= count)) {
				//if(Math.max(2, list2.get(j).length()-2) <= count) {
				//if(2 <= count) {
				//System.out.println(st + " " + list2.get(j));
				if(st.equals(list2.get(j))) {
					if(list1.get(i).startsWith(st))
						mainCount++;
					//System.out.println(st);
					//System.out.println("ffsedfesdfsdfsfdfdsdfsdzxfsdf");
					mainCount++;
					//System.out.println(list2.get(j));
					//System.out.println(count);
					list2.remove(j);
					break;
				}
			}
		/*if(mainCount > 0)
			return len-mainCount;
		return 0;*/
		return mainCount;
	}

}
