package com.serkantken.applicos.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ActivityNoteAddBinding;
import com.serkantken.applicos.models.NotesModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NoteAddActivity extends AppCompatActivity {
    private ActivityNoteAddBinding binding;
    private NotesModel notes;
    private boolean isOldNote = false;
    private int color = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String requestCode = getIntent().getStringExtra("requestCode");
        if (requestCode.equals("102"))
        {
            notes = new NotesModel();
            notes = (NotesModel) getIntent().getSerializableExtra("old_note");
            binding.etTitle.setText(notes.getTitle());
            binding.etNote.setText(notes.getNotes());
            color = notes.getColor();
            isOldNote = true;
        }

        binding.buttonSave.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString();
            String note = binding.etNote.getText().toString();

            if (note.isEmpty())
            {
                Toast.makeText(this, "Please add some note!", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm");
            Date date = new Date();

            if (isOldNote)
            {
                notes.setColor(color);
            }
            else
            {
                notes = new NotesModel();
                notes.setColor(getRandomColor());
            }
            notes.setTitle(title);
            notes.setNotes(note);
            notes.setDate(formatter.format(date));

            Intent intent = new Intent();
            intent.putExtra("note", notes);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        binding.buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        });
    }

    private int getRandomColor()
    {
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.note_color_1);
        colorCode.add(R.color.note_color_2);
        colorCode.add(R.color.note_color_3);
        colorCode.add(R.color.note_color_4);
        colorCode.add(R.color.note_color_5);

        Random random = new Random();
        int randomColor = random.nextInt(colorCode.size());
        return colorCode.get(randomColor);
    }
}