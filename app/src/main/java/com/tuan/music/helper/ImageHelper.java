package com.tuan.music.helper;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tuan.music.R;

public class ImageHelper {

    public static void setImage(Context context, ImageView view, String resource) {
        Glide.with(context).load(resource).centerCrop().into(view);
    }

    public static void setImage(Context context, ImageView view, int resource) {
        Glide.with(context).load(resource).centerCrop().override(Target.SIZE_ORIGINAL).into(view);
    }

    public static void setRoundImage(Context context, ImageView imageView, String resource) {
        if (resource == null || !resource.equals(""))
            Glide.with(context).load(R.drawable.notes).apply(RequestOptions.circleCropTransform()).into(imageView);
        else
            Glide.with(context).load(resource).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

}
