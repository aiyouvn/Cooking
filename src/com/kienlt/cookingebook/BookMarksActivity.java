/*package com.kienlt.cookingebook;

import java.io.File;
import java.util.ArrayList;

import com.artifex.mupdf.MuPDFActivity;
import com.kienlt.cookingebook.db.Bookmarks;
import com.kienlt.cookingebook.db.PdfDatabase;
import com.kienlt.cookingebook.utils.Config;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class BookMarksActivity extends Activity {

	ListView list_bookmark;
	PdfDatabase sql;
	ArrayList<Bookmarks> arrayList;
	ArrayAdapter<Bookmarks> adapter;
	ImageButton deletebutton;
	TextView count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_marks);
		list_bookmark = (ListView) findViewById(R.id.listbookmark_pdf);
		sql = new PdfDatabase(getBaseContext());
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		final int position_Pdf = bundle.getInt("posi");
		arrayList = sql.getBookmarkbyId(position_Pdf);
		adapter = new MyAdapterBoook(BookMarksActivity.this, arrayList);
		list_bookmark.setAdapter(adapter);

		list_bookmark.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				// Bundle bundle=getIntent().getExtras();
				String name_pdf = arrayList.get(position).getName();
				File file = new File(Config.APP_FOLDER + "/" + name_pdf
						+ ".pdf");
				Intent intent = new Intent(BookMarksActivity.this,
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

		count = (TextView) findViewById(R.id.countcheck);

		deletebutton = (ImageButton) findViewById(R.id.deleteButton);
		deletebutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				
				if(arrayList.size()>0)
				{
					 
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						BookMarksActivity.this);
				dialog.setTitle("Thông Báo");
				dialog.setIcon(R.drawable.iconde);
				dialog.setMessage("Bạn có muốn xoá không?");
				dialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								int ii = 0;
								int countcheck = list_bookmark.getCount();
								for (int i = 0; i < countcheck; i++) {
									if (arrayList.get(i).getCheckbox() == true) {

										Bookmarks bookmarks = new Bookmarks();
										bookmarks.setId(arrayList.get(i)
												.getId());
										sql.deleteBookmark(bookmarks);
									//	int a=arrayList.size()-1;
										ii++;

									}
									if (ii > 0) {
										count.setText("Đã xoá: "
												+ String.valueOf(ii).toString()
												+ " bookmark");
									}
								}
									ArrayList<Bookmarks> arraydelete = sql
										.getBookmarkbyId(position_Pdf);
								
								adapter = new MyAdapterBoook(
										BookMarksActivity.this, arraydelete);
								list_bookmark.setAdapter(adapter);
								adapter.notifyDataSetChanged();
								
								MyAdapterBoook myAdapterBoook=new MyAdapterBoook(BookMarksActivity.this, arrayList);
								myAdapterBoook.setData(arrayList);
								int a=arrayList.size()-1;
								Log.d("size adaptr", ""+a);
							
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

	class Viewholder {
		TextView txtnumpage, txtnampdf,txtdatime;
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
				convertView.setTag(R.id.txttitlebkark, hodler.txtnampdf);
				convertView.setTag(R.id.txtnumber_pagebmark, hodler.txtnumpage);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_marks, menu);
		return true;
	}

}
*/