package com.kienlt.cookingebook.db;

public class FilePdf {

	private  int id;
	private String image;
	private String name_dpf ;
	public FilePdf(){};
	public FilePdf(int id, String name_dpf,String image) {
		this.setId(id);
		this.setImage(image);
		this.setName_dpf(name_dpf);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName_dpf() {
		return name_dpf;
	}
	public void setName_dpf(String name_dpf) {
		this.name_dpf = name_dpf;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

}
