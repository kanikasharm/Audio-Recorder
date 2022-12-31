package com.example.audio_recorder;

import static java.security.AccessController.checkPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class record_list extends Fragment {
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private AudioListAdapter audioListAdapter;
    private TextView fileNameText;
    private String getPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    private Chronometer timer;
    public record_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.imageView4);
        recordBtn = view.findViewById(R.id.imageView3);
        timer = view.findViewById(R.id.chronometer2);
        fileNameText = view.findViewById(R.id.textView);
        recordBtn.setOnClickListener(new View.OnClickListener() {


                                         @Override
                                         public void onClick(View view) {
                                             switch (view.getId()) {

                                                 case R.id.imageView3:
                                                     if (isRecording) {
                                                         //stop recording
                                                         stopRecording();
                                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                             recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.pinks, null));
                                                         }
                                                         isRecording = false;
                                                     } else {
                                                         //start recording

                                                         if (checkPermission()) {
                                                             startRecording();

                                                             isRecording = true;
                                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                 recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.redmic, null));
                                                             }
                                                         }
                                                         break;
                                                     }
                                             }
                                         }


                                         private void startRecording() {
                                             timer.setBase(SystemClock.elapsedRealtime());
                                             timer.start();
                                             String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
                                             SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
                                             Date now = new Date();


                                             recordFile = "Recording_" + formatter.format(now) + ".3gp";
                                             fileNameText.setText("Recording, file name: " + recordFile);

                                             mediaRecorder = new MediaRecorder();
                                             mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                             mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                             mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
                                             mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                                             try {
                                                 mediaRecorder.prepare();
                                             } catch (IOException e) {
                                                 e.printStackTrace();
                                             }

                                             mediaRecorder.start();
                                         }

                                         private void stopRecording() {
                                             fileNameText.setText("Recording Stopped, file saved: " + recordFile);
                                             timer.stop();
                                             mediaRecorder.stop();
                                             mediaRecorder.release();
                                             mediaRecorder = null;
                                         }


                                         private boolean checkPermission() {
                                             if (ActivityCompat.checkSelfPermission(getContext(), getPermission) == PackageManager.PERMISSION_GRANTED) {
                                                 return true;
                                             } else {
                                                 ActivityCompat.requestPermissions(getActivity(), new String[]{getPermission}, PERMISSION_CODE);
                                                 return false;

                                             }
                                         }
                                     });

      listBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              switch (view.getId()) {

                  case R.id.imageView4:
                      if (isRecording) {
                          AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                          alertDialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i) {
                                  fileNameText.setText("Recording Stopped, file saved: " + recordFile);
                                  timer.stop();
                                  mediaRecorder.stop();
                                  mediaRecorder.release();
                                  mediaRecorder = null;
                                  navController.navigate(R.id.action_record_list_to_audio_list);
                              }
                          });
                          alertDialog.setNegativeButton("CANCEL", null);
                          alertDialog.setTitle("Audio still recording");
                          alertDialog.setMessage("Are you sure, you want to stop recording?");
                          alertDialog.create().show();
                      } else {
                          navController.navigate(R.id.action_record_list_to_audio_list);
                      }
                      break;
              }
          }
      });

    }

}

