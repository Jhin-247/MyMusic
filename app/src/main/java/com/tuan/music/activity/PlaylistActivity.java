package com.tuan.music.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tuan.music.Constants;
import com.tuan.music.R;
import com.tuan.music.adapter.PlaylistAdapterGrid;
import com.tuan.music.databinding.ActivityPlaylistBinding;
import com.tuan.music.db.SQLiteHelper;
import com.tuan.music.listener.PlaylistListener;
import com.tuan.music.model.Playlist;
import com.tuan.music.player.MyPlayer;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistListener {
    ActivityPlaylistBinding binding;
    SQLiteHelper sqLiteHelper;
    PlaylistAdapterGrid adapter;
    private boolean isModifying;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent, this.getTheme()));
        }
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Glide.with(this).load(R.drawable.bg_play_music).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.layout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                binding.layout.setBackground(placeholder);
            }
        });

        isModifying = false;

        initData();
        initListener();
    }

    private void initListener() {
        binding.btnAddPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPlaylistActivity.class);
            startActivity(intent);
        });
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
        binding.ivClose.setOnClickListener(v -> {
            adapter.openOrCloseMultiSelection(false);
            binding.rltFunction.setVisibility(View.GONE);
        });
        binding.ivDelete.setOnClickListener(v -> {
            sqLiteHelper.deleteListPlaylist(adapter.getChosenPlaylist());
            binding.ivClose.performClick();
            onResume();
        });
        binding.ivModify.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModifyPlaylistActivity.class);
            startActivity(intent);
        });
    }

    private void initData() {
        sqLiteHelper = new SQLiteHelper(this);
        List<Playlist> playlists = sqLiteHelper.getAllPlaylistFromDB();
        binding.tvPlaylistTitle.setText(getResources().getString(R.string.playlist_title, playlists.size()));
        binding.rcvPlaylist.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        adapter = new PlaylistAdapterGrid(playlists, this);
        binding.rcvPlaylist.setAdapter(adapter);
        binding.rltFunction.setVisibility(View.GONE);
        isModifying = false;
    }

    @Override
    public void onPlaylistClick(int position) {
        if (adapter.isOpenSelection()) {
            adapter.changeStatus(position);
            if (adapter.getChosenPlaylistNUmber() == 1) {
                binding.ivModify.setClickable(true);
                binding.ivModify.setAlpha(1.0f);
            } else {
                binding.ivModify.setClickable(false);
                binding.ivModify.setAlpha(0.3f);
            }
            binding.tvChosenNumber.setText("Da chon: " + adapter.getChosenPlaylistNUmber());
        } else {
            MyPlayer.getInstance().setCurrentPlaylist(adapter.getPlaylistAtPosition(position).getSongs());
            MyPlayer.getInstance().setCurrentPlaylistId(adapter.getPlaylistAtPosition(position).getId());
            Intent intent = new Intent(this, PlayMusicActivity.class);
            intent.putExtra(Constants.INTENT_CONSTANT.IS_PLAYLIST, true);
            startActivity(intent);
        }
    }

    @Override
    public void onPlaylistLongClickListener(int position) {
        if (!isModifying) {
            binding.rltFunction.setVisibility(View.VISIBLE);
            adapter.openOrCloseMultiSelection(true);
            onPlaylistClick(position);
            isModifying = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
