package com.serkantken.applicos.launcher;

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
import com.serkantken.applicos.databinding.PinnedContactLayoutBinding;
import com.serkantken.applicos.models.ContactModel;
import com.serkantken.applicos.telephone.ContactAddEditActivity;

import java.util.List;
import java.util.Objects;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>
{
    Context context;
    List<ContactModel> contacts;

    public FavouritesAdapter(Context context, List<ContactModel> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavouritesViewHolder(PinnedContactLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesViewHolder holder, int position) {
        ContactModel contact = contacts.get(position);
        holder.binding.contactName.setText(contact.getName());
        Glide.with(context).load(contact.getPhoto()).placeholder(AppCompatResources.getDrawable(context, R.drawable.ic_person_130)).into(holder.binding.contactPhoto);

        holder.binding.pinnedContactContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, ContactAddEditActivity.class);
            intent.putExtra("contact", contact);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.binding.pinnedContactContainer, Objects.requireNonNull(ViewCompat.getTransitionName(holder.binding.pinnedContactContainer)));
            context.startActivity(intent, optionsCompat.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class FavouritesViewHolder extends RecyclerView.ViewHolder
    {
        PinnedContactLayoutBinding binding;

        public FavouritesViewHolder(@NonNull PinnedContactLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
