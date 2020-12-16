package com.nabase1.my_diary;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedReference {

    private static MySharedReference sMySharedReference;
    private SharedPreferences mSharedPreferences;

    private MySharedReference(Context context){
        mSharedPreferences = context.getSharedPreferences(Constants.SHARED_REFERENCE, Context.MODE_PRIVATE);
    }

    public static MySharedReference getInstance(Context context){
        if(sMySharedReference == null){
            sMySharedReference = new MySharedReference(context);
        }
        return sMySharedReference;
    }

    public void saveData(String key, String value){
        SharedPreferences.Editor prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public String getData(String key){
        if(mSharedPreferences != null){
            return mSharedPreferences.getString(key, null);
        }
        return null;
    }


}
