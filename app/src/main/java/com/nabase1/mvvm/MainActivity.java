package com.nabase1.mvvm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nabase1.mvvm.adapters.NotesAdapter;
import com.nabase1.mvvm.databinding.ActivityMainBinding;
import com.nabase1.mvvm.room.Notes;
import com.nabase1.mvvm.viewModel.ViewModel;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private NotesAdapter mNotesAdapter;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        /* initializing viewModel */
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ViewModel.class);

        /* Ui observe changes from viewModel and updates ui */
        mViewModel.getAllNotes().observe(this, notes -> mNotesAdapter.update(notes));

        initialize(mBinding.recyclerview);
        mBinding.floatingActionButton.setOnClickListener(view -> {
                startActivityForResult(new Intent(this, CreateNote.class), Constants.ADD_NOTE_REQUEST_CODE);
        });


        /* attaching swipe and move to recyclerView */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    mViewModel.delete(mNotesAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(mBinding.recyclerview);

        /*  */
        mNotesAdapter.setOnclickListener(notes -> {
            Intent intent = new Intent(this, CreateNote.class);

            intent.putExtra(Constants.EXTRA_ID, notes.getId());
            intent.putExtra(Constants.TEXT_TITLE, notes.getTitle());
            intent.putExtra(Constants.TEXT_DESCRIPTION, notes.getDescription());
            intent.putExtra(Constants.TIME_STAMP, notes.getTimeStamp());
            intent.putExtra(Constants.TEXT_PRIORITY, notes.getPriority());

            startActivityForResult(intent, Constants.EDIT_NOTE_REQUEST_CODE);
        });



    }

    /* initialize recyclerView */
    private void initialize(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if(layoutManager == null){
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }

        displayNotes(mBinding.recyclerview);

    }

    /* display notes on recyclerView */
    private void displayNotes(RecyclerView recyclerView){
        mNotesAdapter = (NotesAdapter) recyclerView.getAdapter();

        if(mNotesAdapter == null){
            mNotesAdapter = new NotesAdapter();
            recyclerView.setAdapter(mNotesAdapter);
        }
    }

    /* delete all notes from localDb */
    private void deleteAllNotes(){
        mViewModel.deleteAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == Constants.ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
                    String title = data.getStringExtra(Constants.TEXT_TITLE);
                    String desc = data.getStringExtra(Constants.TEXT_DESCRIPTION);
                    long timestamp = data.getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
                    int priority = data.getIntExtra(Constants.TEXT_PRIORITY, 1);

                    Notes notes = new Notes(title,desc,timestamp,priority);
                    mViewModel.insert(notes);

        }else if(requestCode == Constants.EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK){


            int id = getIntent().getIntExtra(Constants.EXTRA_ID, -1);
            Log.d(TAG, "title"+ data.getStringExtra(Constants.TEXT_TITLE));
            Log.d(TAG, "desc" + data.getStringExtra(Constants.TEXT_DESCRIPTION));
            Log.d(TAG, "id" + String.valueOf(id));

            if(id != -1){
                String title = data.getStringExtra(Constants.TEXT_TITLE);
                String desc = data.getStringExtra(Constants.TEXT_DESCRIPTION);
                long timestamp = data.getLongExtra(Constants.TIME_STAMP, Calendar.getInstance().getTimeInMillis());
                int priority = data.getIntExtra(Constants.TEXT_PRIORITY, 1);

                Notes notes = new Notes(title,desc,timestamp,priority);
                notes.setId(id);
                mViewModel.update(notes);
            }else {
                Toast.makeText(this, "Problem Updating", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Note Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}