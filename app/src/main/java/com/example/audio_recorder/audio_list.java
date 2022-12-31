package com.example.audio_recorder;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;


public class audio_list<position> extends Fragment implements AudioListAdapter.onItemListClick {
    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView audioView;
    private File[] allFiles;
    private AudioListAdapter audioListAdapter;
    private MediaPlayer mediaPlayer;
    private File fileToPlay;
    private SeekBar seekBar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    //UI elements
    private ImageView playBtn;
    private TextView playerFileName;
    private TextView playerHeaderName;
    private boolean isPlaying = false;

    public audio_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioView = view.findViewById(R.id.audio_view);
        playBtn = view.findViewById(R.id.imageView6);
        playerFileName = view.findViewById(R.id.textView8);
        playerHeaderName = view.findViewById(R.id.textView6);
        seekBar = view.findViewById(R.id.seekBar);
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(allFiles, this);
        audioView.setHasFixedSize(true);
        audioView.setLayoutManager(new LinearLayoutManager(getContext()));
        audioView.setAdapter(audioListAdapter);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == bottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    pauseAudio();

                } else {
                    if (fileToPlay == null) {
                        resumeAudio();
                    }
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                   pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }

            }
        });
    }

    @Override
    public void setOnClickListener(File file, int position) {
        fileToPlay = file;
        if (isPlaying) {
            stopAudio();
            playAudio(fileToPlay);

        } else {
            if (fileToPlay != null) {
                playAudio(fileToPlay);
            }
        }
    }


        private void pauseAudio () {
            mediaPlayer.pause();
            playBtn.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play, null));
            isPlaying = false;
            seekbarHandler.removeCallbacks(updateSeekbar);
        }

        private void resumeAudio () {
            mediaPlayer.start();
            playBtn.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_pause, null));
            isPlaying = true;
            updateRunnable();
            seekbarHandler.postDelayed(updateSeekbar, 0);
        }
        private void playAudio (File fileToPlay){
            //plays the audio
            mediaPlayer = new MediaPlayer();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            try {
                mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            playBtn.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_pause, null));
            playerFileName.setText(fileToPlay.getName());
            playerHeaderName.setText("Playing");
            isPlaying = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopAudio();
                    playerHeaderName.setText("Finished");
                }
            });
            seekBar.setMax(mediaPlayer.getDuration());
            seekbarHandler = new Handler();
            updateRunnable();
            seekbarHandler.postDelayed(updateSeekbar, 0);

        }

        private void updateRunnable () {
            updateSeekbar = new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    seekbarHandler.postDelayed(this, 500);
                }
            };
        }

        private void stopAudio () {
            //stops the audio
            isPlaying = false;
            mediaPlayer.stop();
            playBtn.setImageDrawable(getActivity().getResources().getDrawable(android.R.drawable.ic_media_play, null));
            playerHeaderName.setText("Stopped");
            seekbarHandler.removeCallbacks(updateSeekbar);
        }

        @Override
        public void onStop () {
            super.onStop();
            stopAudio();
        }

}
