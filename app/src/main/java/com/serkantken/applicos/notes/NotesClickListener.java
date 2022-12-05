package com.serkantken.applicos.notes;

import androidx.cardview.widget.CardView;

import com.serkantken.applicos.models.NotesModel;

public interface NotesClickListener
{
    void onClick(NotesModel notes);
    void onLongClick(NotesModel notes, CardView cardView);
}
