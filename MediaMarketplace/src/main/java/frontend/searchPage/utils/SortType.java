package frontend.searchPage.utils;

public class SortType {
	
	private MediaProperty info;
	
	private SortOrder orderBy;
	
	private Object orderByQaulifier;

	public SortType(MediaProperty info, SortOrder orderBy, Object orderByQaulifier) {
		super();
		this.info = info;
		this.orderBy = orderBy;
		this.orderByQaulifier = orderByQaulifier;
	}

	public MediaProperty getInfo() {
		return info;
	}

	public SortOrder getOrderBy() {
		return orderBy;
	}

	public Object getOrderByQaulifier() {
		return orderByQaulifier;
	}

	public void setInfo(MediaProperty info) {
		this.info = info;
	}

	public void setOrderBy(SortOrder orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrderByQaulifier(Object orderByQaulifier) {
		this.orderByQaulifier = orderByQaulifier;
	}

}
