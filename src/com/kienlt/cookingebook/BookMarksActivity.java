package com.kienlt.cookingebook;

import java.io.File;
import java.util.ArrayList;

import com.artifex.mupdf.MuPDFActivity;
import com.kienlt.cookingebook.db.Bookmarks;
import com.kienlt.cookingebook.db.PdfDatabase;
import com.kienlt.cookingebook.utils.Config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BookMarksActivity extends Activity {

	ListView list_bookmark;
	PdfDatabase sql;
	ArrayList<Bookmarks> arrayList;
	ArrayAdapter<Bookmarks> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_marks);
		list_bookmark = (ListView) findViewById(R.id.listbookmark_pdf);
		sql=new PdfDatabase(getBaseContext());
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		int position_Pdf=bundle.getInt("posi");
		arrayList = sql.getBookmarkbyId(position_Pdf);
		adapter = new MyAdapterBoook(BookMarksActivity.this, arrayList);
		list_bookmark.setAdapter(adapter);
		
		
		list_bookmark.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
			//	Bundle bundle=getIntent().getExtras();
				String name_pdf=arrayList.get(position).getName();
				File file = new File(Config.APP_FOLDER + "/"
						+ name_pdf + ".pdf");
				Intent intent = new Intent(BookMarksActivity.this,
						MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);

				intent.setDataAndType(Uri.fromFile(file), "application/pdf");
				int index=arrayList.get(position).getNumber_bookmark();
				Log.d("chychaychay",name_pdf+index);
				intent.putExtra(Config.INDEX_PAGE, index);
				startActivity(intent);

				
			}
		});
	}
	class Viewholder {
		TextView txtnumpage, txtnampdf;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			Viewholder hodler;
			if (convertView == null) {
				hodler = new Viewholder();
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_bookmark, null);

				hodler.txtnumpage = (TextView) convertView
						.findViewById(R.id.txtnumber_pagebmark);
				hodler.txtnampdf = (TextView) convertView
						.findViewById(R.id.txttitlebkark);

				convertView.setTag(hodler);
				convertView.setTag(R.id.txttitlebkark, hodler.txtnampdf);
				convertView.setTag(R.id.txtnumber_pagebmark, hodler.txtnumpage);

			} else {
				hodler = (Viewholder) convertView.getTag();
			}
			hodler.txtnampdf.setText(arrayList.get(position).getName());
			hodler.txtnumpage.setText(String.valueOf(arrayList.get(position).getNumber_bookmark()));
			return convertView;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_marks, menu);
		return true;
	}

}
