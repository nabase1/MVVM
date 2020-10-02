package com.nabase1.mvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nabase1.mvvm.databinding.ActivityCreateNoteBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateNote extends AppCompatActivity {

    private ActivityCreateNoteBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();

        if(intent.hasExtra(Constants.EXTRA_ID)){
            long timeStamp = intent.getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
            setTitle(getString(R.string.update_diary));
            mBinding.editTextTitle.setText(intent.getStringExtra(Constants.TEXT_TITLE));
            mBinding.editTextBody.setText(intent.getStringExtra(Constants.TEXT_DESCRIPTION));
            mBinding.textViewDate.setText(setDate(timeStamp));
           // mBinding.numberPicker.setValue(intent.getIntExtra(Constants.TEXT_PRIORITY, 1));

        }else {
            setTitle(getString(R.string.create_new_diary));
            mBinding.textViewDate.setText(setDate(Calendar.getInstance().getTimeInMillis()));
        }


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
        return true;
    }

    private void saveNote(){
        String title = mBinding.editTextTitle.getText().toString();
        String desc = mBinding.editTextBody.getText().toString();
        //int priority = mBinding.numberPicker.getValue();

        Intent data = new Intent();

        data.putExtra(Constants.TEXT_TITLE, title);
        data.putExtra(Constants.TEXT_DESCRIPTION,desc);
        data.putExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
        data.putExtra(Constants.TEXT_PRIORITY,1);

        int id = getIntent().getIntExtra(Constants.EXTRA_ID, -1);

        if(id != -1){
            long timestamp = getIntent().getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
            data.putExtra(Constants.EXTRA_ID, id);
            data.putExtra(Constants.TIME_STAMP, timestamp);
        }

        setResult(RESULT_OK);

        finish();
    }

    private String setDate(Long timeStamp){
        Date date=new Date(timeStamp);
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-YYYY  HH:mm:ss");
        sfd.format(date);

        return date.toString();
    }

}