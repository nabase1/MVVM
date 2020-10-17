package com.nabase1.mvvm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nabase1.mvvm.databinding.ActivityCreateNoteBinding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CreateNote extends AppCompatActivity {

    private ActivityCreateNoteBinding mBinding;
    int defaultBackgroundColor, defaultTextColor;
    private SharedPreferences mSharedPreferences;
    private String TAG = getClass().getSimpleName();
    private long mTimeStamp;
    private boolean speaking = false;
    ArrayList<String> mArrayList;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);

        mSharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        textToSpeech();

        Intent intent = getIntent();

        if(intent.hasExtra(Constants.EXTRA_ID)){
            defaultBackgroundColor = mSharedPreferences.getInt(Constants.BACK_COLOR, R.color.white);
            defaultTextColor = mSharedPreferences.getInt(Constants.TEXT_COLOR, R.color.black_de);
           defaultBackgroundColor = intent.getIntExtra(Constants.TEXT_PRIORITY, defaultBackgroundColor);
//            defaultColor = ContextCompat.getColor(this, R.color.lite_blue);
            mTimeStamp = intent.getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
            setTitle(getString(R.string.update_diary));
           // mBinding.editTextTitle.setText(intent.getStringExtra(Constants.TEXT_TITLE));
            mBinding.editTextBody.setText(intent.getStringExtra(Constants.TEXT_DESCRIPTION));
            mBinding.textViewDate.setText(setDate(mTimeStamp));

        }else {
            defaultBackgroundColor = mSharedPreferences.getInt(Constants.BACK_COLOR, R.color.white);
            defaultTextColor = mSharedPreferences.getInt(Constants.TEXT_COLOR, R.color.black_de);
            setTitle(getString(R.string.create_new_diary));
            mTimeStamp = Calendar.getInstance().getTimeInMillis();
            mBinding.textViewDate.setText(setDate(mTimeStamp));
        }

        mBinding.constraintLayout.setBackgroundColor(defaultBackgroundColor);
        mBinding.editTextBody.setTextColor(defaultTextColor);
        mBinding.textView2.setTextColor(defaultTextColor);
        mBinding.textViewDate.setTextColor(defaultTextColor);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.item_save){
            saveNote();
        }

        if(id == R.id.item_back_color){
            backgroundColorPicker();
        }

        if(id == R.id.item_text_color){
            textColorPicker();
        }

        if(id == R.id.item_saveFile){
            writeFile(mBinding.editTextBody.getText().toString());
        }

        if(id == R.id.item_speech_to_text){
            checkPermission();
            speechToText(mArrayList);
        }

        if(id == R.id.item_text_to_speech){
            mTextToSpeech.speak(mBinding.editTextBody.getText().toString(), TextToSpeech.QUEUE_FLUSH,null);
        }
        return true;
    }

    private void saveNote(){

        String multiLines = mBinding.editTextBody.getText().toString();
        String[] diary;
        String delimiter = "\n";
        diary = multiLines.split(delimiter);

        String title = diary[0];

        String desc = mBinding.editTextBody.getText().toString();

        Intent data = new Intent(this, MainActivity.class);

        data.putExtra(Constants.TEXT_TITLE, title);
        data.putExtra(Constants.TEXT_DESCRIPTION,desc);
        data.putExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
        data.putExtra(Constants.TEXT_PRIORITY, defaultBackgroundColor);

        int id = getIntent().getIntExtra(Constants.EXTRA_ID, -1);

        if(id != -1){
            long timestamp = getIntent().getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());

            data.putExtra(Constants.EXTRA_ID, id);
            data.putExtra(Constants.TIME_STAMP, timestamp);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    public String setDate(Long timeStamp){
        Date date=new Date(timeStamp);
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-YYYY  HH:mm:ss");
        sfd.format(date);

        return date.toString();
    }

    private void backgroundColorPicker(){
        final ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#000000");
        colors.add("#f20940");
        colors.add("#fafafa");
        colors.add("#3a19e1");
        colors.add("#daed0c");
        colors.add("#0cedd5");
        colors.add("#0ced17");
        colors.add("#033206");
        colors.add("#ce3006");
        colors.add("#bd05a1");

        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        defaultBackgroundColor = color;
                        mBinding.constraintLayout.setBackgroundColor(defaultBackgroundColor);
                        mSharedPreferences.edit().putInt(Constants.BACK_COLOR, defaultBackgroundColor).apply();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        colorPicker.show();
    }

    private void textColorPicker(){
        final ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#000000");
        colors.add("#f20940");
        colors.add("#fafafa");
        colors.add("#3a19e1");
        colors.add("#daed0c");
        colors.add("#0cedd5");
        colors.add("#0ced17");
        colors.add("#033206");
        colors.add("#ce3006");
        colors.add("#bd05a1");

        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        defaultTextColor = color;
                        mBinding.editTextBody.setTextColor(defaultTextColor);
                        mBinding.textView2.setTextColor(defaultTextColor);
                        mBinding.textViewDate.setTextColor(defaultTextColor);
                        mSharedPreferences.edit().putInt(Constants.TEXT_COLOR, defaultTextColor).apply();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        colorPicker.show();
    }

    private void writeFile(String text){
        if(!mBinding.editTextBody.getText().toString().isEmpty()){
            File file = new File(this.getFilesDir(), getString(R.string.app_name));
            if(!file.exists()){
                file.mkdir();
            }

            try {
                File file1 = new File(file, setDate(mTimeStamp));
                FileWriter fileWriter = new FileWriter(file1);
                fileWriter.append(text);
                fileWriter.flush();
                fileWriter.close();

                Toast.makeText(this, "File Saved On external storage", Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void readFile(){

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void speechToText(ArrayList<String> arrayList){

        mBinding.editTextBody.setHint("Speak To Me...");
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.domain.app");
      //  intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak To Me...");



        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

                if(speaking){
                    Log.d("End", "speaking");
                    speechRecognizer.startListening(intent);
                }
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> arrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(arrayList != null){
                    mBinding.editTextBody.setText(mBinding.editTextBody.getText().toString() + " " +arrayList.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> arrayList = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(arrayList != null){
                    mBinding.editTextBody.setText(mBinding.editTextBody.getText().toString() + " " +arrayList.get(0));
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        if(!speaking){
            speechRecognizer.startListening(intent);
            speaking = true;
        }else {
            speechRecognizer.stopListening();
            speaking = false;
        }

    }


    private void textToSpeech(){

        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    mTextToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

    }

}