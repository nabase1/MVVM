package com.nabase1.my_diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import com.nabase1.my_diary.databinding.ActivityLockScreenBinding;

import static com.nabase1.my_diary.Constants.SAVE_PIN_CODE;

public class LockScreen extends AppCompatActivity {

    ActivityLockScreenBinding mBinding;
    TextWatcher mTextWatcher;
    String mPin,entered_pin, first_pin;
    Boolean mConfirm = false;
    private int mChange_pin, mLog_in;

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
                            setPin();
                     }else {
                         if(mChange_pin == 1){
                             setPin();
                         }else {
                             if(entered_pin.equals(mPin)){
                                 MySharedReference.getInstance(getApplicationContext()).saveData(Constants.LOG_IN_CODE,"1");
                                 if(mLog_in == 2){
                                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                 }
                                 finish();
                             }
                             if(s.length() >= mPin.length() && !entered_pin.equals(mPin)){
                                 Toast.makeText(LockScreen.this, R.string.incorrect_pin, Toast.LENGTH_LONG).show();
                             }
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
                first_pin = mBinding.editTextPin.getText().toString();
                mBinding.editTextPin.setText("");
                mBinding.textView.setText(R.string.confirm_pin);
                mBinding.buttonNext.setText(R.string.done);
                mBinding.buttonCancel.setVisibility(View.VISIBLE);
                mConfirm = true;
            }
            else {
                if(entered_pin.equals(first_pin)){
                    MySharedReference.getInstance(getApplicationContext()).saveData(SAVE_PIN_CODE, entered_pin);
                    if(mChange_pin != 1){
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    finish();
                }
                else {
                    mBinding.textView.setText(R.string.mismatch_error);
                }
            }

        });

        mBinding.buttonCancel.setOnClickListener(v -> {
            mBinding.editTextPin.setText("");
            mBinding.textView.setText(R.string.secure_your_diary_with_four_digit_pin);
            mBinding.buttonCancel.setVisibility(View.GONE);
            mBinding.buttonNext.setText(R.string.next);
            mConfirm = false;
        });
    }

    private void setPin(){
        CharSequence s = mBinding.editTextPin.getText().toString();
        if(entered_pin.isEmpty()){
            if(mConfirm){
                mBinding.textView.setText(R.string.confirm_pin);
                mBinding.buttonNext.setText(R.string.done);
            }else{
                mBinding.textView.setText(getString(R.string.secure_your_diary_with_four_digit_pin));
                mBinding.buttonNext.setVisibility(View.GONE);
            }
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
    }


    @Override
    protected void onStart() {
        Intent intent = getIntent();
        mChange_pin = intent.getIntExtra(Constants.CHANGE_PIN, 2);
        mLog_in = intent.getIntExtra(Constants.LOG_IN, 2);
        mPin = MySharedReference.getInstance(getApplicationContext()).getData(SAVE_PIN_CODE);
        if(mPin == null){
            mBinding.textView.setText(getString(R.string.secure_your_diary_with_four_digit_pin));
        }else{
            mBinding.textView.setText(getString(R.string.enter_pin));
        }
        super.onStart();
    }
}