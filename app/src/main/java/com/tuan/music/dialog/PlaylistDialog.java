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

import com.blankj.utilcode.util.ToastUtils;
import com.tuan.music.R;
import com.tuan.music.adapter.PlaylistChoosingAdapter;
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.listener.PlaylistSongAddingClickListener;
import com.tuan.music.model.Playlist;
import com.tuan.music.model.Song;

import java.util.ArrayList;
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
        adapter.changeStatus(position);
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
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        sqLiteHelper = new SQLiteHelper(context);
        List<Playlist> playlists = sqLiteHelper.getAllPlaylistFromDB();

        for(Playlist playlist : playlists){
            for(Song song : playlist.getSongs()){
                if(song.getId() == songId){
                    playlist.setChosen(true);
                    break;
                }
            }
        }


        recyclerView = findViewById(R.id.rcv_playlist);
        buttonOk = findViewById(R.id.btn_ok);
        buttonCancel = findViewById(R.id.btn_cancel);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PlaylistChoosingAdapter(playlists, this);
        recyclerView.setAdapter(adapter);

        buttonCancel.setOnClickListener(v -> {
            cancel();
        });
        buttonOk.setOnClickListener(v -> {
            for(Playlist playlist : adapter.getPlaylists()){
                boolean isSongInPlaylist = false;
                for(Song song : playlist.getSongs()){
                    if(song.getId() == songId && !playlist.isChosen()){
                        isSongInPlaylist = true;
                        sqLiteHelper.deleteSongFromPlaylist(songId,playlist.getId());
                        break;
                    }
                }
                if(!isSongInPlaylist && playlist.isChosen()){
                    sqLiteHelper.addSongToPlaylist(songId,playlist.getId());
                }
            }
            List<Playlist> playlists1 = sqLiteHelper.getAllPlaylistFromDB();
            List<Playlist> listToDelete = new ArrayList<>();
            for(Playlist playlist : playlists1){
                if(playlist.getSongs().size() == 0){
                    listToDelete.add(playlist);
                }
            }
            sqLiteHelper.deleteListPlaylist(listToDelete);
            cancel();
        });
    }


}
