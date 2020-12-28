package com.nabase1.my_diary;

import android.graphics.Typeface;
import android.widget.EditText;
import android.widget.TextView;

public class SetFont {

    private void setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex) {
        typefaceIndex = R.font.dephiana;
        Typeface tf = null;
        if (familyName != null) {
            tf = Typeface.create(familyName, styleIndex);
            if (tf != null) {
              //  setTypeface(tf);
                return;
            }
        }
//        switch (typefaceIndex) {
//            case SANS:
//                tf = Typeface.SANS_SERIF;
//                break;
//
//            case SERIF:
//                tf = Typeface.SERIF;
//                break;
//
//            case MONOSPACE:
//                tf = Typeface.MONOSPACE;
//                break;
//        }
//        setTypeface(tf, styleIndex);
    }

    public void setTypeface(Typeface tf, int style, TextView textView, EditText editText) {
        if (style > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }

           // setTypeface(tf);
        }
    }
}
