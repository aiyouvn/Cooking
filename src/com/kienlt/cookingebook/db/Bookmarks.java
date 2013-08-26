package com.kienlt.cookingebook.db;

public class Bookmarks {
	private int id;
	private String name;
	private int number_bookmark;
	private int id_ck;
	private String datetime;


	Boolean checkbox =false;
	
	
	
	
	public Bookmarks(int id, String name, int number_bookmark, int id_ck,
			Boolean checkbox,String datetime) {
		super();
		this.id = id;
		this.name = name;
		this.number_bookmark = number_bookmark;
		this.id_ck = id_ck;
		this.checkbox = checkbox;
		this.datetime=datetime;
	}
	public Bookmarks(){}
	public Bookmarks(int id, String name, int number_bookmark,int id_ck,String datetime) {
		super();
		this.id = id;
		this.name = name;
		this.number_bookmark = number_bookmark;
		this.id_ck=id_ck;
		this.datetime=datetime;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumber_bookmark() {
		return number_bookmark;
	}
	public void setNumber_bookmark(int number_bookmark) {
		this.number_bookmark = number_bookmark;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getCheckbox() {
		return checkbox;
	}
	public void setCheckbox(Boolean checkbox) {
		this.checkbox = checkbox;
	}

	public int getId_ck() {
		return id_ck;
	}
	public void setId_ck(int id_ck) {
		this.id_ck = id_ck;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
