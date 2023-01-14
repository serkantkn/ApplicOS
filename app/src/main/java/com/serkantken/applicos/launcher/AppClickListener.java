package com.serkantken.applicos.launcher;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.serkantken.applicos.models.AppModel;

public interface AppClickListener
{
    void onClick(AppModel model);
    void onLongClick(AppModel model, ConstraintLayout container);
}
