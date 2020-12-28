package com.nabase1.my_diary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.Font.Builder;
import android.graphics.fonts.FontFamily;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.nabase1.my_diary.databinding.ActivityCreateNoteBinding;
import com.nabase1.my_diary.databinding.ChooseFontDialogBinding;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import petrov.kristiyan.colorpicker.ColorPicker;

import static com.nabase1.my_diary.Constants.*;

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
    private boolean pdfViewer = false;
    private boolean isSpeaking = false;
    private Uri mUri;
    AlertDialog mAlertDialog;
    private AlertDialog.Builder mBuilder;
    private String defaultFont = "abel-regular.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);

        Toolbar toolbar = findViewById(R.id.mtoolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        toolbar.setTitle("");
        mSharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);

        textToSpeech();
        speechToText();

        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){
            defaultBackgroundColor = mSharedPreferences.getInt(BACK_COLOR, R.color.white);
            defaultTextColor = mSharedPreferences.getInt(TEXT_COLOR1, R.color.black_de);
           defaultBackgroundColor = intent.getIntExtra(TEXT_PRIORITY, defaultBackgroundColor);
           defaultTextColor = intent.getIntExtra(TEXT_COLOR2, defaultTextColor);
           defaultFont = intent.getStringExtra(FONT_FAMILY);
            mTimeStamp = intent.getLongExtra(TIME_STAMP, Calendar.getInstance().getTimeInMillis());
            mBinding.editTextBody.setText(intent.getStringExtra(TEXT_DESCRIPTION));


        }else {
            defaultBackgroundColor = mSharedPreferences.getInt(BACK_COLOR, 1);
            defaultTextColor = mSharedPreferences.getInt(TEXT_COLOR1, R.color.black_de);
            defaultFont = intent.getStringExtra(DEFAULT_FONT_FAMILY);
            mTimeStamp = Calendar.getInstance().getTimeInMillis();


        }

        selectFontFamily(defaultFont);
        mBinding.textViewDate.setText(setDate(mTimeStamp));
        mBinding.constraintLayout.setBackgroundColor(defaultBackgroundColor);
        mBinding.editTextBody.setTextColor(defaultTextColor);
        mBinding.textView2.setTextColor(defaultTextColor);
        mBinding.textViewDate.setTextColor(defaultTextColor);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v ->{ if(isSpeaking){
                                    mDroidSpeech.closeDroidSpeechOperations();}
                                    onBackPressed(); } );

        // creating dialog
        mBuilder = new AlertDialog.Builder(this);

        mBuilder.setTitle(R.string.dialog_header);
        mBuilder.setMessage(R.string.dialog_info);

        mBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });

        mBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = mBuilder.create();


    }

    @Override
    protected void onStart() {
        getPath();
        if(MySharedReference.getInstance(getApplicationContext()).getData(LOG_IN_CODE) == null){
            Intent intent = new Intent(this, LockScreen.class);
            intent.putExtra(LOG_IN, 1);
            startActivity(intent);
        }
        super.onStart();
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
            if(pdfViewer){
                closePdfViewer();
            }else {
                saveNote();
            }
        }
        if(id == R.id.item_back_color){
            backgroundColorPicker();
        }

        if(id == R.id.item_text_color){
            textColorPicker();
        }

        if(id == R.id.item_speech_to_text){

            if(pdfViewer){
                closePdfViewer();
            }
              checkPermission();

        }

        if(id == R.id.item_stop_speech_to_text){
            stopListening();
        }

        if(id == R.id.item_text_to_speech){
            if(pdfViewer){
                Snackbar.make(mBinding.pdfView, R.string.read_error, Snackbar.LENGTH_LONG).show();
            }
            mTextToSpeech.speak(mBinding.editTextBody.getText().toString(), TextToSpeech.QUEUE_FLUSH,null);
        }
        if(id == R.id.item_about){
            if(isSpeaking){
                stopListening();
            }
            startActivity(new Intent(this, About.class));
        }

        if(id == R.id.item_text_font){
            showFontDialog();
        }
        return true;
    }

    private void stopListening(){
        mDroidSpeech.closeDroidSpeechOperations();
        isSpeaking = false;
        Toast.makeText(this,R.string.diary_stopped, Toast.LENGTH_LONG).show();
        mTalking_item.setVisible(true);
        mDone_talking_item.setVisible(false);
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void closePdfViewer(){
            mBinding.pdfView.setVisibility(View.GONE);
            mBinding.editTextBody.setVisibility(View.VISIBLE);
            mBinding.textView2.setVisibility(View.VISIBLE);

            pdfViewer = false;
    }
    private void saveNote(){

        if(isSpeaking){
           stopListening();
        }
            String multiLines = mBinding.editTextBody.getText().toString();
            String[] diary;
            String delimiter = "\n";
            diary = multiLines.split(delimiter);

            String title = diary[0];

            String desc = mBinding.editTextBody.getText().toString();

            Intent data = new Intent(CreateNote.this, MainActivity.class);

            data.putExtra(TEXT_TITLE, title);
            data.putExtra(TEXT_DESCRIPTION,desc);
            data.putExtra(TIME_STAMP, Calendar.getInstance().getTimeInMillis());
            data.putExtra(TEXT_PRIORITY, defaultBackgroundColor);
            data.putExtra(TEXT_COLOR2, defaultTextColor);
            data.putExtra(FONT_FAMILY, defaultFont);

            int id = getIntent().getIntExtra(EXTRA_ID, -1);

            if(id != -1){
                long timestamp = getIntent().getLongExtra(TIME_STAMP, Calendar.getInstance().getTimeInMillis());

                data.putExtra(EXTRA_ID, id);
                data.putExtra(TIME_STAMP, timestamp);
            }

            if(!multiLines.isEmpty()){
                writeToFile();
            }

            if(mUri != null){
                startActivityForResult(data, ADD_NOTE_REQUEST_CODE);
            }
             setResult(RESULT_OK, data);
            finish();


    }

    private void openFile() {
        String[] mimeTypes = {"text/plain", "application/pdf"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(intent.EXTRA_MIME_TYPES, mimeTypes);


        startActivityForResult(intent, OPEN_FILE);
    }

    private void writeToFile(){
        if(storageUtils.isExternalStorageWritable()){
            storageUtils.setTextInStorage(Environment.getExternalStorageDirectory(),
                    this, setDate(mTimeStamp)+".txt", getString(R.string.app_name), mBinding.editTextBody.getText().toString());

        }else {
           // Toast.makeText(this, "internal Storage", Toast.LENGTH_SHORT).show();
            storageUtils.setTextInStorage(getDir(getString(R.string.app_name), MODE_PRIVATE),
                    getApplicationContext(), setDate(mTimeStamp)+".txt", getString(R.string.app_name),mBinding.editTextBody.getText().toString());
        }
    }

    public String setDate(Long timeStamp){
        Date date=new Date(timeStamp);
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss");
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
        colors.add("#e2c38c");
        colors.add("#2b1311");
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
                        mSharedPreferences.edit().putInt(BACK_COLOR, defaultBackgroundColor).apply();
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
        colors.add("#e2c38c");
        colors.add("#2b1311");
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
                        mSharedPreferences.edit().putInt(TEXT_COLOR1, defaultTextColor).apply();
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

                mAlertDialog.show();

            }
            else {
                mBinding.editTextBody.setHint(R.string.listen_msg);
                mDroidSpeech.startDroidSpeechRecognition();
                isSpeaking = true;
                mTalking_item.setVisible(false);
                mDone_talking_item.setVisible(true);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

//            @Override
//            public void onDroidSpeechAudioPermissionStatus(boolean audioPermissionGiven, String errorMsgIfAny)
//            {
//                if(!audioPermissionGiven){
//                    Snackbar.make(mBinding.editTextBody, "Access denied to use microphone!", Snackbar.LENGTH_LONG).show();
//                }
//
//            }


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

    private void getPath(){
        mUri = getIntent().getData();
        if(mUri != null){
            getFileType(mUri);
        }
    }

    private void getFileType(Uri uri){
        String existing_text =  mBinding.editTextBody.getText().toString();
        String new_text = "";
        try {
            String mimetype = getContentResolver().getType(uri);
            if(mimetype.equals("text/plain")){
                if(pdfViewer){
                    mBinding.pdfView.setVisibility(View.GONE);
                    mBinding.editTextBody.setVisibility(View.VISIBLE);
                    mBinding.textView2.setVisibility(View.VISIBLE);

                    pdfViewer = false;
                }

                new_text = existing_text + "\n" + storageUtils.readTextFromUri(this, uri);
            }
            if(mimetype.equals("application/pdf")){
                Log.d("path", uri.toString());

                mBinding.pdfView.fromUri(uri).load();
                mBinding.pdfView.setVisibility(View.VISIBLE);
                mBinding.editTextBody.setVisibility(View.GONE);
                mBinding.textView2.setVisibility(View.GONE);
                pdfViewer = true;
            }
            mBinding.editTextBody.setText(new_text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showFontDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        ChooseFontDialogBinding fontDialogBinding = ChooseFontDialogBinding.inflate(inflater);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(fontDialogBinding.getRoot())
                .create();

        alertDialog.show();

        fontDialogBinding.buttonCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        fontDialogBinding.buttonConfirm.setOnClickListener(v -> {

            if(fontDialogBinding.radioDefault.isChecked()){
                defaultFont = "abel-regular.ttf";
                selectFontFamily(defaultFont);
            }

            if(fontDialogBinding.radioMonospace.isChecked()){
                defaultFont = "Anonymous.ttf";
                selectFontFamily(defaultFont);
            }
            if (fontDialogBinding.radioSanSarif.isChecked()) {
                defaultFont = "livingston_sanserif.otf";
                selectFontFamily(defaultFont);
            }
            if (fontDialogBinding.radioMontag.isChecked()) {
                defaultFont = "lux_montag.ttf";
                selectFontFamily(defaultFont);
            }
            if (fontDialogBinding.radioDephiana.isChecked()) {
                defaultFont = "dephiana.otf";
                selectFontFamily(defaultFont);
            }
            if (fontDialogBinding.radioRaphtalia.isChecked()) {
                defaultFont = "raphtalia.otf";
                selectFontFamily(defaultFont);
            }
            if (fontDialogBinding.radioAlleyster.isChecked()) {
                defaultFont = "alleyster.otf";
                selectFontFamily(defaultFont);
            }

            if(fontDialogBinding.radioThinkDreams.isChecked()){
                defaultFont = "thinkdreams_italic.otf";
                selectFontFamily(defaultFont);
            }

            mSharedPreferences.edit().putString(DEFAULT_FONT_FAMILY, defaultFont).apply();

            alertDialog.dismiss();

        });
    }

    private void selectFontFamily(String path){
        Typeface tf = null;
        if(path != null){
             tf = Typeface.createFromAsset(getAssets(), path);
        }
        mBinding.editTextBody.setTypeface(tf);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri uri = null;
            if(requestCode == OPEN_FILE){
                if(data != null){
                    uri = data.getData();
                     getFileType(uri);
                }
            }
        }
    }
}