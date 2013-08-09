package com.kienlt.cookingebook.db;

public class DetailsPdf {

	private int id;
	private int number_page;
	private String title;
	private int id_pdf;
	
	public DetailsPdf(){}
	public DetailsPdf(int id, int number_page, String title, int id_pdf) {
		super();
		this.id = id;
		this.number_page = number_page;
		this.title = title;
		this.id_pdf = id_pdf;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumber_page() {
		return number_page;
	}
	public void setNumber_page(int number_page) {
		this.number_page = number_page;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	
	public int getId_pdf() {
		return id_pdf;
	}
	public void setId_pdf(int id_pdf) {
		this.id_pdf = id_pdf;
	}

}
