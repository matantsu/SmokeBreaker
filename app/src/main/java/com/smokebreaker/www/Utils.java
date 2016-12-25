package com.smokebreaker.www;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.smokebreaker.www.bl.models.User;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Utils {
    public static void show(View view, boolean show){
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static void setUser(TextView textView, User user){
        textView.setText(user.getDisplayName());
        Glide.with(textView.getContext())
                .load(user.getPhotoUrl())
                .asBitmap()
                .transform(new CropCircleTransformation(textView.getContext()))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(textView.getResources(),resource);
                        drawable.setBounds(0,0, (int) (drawable.getIntrinsicWidth()*1.5), (int) (drawable.getIntrinsicHeight()*1.5));
                        textView.setCompoundDrawables(drawable,null,null,null);
                    }
                });
    }

    public static void setUserWidget(Context context, RemoteViews views, @IdRes int textId,@IdRes int imageId, User user){
        views.setTextViewText(textId,user.getDisplayName());
        Glide.with(context)
                .load(user.getPhotoUrl())
                .asBitmap()
                .transform(new CropCircleTransformation(context))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        views.setImageViewBitmap(imageId,resource);
                    }
                });
    }
}
