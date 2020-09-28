package com.nabase1.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<List<Notes>> allNotes;

    public ViewModel(@NonNull Application application) {
        super(application);

        mNoteRepository = new NoteRepository(application);
        allNotes = mNoteRepository.getAllNote();
    }

    public void insert(Notes notes){
        mNoteRepository.insert(notes);
    }

    public void update(Notes notes){
        mNoteRepository.update(notes);
    }

    public void delete(Notes notes){
        mNoteRepository.delete(notes);
    }

    public void deletAll(){
        mNoteRepository.deleteAll();
    }

    public LiveData<List<Notes>> getAllNotes(){
        return allNotes;
    }
}
