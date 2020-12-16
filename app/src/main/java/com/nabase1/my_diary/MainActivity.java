package com.nabase1.my_diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.nabase1.my_diary.adapters.NotesAdapter;
import com.nabase1.my_diary.databinding.ActivityMainBinding;
import com.nabase1.my_diary.room.Notes;
import com.nabase1.my_diary.viewModel.ViewModel;

import java.util.Calendar;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.nabase1.my_diary.Constants.*;

public class MainActivity extends AppCompatActivity {

    private ViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private NotesAdapter mNotesAdapter;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mtoolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        /* initializing viewModel */
        mViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ViewModel.class);

        /* Ui observe changes from viewModel and updates ui */
        mViewModel.getAllNotes().observe(this, notes -> mNotesAdapter.update(notes));

        initialize(mBinding.recyclerview);
        mBinding.floatingActionButton.setOnClickListener(view -> {
                startActivityForResult(new Intent(this, CreateNote.class), ADD_NOTE_REQUEST_CODE);
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
                    Snackbar.make(mBinding.recyclerview, R.string.diary_deleted, Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color4))
                        .addActionIcon(R.drawable.ic_baseline_delete_forever_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mBinding.recyclerview);


        /*  */
        mNotesAdapter.setOnclickListener(notes -> {
            Intent intent = new Intent(this, CreateNote.class);

            intent.putExtra(EXTRA_ID, notes.getId());
            intent.putExtra(TEXT_TITLE, notes.getTitle());
            intent.putExtra(TEXT_DESCRIPTION, notes.getDescription());
            intent.putExtra(TIME_STAMP, notes.getTimeStamp());
            intent.putExtra(TEXT_PRIORITY, notes.getBackgroundColor());
            intent.putExtra(TEXT_COLOR2, notes.getTextColor());

            startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
        });
    }

    @Override
    protected void onStart() {
        if(MySharedReference.getInstance(getApplicationContext()).getData(LOG_IN_CODE) == null){
            Intent intent = new Intent(this, LockScreen.class);
            intent.putExtra(LOG_IN, 1);
            startActivity(intent);
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        MySharedReference.getInstance(getApplicationContext()).saveData(LOG_IN_CODE, null);
        super.onBackPressed();
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.settings_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.item_about){
            startActivity(new Intent(this, About.class));
        }
        if(id == R.id.item_change_pin){
            Intent lockScreenIntent = new Intent(this, LockScreen.class);
            lockScreenIntent.putExtra(CHANGE_PIN, 1);
            startActivity(lockScreenIntent);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
                    String title = data.getStringExtra(TEXT_TITLE);
                    if(!title.isEmpty()){
                        String desc = data.getStringExtra(TEXT_DESCRIPTION);
                        long timestamp = data.getLongExtra(TIME_STAMP, Calendar.getInstance().getTimeInMillis());
                        int back_color = data.getIntExtra(TEXT_PRIORITY, R.color.white);
                        int text_color = data.getIntExtra(TEXT_COLOR2, R.color.black_de);

                        Notes notes = new Notes(title,desc,timestamp,text_color,back_color);
                        mViewModel.insert(notes);

                        Snackbar.make(mBinding.recyclerview, R.string.success_msg, Snackbar.LENGTH_LONG).show();
                    }


        }else if(requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK){

            int id = data.getIntExtra(EXTRA_ID, -1);

            if(id != -1){
                String title = data.getStringExtra(TEXT_TITLE);
                String desc = data.getStringExtra(TEXT_DESCRIPTION);
                long timestamp = data.getLongExtra(TIME_STAMP, Calendar.getInstance().getTimeInMillis());
                int back_color = data.getIntExtra(TEXT_PRIORITY, R.color.white);
                int text_color = data.getIntExtra(TEXT_COLOR2, R.color.black_de);

                Notes notes = new Notes(title,desc,timestamp,text_color, back_color);
                notes.setId(id);

                if(title.isEmpty() && desc.isEmpty()){
                    mViewModel.delete(notes);
                    Snackbar.make(mBinding.recyclerview, R.string.diary_deleted, Snackbar.LENGTH_LONG).show();

                }else {
                    mViewModel.update(notes);
                    Snackbar.make(mBinding.recyclerview, R.string.diary_updated, Snackbar.LENGTH_LONG).show();
                }



            }else {
                Snackbar.make(mBinding.recyclerview, R.string.error_msg, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}