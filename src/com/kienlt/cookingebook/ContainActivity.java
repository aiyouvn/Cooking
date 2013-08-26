package com.kienlt.cookingebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.artifex.mupdf.MuPDFActivity;
import com.kienlt.cookingebook.db.Bookmarks;
import com.kienlt.cookingebook.db.DetailsPdf;
import com.kienlt.cookingebook.db.PdfDatabase;
import com.kienlt.cookingebook.utils.Config;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class ContainActivity extends Activity {
	private ImageButton mshowbookmarkButton, mshowmenuButton;
	ArrayList<DetailsPdf> arraylist_details;
	int id_pdf_send;
	PdfDatabase sql;
	ListView listdetails_pdf, listdetails_pdf2;
	ArrayAdapter<DetailsPdf> adapter;
	String name_pdf;

	ArrayList<Bookmarks> arrayList;
	ArrayAdapter<Bookmarks> adapterbook;
	ImageButton deletehomebutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contain);

		mshowbookmarkButton = (ImageButton) findViewById(R.id.showBookmarkButton);

		listdetails_pdf = (ListView) findViewById(R.id.listdetails_pdf);
		listdetails_pdf2 = (ListView) findViewById(R.id.listdetails_pdf2);
		// All Category
		Bundle bundle = getIntent().getExtras();
		id_pdf_send = bundle.getInt("posishow");
		name_pdf = bundle.getString("name_pdf_show");
		String line = "";
		int id = 0;
		String title = "";
		String number_page = "";
		String id_pdf = "";
		sql = new PdfDatabase(getBaseContext());
		ArrayList<Integer> arrayPdf = sql.getAllIDDetailsPdf();

		try {
			String path = Config.FOLDER_DATABASE + "/DetailsPdf.csv";
			FileInputStream iStream = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					iStream, "UTF-8"), 8);
			while ((line = reader.readLine()) != null) {
				String[] str = line.split(",");
				if (str[0].contains("id") || str[0].contains("title")
						|| str[0].contains("number_page")
						|| str[0].contains("id_pdf"))
					continue;
				id = Integer.parseInt(str[0].toString());
				title = str[1].toString();
				number_page = str[2].toString();
				id_pdf = str[3].toString();
				if (!arrayPdf.contains(id)) {
					DetailsPdf details_pdf = new DetailsPdf();
					details_pdf.setId(id);
					details_pdf.setTitle(title);
					details_pdf.setNumber_page(Integer.parseInt(number_page));
					details_pdf.setId_pdf(Integer.parseInt(id_pdf));
					sql.insertDetails_Pdf(details_pdf);
				}

			}
			reader.close();
			arraylist_details = sql.getAllDetailsPdf(id_pdf_send);
			adapter = new MyAdapter(ContainActivity.this, arraylist_details);
			listdetails_pdf = (ListView) findViewById(R.id.listdetails_pdf);
			listdetails_pdf.setAdapter(adapter);

			listdetails_pdf.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub

					Bundle bundle = getIntent().getExtras();
					name_pdf = bundle.getString("name_pdf_show");
					File file = new File(Config.APP_FOLDER + "/" + name_pdf
							+ ".pdf");
					Intent intent = new Intent(ContainActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					int index = arraylist_details.get(position)
							.getNumber_page();
					Log.d("chychaychay", "" + index);
					intent.putExtra(Config.INDEX_PAGE, index);
					intent.putExtra("name_pdf", name_pdf);
					intent.putExtra("id_PdfFile",
							arraylist_details.get(position).getId_pdf());
					int a = arraylist_details.get(position).getId_pdf();
					Log.d("id_catelogry", "" + a + name_pdf);

					startActivity(intent);

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		// End All Category

		mshowmenuButton = (ImageButton) findViewById(R.id.showMenuButton);
		deletehomebutton = (ImageButton) findViewById(R.id.deletehomeButton);

		mshowbookmarkButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = getIntent().getExtras();
				int id_pdf = bundle.getInt("posishow");
				sql = new PdfDatabase(getBaseContext());
				arrayList = sql.getBookmarkbyId(id_pdf);
				if (arrayList.size() == 0) {
					Toast.makeText(getApplicationContext(),
							"Chưa có bookmark nào !", Toast.LENGTH_SHORT)
							.show();

				}
				runBookMarkAll runcategory = new runBookMarkAll();
				runcategory.execute();
				listdetails_pdf2.setVisibility(View.VISIBLE);
				listdetails_pdf.setVisibility(View.GONE);
				deletehomebutton.setVisibility(View.VISIBLE);

			}
		});

		mshowmenuButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Bundle bundle = getIntent().getExtras();
				id_pdf_send = bundle.getInt("posishow");
				name_pdf = bundle.getString("name_pdf_show");
				Log.d("vitri", "" + id_pdf_send + name_pdf);
				RunCategory runcategory = new RunCategory();
				runcategory.execute();
				listdetails_pdf.setVisibility(View.VISIBLE);
				listdetails_pdf2.setVisibility(View.GONE);
				deletehomebutton.setVisibility(View.GONE);
				mshowbookmarkButton.setVisibility(View.VISIBLE);
			}
		});

		deletehomebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub

				if (arrayList.size() > 0) {

					AlertDialog.Builder dialog = new AlertDialog.Builder(
							ContainActivity.this);
					dialog.setTitle("Thông Báo");
					dialog.setIcon(R.drawable.iconde);
					dialog.setMessage("Bạn có muốn xoá không?");
					dialog.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									int countcheck = listdetails_pdf2
											.getCount();
									int dem = 0;
									for (int i = 0; i < countcheck; i++) {
										if (arrayList.get(i).getCheckbox() == true) {

											Bookmarks bookmarks = new Bookmarks();
											bookmarks.setId(arrayList.get(i)
													.getId());
											sql.deleteBookmark(bookmarks);
											dem++;

										}

									}
									/*
									 * adapterbook.notifyDataSetChanged();
									 * listdetails_pdf2.setAdapter(adapterbook);
									 */
									// listdetails_pdf2.setVisibility(View.GONE);
									// listdetails_pdf2.setVisibility(View.VISIBLE);
									Toast.makeText(getApplicationContext(),
											"Bạn đã xoá : " + dem +" bookmark!",
											Toast.LENGTH_SHORT).show();
									runBookMarkAll runcategory = new runBookMarkAll();
									runcategory.execute();

								}

							});
					dialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							});
					dialog.create().show();
				}

			}

		});

	}

	class runBookMarkAll extends AsyncTask<Void, Void, ArrayList<Bookmarks>> {

		@Override
		protected ArrayList<Bookmarks> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Bundle bundle = getIntent().getExtras();
			int id_pdf = bundle.getInt("posishow");
			sql = new PdfDatabase(getBaseContext());
			arrayList = sql.getBookmarkbyId(id_pdf);
			return arrayList;
		}

		@Override
		protected void onPostExecute(ArrayList<Bookmarks> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			arrayList = result;
			listdetails_pdf2 = (ListView) findViewById(R.id.listdetails_pdf2);
			adapterbook = new MyAdapterBoook(ContainActivity.this, arrayList);
			listdetails_pdf2.setAdapter(adapterbook);

			listdetails_pdf2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					// Bundle bundle=getIntent().getExtras();
					String name_pdf = arrayList.get(position).getName();
					File file = new File(Config.APP_FOLDER + "/" + name_pdf
							+ ".pdf");
					Intent intent = new Intent(ContainActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					int index = arrayList.get(position).getNumber_bookmark();
					Log.d("chychaychay", name_pdf + index);
					intent.putExtra(Config.INDEX_PAGE, index);
					intent.putExtra("name_pdf", name_pdf);
					intent.putExtra("id_PdfFile", arrayList.get(position)
							.getId_ck());
					startActivity(intent);

				}
			});
		}

	}

	class Viewholder {
		TextView txtnumpage, txtnampdf, txtdatime;
		CheckBox ckbdelete;
	}

	public class MyAdapterBoook extends ArrayAdapter<Bookmarks> {
		Context context;
		ArrayList<Bookmarks> arrayList;

		public MyAdapterBoook(Context context, ArrayList<Bookmarks> arrayList) {
			super(context, R.layout.activity_book_marks, arrayList);
			this.context = context;
			this.arrayList = arrayList;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			Viewholder hodler;
			if (convertView == null) {
				hodler = new Viewholder();
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_bookmark, null);

				hodler.txtnumpage = (TextView) convertView
						.findViewById(R.id.txtnumber_pagebmark);
				hodler.txtdatime = (TextView) convertView
						.findViewById(R.id.txttime);
				hodler.txtnampdf = (TextView) convertView
						.findViewById(R.id.txttitlebkark);
				hodler.ckbdelete = (CheckBox) convertView
						.findViewById(R.id.ckbdelete);
				hodler.ckbdelete
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								arrayList.get(position).setCheckbox(isChecked);

							}
						});
				convertView.setTag(hodler);
				convertView.setTag(R.id.txttitlebkark, hodler.txtnampdf);
				convertView.setTag(R.id.txtnumber_pagebmark, hodler.txtnumpage);
				convertView.setTag(R.id.txttime, hodler.txtdatime);
			} else {
				hodler = (Viewholder) convertView.getTag();
			}
			hodler.txtnampdf.setText(arrayList.get(position).getName());
			hodler.txtnumpage.setText(String.valueOf(arrayList.get(position)
					.getNumber_bookmark()));
			hodler.txtdatime.setText(arrayList.get(position).getDatetime());
			return convertView;

		}

		public void setData(ArrayList<Bookmarks> arrayList) {
			this.arrayList = arrayList;
			this.notifyDataSetChanged();
		}

	}

	// show All Menu by ID
	class RunCategory extends AsyncTask<Void, Void, ArrayList<DetailsPdf>> {

		@Override
		protected ArrayList<DetailsPdf> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			sql = new PdfDatabase(getBaseContext());
			arraylist_details = sql.getAllDetailsPdf(id_pdf_send);
			return arraylist_details;

		}

		@Override
		protected void onPostExecute(ArrayList<DetailsPdf> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			arraylist_details = result;

			adapter = new MyAdapter(ContainActivity.this, arraylist_details);
			listdetails_pdf = (ListView) findViewById(R.id.listdetails_pdf);
			listdetails_pdf.setAdapter(adapter);

			listdetails_pdf.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub

					Bundle bundle = getIntent().getExtras();
					name_pdf = bundle.getString("name_pdf_show");
					File file = new File(Config.APP_FOLDER + "/" + name_pdf
							+ ".pdf");
					Intent intent = new Intent(ContainActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					int index = arraylist_details.get(position)
							.getNumber_page();
					Log.d("chychaychay", "" + index);
					intent.putExtra(Config.INDEX_PAGE, index);
					intent.putExtra("name_pdf", name_pdf);
					intent.putExtra("id_PdfFile",
							arraylist_details.get(position).getId_pdf());
					int a = arraylist_details.get(position).getId_pdf();
					Log.d("id_catelogry", "" + a + name_pdf);

					startActivity(intent);

				}
			});

		}
	}

	class Viewhodler {
		TextView title, number_page;

	}

	public class MyAdapter extends ArrayAdapter<DetailsPdf> {
		Activity context;
		ArrayList<DetailsPdf> arraylist_adapter;

		public MyAdapter(Context context,
				ArrayList<DetailsPdf> arraylist_adapter) {
			super(context, R.layout.activity_category, arraylist_adapter);
			// TODO Auto-generated constructor stub
			this.context = (Activity) context;
			this.arraylist_adapter = arraylist_adapter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Viewhodler hodler = null;
			if (convertView == null) {
				hodler = new Viewhodler();
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_details, null);
				hodler.title = (TextView) convertView
						.findViewById(R.id.txttitle);
				hodler.number_page = (TextView) convertView
						.findViewById(R.id.txtnumber_page);

				convertView.setTag(hodler);
				convertView.setTag(R.id.txttitle, hodler.title);
				convertView.setTag(R.id.txtnumber_page, hodler.number_page);

			} else {
				hodler = (Viewhodler) convertView.getTag();
			}

			hodler.title.setText(arraylist_adapter.get(position).getTitle());
			hodler.number_page.setText(String.valueOf(arraylist_adapter.get(
					position).getNumber_page()));

			return convertView;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contain, menu);
		return true;
	}

}
