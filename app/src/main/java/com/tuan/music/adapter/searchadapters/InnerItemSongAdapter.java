package com.tuan.music.adapter.searchadapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tuan.music.R;
import com.tuan.music.databinding.ItemInnerSearchBinding;
import com.tuan.music.listener.SearchListener;
import com.tuan.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class InnerItemSongAdapter extends RecyclerView.Adapter<InnerItemSongAdapter.InnerHolder> {
    private List<Song> songs = new ArrayList<>();
    private SearchListener listener;

    public InnerItemSongAdapter(List<Song> songs, SearchListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InnerHolder(ItemInnerSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        Song song = songs.get(position);
        holder.binding.tvPlaylistTitle.setText(song.getTitle());
        if (song.getThumbnail() != null && !song.getThumbnail().equals(""))
            Glide.with(holder.itemView.getContext()).load(song.getThumbnail()).into(holder.binding.ivPlaylistThumbnail);
        else
            Glide.with(holder.itemView.getContext()).load(R.drawable.notes).into(holder.binding.ivPlaylistThumbnail);
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {
        ItemInnerSearchBinding binding;

        public InnerHolder(@NonNull ItemInnerSearchBinding binding, SearchListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                listener.onItemClick(getAbsoluteAdapterPosition());
            });
        }
    }
}
