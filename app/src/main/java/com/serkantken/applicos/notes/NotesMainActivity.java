package com.serkantken.applicos.notes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ActivityNotesMainBinding;
import com.serkantken.applicos.models.NotesModel;
import com.serkantken.applicos.notes.adapters.NotesListAdapter;
import com.serkantken.applicos.notes.database.RoomDB;

import java.util.ArrayList;
import java.util.List;

public class NotesMainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
{
    private ActivityNotesMainBinding binding;
    private NotesListAdapter notesListAdapter;
    private List<NotesModel> notes = new ArrayList<>();
    private RoomDB database;
    private NotesModel selectedNote, receivedNote;
    private String requestCode = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(v -> onBackPressed());

        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();

        updateRecyclerView(notes);

        try {
            requestCode = getIntent().getStringExtra("requestCode");
            receivedNote = new NotesModel();
            receivedNote = (NotesModel) getIntent().getSerializableExtra("note");
            if (requestCode.equals("10"))
            {
                Intent intent = new Intent(NotesMainActivity.this, NoteAddActivity.class);
                intent.putExtra("requestCode", "101");
                startActivityForResult(intent, 102);
            }
            else if (requestCode.equals("11"))
            {
                onClick(receivedNote);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        binding.buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(NotesMainActivity.this, NoteAddActivity.class);
            intent.putExtra("requestCode", "101");
            startActivityForResult(intent, 101);
        });

        binding.searchNotes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<NotesModel> filteredList = new ArrayList<>();
        for (NotesModel singleNote : notes)
        {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase()))
            {
                filteredList.add(singleNote);
            }
        }
        notesListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                NotesModel newNote = (NotesModel) data.getSerializableExtra("note");
                database.mainDAO().insert(newNote);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Note removed", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 102)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                NotesModel newNote = (NotesModel) data.getSerializableExtra("note");
                database.mainDAO().update(newNote.getID(), newNote.getTitle(), newNote.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Note is not changed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateRecyclerView(List<NotesModel> notes) {
        binding.notesRV.setHasFixedSize(true);
        binding.notesRV.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(NotesMainActivity.this, notes, notesClickListener);
        binding.notesRV.setAdapter(notesListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(NotesModel notes) {
            NotesMainActivity.this.onClick(notes);
        }

        @Override
        public void onLongClick(NotesModel notes, CardView cardView) {
            selectedNote = notes;
            showPopup(cardView);
        }
    };

    private void onClick(NotesModel notes)
    {
        Intent intent = new Intent(NotesMainActivity.this, NoteAddActivity.class);
        intent.putExtra("old_note", notes);
        intent.putExtra("requestCode", "102");
        startActivityForResult(intent, 102);
    }

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.notes_popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.pin_note:
                if (selectedNote.isPinned())
                {
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(NotesMainActivity.this, "Note is unpinned", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(NotesMainActivity.this, "Note is pinned to desktop", Toast.LENGTH_SHORT).show();
                }

                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
                return true;
            case R.id.delete_note:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                Toast.makeText(NotesMainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}