package com.nabase1.mvvm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.nabase1.mvvm.adapters.NotesAdapter;
import com.nabase1.mvvm.databinding.ActivityMainBinding;
import com.nabase1.mvvm.room.Notes;
import com.nabase1.mvvm.viewModel.ViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private NotesAdapter mNotesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ViewModel.class);

        mViewModel.getAllNotes().observe(this, notes -> mNotesAdapter.update(notes));

        initialize(mBinding.recyclerview);
        mBinding.floatingActionButton.setOnClickListener(view -> {
                startActivityForResult(new Intent(this, CreateNote.class), Constants.ADD_NOTE_REQUEST_CODE);
        });

    }

    private void initialize(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if(layoutManager == null){
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }

        displayNotes(mBinding.recyclerview);

    }

    private void displayNotes(RecyclerView recyclerView){
        mNotesAdapter = (NotesAdapter) recyclerView.getAdapter();

        if(mNotesAdapter == null){
            mNotesAdapter = new NotesAdapter();
            recyclerView.setAdapter(mNotesAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
                    String title = data.getStringExtra(Constants.TEXT_TITLE);
                    String desc = data.getStringExtra(Constants.TEXT_DESCRIPTION);
                    int priority = data.getIntExtra(Constants.TEXT_PRIORITY, 1);

                    Notes notes = new Notes(title,desc,priority);
                    mViewModel.insert(notes);
        }else {
            Toast.makeText(this, "Note Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}