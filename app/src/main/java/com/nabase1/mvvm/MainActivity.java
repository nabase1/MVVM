package com.nabase1.mvvm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        mViewModel.getAllNotes().observe(this, new Observer<List<Notes>>() {
            @Override
            public void onChanged(List<Notes> notes) {
               mNotesAdapter.update(notes);
            }
        });

        initialize(mBinding.recyclerview);

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
}