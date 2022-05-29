package com.tuan.music.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tuan.music.Constants;
import com.tuan.music.model.Playlist;
import com.tuan.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context) {
        super(context, Constants.DB_CONSTANTS.DATABASE_NAME, null, Constants.DB_CONSTANTS.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)",
                Constants.DB_CONSTANTS.TABLE_NAME_SONG,
                Constants.DB_CONSTANTS.ID,
                Constants.DB_CONSTANTS.SONG_TITLE,
                Constants.DB_CONSTANTS.SONG_ARTIST,
                Constants.DB_CONSTANTS.SONG_PATH,
                Constants.DB_CONSTANTS.SONG_DURATION,
                Constants.DB_CONSTANTS.SONG_THUMBNAIL,
                Constants.DB_CONSTANTS.SONG_ALBUM_ID));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY NOT NULL, %s TEXT, %s TEXT)",
                Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST,
                Constants.DB_CONSTANTS.ID,
                Constants.DB_CONSTANTS.PLAYLIST_TITLE,
                Constants.DB_CONSTANTS.PLAYLIST_IMAGE));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(%s TEXT, %s TEXT)",
                Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST,
                Constants.DB_CONSTANTS.PLAYLIST_ID,
                Constants.DB_CONSTANTS.SONG_ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertSong(Song song) {
        List<Song> songInDatabase = getAllSongs();
        for (Song song1 : songInDatabase) {
            if (song.equals(song1)) {
                return;
            }
        }

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_CONSTANTS.SONG_ARTIST, song.getArtist());
        contentValues.put(Constants.DB_CONSTANTS.SONG_PATH, song.getPath());
        contentValues.put(Constants.DB_CONSTANTS.SONG_TITLE, song.getTitle());
        contentValues.put(Constants.DB_CONSTANTS.SONG_DURATION, song.getDuration());
        contentValues.put(Constants.DB_CONSTANTS.SONG_ALBUM_ID, song.getAlbumId());
        contentValues.put(Constants.DB_CONSTANTS.SONG_THUMBNAIL, song.getThumbnail());
        sqLiteDatabase.insert(Constants.DB_CONSTANTS.TABLE_NAME_SONG, null, contentValues);
