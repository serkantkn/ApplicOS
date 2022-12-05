package com.serkantken.applicos.telephone.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.FragmentContactsBinding;
import com.serkantken.applicos.models.ContactModel;
import com.serkantken.applicos.telephone.ContactAddEditActivity;
import com.serkantken.applicos.telephone.ContactClickListener;
import com.serkantken.applicos.telephone.adapters.ContactAdapter;
import com.serkantken.applicos.telephone.database.ContactsDB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ContactsFragment extends Fragment implements ContactClickListener
{
    private FragmentContactsBinding binding;
    private List<ContactModel> contacts = new ArrayList<>();
    private ContactAdapter adapter;
    private ContactModel selectedContact;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentContactsBinding.inflate(requireActivity().getLayoutInflater());

        adapter = new ContactAdapter(requireContext(), contacts, this);
        binding.contactListRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.contactListRV.setAdapter(adapter);

        getPhoneContacts();

        return binding.getRoot();
    }

    private void getPhoneContacts() {
        checkPermission();

        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[] {ContactsContract.Data._ID,
                                            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                                            ContactsContract.Data.DATA4,
                                            ContactsContract.Data.PHOTO_URI,
                                            ContactsContract.Data.DATA7,
                                            ContactsContract.Data.MIMETYPE};

        String selection = ContactsContract.Data.MIMETYPE + "=?";
        String[] selectionArgs = new String[] {ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor.getCount() > 0)
        {
            while (cursor.moveToNext())
            {
                ContactModel contact = new ContactModel();
                contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY)));
                contact.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DATA4)));
                contact.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.PHOTO_URI)));
                contact.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data._ID)));
                contacts.add(contact);
            }
            cursor.close();
            contacts.sort(Comparator.comparing(ContactModel::getName));
            adapter.notifyDataSetChanged();
        }
    }

    private void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    @Override
    public void onClick(ContactModel contact, ConstraintLayout constraintLayout, ImageView imageView) {
        Intent intent = new Intent(requireContext(), ContactAddEditActivity.class);
        intent.putExtra("requestCode", 101);
        intent.putExtra("contact", contact);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), new Pair<>(constraintLayout, "contact"), new Pair<>(imageView, "profilePicture"));
        requireContext().startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void onLongClick(ContactModel contact, ConstraintLayout constraintLayout) {
        selectedContact = new ContactModel();
        selectedContact = contact;
        showPopup(constraintLayout);
    }

    private void showPopup(ConstraintLayout constraintLayout) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), constraintLayout);
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.inflate(R.menu.contacts_popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.contactPin)
        {
            boolean removed = false;
            ContactsDB database = ContactsDB.getInstance(requireContext());
            List<ContactModel> pinnedContacts = database.MainDao().getAll();
            for (int i = 0; i < pinnedContacts.size(); i++)
            {
                if (Objects.equals(pinnedContacts.get(i).getEmail(), selectedContact.getEmail()))
                {
                    database.MainDao().delete(pinnedContacts.get(i));
                    removed = true;
                    Toast.makeText(requireContext(), "Contact has been unpinned from launcher", Toast.LENGTH_SHORT).show();
                }
            }
            if (!removed)
            {
                database.MainDao().insert(selectedContact);
                Toast.makeText(requireContext(), "Contact has been pinned to launcher", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }
}