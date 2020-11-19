package com.nabase1.mvvm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class storageUtils {

    private static String TAG = "mvvm";

    public storageUtils() {
    }

    // ----------------------------------
    // EXTERNAL STORAGE
    // ----------------------------------

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    private static File createOrGetFile(File destination, String fileName, String folderName){
        File folder = new File(destination, folderName);
        return new File(folder, fileName);
    }

    public static void setTextInStorage(File rootDestination, Context context, String fileName, String folderName, String text){
        File file = createOrGetFile(rootDestination, fileName, folderName);
        writeFile(context, text, file);
    }

    private static void writeFile(Context context, String text, File file){
        try {
            if(!file.exists()){
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);

            try (Writer w = new BufferedWriter(new OutputStreamWriter(fos))) {
                w.write(text);
                w.flush();
                fos.getFD().sync();
            } finally {
                fos.close();
                //Toast.makeText(context, "saved" + context.getFilesDir(), Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



   public static String readTextFromUri(Context context, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line+"\n");
            }
        }
        return stringBuilder.toString();
    }

}