//        return result != -1;
    }

    public int insertPlaylist(@NonNull Playlist playlist) {
        if (playlist.getSongs() == null || playlist.getSongs().size() == 0) {
            return 0;
        }
        if (!isTableExists(Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST)) {
            return 0;
        }
        List<Playlist> playlists = getPlaylistModelDB();
        for (Playlist playlist1 : playlists) {
            if (playlist1.getName().equals(playlist.getName())) {
                return -1;
            }
        }
        int id;
        if (playlists.size() == 0) {
            id = 0;
        } else {
            id = playlists.get(playlists.size() - 1).getId() + 1;
        }
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValuesPlaylist = new ContentValues();
        contentValuesPlaylist.put(Constants.DB_CONSTANTS.ID, id);
        contentValuesPlaylist.put(Constants.DB_CONSTANTS.PLAYLIST_TITLE, playlist.getName());
        contentValuesPlaylist.put(Constants.DB_CONSTANTS.PLAYLIST_IMAGE, playlist.getImage());
        sqLiteDatabase.insert(Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST, null, contentValuesPlaylist);

        ContentValues contentValuesPlaylistSong = new ContentValues();
        for (Song song : playlist.getSongs()) {
            contentValuesPlaylistSong.put(Constants.DB_CONSTANTS.PLAYLIST_ID, id);
            contentValuesPlaylistSong.put(Constants.DB_CONSTANTS.SONG_ID, song.getId());
            sqLiteDatabase.insert(Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST, null, contentValuesPlaylistSong);
        }


        return 1;
    }

    private List<Playlist> getPlaylistModelDB() {
        List<Playlist> playlists = new ArrayList<>();
        if (isTableExists(Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST)) {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s", Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST), null);
            while (cursor.moveToNext()) {
                Playlist playlist = new Playlist();
                playlist.setId(cursor.getInt(0));
                playlist.setName(cursor.getString(1));
                playlist.setImage(cursor.getString(2));
                List<Song> songs = new ArrayList<>();
                playlist.setSongs(songs);
                playlists.add(playlist);
            }
            cursor.close();
        }
        return playlists;
    }

    private List<Playlist> getPlaylistData(List<Playlist> playlists) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        for (Playlist playlist : playlists) {
            List<Song> songs = new ArrayList<>();
            Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s WHERE %s=?", Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST, Constants.DB_CONSTANTS.PLAYLIST_ID), new String[]{String.valueOf(playlist.getId())});
            while (cursor.moveToNext()) {
                Cursor cursor2 = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s WHERE %s=?", Constants.DB_CONSTANTS.TABLE_NAME_SONG, Constants.DB_CONSTANTS.ID), new String[]{cursor.getString(1)});
                cursor2.moveToFirst();
                Song song = new Song();
                song.setId(cursor2.getInt(0));
                song.setTitle(cursor2.getString(1));
                song.setArtist(cursor2.getString(2));
                song.setPath(cursor2.getString(3));
                song.setDuration(cursor2.getString(4));
                song.setThumbnail(cursor2.getString(5));
                song.setAlbumId(cursor2.getInt(6));
                songs.add(song);
                cursor2.close();
            }
            playlist.setSongs(songs);
            cursor.close();
        }
        return playlists;
    }

    public List<Playlist> getAllPlaylistFromDB() {
        List<Playlist> playlists = getPlaylistModelDB();
        if (playlists.size() != 0) {
            return getPlaylistData(playlists);
        }
        return playlists;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        if (isTableExists(Constants.DB_CONSTANTS.TABLE_NAME_SONG)) {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s", Constants.DB_CONSTANTS.TABLE_NAME_SONG), null);
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setId(cursor.getInt(0));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));
                song.setPath(cursor.getString(3));
                song.setDuration(cursor.getString(4));
                song.setThumbnail(cursor.getString(5));
                song.setAlbumId(cursor.getInt(6));
                songs.add(song);
            }
            cursor.close();
        }
        return songs;
    }

    public boolean isTableExists(String tableName) {
        boolean isExist = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    public void deleteListPlaylist(List<Playlist> chosenPlaylist) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for (Playlist playlist : chosenPlaylist) {
            Cursor cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s WHERE %s=?", Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST, Constants.DB_CONSTANTS.PLAYLIST_ID), new String[]{String.valueOf(playlist.getId())});
            while (cursor.moveToNext()) {
                String whereClause = String.format("%s = ? and %s = ?", Constants.DB_CONSTANTS.PLAYLIST_ID, Constants.DB_CONSTANTS.SONG_ID);
                String[] whereArgs = new String[]{String.valueOf(cursor.getInt(0)), String.valueOf(cursor.getInt(1))};
                sqLiteDatabase.delete(Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST, whereClause, whereArgs);
            }
            sqLiteDatabase.delete(Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST, String.format("%s = ?", Constants.DB_CONSTANTS.ID), new String[]{String.valueOf(playlist.getId())});
            cursor.close();
        }
    }

    public List<Song> findSongByKey(String keySearch) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(
                Constants.DB_CONSTANTS.TABLE_NAME_SONG,
                null,
                Constants.DB_CONSTANTS.SONG_TITLE + " LIKE '%" + keySearch + "%' or " + Constants.DB_CONSTANTS.SONG_ARTIST + " LIKE '%" + keySearch + "%'",
                null, null, null, null, null);
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Constants.DB_CONSTANTS.TABLE_NAME_SONG + " WHERE " + Constants.DB_CONSTANTS.SONG_TITLE + " LIKE %" + keySearch + "%",null);
        while (cursor.moveToNext()) {
            Song song = new Song();
            song.setId(cursor.getInt(0));
            song.setTitle(cursor.getString(1));
            song.setArtist(cursor.getString(2));
            song.setPath(cursor.getString(3));
            song.setDuration(cursor.getString(4));
            song.setThumbnail(cursor.getString(5));
            song.setAlbumId(cursor.getInt(6));
            songs.add(song);
        }
        cursor.close();
        return songs;

    }

    public List<Playlist> findPlaylistByKey(String keySearch) {
        List<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(
                Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST,
                null,
                Constants.DB_CONSTANTS.PLAYLIST_TITLE + " LIKE '%" + keySearch + "%'",
                null, null, null, null, null);
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Constants.DB_CONSTANTS.TABLE_NAME_SONG + " WHERE " + Constants.DB_CONSTANTS.SONG_TITLE + " LIKE %" + keySearch + "%",null);
        while (cursor.moveToNext()) {
            Playlist playlist = new Playlist();
            playlist.setId(cursor.getInt(0));
            playlist.setName(cursor.getString(1));
            playlist.setImage(cursor.getString(2));
            List<Song> songs = new ArrayList<>();
            playlist.setSongs(songs);
            playlists.add(playlist);
        }
        cursor.close();
        return playlists;
    }

    public Playlist getPlaylistById(int id) {
        Playlist playlist = new Playlist();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(
                Constants.DB_CONSTANTS.TABLE_NAME_PLAYLIST,
                null,
                Constants.DB_CONSTANTS.ID + " = " + id,
                null, null, null, null, null);
//        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Constants.DB_CONSTANTS.TABLE_NAME_SONG + " WHERE " + Constants.DB_CONSTANTS.SONG_TITLE + " LIKE %" + keySearch + "%",null);
        while (cursor.moveToNext()) {
            playlist.setId(cursor.getInt(0));
            playlist.setName(cursor.getString(1));
            playlist.setImage(cursor.getString(2));

            List<Song> songs = new ArrayList<>();
            Cursor cursor1 = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s WHERE %s=?", Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST, Constants.DB_CONSTANTS.PLAYLIST_ID), new String[]{String.valueOf(playlist.getId())});
            while (cursor1.moveToNext()) {
                Cursor cursor2 = sqLiteDatabase.rawQuery(String.format("SELECT * FROM %s WHERE %s=?", Constants.DB_CONSTANTS.TABLE_NAME_SONG, Constants.DB_CONSTANTS.ID), new String[]{cursor1.getString(1)});
                cursor2.moveToFirst();
                Song song = new Song();
                song.setId(cursor2.getInt(0));
                song.setTitle(cursor2.getString(1));
                song.setArtist(cursor2.getString(2));
                song.setPath(cursor2.getString(3));
                song.setDuration(cursor2.getString(4));
                song.setThumbnail(cursor2.getString(5));
                song.setAlbumId(cursor2.getInt(6));
                songs.add(song);
                cursor2.close();
            }
            playlist.setSongs(songs);
            cursor1.close();
        }
        cursor.close();

        return playlist;
    }

    public void deleteSongFromPlaylist(int songId,int playlistId){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        sqLiteDatabase.delete(Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST,Constants.DB_CONSTANTS.SONG_ID + " = " + songId + " and " + Constants.DB_CONSTANTS.PLAYLIST_ID + " = " + playlistId, null);
    }

    public void addSongToPlaylist(int songId,int playlistId){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_CONSTANTS.PLAYLIST_ID, playlistId);
        contentValues.put(Constants.DB_CONSTANTS.SONG_ID, songId);
        sqLiteDatabase.insert(Constants.DB_CONSTANTS.TABLE_NAME_SONG_PLAYLIST, null, contentValues);
    }
}
