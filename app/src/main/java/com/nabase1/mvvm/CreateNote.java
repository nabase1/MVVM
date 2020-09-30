package com.nabase1.mvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.nabase1.mvvm.databinding.ActivityCreateNoteBinding;

public class CreateNote extends AppCompatActivity {

    private ActivityCreateNoteBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_note);


    }

    private void saveNote(){
        String title = mBinding.editTextTextPersonName.getText().toString();
        String desc = mBinding.editTextTextMultiLine.getText().toString();
        int priority = mBinding.numberPicker.getValue();

        Intent data = new Intent();

        data.putExtra(Constants.TEXT_TITLE, title);
        data.putExtra(Constants.TEXT_DESCRIPTION,desc);
        data.putExtra(Constants.TEXT_PRIORITY,priority);

        setResult(RESULT_OK);

        finish();
    }
}