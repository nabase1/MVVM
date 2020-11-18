package com.nabase1.mvvm.room;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nabase1.mvvm.R;

import java.util.Calendar;

@Database(entities = Notes.class, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao mNoteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();

        }
        return instance;
    }

    public static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
           // new populateDbAsyncTask(instance).execute();
        }
    };

    private static class populateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao mNoteDao;

        private populateDbAsyncTask(NoteDatabase noteDatabase){
            mNoteDao = noteDatabase.mNoteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            long timeStamp = Calendar.getInstance().getTimeInMillis();
            mNoteDao.insert(new Notes("title 1", "description 1", timeStamp, R.color.white));
            return null;
        }
    }
}
