package com.nabase1.my_diary;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import com.nabase1.my_diary.databinding.ActivityAboutBinding;


public class About extends AppCompatActivity {
    private ActivityAboutBinding mBinding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.mtoolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setTitle(R.string.about);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v->onBackPressed());
    }

    public void callDev(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel://"+ mBinding.devContact.getText().toString().trim())));
    }

    public void mailDev(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+ mBinding.devMail.getText().toString().trim())));
    }


    public void visitWeb(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://smartcabgh.890m.com")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
