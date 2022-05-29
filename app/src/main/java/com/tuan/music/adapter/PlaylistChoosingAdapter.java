package com.tuan.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tuan.music.R;
import com.tuan.music.databinding.ItemSongChosingHolderBinding;
import com.tuan.music.listener.PlaylistSongAddingClickListener;
import com.tuan.music.model.Playlist;

import java.util.List;

public class PlaylistChoosingAdapter extends RecyclerView.Adapter<PlaylistChoosingAdapter.PlaylistHolder> {
    private final List<Playlist> playlists;
    private final PlaylistSongAddingClickListener listener;

    public PlaylistChoosingAdapter(List<Playlist> playlists, PlaylistSongAddingClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistHolder(ItemSongChosingHolderBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false),listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        boolean isChosen = playlist.isChosen();
        holder.binding.tvSongName.setText(playlist.getName());
        holder.binding.tvSongArtist.setVisibility(View.GONE);
        Glide.with(holder.binding.getRoot()).load(R.drawable.notes).into(holder.binding.ivSongThumbnail);
        if (isChosen) {
            holder.binding.ivStatus.setBackground(AppCompatResources.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_done));
        } else {
            holder.binding.ivStatus.setBackground(AppCompatResources.getDrawable(holder.binding.getRoot().getContext(), R.drawable.bg_unselect));
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {
        ItemSongChosingHolderBinding binding;

        public PlaylistHolder(@NonNull ItemSongChosingHolderBinding binding, PlaylistSongAddingClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                listener.onSongClick(getAdapterPosition());
            });
        }
    }
}
