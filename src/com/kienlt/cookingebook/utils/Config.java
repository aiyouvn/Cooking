package com.kienlt.cookingebook.utils;

import android.os.Environment;

public class Config {
	public static final String APP_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/Cooking";
	public static final String IMAGE_FOLDER = APP_FOLDER +"/Image";
	public static final String IMAGE_UNZIP = APP_FOLDER +"/Image";
    public static final String FOLDER_DATABASE = APP_FOLDER + "/databases";
	public static final String PDF_EXT = ".pdf";
	public static final String IMAGE_ZIP = ".zip";
	public static final String DATABASE_CSV = ".csv";
	public static String PDF_BANKING_BASIC ="Baking Basic.pdf";
	public static String PDF_FAMILY_FEAST ="Family Feast.pdf";
	public static String  PDF_ONE_SWEET  ="One Sweet Bite.pdf";
	public static String PDF_QUICK_AND_EASY ="Quick_And_Easy.pdf";
	public static String PDF_STUDENTS_MEALS  ="Students_Meals.pdf";
	public static String FOLDER_NAME="Cooking";
	public static String INDEX_PAGE="page_index";
	
	
	
}
