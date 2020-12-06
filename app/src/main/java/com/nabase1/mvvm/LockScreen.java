package com.nabase1.mvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nabase1.mvvm.databinding.ActivityLockScreenBinding;

import static com.nabase1.mvvm.Constants.SAVE_PIN_CODE;

public class LockScreen extends AppCompatActivity {

    ActivityLockScreenBinding mBinding;
    TextWatcher mTextWatcher;
    String mPin,entered_pin;
    Boolean mConfirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen);

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                     entered_pin = mBinding.editTextPin.getText().toString();
                     if(mPin == null){
                         if(entered_pin.isEmpty()){
                             mBinding.textView.setText(getString(R.string.secure_your_diary_with_four_digit_pin));
                             mBinding.buttonNext.setVisibility(View.GONE);
                         }
                         else {
                             mBinding.textView.setText(R.string.at_least_4_digit);
                             if(s.length() >= 4){
                                 mBinding.buttonNext.setVisibility(View.VISIBLE);
                             }
                             else {
                                 mBinding.buttonNext.setVisibility(View.GONE);
                             }

                         }
                     }else {
                         if(entered_pin.equals(mPin)){
                             startActivity(new Intent(getApplicationContext(), MainActivity.class));
                             finish();
                         }
                         if(s.length() >= 4 && !entered_pin.equals(mPin)){
                             Toast.makeText(LockScreen.this, R.string.incorrect_pin, Toast.LENGTH_LONG).show();
                         }
                     }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mBinding.editTextPin.addTextChangedListener(mTextWatcher);

        mBinding.buttonNext.setOnClickListener(v -> {
            if(!mConfirm){
                MySharedReference.getInstance(getApplicationContext()).saveData(SAVE_PIN_CODE, entered_pin);
                mBinding.editTextPin.setText("");
                mBinding.textView.setText(R.string.confirm_pin);
                mBinding.buttonNext.setText(R.string.done);
                mConfirm = true;
            }
            if(mConfirm){
                String confirm_pin = MySharedReference.getInstance(getApplicationContext()).getData(SAVE_PIN_CODE);
                if(entered_pin.equals(confirm_pin)){
                    startActivity(new Intent(this, MainActivity.class));
                }
            }

        });
    }


    @Override
    protected void onStart() {
        mPin = MySharedReference.getInstance(getApplicationContext()).getData(SAVE_PIN_CODE);
        if(mPin == null){
            mBinding.textView.setText(getString(R.string.secure_your_diary_with_four_digit_pin));
        }else{
            mBinding.textView.setText(getString(R.string.enter_pin));
        }
        super.onStart();
    }
}