package com.nabase1.mvvm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

public class storageUtils {

    private static String TAG = "mvvm";

    public storageUtils() {
    }

    public static void writeFile(String text, Context context, String name){
        if(!text.isEmpty()){
            File file = new File(context.getFilesDir(), name);
            FileOutputStream fos = null;
            if(!file.exists()){
                file.mkdir();
            }

            try {
                //File file1 = new File(file, setDate(mTimeStamp));

                fos = context.openFileOutput(name, MODE_PRIVATE);
                fos.write(text.getBytes());
                // FileWriter fileWriter = new FileWriter(file1);
//                fileWriter.append(text);
//                fileWriter.flush();
//                fileWriter.close();

                fos.close();

                Toast.makeText(context, "File Saved" + context.getFilesDir().toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG, context.getFilesDir().getAbsolutePath().toString());


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("zak.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
