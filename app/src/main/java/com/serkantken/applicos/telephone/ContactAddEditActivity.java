package com.serkantken.applicos.telephone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ActivityContactAddEditBinding;
import com.serkantken.applicos.models.ContactModel;

public class ContactAddEditActivity extends AppCompatActivity {
    private ActivityContactAddEditBinding binding;
    private ContactModel contactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactAddEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(v -> onBackPressed());

        contactModel = (ContactModel) getIntent().getSerializableExtra("contact");
        binding.twContactName.setText(contactModel.getName());
        binding.twPhone.setText(contactModel.getPhone());
        binding.twEmail.setText(contactModel.getEmail());
        Glide.with(this).load(contactModel.getPhoto()).placeholder(AppCompatResources.getDrawable(this, R.drawable.ic_person_130)).into(binding.contactPicture);
    }
}