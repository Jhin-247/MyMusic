package com.tuan.music.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tuan.music.R;
import com.tuan.music.services.NotificationActionService;
import com.tuan.music.model.Song;

public class MyNotificationBuilder {
    public static final String CHANNEL_ID = "channel_id";

    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";
    public static final String CHANNEL_PLAY = "";
    public static final String CHANNEL_NEXT = "channel_id";

    public static Notification notification;

    public static void createNotification(Context context, Song song, int index, int size, int drw_play) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_play_music);

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (index == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                drw_previous = R.drawable.ic_play_prev;

            }

            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drw_next;
            if (index == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                drw_next = R.drawable.ic_play_next;

            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_pause)
                    .setContentTitle(song.getTitle())
                    .setContentText(song.getArtist())
                    .setLargeIcon(bitmap)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(drw_play, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();
            managerCompat.notify(1, notification);
        }
    }
}
