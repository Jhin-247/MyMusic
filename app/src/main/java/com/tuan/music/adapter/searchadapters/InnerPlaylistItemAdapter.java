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
import com.tuan.music.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class InnerPlaylistItemAdapter extends RecyclerView.Adapter<InnerPlaylistItemAdapter.PlaylistHolder> {
    private List<Playlist> playlists = new ArrayList<>();
    private SearchListener listener;

    public InnerPlaylistItemAdapter(List<Playlist> playlists, SearchListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistHolder(ItemInnerSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.binding.tvPlaylistTitle.setText(playlist.getName());
        if (playlist.getImage() != null && !playlist.getImage().equals(""))
            Glide.with(holder.itemView.getContext()).load(playlist.getImage()).into(holder.binding.ivPlaylistThumbnail);
        else
            Glide.with(holder.itemView.getContext()).load(R.drawable.notes).into(holder.binding.ivPlaylistThumbnail);
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {
        ItemInnerSearchBinding binding;

        public PlaylistHolder(@NonNull ItemInnerSearchBinding binding, SearchListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                listener.onItemClick(getAbsoluteAdapterPosition());
            });
        }
    }
}
