package com.serkantken.applicos;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Utils
{
    private Activity activity;
    private Context context;

    public Utils(Activity activity, Context context)
    {
        this.activity = activity;
        this.context = context;
    }

    public void blurMultipleViews(float radius, boolean isRounded, BlurView... views)
    {
        for (BlurView view: views)
        {
            blur(view, radius, isRounded);
        }
    }

    public void blur(BlurView view, float radius, boolean isRounded)
    {
        View decorView = activity.getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        view.setupWith(rootView, new RenderScriptBlur(context))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true);
        if (isRounded) {
            view.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            view.setClipToOutline(true);
        }
    }
}
