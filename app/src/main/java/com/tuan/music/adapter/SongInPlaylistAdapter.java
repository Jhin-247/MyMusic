package com.tuan.music.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.tuan.music.R;
import com.tuan.music.databinding.ItemSongChosingHolderBinding;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.listener.PlaylistSongAddingClickListener;
import com.tuan.music.model.Song;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.SongListItemHolder> {
    private final List<Song> songs;
    private PlaylistSongAddingClickListener listener;

    public void changeStatus(int position) {
        boolean b = songs.get(position).isChosen();
        songs.get(position).setChosen(!b);
        notifyDataSetChanged();
    }

    public SongInPlaylistAdapter(List<Song> songs, PlaylistSongAddingClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongListItemHolder(ItemSongChosingHolderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListItemHolder holder, int position) {
        Song song = songs.get(position);
        boolean isChosen = song.isChosen();
        ImageHelper.setImageNormalSize(holder.binding.getRoot().getContext(), holder.binding.ivSongThumbnail, song.getThumbnail());
        holder.binding.tvSongName.setText(song.getTitle());
        holder.binding.tvSongArtist.setText(song.getArtist());
        if (isChosen) {
            holder.binding.ivStatus.setBackground(AppCompatResources.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_done));
        } else {
            holder.binding.ivStatus.setBackground(AppCompatResources.getDrawable(holder.binding.getRoot().getContext(), R.drawable.bg_unselect));
        }

    }

    public int getNumberChosen() {
        int chosen = 0;
        for (Song song : songs) {
            if (song.isChosen()) {
                chosen++;
            }
        }
        return chosen;
    }

    public List<Song> getSongs(){
        return songs;
    }

    public List<Song> getChosenSong() {
        List<Song> chosenSongs = new ArrayList<>();
        for (Song song : songs) {
            if (song.isChosen()) {
                chosenSongs.add(song);
            }
        }
        return chosenSongs;
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public static class SongListItemHolder extends RecyclerView.ViewHolder {
        ItemSongChosingHolderBinding binding;

        public SongListItemHolder(@NonNull ItemSongChosingHolderBinding binding, PlaylistSongAddingClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                listener.onSongClick(getAdapterPosition());
//                if(binding.)
            });
        }
    }
}
