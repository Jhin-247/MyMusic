package com.tuan.music.helper;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicHelper {

    public static List<Song> getAllMusicFromDevice(Context context) {

        List<Song> songs = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        while (cursor.moveToNext()) {
            Song music = new Song();
            music.setPath(cursor.getString(1));
            music.setDuration(cursor.getString(2));
            music.setTitle(cursor.getString(0));
            music.setArtist(cursor.getString(3));
            music.setAlbumId(cursor.getLong(4));

//            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//            byte[] rawArt;
//            Bitmap art = null;
//            BitmapFactory.Options bfo=new BitmapFactory.Options();
//
//            mmr.setDataSource(music.getPath());
//            rawArt = mmr.getEmbeddedPicture();


//            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//            /*getAlbumId gives you the ID of the album
//             *(provided by - long albumId = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//             */
//            Uri imgUri = ContentUris.withAppendedId(sArtworkUri, music.getAlbumId());

//            if (null != rawArt)
//                art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);

//            music.setBmThumbnail(art);
//            music.setUri(imgUri);
//            Log.d("123123",imgUri.toString());
            if (new File(music.getPath()).exists()) {
                songs.add(music);
            }
        }
        cursor.close();
        songs.remove(songs.size()-1);
        return songs;
    }

    public static List<Song> getAllSongFromDB(Context context){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context);
        return sqLiteHelper.getAllSongs();
    }

}
