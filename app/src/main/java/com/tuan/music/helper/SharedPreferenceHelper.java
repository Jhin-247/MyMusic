package com.tuan.music.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.tuan.music.Constants;
import com.tuan.music.model.Song;

public class SharedPreferenceHelper {
    public static void saveCurrentSongToPref(Activity activity, Song song){
        SharedPreferences preferences = activity.getSharedPreferences(Constants.SHARED_PREF.SHARED_FILE,Context.MODE_PRIVATE);

    }
}
