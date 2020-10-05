package com.nabase1.mvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nabase1.mvvm.databinding.ActivityCreateNoteBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CreateNote extends AppCompatActivity {

    private ActivityCreateNoteBinding mBinding;
    int defaultBackgroundColor, defaultTextColor;
    private SharedPreferences mSharedPreferences;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);

        mSharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();

        if(intent.hasExtra(Constants.EXTRA_ID)){
            defaultBackgroundColor = mSharedPreferences.getInt("backColor", R.color.white);
            defaultTextColor = mSharedPreferences.getInt("textColor", R.color.black_de);
           defaultBackgroundColor = intent.getIntExtra(Constants.TEXT_PRIORITY, defaultBackgroundColor);
//            defaultColor = ContextCompat.getColor(this, R.color.lite_blue);
            long timeStamp = intent.getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
            setTitle(getString(R.string.update_diary));
           // mBinding.editTextTitle.setText(intent.getStringExtra(Constants.TEXT_TITLE));
            mBinding.editTextBody.setText(intent.getStringExtra(Constants.TEXT_DESCRIPTION));
            mBinding.textViewDate.setText(setDate(timeStamp));

        }else {
            defaultBackgroundColor = mSharedPreferences.getInt("backColor", R.color.white);
            defaultTextColor = mSharedPreferences.getInt("textColor", R.color.black_de);
            setTitle(getString(R.string.create_new_diary));
            mBinding.textViewDate.setText(setDate(Calendar.getInstance().getTimeInMillis()));
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
                        mSharedPreferences.edit().putInt("backColor", defaultBackgroundColor).apply();
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
                        mSharedPreferences.edit().putInt("textColor", defaultTextColor).apply();
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        colorPicker.show();
    }

}