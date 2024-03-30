package com.ntakomisiyo.musicplayer250;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button btnPlay, btnNext, btnPrevious, btnFastForward, btnFastBackward;
    TextView txtSongName, txtSongStart, txtSongEnd;
    SeekBar seekMusicBar;
    BarVisualizer barVisualizer;
    ImageView imageView;
    static String songName;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(barVisualizer != null)
        {
            barVisualizer.release();
        }
        super.onDestroy();
    }

    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("250 Music Playing...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnFastBackward = findViewById(R.id.btnFastBackward);
        btnFastForward = findViewById(R.id.btnFastForward);

        txtSongName = findViewById(R.id.txtSong);
        txtSongStart = findViewById(R.id.txtSongStart);
        txtSongEnd = findViewById(R.id.txtSongEnd);

        seekMusicBar = findViewById(R.id.seekBar);
        barVisualizer = findViewById(R.id.barVisualizer);

        imageView = findViewById(R.id.imgView);

        if(mediaPlayer != null)
        {
            mediaPlayer.start();
            mediaPlayer.release();
        }
        
    try {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songname");
        position = bundle.getInt("pos", 0);

        String action = intent.getStringExtra("action");
        if (action != null &&  action.equals("play")) {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            else
            {
                mediaPlayer.start();
            }
        }

        txtSongName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songName = mySongs.get(position).getName();
        txtSongName.setText(songName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        afterSong();

//            showNotification(songName);
    }
    catch (Exception e)
    {
        Toast.makeText(this, "Main Error!", Toast.LENGTH_SHORT).show();
    }



        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                    showNotification(songName);
                }
                else
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    showNotification(songName);
                    TranslateAnimation moveAnim = new TranslateAnimation(-25,25,-25,25);
                    moveAnim.setInterpolator(new AccelerateInterpolator());
                    moveAnim.setDuration(600);
                    moveAnim.setFillEnabled(true);
                    moveAnim.setFillAfter(true);
                    moveAnim.setRepeatMode(Animation.REVERSE);
                    moveAnim.setRepeatCount(1);
                    imageView.startAnimation(moveAnim);

                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    position = ((position+1)%mySongs.size());
                    Uri uri = Uri.parse(mySongs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    songName = mySongs.get(position).getName();
                    txtSongName.setText(songName);
                    mediaPlayer.start();
                    startAnimation(imageView, 360f);
                    afterSong();
                }
                catch (Exception e)
                {
                    Toast.makeText(PlayerActivity.this, "Next Error!", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):position-1;
                Uri uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                songName = mySongs.get(position).getName();
                txtSongName.setText(songName);
                mediaPlayer.start();
                startAnimation(imageView, -360f);

                afterSong();

            }
        });

        btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnFastBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });


    }

    public void startAnimation(View view, Float degree)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f,degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    public String createTime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time = time+min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }

    public void afterSong()
    {
        try {
            btnPlay.setBackgroundResource(R.drawable.ic_pause);
            showNotification(songName);
            int audioSessionId = mediaPlayer.getAudioSessionId();
            if (audioSessionId != -1) {
                barVisualizer.setAudioSessionId(audioSessionId);
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    btnNext.performClick();
                }
            });

            updateSeekBar = new Thread() {
                @Override
                public void run() {
                    int totalDuration = mediaPlayer.getDuration();
                    int currentPosition = 0;
                    while (currentPosition < totalDuration) {
                        try {
                            sleep(500);
                            currentPosition = mediaPlayer.getCurrentPosition();
                            seekMusicBar.setProgress(currentPosition);
                        } catch (InterruptedException | IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            seekMusicBar.setMax(mediaPlayer.getDuration());
            updateSeekBar.start();
            seekMusicBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.MULTIPLY);
            seekMusicBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.SRC_IN);

            seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });

            String endTime = createTime(mediaPlayer.getDuration());
            txtSongEnd.setText(endTime);

            final Handler handler = new Handler();
            final int delay = 500;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String currentTime = createTime(mediaPlayer.getCurrentPosition());
                    txtSongStart.setText(currentTime);
                    handler.postDelayed(this, delay);
                }
            }, delay);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "After Error!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showNotification(String songName1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("250MusicPlayer", "250 Music Player", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

////         create pending intent for the notification buttons
//        Intent playIntent = new Intent(this, PlayerManager.class);
//        playIntent.putExtra("action", "play");
//        playIntent.setAction("play");
//        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);
////
//        Intent nextIntent = new Intent(this, PlayerService.class);
//        nextIntent.putExtra("action", "next");
//        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);
//
//        Intent previousIntent = new Intent(this, PlayerService.class);
//        previousIntent.putExtra("action", "previous");
//        PendingIntent previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        // create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "250MusicPlayer")
                .setSmallIcon(R.mipmap.music_note_edit)
                .setContentTitle(songName)
                .setContentText(mediaPlayer.isPlaying() ? "Playing..." : "Paused...");
//                .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent)
//                .addAction(mediaPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play, mediaPlayer.isPlaying() ? "Pause" : "Play", playPendingIntent);
//                .addAction(R.drawable.ic_next, "Next", nextPendingIntent);

        // create the notification manager and show the notification
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

//    public static MediaPlayer getMediaPlayer() {
//        return mediaPlayer;
//    }


}