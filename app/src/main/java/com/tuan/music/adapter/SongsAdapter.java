package com.tuan.music.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tuan.music.R;
import com.tuan.music.databinding.ItemSongsBinding;
import com.tuan.music.listener.HomeSongListener;
import com.tuan.music.model.Song;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongHolder> {

    private final HomeSongListener listener;
    private List<Song> songs;
    //Loai adapter: 1 -> man home, 0-> man khac
    private int typeAdapter;
    private Song currentSong;
    private int position;

    public SongsAdapter(List<Song> songs, int typeAdapter, HomeSongListener listener) {
        this.songs = songs;
        this.typeAdapter = typeAdapter;
        this.listener = listener;
        currentSong = songs.get(0);
        position = 0;
    }



    public SongsAdapter(List<Song> songs, int typeAdapter, HomeSongListener listener, Song song, int position) {
        this.songs = songs;
        this.typeAdapter = typeAdapter;
        this.listener = listener;
        currentSong = song;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentSong(int song) {
        this.currentSong = songs.get(song);
        position = song;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongHolder(ItemSongsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        if ((typeAdapter == 0)) {
            holder.binding.tvSongName.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.white));
            holder.binding.tvSongArtist.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.white));
        }
        Song song = songs.get(position);
        holder.binding.tvSongName.setText(song.getTitle());
        if (!song.getArtist().equals("<unknown>"))
            holder.binding.tvSongArtist.setText(song.getArtist());
        Glide.with(holder.binding.getRoot().getContext()).load(song.getThumbnail()).placeholder(R.drawable.notes).into(holder.binding.ivSongThumbnail);
        if (typeAdapter == 0)
            if (song.equals(currentSong)) {
                holder.binding.tvSongName.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.black));
                holder.binding.tvSongArtist.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.black));
                Glide.with(holder.binding.layout.getContext()).load(R.drawable.bg_song_selected).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.binding.layout.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        holder.binding.layout.setBackground(placeholder);
                    }
                });
            } else {
                holder.binding.tvSongName.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.white));
                holder.binding.tvSongArtist.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.white));
                Glide.with(holder.binding.layout.getContext()).load(R.drawable.bg_song_unselected).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.binding.layout.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        holder.binding.layout.setBackground(placeholder);
                    }
                });
            }
    }

    @Override
    public int getItemCount() {
        if (songs == null) return 0;
        if (typeAdapter == 1 && songs.size() >= 5) {
            return 5;
        }
        return songs.size();
    }

    public static class SongHolder extends RecyclerView.ViewHolder {
        ItemSongsBinding binding;

        public SongHolder(@NonNull ItemSongsBinding binding, HomeSongListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ivMoreOption.setOnClickListener(v -> {
                listener.onMoreClick(getAdapterPosition());
            });
            binding.getRoot().setOnClickListener(v -> {
                listener.onSongClick(getAdapterPosition());
            });
        }
    }
}
