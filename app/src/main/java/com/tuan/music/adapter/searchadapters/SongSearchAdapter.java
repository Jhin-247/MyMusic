package com.tuan.music.adapter.searchadapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.tuan.music.databinding.ItemSearchBinding;
import com.tuan.music.listener.SearchListener;
import com.tuan.music.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongSearchAdapter extends RecyclerView.Adapter<SongSearchAdapter.SongSearchHolder> {
    private boolean hasData;
    private final InnerItemSongAdapter innerItemSongAdapter;
    private List<Song> songSearch;

    public SongSearchAdapter(SearchListener listener) {
        hasData = false;
        innerItemSongAdapter = new InnerItemSongAdapter(new ArrayList<>(), listener);
        songSearch = new ArrayList<>();
    }

    public Song getSongSearch(int position){
        return songSearch.get(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataSearch(List<Song> songs){
        this.songSearch = songs;
        hasData = true;
        innerItemSongAdapter.setSongs(songs);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setHasData(boolean hasData){
        this.hasData = hasData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongSearchHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongSearchHolder holder, int position) {
        LogUtils.d("here");
        holder.binding.tvTitle.setText("Song (" + songSearch.size() + ")");
        holder.binding.rcvDataSearch.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext(), RecyclerView.HORIZONTAL, false));
        holder.binding.rcvDataSearch.setAdapter(innerItemSongAdapter);
    }

    @Override
    public int getItemCount() {
        return hasData ? 1 : 0;
    }

    public static class SongSearchHolder extends RecyclerView.ViewHolder {
        ItemSearchBinding binding;

        public SongSearchHolder(@NonNull ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
