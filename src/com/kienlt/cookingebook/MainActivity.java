/*package com.kienlt.cookingebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.artifex.mupdf.MuPDFActivity;
import com.kienlt.cookingebook.utils.Config;
import com.kienlt.cookingebook.utils.DialogUtil;
import com.kienlt.cookingebook.utils.FileUtils;

public class MainActivity extends Activity {
	
	private ProgressDialog mProgressDialog;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			DialogUtil.showNoSdAlert(this, R.string.dialog_title, R.string.sdcard_not_mounted);
		}
		Bundle bundle=getIntent().getExtras();
		String name_pdf=bundle.getString("name_pdf");
		
		prefs = getSharedPreferences("com.kienlt.cookingebook", MODE_PRIVATE);
	
		mProgressDialog = DialogUtil.createProgressDialog(this, getResources().getString(R.string.copy_data));
		
		File file = new File(Config.APP_FOLDER + "/" + name_pdf+".pdf");
        Intent intent = new Intent(MainActivity.this, MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
      //  String pos="20";
//		intent.putExtra(Config.INDEX_PAGE, Integer.parseInt(pos));
		intent.putExtra(Config.INDEX_PAGE, 0);

		startActivity(intent);
	}
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
	
//	@SuppressWarnings("unused")
//	private void startPDFActivity(File file) {
//		//File file2  = new File(Config.APP_FOLDER + "/" + Config.PDF_FILE_NAME);
//		Intent intent = new Intent(MainActivity.this, MuPDFActivity.class);
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
//       // String pos="20";
//  		//intent.putExtra(Config.INDEX_PAGE, Integer.parseInt(pos));
//        intent.putExtra(Config.INDEX_PAGE, 0);
//  	//	startActivity(intent);
//          
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(MainActivity.this, "NO Pdf Viewer", Toast.LENGTH_SHORT).show();
//        }
//
//	}
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
            File file = new File(FileUtils.getSdcardDir() + "/" + files[i]);
            if (file.exists()) {
            	continue;
            }
            if (fStr.contains(Config.PDF_EXT)) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(files[i]);
                    out = new FileOutputStream(FileUtils.getSdcardDir() + "/" + files[i]);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.d("KienLT", "copy file to sdcard error: " + e.getMessage());
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

}
*/