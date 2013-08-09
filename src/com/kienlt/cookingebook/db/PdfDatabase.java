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

	// Attribute table 1
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name_pdf";
	private static final String KEY_IMAGES = "image";
	// Attribute table 2
	private static final String KEY_ID2 = "id";
	private static final String KEY_NUMBER = "page_number";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CONSTANST = "id_pdf";

	public PdfDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String SQL = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY  ," + KEY_NAME + " TEXT ," + KEY_IMAGES
				+ " TEXT )";
		db.execSQL(SQL);

		String SQL2 = "CREATE TABLE " + TABLE_NAME2 + "(" + KEY_ID2
				+ " INTEGER PRIMARY KEY  ," + KEY_TITLE + " TEXT ,"
				+ KEY_NUMBER + " INTEGER ," + KEY_CONSTANST + " INTEGER )";
		db.execSQL(SQL2);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF NOT EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF NOT EXISTS " + TABLE_NAME2);
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

	// SELECT BOOK TO ID
/*	public FilePdf SelectBookId(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = { KEY_ID, KEY_NAME, KEY_IMAGES };
		String selection = KEY_ID + "='" + id + "'";
		String[] selectionArgs = { String.valueOf(id) };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor cusor = db.query(TABLE_NAME, columns, selection, selectionArgs,groupBy, having, orderBy);
		if (cusor != null)
	
			cusor.moveToFirst();
		FilePdf book = new FilePdf(cusor.getInt(0), cusor.getString(1),cusor.getString(2));
		
		return book;
		
		
		
		
	}*/
	
	public FilePdf getBookToId(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
	            KEY_NAME, KEY_IMAGES }, KEY_ID + "=?",
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
				details_pdf
						.setNumber_page(Integer.parseInt(cursor.getString(2)));
				details_pdf.setId_pdf(Integer.parseInt(cursor.getString(3)));
				arraylistpdf.add(details_pdf);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return arraylistpdf;
	}

}
