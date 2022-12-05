package com.serkantken.applicos.telephone.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.serkantken.applicos.R;
import com.serkantken.applicos.databinding.ContactListItemBinding;
import com.serkantken.applicos.models.ContactModel;
import com.serkantken.applicos.telephone.ContactAddEditActivity;
import com.serkantken.applicos.telephone.ContactClickListener;
import com.serkantken.applicos.telephone.database.ContactsDB;

import java.util.List;
import java.util.Objects;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
{
    Context context;
    List<ContactModel> contacts;
    ContactClickListener contactClickListener;

    public ContactAdapter(Context context, List<ContactModel> contacts, ContactClickListener contactClickListener) {
        this.context = context;
        this.contacts = contacts;
        this.contactClickListener = contactClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(ContactListItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactModel contact = contacts.get(position);
        holder.binding.contactName.setText(contact.getName());
        holder.binding.contactPhone.setText(contact.getPhone());
        Glide.with(context).load(contact.getPhoto()).placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_person_130)).into(holder.binding.contactPicture);

        ContactsDB db = ContactsDB.getInstance(context);
        List<ContactModel> list = db.MainDao().getAll();

        holder.binding.contactItemContainer.setOnClickListener(v -> {
            contactClickListener.onClick(contact, holder.binding.contactItemContainer, holder.binding.contactPicture);
        });
        holder.binding.contactItemContainer.setOnLongClickListener(v -> {
            contactClickListener.onLongClick(contact, holder.binding.contactItemContainer);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder
    {
        ContactListItemBinding binding;

        public ContactViewHolder(@NonNull ContactListItemBinding itemView)
        {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
