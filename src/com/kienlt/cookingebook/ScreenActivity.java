package com.kienlt.cookingebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.artifex.mupdf.MuPDFActivity;
import com.artifex.mupdf.ReaderView;
import com.kienlt.cookingebook.db.FilePdf;
import com.kienlt.cookingebook.db.PdfDatabase;
import com.kienlt.cookingebook.utils.Config;
import com.kienlt.cookingebook.utils.DialogUtil;
import com.kienlt.cookingebook.utils.FileUtils;

public class ScreenActivity extends Activity {

	private ProgressDialog mProgressDialog;
	private SharedPreferences prefs;
	ListView lstpdf;
	TextView txtpdf;
	EditText editsearch;
	ReaderView mDocView;
	Button btngrid, btnlist, btnxoa, btnmenu;
	GridView grid_view;
	ArrayList<FilePdf> arrayPdf;
	ArrayAdapter<FilePdf> adapter;
	PdfDatabase sql;
	String path;
	String keyWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen);
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			DialogUtil.showNoSdAlert(this, R.string.dialog_title,
					R.string.sdcard_not_mounted);
		}
		prefs = getSharedPreferences("com.kienlt.cookingebook", MODE_PRIVATE);
		mProgressDialog = DialogUtil.createProgressDialog(this, getResources()
				.getString(R.string.copy_data));
		path = Config.FOLDER_DATABASE + "/PdfName.csv";
		// chay copy data
		if (prefs.getBoolean("first_run", true)) {
			// copy data to sdcard
			CopyDataTask copyTask = new CopyDataTask();
			copyTask.execute();
			prefs.edit().putBoolean("first_run", false).commit();
		}
		
		sql = new PdfDatabase(getBaseContext());
		RunBackround runbacround = new RunBackround();
		runbacround.execute();
		// /search
		editsearch = (EditText) findViewById(R.id.editxoa);
		btnxoa = (Button) findViewById(R.id.btnxoa);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		btnxoa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				editsearch.setText("");
				btnxoa.setVisibility(View.GONE);

			}
		});
		editsearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				keyWord = editsearch.getText().toString();
				RunBackroundSearch runsearch = new RunBackroundSearch();
				runsearch.execute(keyWord);

				RunBackroundSearchList runsearchlist = new RunBackroundSearchList();
				runsearchlist.execute(keyWord);
				if (keyWord.equals("")) {
					btnxoa.setVisibility(View.GONE);
					/*
					 * RunBackround runbacround = new RunBackround();
					 * runbacround.execute(); RunBackGround2 runbacround2 = new
					 * RunBackGround2(); runbacround2.execute();
					 */

				} else {
					btnxoa.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		// list and grid
		lstpdf = (ListView) findViewById(R.id.lstPdf);
		grid_view = (GridView) findViewById(R.id.gridPdf);
		btngrid = (Button) findViewById(R.id.btngrid);
		btngrid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editsearch.setText("");
				RunBackround runbacround = new RunBackround();
				runbacround.execute();
				lstpdf.setVisibility(View.GONE);
				grid_view.setVisibility(View.VISIBLE);

			}
		});
		btnlist = (Button) findViewById(R.id.btnlist);
		btnlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editsearch.setText("");
				RunBackGround2 runbacround2 = new RunBackGround2();
				runbacround2.execute();
				lstpdf.setVisibility(View.VISIBLE);
				grid_view.setVisibility(View.GONE);
			}
		});

	}

	class RunBackround extends AsyncTask<Void, Void, ArrayList<FilePdf>> {
		String line = "";
		int id_pdf;
		String namePdf = "";
		String imagePdf = "";

		@Override
		protected ArrayList<FilePdf> doInBackground(Void... params) {
			ArrayList<Integer> arrayPdf = sql.getAllIDFilePdf();
			try {
				FileInputStream iStream = new FileInputStream(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						iStream, "utf-8"), 8);
				while ((line = reader.readLine()) != null) {
					String[] str = line.split(",");
					if (str[0].contains("id") || str[0].contains("name")
							|| str[0].contains("image"))
						continue;
					id_pdf = Integer.parseInt(str[0].toString());
					namePdf = str[1].toString();
					imagePdf = str[2].toString();
				//	Log.d("in ra", id_pdf + namePdf + imagePdf);
					if (!arrayPdf.contains(id_pdf)) {
						FilePdf pdf = new FilePdf();
						pdf.setId(id_pdf);
						pdf.setName_dpf(namePdf);
						pdf.setImage(imagePdf);
						// if not exist
						// if(sql.getPDFById(id_pdf)==null){
						sql.insertPdf_File(pdf);
						// }
					}

				}

				reader.close();
				ArrayList<FilePdf> arrayPdf2 = sql.getAllFilePdf();
				return arrayPdf2;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ArrayList<FilePdf>();
			
		   
		}

		@Override
		protected void onPostExecute(ArrayList<FilePdf> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			arrayPdf = result;
			grid_view = (GridView) findViewById(R.id.gridPdf);
			adapter = new MyAdapter(ScreenActivity.this, arrayPdf);
			grid_view.setAdapter(adapter);
			grid_view.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					File file = new File(Config.APP_FOLDER + "/"
							+ arrayPdf.get(position).getName_dpf() + ".pdf");
					final Intent intent = new Intent(ScreenActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.putExtra(Config.INDEX_PAGE, 0);
					intent.putExtra("name_pdf", arrayPdf.get(position)
							.getName_dpf());
					intent.putExtra("id_PdfFile", arrayPdf.get(position).getId());
					startActivity(intent);
		

				}
			});
		}

	}

	@SuppressLint("DefaultLocale")
	// /asyntask search
	class RunBackroundSearch extends
			AsyncTask<Object, Object, ArrayList<FilePdf>> {
		@Override
		protected ArrayList<FilePdf> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			keyWord = editsearch.getText().toString();

			ArrayList<FilePdf> arraysearch = new ArrayList<FilePdf>();
			ArrayList<FilePdf> arrayPdfsearch = sql.getAllFilePdf();
			try {
				if (arrayPdfsearch.size() != 0) {
					for (int i = 0; i < arrayPdfsearch.size(); i++) {
						int id=arrayPdfsearch.get(i).getId();
						String namegrid = arrayPdfsearch.get(i).getName_dpf();
						String a = namegrid.toLowerCase();
						String imagegrid = arrayPdfsearch.get(i).getImage();
						if (a.contains(keyWord.trim())) {
							FilePdf pdf = new FilePdf();
							pdf.setId(id);
							pdf.setName_dpf(namegrid);
							pdf.setImage(imagegrid);
							arraysearch.add(pdf);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return arraysearch;
		}
		@Override
		protected void onPostExecute(ArrayList<FilePdf> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			arrayPdf = result;
			grid_view = (GridView) findViewById(R.id.gridPdf);
			adapter = new MyAdapter(ScreenActivity.this, arrayPdf);
			grid_view.setAdapter(adapter);
			grid_view.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					File file = new File(Config.APP_FOLDER + "/"
							+ arrayPdf.get(position).getName_dpf() + ".pdf");
					Intent intent = new Intent(ScreenActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.putExtra(Config.INDEX_PAGE, 0);
					intent.putExtra("name_pdf", arrayPdf.get(position)
							.getName_dpf());
					intent.putExtra("id_PdfFile", arrayPdf.get(position).getId());
					startActivity(intent);
				

				}
			});
		}

	}

	// /asyntask search

	// //asyntask search List

	class RunBackroundSearchList extends
			AsyncTask<Object, Object, ArrayList<FilePdf>> {
		@Override
		protected ArrayList<FilePdf> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			keyWord = editsearch.getText().toString();
			ArrayList<FilePdf> arraysearchlist = new ArrayList<FilePdf>();
			ArrayList<FilePdf> arrayPdfsearchlist = sql.getAllFilePdf();
			try {
				if (arrayPdfsearchlist.size() != 0) {
					for (int i = 0; i < arrayPdfsearchlist.size(); i++) {
						int id=arrayPdfsearchlist.get(i).getId();
						String namelist = arrayPdfsearchlist.get(i)
								.getName_dpf();
						String a = namelist.toLowerCase();
						String imagegrid = arrayPdfsearchlist.get(i).getImage();
						if (a.contains(keyWord.trim())) {
							FilePdf pdf = new FilePdf();
							pdf.setId(id);
							pdf.setName_dpf(namelist);
							pdf.setImage(imagegrid);
							arraysearchlist.add(pdf);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return arraysearchlist;
		}

		@Override
		protected void onPostExecute(ArrayList<FilePdf> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			lstpdf = (ListView) findViewById(R.id.lstPdf);
			arrayPdf = new ArrayList<FilePdf>();
			arrayPdf = result;
			adapter = new MyAdapterlist(ScreenActivity.this, arrayPdf);
			lstpdf.setAdapter(adapter);

			lstpdf.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					File file = new File(Config.APP_FOLDER + "/"
							+ arrayPdf.get(position).getName_dpf() + ".pdf");
					Intent intent = new Intent(ScreenActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.putExtra(Config.INDEX_PAGE, 0);
					intent.putExtra("name_pdf", arrayPdf.get(position)
							.getName_dpf());
					intent.putExtra("id_PdfFile", arrayPdf.get(position).getId());
					startActivity(intent);


				}
			});
		}

	}

	// //asyntask search List

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("first_run", true)) {
			// copy data to sdcard
			CopyDataTask copyTask = new CopyDataTask();
			copyTask.execute();
			prefs.edit().putBoolean("first_run", false).commit();
		}

	}

	class Viewhodler {
		TextView id, name;
		ImageView image;
		Button btnmenu;
	}

	public class MyAdapter extends ArrayAdapter<FilePdf> {
		Activity context;
		ArrayList<FilePdf> arrayPdf;
		public ImageLoader imageLoader;

		public MyAdapter(Context context, ArrayList<FilePdf> arrayPdf) {
			super(context, R.layout.activity_screen, arrayPdf);
			// TODO Auto-generated constructor stub
			this.context = (Activity) context;
			this.arrayPdf = arrayPdf;
			imageLoader = new ImageLoader(context.getApplicationContext());

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Viewhodler hodler = null;
			if (convertView == null) {
				hodler = new Viewhodler();
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.lst_grid, null);
				hodler.image = (ImageView) convertView
						.findViewById(R.id.imageview);

				convertView.setTag(hodler);
				convertView.setTag(R.id.imageview, hodler.image);

			} else {
				hodler = (Viewhodler) convertView.getTag();
			}
			imageLoader.DisplayImage(Config.IMAGE_UNZIP + "/Images/"
					+ arrayPdf.get(position).getImage(), hodler.image);

			return convertView;
		}

	}

	class RunBackGround2 extends AsyncTask<Void, Void, ArrayList<FilePdf>> {

		@Override
		protected ArrayList<FilePdf> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<FilePdf> array2 = new ArrayList<FilePdf>();
			array2 = sql.getAllFilePdf();
			return array2;
		}

		@Override
		protected void onPostExecute(ArrayList<FilePdf> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.clear();
			lstpdf = (ListView) findViewById(R.id.lstPdf);
			arrayPdf = new ArrayList<FilePdf>();
			arrayPdf = result;
			adapter = new MyAdapterlist(ScreenActivity.this, arrayPdf);
			lstpdf.setAdapter(adapter);

			lstpdf.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					File file = new File(Config.APP_FOLDER + "/"
							+ arrayPdf.get(position).getName_dpf() + ".pdf");
					Intent intent = new Intent(ScreenActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.putExtra(Config.INDEX_PAGE, 0);
					intent.putExtra("name_pdf", arrayPdf.get(position)
							.getName_dpf());
					intent.putExtra("id_PdfFile", arrayPdf.get(position).getId());
					startActivity(intent);
					

				}
			});

		}
	}

	public class MyAdapterlist extends ArrayAdapter<FilePdf> {
		Activity context;
		ArrayList<FilePdf> arrayPdf;
		public ImageLoader imageLoader;

		public MyAdapterlist(Context context, ArrayList<FilePdf> arrayPdf) {
			super(context, R.layout.activity_screen, arrayPdf);
			// TODO Auto-generated constructor stub
			this.context = (Activity) context;
			this.arrayPdf = arrayPdf;
			imageLoader = new ImageLoader(context.getApplicationContext());
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Viewhodler hodler = null;
			if (convertView == null) {
				hodler = new Viewhodler();
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_pdf, null);
				hodler.image = (ImageView) convertView
						.findViewById(R.id.imageview2);
				hodler.name = (TextView) convertView.findViewById(R.id.txtpdf);
				btnmenu = (Button) findViewById(R.id.btnmenu);
				hodler.btnmenu = (Button) convertView
						.findViewById(R.id.btnmenu);

				convertView.setTag(hodler);
				convertView.setTag(R.id.btnmenu, hodler.btnmenu);
				hodler.btnmenu.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Viewhodler holder1 = (Viewhodler)v.getTag();
						// Access the Textview from holder1 like below
						// holder1.btnmenu.setText("Plus");

						Intent intentmenu = new Intent(ScreenActivity.this,
								CategoryActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("id_pdf", arrayPdf.get(position).getId());
						bundle.putString("name_pdf", arrayPdf.get(position)
								.getName_dpf());
						intentmenu.putExtras(bundle);
						startActivity(intentmenu);
						

					}
				});
				convertView.setTag(R.id.txtpdf, hodler.name);

				convertView.setTag(R.id.imageview2, hodler.image);

			} else {
				hodler = (Viewhodler) convertView.getTag();
			}
			hodler.name.setText(arrayPdf.get(position).getName_dpf());
			imageLoader.DisplayImage(Config.IMAGE_UNZIP + "/Images/"
					+ arrayPdf.get(position).getImage(), hodler.image);

			return convertView;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen, menu);
		return true;

	}

	private class CopyDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// copy pdf files from asset to sdcard
			copyAssetFileToSd();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideDialog();
		}

		@Override
		protected void onCancelled() {
			hideDialog();
		}
	};

	private void showDialog() {
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	private void hideDialog() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	// use this method to write the PDF file to sdcard
	private void copyAssetFileToSd() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		for (int i = 0; i < files.length; i++) {
			String fStr = files[i];
			File file = new File(FileUtils.getImageDir() + "/" + files[i]);
			File file2 = new File(FileUtils.getDatabaseDir() + "/" + files[i]);
			if (file2.exists()) {
				continue;
			}
			if (file.exists()) {
				continue;
			}
			if (fStr.contains(Config.PDF_EXT)) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(files[i]);
					out = new FileOutputStream(FileUtils.getSdcardDir() + "/"
							+ files[i]);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				} catch (Exception e) {
					Log.d("KienLT",
							"copy file to sdcard error: " + e.getMessage());
				}
			}
			if (fStr.contains(Config.IMAGE_ZIP)) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(files[i]);
					out = new FileOutputStream(FileUtils.getImageDir() + "/"
							+ files[i]);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
					String zipFile = Config.IMAGE_UNZIP + "/Images.zip";
					Decompress decompress = new Decompress(zipFile,
							Config.IMAGE_UNZIP + "/");
					decompress.unzip();
				}

				catch (Exception e) {
					Log.d("KienLT",
							"copy file to sdcard error: " + e.getMessage());
				}
			}

			if (fStr.contains(Config.DATABASE_CSV)) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(files[i]);
					out = new FileOutputStream(FileUtils.getDatabaseDir() + "/"
							+ files[i]);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				} catch (Exception e) {
					Log.d("KienLT",
							"copy file to sdcard error: " + e.getMessage());
				}
			}

		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
