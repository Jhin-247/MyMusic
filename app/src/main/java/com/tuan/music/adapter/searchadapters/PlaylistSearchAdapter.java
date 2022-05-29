package com.tuan.music.adapter.searchadapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuan.music.databinding.ItemSearchBinding;
import com.tuan.music.listener.SearchListener;
import com.tuan.music.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSearchAdapter extends RecyclerView.Adapter<PlaylistSearchAdapter.PlaylistHolder> {
    private boolean hasData;
    private final InnerPlaylistItemAdapter innerItemSongAdapter;
    private List<Playlist> playlists;

    public PlaylistSearchAdapter(SearchListener listener) {
        hasData = false;
        innerItemSongAdapter = new InnerPlaylistItemAdapter(new ArrayList<>(), listener);
        playlists = new ArrayList<>();
    }

    public int getPlaylistId(int position){
        return playlists.get(position).getId();
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        holder.binding.tvTitle.setText("Playlist (" + playlists.size() + ")");
        holder.binding.rcvDataSearch.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext(), RecyclerView.HORIZONTAL, false));
        holder.binding.rcvDataSearch.setAdapter(innerItemSongAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataSearch(List<Playlist> playlists) {
        this.playlists = playlists;
        hasData = true;
        innerItemSongAdapter.setPlaylists(playlists);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setHasData(boolean hasData) {
        this.hasData = hasData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return hasData ? 1 : 0;
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {
        ItemSearchBinding binding;

        public PlaylistHolder(@NonNull ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
