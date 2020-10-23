package com.nabase1.mvvm;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.util.List;
import java.util.Locale;

public class SpeechText {


    public SpeechText() {
    }


    /* text to speech */

    private static TextToSpeech textToSpeech(Context context){
        return new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
    }

    public static void tp(Context context){
        textToSpeech(context).setLanguage(Locale.getDefault());
    }

    public static void readText(Context context, String text){
        textToSpeech(context).speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }



    /* speech to text */

    private static DroidSpeech speechDroid (Context context){
        return new DroidSpeech(context, null);
    }


    public static void speechToText(Context context, EditText editText){

       // mDroidSpeech = new DroidSpeech(context, null);

       speechDroid(context).setOnDroidSpeechListener(new OnDSListener() {
            @Override
            public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

            }

            @Override
            public void onDroidSpeechRmsChanged(float rmsChangedValue) {

            }

            @Override
            public void onDroidSpeechLiveResult(String liveSpeechResult) {
                if(liveSpeechResult != null){
                    // editText.setText(editText.getText().toString() + " " +liveSpeechResult);
                    Toast.makeText(context, "listening", Toast.LENGTH_SHORT).show();
                    Log.d("live speech", liveSpeechResult);
                }
            }

            @Override
            public void onDroidSpeechFinalResult(String finalSpeechResult) {

                if(finalSpeechResult != null){
                    editText.setText(editText.getText().toString() + " " +finalSpeechResult);
                }
            }

            @Override
            public void onDroidSpeechClosedByUser() {
                Toast.makeText(context, "Diary Loves Your Voice", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDroidSpeechError(String errorMsg) {
                Log.d("TAG", errorMsg);
            }
        });

    }

    public static void startSpeech(Context context){
        speechDroid(context).startDroidSpeechRecognition();
    }

    public static void endSpeech(Context context){
        speechDroid(context).closeDroidSpeechOperations();
        Toast.makeText(context, "Diary Stopped Listening", Toast.LENGTH_SHORT).show();
    }

}
