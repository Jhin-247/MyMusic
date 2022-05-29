package com.tuan.music.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tuan.music.R;
import com.tuan.music.databinding.ItemPlaylistGridBinding;
import com.tuan.music.listener.PlaylistListener;
import com.tuan.music.model.Playlist;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class PlaylistAdapterGrid extends RecyclerView.Adapter<PlaylistAdapterGrid.PlaylistHolder> {


    private List<Playlist> playlists;
    private final PlaylistListener listener;
    private int type;
    private boolean isOpenSelection;

    public void setType(int type) {
        this.type = type;
    }

    public PlaylistAdapterGrid(List<Playlist> playlists, PlaylistListener listener) {
        this.playlists = playlists;
        this.listener = listener;
        type = 0;
    }

    public int getChosenPlaylistNUmber(){
        int count = 0;
        for(Playlist playlist: playlists){
            if(playlist.isChosen()){
                count++;
            }
        }
        return count;
    }

    public List<Playlist> getChosenPlaylist(){
        List<Playlist> chosenPlaylists = new ArrayList<>();
        for(Playlist playlist:playlists){
            if(playlist.isChosen()){
                chosenPlaylists.add(playlist);
            }
        }
        return chosenPlaylists;
    }

    public boolean isOpenSelection() {
        return isOpenSelection;
    }

    public Playlist getPlaylistAtPosition(int index){
        return playlists.get(index);
    }

    public void changeStatus(int position) {
        boolean b = playlists.get(position).isChosen();
        playlists.get(position).setChosen(!b);
        notifyDataSetChanged();
    }

    public void openOrCloseMultiSelection(boolean status) {
        this.isOpenSelection = status;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistAdapterGrid.PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistAdapterGrid.PlaylistHolder(ItemPlaylistGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapterGrid.PlaylistHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        boolean isChosen = playlist.isChosen();
        holder.binding.tvPlaylistTitle.setText(playlist.getName());
        if (playlist.getImage() == null || playlist.getImage().equals(""))
            Glide.with(holder.binding.getRoot().getContext()).load(R.drawable.notes).into(holder.binding.ivPlaylistThumbnail);
        else
            Glide.with(holder.binding.getRoot().getContext()).load(playlist.getImage()).into(holder.binding.ivPlaylistThumbnail);
        if (type == 1) {
            holder.binding.tvPlaylistTitle.setTextColor(holder.binding.getRoot().getContext().getResources().getColor(R.color.black));
        }
        if (isChosen) {
            holder.binding.ivStatus.setBackground(AppCompatResources.getDrawable(holder.binding.getRoot().getContext(), R.drawable.ic_done));
        } else {
            holder.binding.ivStatus.setBackground(AppCompatResources.getDrawable(holder.binding.getRoot().getContext(), R.drawable.bg_unselect));
        }
        if (isOpenSelection) {
            holder.binding.cvStatus.setVisibility(View.VISIBLE);
        } else {
            holder.binding.cvStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public static class PlaylistHolder extends RecyclerView.ViewHolder {
        ItemPlaylistGridBinding binding;

        public PlaylistHolder(@NonNull ItemPlaylistGridBinding binding, PlaylistListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                listener.onPlaylistClick(getAdapterPosition());
            });
            binding.getRoot().setOnLongClickListener(view -> {
                listener.onPlaylistLongClickListener(getAdapterPosition());
                return true;
            });
        }
    }
}
