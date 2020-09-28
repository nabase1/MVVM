package com.nabase1.mvvm;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private NoteDao mNoteDao;
    private LiveData<List<Notes>> allNote;

    public NoteRepository(Application application){
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        mNoteDao = noteDatabase.mNoteDao();
        allNote = mNoteDao.getAllNotes();
    }

    public void insert(Notes note){
        new insertNotesAsyncTask(mNoteDao).execute(note);
    }

    public void update(Notes note){
        new updateNotesAsyncTask(mNoteDao).execute(note);
    }

    public void delete(Notes note){
        new deleteAllNotesAsyncTask(mNoteDao).execute(note);
    }

    public void deleteAll(){
        new deleteAllNotesAsyncTask(mNoteDao).execute();
    }

    public LiveData<List<Notes>> getAllNote(){
        return allNote;
    }

    private static class insertNotesAsyncTask extends AsyncTask<Notes, Void, Void>{

        private NoteDao mNoteDao;

        private insertNotesAsyncTask(NoteDao noteDao){
            this.mNoteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Notes... notes) {
            mNoteDao.insert(notes[0]);
            return null;
        }
    }

    private static class updateNotesAsyncTask extends AsyncTask<Notes, Void, Void>{

        private NoteDao mNoteDao;

        private updateNotesAsyncTask(NoteDao noteDao){
            this.mNoteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Notes... notes) {
            mNoteDao.update(notes[0]);
            return null;
        }
    }

    private static class deleteNotesAsyncTask extends AsyncTask<Notes, Void, Void>{

        private NoteDao mNoteDao;

        private deleteNotesAsyncTask(NoteDao noteDao){
            this.mNoteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Notes... notes) {
            mNoteDao.delete(notes[0]);
            return null;
        }
    }

    private static class deleteAllNotesAsyncTask extends AsyncTask<Notes, Void, Void>{

        private NoteDao mNoteDao;

        private deleteAllNotesAsyncTask(NoteDao noteDao){
            this.mNoteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Notes... notes) {
            mNoteDao.deleteAllNotes();
            return null;
        }
    }

}
