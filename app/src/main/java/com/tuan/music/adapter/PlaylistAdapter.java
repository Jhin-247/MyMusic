package com.tuan.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tuan.music.R;
import com.tuan.music.databinding.ItemPlaylistBinding;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.listener.PlaylistListener;
import com.tuan.music.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {
    private List<Playlist> playlists;
    private final PlaylistListener listener;
    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public PlaylistAdapter(List<Playlist> playlists, PlaylistListener listener) {
        this.playlists = playlists;
        this.listener = listener;
        type = 0;
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.binding.tvPlaylistTitle.setText(playlist.getName());
        if (playlist.getImage() == null || playlist.getImage().equals(""))
            Glide.with(holder.binding.getRoot().getContext()).load(R.drawable.notes).into(holder.binding.ivPlaylistThumbnail);
        else
            Glide.with(holder.binding.getRoot().getContext()).load(playlist.getImage()).into(holder.binding.ivPlaylistThumbnail);
        if (type == 1) {
            holder.binding.tvPlaylistTitle.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.black));
        }
    }

    public Playlist getPlaylistAtPosition(int index){
        return playlists.get(index);
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {
        ItemPlaylistBinding binding;

        public PlaylistHolder(@NonNull ItemPlaylistBinding binding, PlaylistListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                listener.onPlaylistClick(getAdapterPosition());
            });
        }
    }
}
