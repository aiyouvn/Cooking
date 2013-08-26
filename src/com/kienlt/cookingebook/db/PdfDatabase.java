package com.kienlt.cookingebook.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PdfDatabase extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "CookingBooks";
	private static final String TABLE_NAME = "Pdf_File";
	private static final String TABLE_NAME2 = "Details_Pdf";
	private static final String TABLE_NAME3 = "Bookmarks";

	// Attribute table 1
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name_pdf";
	private static final String KEY_IMAGES = "image";
	// Attribute table 2
	private static final String KEY_ID2 = "id";
	private static final String KEY_NUMBER = "page_number";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CONSTANST = "id_pdf";

	// Attribute table 3
	private static final String KEY_ID3 = "id";
	private static final String KEY_TITLE3 = "name";
	private static final String KEY_NUMBER3 = "number_bookmark";
	private static final String KEY_CONSTANST3 = "id_pdf";
	private static final String KEY_DATETIME ="date_time";
	

	public PdfDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String SQL = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY  ," + KEY_NAME + " TEXT ,"
				+ KEY_IMAGES + " TEXT )";
		db.execSQL(SQL);

		String SQL2 = "CREATE TABLE " + TABLE_NAME2 + "(" + KEY_ID2
				+ " INTEGER PRIMARY KEY  ," + KEY_TITLE + " TEXT ,"
				+ KEY_NUMBER + " INTEGER ," + KEY_CONSTANST + " INTEGER )";
		db.execSQL(SQL2);

		String SQL3 = "CREATE TABLE " + TABLE_NAME3 + "(" + KEY_ID3
				+ " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_TITLE3 + " TEXT ,"
				+ KEY_NUMBER3 + " INTEGER ," + KEY_CONSTANST3 + " INTEGER ,"+ KEY_DATETIME+" TEXT )";
		db.execSQL(SQL3);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF NOT EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF NOT EXISTS " + TABLE_NAME2);
		db.execSQL("DROP TABLE IF NOT EXISTS " + TABLE_NAME3);
		onCreate(db);

	}

	// INSERT DATA TO TABLE PDF_FILE
	public void insertPdf_File(FilePdf filedpf) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, filedpf.getId());
		values.put(KEY_NAME, filedpf.getName_dpf());
		values.put(KEY_IMAGES, filedpf.getImage());
		db.insert(TABLE_NAME, null, values);
		db.close();
	}

	// GETALL DATA IN TABLE PDF_FILE
	public ArrayList<FilePdf> getAllFilePdf() {
		ArrayList<FilePdf> arraylistpdf = new ArrayList<FilePdf>();

		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select * from " + TABLE_NAME;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			do {
				FilePdf filepdf = new FilePdf();
				filepdf.setId(Integer.parseInt(cursor.getString(0)));
				filepdf.setName_dpf(cursor.getString(1));
				filepdf.setImage(cursor.getString(2));

				arraylistpdf.add(filepdf);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;

	}
	
	// GETALL DATA IN TABLE PDF_FILE
	public ArrayList<Integer> getAllIDFilePdf() {
		ArrayList<Integer> arraylistpdf = new ArrayList<Integer>();

		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select "+KEY_ID+" from " + TABLE_NAME;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			do {
				arraylistpdf.add(Integer.parseInt(cursor.getString(0)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;

	}

	
	// GETALL DATA IN TABLE PDF_FILE
	public FilePdf getPDFById(int id) {

		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select * from " + TABLE_NAME + " where "+ KEY_ID +" = "+id;

		Cursor cursor = db.rawQuery(sql, null);
		FilePdf filepdf = null;
		if (cursor.moveToFirst()) {
			do {
				filepdf = new FilePdf();
				filepdf.setId(Integer.parseInt(cursor.getString(0)));
				filepdf.setName_dpf(cursor.getString(1));
				filepdf.setImage(cursor.getString(2));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return filepdf;

	}


	public FilePdf getBookToId(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID, KEY_NAME,
				KEY_IMAGES }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		FilePdf book = new FilePdf(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2));

		return book;
	}

	// INSERT DATA TO TABLE DETAILS_PDF
	public void insertDetails_Pdf(DetailsPdf detailsPdf) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID2, detailsPdf.getId());
		values.put(KEY_TITLE, detailsPdf.getTitle());
		values.put(KEY_NUMBER, detailsPdf.getNumber_page());
		values.put(KEY_CONSTANST, detailsPdf.getId_pdf());
		db.insert(TABLE_NAME2, null, values);
		db.close();
	}
	
	
	public ArrayList<Integer> getAllIDDetailsPdf() {
		ArrayList<Integer> arraylistpdf = new ArrayList<Integer>();

		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select "+KEY_ID2+" from " + TABLE_NAME2;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			do {
				arraylistpdf.add(Integer.parseInt(cursor.getString(0)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;

	}

	// GETALL DATA IN TABLE DETAILS_PDF
	public ArrayList<DetailsPdf> getAllDetailsPdf(int id_pdf) {
		ArrayList<DetailsPdf> arraylistpdf = new ArrayList<DetailsPdf>();
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select * from " + TABLE_NAME2 + " where " + KEY_CONSTANST
				+ "='" + id_pdf + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			do {
				DetailsPdf details_pdf = new DetailsPdf();
				details_pdf.setId(Integer.parseInt(cursor.getString(0)));
				details_pdf.setTitle(cursor.getString(1));
				details_pdf.setNumber_page(Integer.parseInt(cursor.getString(2)));
				details_pdf.setId_pdf(Integer.parseInt(cursor.getString(3)));
				
				arraylistpdf.add(details_pdf);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;
	}
	// Insert BookMark
	public void insertBookmark_Pdf(Bookmarks bookmark) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE3, bookmark.getName());
		values.put(KEY_NUMBER3, bookmark.getNumber_bookmark());
		values.put(KEY_CONSTANST3, bookmark.getId_ck());
		values.put(KEY_DATETIME, bookmark.getDatetime());
		db.insert(TABLE_NAME3, null, values);
		db.close();
	}
	// GETALL DATA IN TABLE BookMark
	public ArrayList<Bookmarks> getAllBookmark() {
		ArrayList<Bookmarks> arraylistpdf = new ArrayList<Bookmarks>();

		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select * from " + TABLE_NAME3;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			do {
				Bookmarks bookmarks = new Bookmarks();
				bookmarks.setId(Integer.parseInt(cursor.getString(0)));
				bookmarks.setName(cursor.getString(1));
				bookmarks.setNumber_bookmark(cursor.getInt(2));
				bookmarks.setId_ck(cursor.getInt(3));
				bookmarks.setDatetime(cursor.getString(4));
				arraylistpdf.add(bookmarks);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;

	}
	// GETALL DATA IN TABLE Bookmark to ID
	public ArrayList<Bookmarks> getBookmarkbyId(int id_pdf) {
		ArrayList<Bookmarks> arraylistpdf = new ArrayList<Bookmarks>();
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "select * from " + TABLE_NAME3 + " where " + KEY_CONSTANST3
				+ "='" + id_pdf + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			do {
				Bookmarks bookmarks = new Bookmarks();
				bookmarks.setId(Integer.parseInt(cursor.getString(0)));
				bookmarks.setName(cursor.getString(1));
				bookmarks.setNumber_bookmark(cursor.getInt(2));
				bookmarks.setId_ck(cursor.getInt(3));
				bookmarks.setDatetime(cursor.getString(4));
				arraylistpdf.add(bookmarks);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;
	}
	// Delete Bookmark
	public void deleteBookmark(Bookmarks bookmark) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    db.delete(TABLE_NAME3, KEY_ID3 + " = ?",
	            new String[] { String.valueOf(bookmark.getId()) });
	    db.close();
	}
}
