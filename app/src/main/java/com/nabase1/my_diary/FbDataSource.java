package com.nabase1.my_diary;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabase1.my_diary.room.Notes;

import java.util.List;

public class FbDataSource {

    String uid;

    public FbDataSource(List<Notes> allNotes) {

    }

    public void InsertNotes(List<Notes> allNotes){
        for(int i = 0; i < allNotes.size(); i++){
            DatabaseReference databaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference().child("Diary").child(uid).child(String.valueOf(allNotes.get(i).getId()));

            databaseReference.setValue(allNotes);

        }


    }
}
