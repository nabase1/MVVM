package com.nabase1.mvvm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nabase1.mvvm.databinding.ActivityCreateNoteBinding;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

import static com.nabase1.mvvm.Constants.CREATE_FILE;
import static com.nabase1.mvvm.Constants.OPEN_FILE;

public class CreateNote extends AppCompatActivity {

    private ActivityCreateNoteBinding mBinding;
    int defaultBackgroundColor, defaultTextColor;
    private SharedPreferences mSharedPreferences;
    private String TAG = getClass().getSimpleName();
    private long mTimeStamp;
    private TextToSpeech mTextToSpeech;
    private DroidSpeech mDroidSpeech;
    private MenuItem mTalking_item;
    private MenuItem mDone_talking_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);

        mSharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        textToSpeech();
        speechToText();

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

        mDone_talking_item = menu.findItem(R.id.item_stop_speech_to_text);
        mDone_talking_item.setVisible(false);

        mTalking_item = menu.findItem(R.id.item_speech_to_text);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.item_read){
            openFile();
        }
        if(id == R.id.item_save){
            saveNote();
        }

        if(id == R.id.item_back_color){
            backgroundColorPicker();
        }

        if(id == R.id.item_text_color){
            textColorPicker();
        }

        if(id == R.id.item_speech_to_text){
            checkPermission();
            mBinding.editTextBody.setHint("Diary is listening to you...");
            mDroidSpeech.startDroidSpeechRecognition();
            mTalking_item.setVisible(false);
            mDone_talking_item.setVisible(true);
        }

        if(id == R.id.item_stop_speech_to_text){
            mDroidSpeech.closeDroidSpeechOperations();
            Toast.makeText(this, "Diary Stopped Listening", Toast.LENGTH_SHORT).show();
            mTalking_item.setVisible(true);
            mDone_talking_item.setVisible(false);
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

         createFile();
        //writeToFile();
        setResult(RESULT_OK, data);
        finish();
    }

    // Request code for selecting a PDF document.


    private void openFile() {
        String[] mimeTypes = {"text/plain", "application/pdf"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(intent.EXTRA_MIME_TYPES, mimeTypes);


        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
       // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, OPEN_FILE);
    }


    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, setDate(mTimeStamp));

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
       // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }

    public void openDirectory() {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Provide read access to files and sub-directories in the user-selected
        // directory.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        startActivityForResult(intent, CREATE_FILE);
    }

    private void writeToFile(){
        if(storageUtils.isExternalStorageWritable()){
            storageUtils.setTextInStorage(Environment.getExternalStorageDirectory(),
                    this, setDate(mTimeStamp), getString(R.string.app_name), mBinding.editTextBody.getText().toString());

        }else {
            Toast.makeText(this, "internal Storage", Toast.LENGTH_SHORT).show();
            storageUtils.setTextInStorage(getDir(getString(R.string.app_name), MODE_PRIVATE),
                    getApplicationContext(), setDate(mTimeStamp), getString(R.string.app_name),mBinding.editTextBody.getText().toString());
        }
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

    private void speechToText(){

        mDroidSpeech = new DroidSpeech(this, null);

        mDroidSpeech.setOnDroidSpeechListener(new OnDSListener() {
            @Override
            public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

            }

            @Override
            public void onDroidSpeechRmsChanged(float rmsChangedValue) {

            }

            @Override
            public void onDroidSpeechLiveResult(String liveSpeechResult) {
                if(liveSpeechResult != null){
                   // mBinding.editTextBody.setText(mBinding.editTextBody.getText().toString() + " " +liveSpeechResult);
                }
            }

            @Override
            public void onDroidSpeechFinalResult(String finalSpeechResult) {

                if(finalSpeechResult != null){
                    mBinding.editTextBody.setText(mBinding.editTextBody.getText().toString() + " " +finalSpeechResult);
                }
            }

            @Override
            public void onDroidSpeechClosedByUser() {
                Toast.makeText(CreateNote.this, "Diary Loves Your Voice", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDroidSpeechError(String errorMsg) {
                Log.d(TAG, errorMsg);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri uri = null;
            String existing_text =  mBinding.editTextBody.getText().toString();
            String new_text = "";
            if(requestCode == OPEN_FILE){
                if(data != null){
                    uri = data.getData();
                    try {
                        String mimetype = getContentResolver().getType(uri);
                        if(mimetype.equals("text/plain")){

                            new_text = existing_text + "\n" + storageUtils.readTextFromUri(this, uri);
                        }
                        if(mimetype.equals("application/pdf")){
                            Log.d("path", uri.toString());
                            new_text = existing_text + "\n" + storageUtils.readPdfFile(uri.toString(), existing_text);
                        }

                        mBinding.editTextBody.setText(new_text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

           else if(requestCode == CREATE_FILE){
                if(data != null){
                    uri = data.getData();
                        existing_text = mBinding.editTextBody.getText().toString();
                        storageUtils.writeFileContent(this, uri, existing_text);
                        //                    storageUtils.setTextInStorage(getDir(getString(R.string.app_name), MODE_PRIVATE),
//                            getApplicationContext(), setDate(mTimeStamp), getString(R.string.app_name),mBinding.editTextBody.getText().toString());

                }
            }
        }
    }
}