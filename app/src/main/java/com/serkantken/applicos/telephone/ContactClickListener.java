package com.serkantken.applicos.telephone;

import android.view.MenuItem;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.serkantken.applicos.models.ContactModel;

public interface ContactClickListener
{
    boolean onMenuItemClick(MenuItem item);

    void onClick(ContactModel contact, ConstraintLayout constraintLayout, ImageView imageView);
    void onLongClick(ContactModel contact, ConstraintLayout constraintLayout);
}
