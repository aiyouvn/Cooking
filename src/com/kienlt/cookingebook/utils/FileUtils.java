package com.kienlt.cookingebook.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.kienlt.cookingebook.Decompress;

public final class FileUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static boolean copyDataFromAssetToSd(Context context, String fileName, String sdDir) throws IOException {
        if (checkFileExist(sdDir + "/" + fileName)) {
            Decompress d = new Decompress(sdDir + "/" + fileName, sdDir + "/");
            return d.unzip();
        }

        InputStream is = context.getAssets().open(fileName);
        OutputStream os = new FileOutputStream(sdDir + "/" + fileName);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        // Close the streams
        os.flush();
        os.close();
        is.close();

        Decompress d = new Decompress(sdDir + "/" + fileName, sdDir + "/");
        return d.unzip();
    }
    
    private static boolean checkFileExist(String filePath) {
        File dbfile = new File(filePath);
        return dbfile.exists();
    }
    
    public static void copyFile(String fileNamePath, String fileDestPath) throws IOException {
        File srcFile = new File(fileNamePath);
        InputStream inputStream = new FileInputStream(srcFile);
        FileOutputStream fileOutputStream = new FileOutputStream(fileDestPath);
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        int n;
        while ((n = inputStream.read(buf, 0, DEFAULT_BUFFER_SIZE)) > -1) {
            fileOutputStream.write(buf, 0, n);
        }
        inputStream.close();
        fileOutputStream.close();
    }

    public static String getSdcardDir() {
        File mediaStorageDir = new File(Config.APP_FOLDER);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("KienLT", "Failed to create directory");
                return null;
            }
        }
        return mediaStorageDir.getPath();
    }
    
   

    public static String getDatabaseDir() {
        File mediaStorageDir = new File(Config.FOLDER_DATABASE);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("KienLT", "Failed to create directory");
                return null;
            }
        }
        return mediaStorageDir.getPath();
    }

	public static String getImageDir() {
		// TODO Auto-generated method stub
		 File mediaStorageDir = new File(Config.IMAGE_FOLDER);

	        if (!mediaStorageDir.exists()) {
	            if (!mediaStorageDir.mkdirs()) {
	                Log.d("KienLT", "Failed to create directory");
	                return null;
	            }
	        }
	        return mediaStorageDir.getPath();
	}
}
