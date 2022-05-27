package com.tuan.music;

public class Constants {
    public static class DB_CONSTANTS {
        public static final String DATABASE_NAME = "student_db";
        public static final int DATABASE_VERSION = 1;
        public static final String TABLE_NAME = "students";

        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_CLASS = "class";
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

        public static final String CAN_PLAY = "can_play";
    }

    public static class SHARED_PREF {
        public static final String SHARED_FILE = "Song";
    }
}
