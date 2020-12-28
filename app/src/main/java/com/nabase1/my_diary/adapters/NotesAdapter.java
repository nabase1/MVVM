package com.nabase1.my_diary.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nabase1.my_diary.CreateNote;
import com.nabase1.my_diary.R;
import com.nabase1.my_diary.databinding.ItemRowBinding;
import com.nabase1.my_diary.room.Notes;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.AdapterViewHolder>{

    private List<Notes> mNotesList;
    private OnItemClickListener mListener;
    private Context mContext;

    public NotesAdapter() {
        mNotesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mContext = parent.getContext();

       ItemRowBinding ItemRowBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
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

    public Notes getNoteAt(int position){
        return mNotesList.get(position);
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        ItemRowBinding mBinding;
        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(mListener != null && position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(mNotesList.get(position));
                }
            });
        }

        public void bind(Notes notes){
            String path = notes.getFontFamily();
            Typeface tf = null;
            if(path != null){
                tf = Typeface.createFromAsset(mContext.getAssets(), path);
                mBinding.textViewTitle.setTypeface(tf);
            }
            mBinding.textViewTitle.setText(notes.getTitle());
           // mBinding.textViewDescription.setText(desc + "..." );
            mBinding.textViewPriority.setText(new CreateNote().setDate(notes.getTimeStamp()));

        }

    }

    public void setOnclickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(Notes notes);
    }
}
