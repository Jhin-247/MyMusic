package com.tuan.music.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuan.music.R;
import com.tuan.music.adapter.PlaylistChoosingAdapter;
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.listener.PlaylistSongAddingClickListener;
import com.tuan.music.model.Playlist;
import com.tuan.music.model.Song;

import java.util.List;

public class PlaylistDialog extends Dialog implements PlaylistSongAddingClickListener {
    Context context;
    SQLiteHelper sqLiteHelper;
    RecyclerView recyclerView;
    Button buttonOk, buttonCancel;
    PlaylistChoosingAdapter adapter;
    private int songId;

    @Override
    public void onSongClick(int position) {

    }

    public interface DismissListenerNew {
        void onDismissListener();
    }

    public PlaylistDialog(@NonNull Context context,int songId) {
        super(context);
        this.context = context;
        this.songId = songId;
        setCancelable(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_playlist);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        sqLiteHelper = new SQLiteHelper(context);
        List<Playlist> playlists = sqLiteHelper.getAllPlaylistFromDB();

        recyclerView = findViewById(R.id.rcv_playlist);
        buttonOk = findViewById(R.id.btn_ok);
        buttonCancel = findViewById(R.id.btn_cancel);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PlaylistChoosingAdapter(playlists, this);

        buttonCancel.setOnClickListener(v -> {
            cancel();
        });
        buttonOk.setOnClickListener(v -> {
            for(Playlist playlist : playlists){
                for(Song song : playlist.getSongs()){
                    if(song.getId() == songId && !playlist.isChosen()){
                        sqLiteHelper.deleteSongFromPlaylist(songId,playlist.getId());
                    }
                    if(son)
                }
            }
        });
    }


}
