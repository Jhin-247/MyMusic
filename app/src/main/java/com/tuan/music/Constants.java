package com.tuan.music;

public class Constants {
    public static class DB_CONSTANTS {
        public static final String DATABASE_NAME = "song_db";
        public static final int DATABASE_VERSION = 2;

        public static final String TABLE_NAME_PLAYLIST = "play_list";
        public static final String TABLE_NAME_SONG = "song";
        public static final String TABLE_NAME_SONG_PLAYLIST = "song_playlist";

        public static final String ID = "id";

        //Song db constant
        public static final String SONG_TITLE = "title";
        public static final String SONG_ARTIST = "artist";
        public static final String SONG_PATH = "path";
        public static final String SONG_THUMBNAIL = "thumbnail";
        public static final String SONG_DURATION = "duration";
        public static final String SONG_ALBUM_ID = "album_id";

        //Playlist db constants
        public static final String PLAYLIST_TITLE = "title";
        public static final String PLAYLIST_IMAGE = "image";

        //Playlist-Song db constants
        public static final String PLAYLIST_ID = "playlist_id";
        public static final String SONG_ID = "song_id";

    }

    public static class PERMISSION_CONSTANTS {
        public static final int REQUEST_PERMISSION_CODE = 0;
    }

    public static class LOG_CONSTANTS {
        public static final String DEBUG_LOG = "LOGGING";
    }

    public static class INTENT_CONSTANT {
        public static final String PLAYLIST = "playlist";
        public static final String CURRENT_SONG = "current_song";
        public static final String IS_PLAYLIST = "is_playlist";
        public static final String CAN_PLAY = "can_play";
    }

    public static class SHARED_PREF {
        public static final String SHARED_FILE = "Song";
    }
}
