package com.kienlt.cookingebook.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;


public class ParserCsv extends Activity{

	
	public ParserCsv(){};
	
	public String ReadCsv() {
		String result="";
		try {
			AssetManager asset=getBaseContext().getAssets();
			InputStream is=asset.open("PdfName.csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			String line="";
			StringBuilder sb = new StringBuilder();
			while((line=reader.readLine())!=null)
			{
				sb.append(line + "\n");
			}
			reader.close();
			result=sb.toString();
		
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

}
