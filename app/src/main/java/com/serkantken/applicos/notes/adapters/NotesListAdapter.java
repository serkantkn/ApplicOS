package com.serkantken.applicos.notes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.NotesItemBinding;
import com.serkantken.applicos.models.NotesModel;
import com.serkantken.applicos.notes.NotesClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesViewHolder>
{
    private Context context;
    private List<NotesModel> noteList;
    private NotesClickListener notesClickListener;

    public NotesListAdapter(Context context, List<NotesModel> noteList, NotesClickListener notesClickListener) {
        this.context = context;
        this.noteList = noteList;
        this.notesClickListener = notesClickListener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(NotesItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.binding.title.setText(noteList.get(position).getTitle());
        holder.binding.title.setSelected(true);

        holder.binding.noteText.setText(noteList.get(position).getNotes());

        holder.binding.date.setText(noteList.get(position).getDate());
        holder.binding.date.setSelected(true);

        if (noteList.get(position).isPinned())
        {
            holder.binding.iconPin.setImageResource(R.drawable.ic_pin);
        }
        else
        {
            holder.binding.iconPin.setImageResource(0);
        }

        holder.binding.cardBackground.setBackgroundColor(holder.itemView.getResources().getColor(noteList.get(position).getColor(), null));

        holder.binding.noteContainer.setOnClickListener(v -> {
            notesClickListener.onClick(noteList.get(holder.getAdapterPosition()));
        });
        holder.binding.noteContainer.setOnLongClickListener(v -> {
            notesClickListener.onLongClick(noteList.get(holder.getAdapterPosition()), holder.binding.noteContainer);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void filterList(List<NotesModel> filteredList)
    {
        noteList = filteredList;
        notifyDataSetChanged();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder
    {
        private NotesItemBinding binding;

        public NotesViewHolder(@NonNull NotesItemBinding itemView)
        {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
