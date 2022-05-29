package com.tuan.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tuan.music.databinding.FragmentPlayMusicBinding;
import com.tuan.music.helper.ImageHelper;
import com.tuan.music.model.event.ChangeSongEventCurrentSong;
import com.tuan.music.player.MyPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlayMusicFragment extends Fragment {
    FragmentPlayMusicBinding binding;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        rotate();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayMusicBinding.inflate(inflater, container, false);

        setupImage();

        return binding.getRoot();
    }

    private void setupImage() {
        ImageHelper.setRoundImage(requireContext(), binding.ivSongThumbnail, MyPlayer.getInstance().getCurrentSong().getThumbnail());
        rotate();
    }

    private void rotate(){
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        binding.ivSongThumbnail.startAnimation(rotate);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeSong(ChangeSongEventCurrentSong event) {

    }
}
