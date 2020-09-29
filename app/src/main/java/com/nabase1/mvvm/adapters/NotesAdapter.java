package com.nabase1.mvvm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nabase1.mvvm.R;
import com.nabase1.mvvm.databinding.ItemRowBinding;
import com.nabase1.mvvm.room.Notes;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.AdapterViewHolder>{

    private List<Notes> mNotesList;
    private ItemRowBinding mItemRowBinding;

    public NotesAdapter() {
        mNotesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

       ItemRowBinding ItemRowBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.item_row,parent,false);

        return new AdapterViewHolder(ItemRowBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {

        Notes notes = mNotesList.get(position);
        holder.bind(notes);

        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mNotesList.size();
    }

    public void update(List<Notes> notes){
        this.mNotesList = notes;
        notifyDataSetChanged();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder{

        ItemRowBinding mBinding;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);
        }

        public void bind(Notes notes){
            mBinding.textViewTitle.setText(notes.getTitle());
            mBinding.textViewDescription.setText(notes.getDescription());
            mBinding.textViewPriority.setText(String.valueOf(notes.getPriority()));

        }

    }
}
