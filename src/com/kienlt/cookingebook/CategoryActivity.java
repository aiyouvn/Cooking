	package com.kienlt.cookingebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.artifex.mupdf.MuPDFActivity;
import com.kienlt.cookingebook.db.DetailsPdf;
import com.kienlt.cookingebook.db.PdfDatabase;
import com.kienlt.cookingebook.utils.Config;

public class CategoryActivity extends Activity {
 
 
int id_pdf_send;
  ArrayList<DetailsPdf> arraylist_details;
  PdfDatabase sql;
  ListView listdetails_pdf;
  ArrayAdapter<DetailsPdf> adapter;
  String name_pdf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Bundle bundle=getIntent().getExtras();
		id_pdf_send=bundle.getInt("id_pdf");
		name_pdf=bundle.getString("name_pdf");
		Log.d("aaaaaaa",String.valueOf(id_pdf_send));
		//text.setText(String.valueOf(id_pdf));
		sql=new  PdfDatabase(getApplicationContext());
		RunCategory runcategory=new RunCategory();
		runcategory.execute();
	}
	
	class RunCategory extends AsyncTask<Void, Void, ArrayList<DetailsPdf>>
	{
		String line="";
		String id="";
		String title="";
		String number_page="";
		String id_pdf="";
		 @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	        //
	        }

		
		
		@Override
		protected ArrayList<DetailsPdf> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				String path = Config.FOLDER_DATABASE+"/DetailsPdf.csv";
				FileInputStream iStream =  new FileInputStream(path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						iStream, "UTF-8"),8);
				while((line=reader.readLine())!=null)
				{
					 String[] str = line.split(",");
					 if(str[0].contains("id")||str[0].contains("title")||str[0].contains("number_page")||str[0].contains("id_pdf"))
			                continue;
					 id=str[0].toString();
					 title=str[1].toString();
					 number_page=str[2].toString();
					 id_pdf=str[3].toString();
					    Log.d("in ra",id+title+number_page+id_pdf );
				    DetailsPdf details_pdf=new DetailsPdf();
				    details_pdf.setId(Integer.parseInt(id));
				    details_pdf.setTitle(title);
				    details_pdf.setNumber_page(Integer.parseInt(number_page));
				    details_pdf.setId_pdf(Integer.parseInt(id_pdf));
					sql.insertDetails_Pdf(details_pdf);
				}
				reader.close();
				arraylist_details=sql.getAllDetailsPdf(id_pdf_send);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			//arraylist_details=sql.getAllDetailsPdf(id_pdf_send);
			/*for(int i=0;i<=arraylist_details.size();i++)
			{
				String a=arraylist_details.get(i).getTitle();
				int b=arraylist_details.get(i).getNumber_page();
				int  c=arraylist_details.get(i).getId_pdf();
				Log.d("sdfsdf", a+b+c);
			}	*/
			return arraylist_details;
		}
		@Override
		protected void onPostExecute(ArrayList<DetailsPdf> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			arraylist_details=result;
			adapter=new MyAdapter(CategoryActivity.this,arraylist_details);
			listdetails_pdf=(ListView)findViewById(R.id.listdetails_pdf);
			listdetails_pdf.setAdapter(adapter);
			
			listdetails_pdf.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					
					Bundle bundle=getIntent().getExtras();
					name_pdf=bundle.getString("name_pdf");
					File file = new File(Config.APP_FOLDER + "/"
							+ name_pdf + ".pdf");
					Intent intent = new Intent(CategoryActivity.this,
							MuPDFActivity.class);
					intent.setAction(Intent.ACTION_VIEW);

					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					int index=arraylist_details.get(position).getNumber_page();
					Log.d("chychaychay",""+index);
					intent.putExtra(Config.INDEX_PAGE, index);
					startActivity(intent);
					
					
				}
			});
			
		}
	}
	
	class Viewhodler
	{
		TextView title,number_page;
		
	}
	public class MyAdapter extends ArrayAdapter<DetailsPdf> {
		Activity context;
		ArrayList<DetailsPdf> arraylist_adapter; 
	
		
		public MyAdapter(Context context, ArrayList<DetailsPdf> arraylist_adapter) {
			super(context,R.layout.activity_category, arraylist_adapter);
			// TODO Auto-generated constructor stub
			this.context=(Activity) context;
			this.arraylist_adapter=arraylist_adapter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Viewhodler hodler=null;
			if(convertView==null)
			{
				hodler= new Viewhodler();
				LayoutInflater inflater=context.getLayoutInflater();
			convertView=inflater.inflate(R.layout.list_details, null);
			hodler.title=(TextView) convertView.findViewById(R.id.txttitle);
			hodler.number_page=(TextView)convertView.findViewById(R.id.txtnumber_page);
			
			convertView.setTag(hodler);
			convertView.setTag(R.id.txttitle,hodler.title);
			convertView.setTag(R.id.txtnumber_page,hodler.number_page);
			
			}
			else
			{
				hodler=(Viewhodler) convertView.getTag();
			}
			
			hodler.title.setText(arraylist_adapter.get(position).getTitle());
			hodler.number_page.setText(String.valueOf( arraylist_adapter.get(position).getNumber_page()));
			
		
			return convertView;
		}
			
		}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category, menu);
		return true;
	}

}
