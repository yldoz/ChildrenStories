package com.example.alamaudioplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.example.alamaudioplayer.model.Rows;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.thanel.swipeactionview.SwipeActionView;
import me.thanel.swipeactionview.SwipeGestureListener;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder> {
    List<Rows> postList;
    static Context context;
    static MediaPlayer mediaPlayer;
    String file_url;
    private static final String TAG = "de_main";
    PlayerListener playerListener;
    ExecutorService service = Executors.newFixedThreadPool(4);
    Handler handler = new Handler(Looper.getMainLooper());

    public static void stopMedia() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
        if (lastSelected != null) {
            lastSelected.setBackgroundResource(R.drawable.ic_pause);
        }

    }

    public static void startMedia() {
        if (mediaPlayer != null) {
            try {
                // mediaPlayer.setDataSource(Constants.CURRENT_PLAYING_URL);
                //  mediaPlayer.prepare();
            } catch (Exception e) {
                Log.d(TAG, "startMedia: " + e.getMessage());
                e.printStackTrace();
            }
            mediaPlayer.start();
            if (lastSelected != null) {
                lastSelected.setBackgroundResource(R.drawable.ic_play_green_bg);
            }
        }
    }

    public MainRecyclerAdapter(List<Rows> postList, Context context, PlayerListener listener) {
        this.postList = postList;
        this.context = context;
        mediaPlayer = new MediaPlayer();
        PRDownloader.initialize(context);
        this.playerListener = listener;
        if (postList.size() > 0) {
            listener.dataFound();
        } else {
            listener.noDataFound();
        }
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_recycler_item, parent, false);
        return new MainViewHolder(view);
    }

    int currentSelect = -1;
    static FrameLayout lastSelected;

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Rows mainRow = postList.get(position);
        Log.d(TAG, "onBindViewHolder: " + mainRow.getLanguage());
        File file = new File(getFilePath(context), mainRow.getStoryName() + ".mp3");
        File mp3file = new File(getImagePath(context), mainRow.getStoryName() + ".png");
        if (currentSelect == position) {
            lastSelected = holder.frameLayout;
            holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
        } else {
            holder.frameLayout.setBackgroundResource(R.drawable.ic_pause);
        }
        if (mp3file.exists()) {
            Log.d(TAG, "mp3: " + mp3file.getPath());
            Glide.with(context).load(mp3file.getPath()).into(holder.image);
        } else
            Glide.with(context).load(Constants.IMAGE_URL + mainRow.getStorypicture()).into(holder.image);

        holder.swipeActionView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: onclik");
            if (lastSelected == null) {
                lastSelected = holder.frameLayout;
            } else {
                lastSelected.setBackgroundResource(R.drawable.ic_pause);
                lastSelected = holder.frameLayout;
            }
            currentSelect = position;


            if (!file.exists()) {
                Log.d(TAG, "mp4: " + file.getPath());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        file_url = Constants.AUDIO_URL + mainRow.getAudio();
                        PRDownloader.download(file_url, getFilePath(context), mainRow.getStoryName() + ".mp3")
                                .build()
                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {

                                    }
                                })
                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {

                                    }
                                })
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
                                        Log.d(TAG, "onProgress: " + progress.toString());
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {

                                    }

                                    @Override
                                    public void onError(Error error) {

                                    }
                                });
                    }
                }).start();


            } else {
                file_url = file.getPath();
            }
            if (!mp3file.exists()) {


                PRDownloader.download(Constants.IMAGE_URL + mainRow.getStorypicture(), getImagePath(context), mainRow.getStoryName() + ".png")
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {

                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                Log.d(TAG, "onProgress: " + progress.toString());
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                            }

                            @Override
                            public void onError(Error error) {

                            }
                        });
            }

            if (Constants.CURRENT_PLAYING_URL.equals(file_url)) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    holder.frameLayout.setBackgroundResource(R.drawable.ic_pause);
                } else {
//                    if (mediaPlayer.getCurrentPosition() > 30000) {
//                        Log.d(TAG, "onSwipedRight: pase " + (mediaPlayer.getCurrentPosition() - 30000));
//                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 30000);
//                    } else {
//                        mediaPlayer.seekTo(0);
//                    }
                    mediaPlayer.start();
                    holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
                }

            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
                Constants.CURRENT_PLAYING_URL = file_url;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                //service.execute(() -> {
                handler.post(() -> {
                    try {

                        Toast.makeText(context, "Story is playing...", Toast.LENGTH_SHORT).show();
                        mediaPlayer.setDataSource(Constants.CURRENT_PLAYING_URL);


                        mediaPlayer.prepare();// might take long! (for buffering, etc)
                        mediaPlayer.start();


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                });

            }

        });

        holder.swipeActionView.setSwipeGestureListener(new SwipeGestureListener() {
            @Override
            public boolean onSwipedLeft(@NonNull SwipeActionView swipeActionView) {

                int twominut = 60 * 1000;
                try {
                    if (mediaPlayer.getDuration() > twominut) {
                        Log.d(TAG, "onSwipedLeft: seek " + (mediaPlayer.getCurrentPosition() - twominut));
                        int m = mediaPlayer.getCurrentPosition() - twominut;
                        if (m > 0)
                            mediaPlayer.seekTo(m);
                        else {
                            mediaPlayer.seekTo(0);
                        }
                    } else {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onSwipedLeft: " + e.getMessage());
                }

                return true;
            }

            @Override
            public boolean onSwipedRight(@NonNull SwipeActionView swipeActionView) {
                Log.d(TAG, "onSwipedLeft: " + mediaPlayer.getDuration());
                Log.d(TAG, "onSwipedLeft: " + mediaPlayer.getCurrentPosition());
                int twominut = 60 * 1000;
                try {
                    if (mediaPlayer.getDuration() > twominut) {
                        Log.d(TAG, "onSwipedLeft: seek " + (mediaPlayer.getCurrentPosition() - twominut));
                        int m = mediaPlayer.getCurrentPosition() + twominut;
                        if (m > 0)
                            mediaPlayer.seekTo(m);
                        else {
                            mediaPlayer.seekTo(0);
                        }
                    } else {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onSwipedLeft: " + e.getMessage());
                }


               /* Log.d(TAG, "onSwipedRight: ");
                if (lastSelected == null) {
                    lastSelected = holder.frameLayout;
                } else {
                    lastSelected.setBackgroundResource(R.drawable.ic_pause);
                    lastSelected = holder.frameLayout;
                }
                currentSelect = position;


                if (!file.exists()) {
                    Log.d(TAG, "mp4: " + file.getPath());


                    file_url = Constants.AUDIO_URL + mainRow.getAudio();
                    PRDownloader.download(file_url, getFilePath(context), mainRow.getStoryName() + ".mp3")
                            .build()
                            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                @Override
                                public void onStartOrResume() {

                                }
                            })
                            .setOnPauseListener(new OnPauseListener() {
                                @Override
                                public void onPause() {

                                }
                            })
                            .setOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {

                                }
                            })
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    Log.d(TAG, "onProgress: " + progress.toString());
                                }
                            })
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {

                                }

                                @Override
                                public void onError(Error error) {

                                }
                            });
                } else {
                    file_url = file.getPath();
                }
                if (!mp3file.exists()) {


                    PRDownloader.download(Constants.IMAGE_URL + mainRow.getStorypicture(), getImagePath(context), mainRow.getStoryName() + ".png")
                            .build()
                            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                @Override
                                public void onStartOrResume() {

                                }
                            })
                            .setOnPauseListener(new OnPauseListener() {
                                @Override
                                public void onPause() {

                                }
                            })
                            .setOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {

                                }
                            })
                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    Log.d(TAG, "onProgress: " + progress.toString());
                                }
                            })
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {

                                }

                                @Override
                                public void onError(Error error) {

                                }
                            });
                }

                if (Constants.CURRENT_PLAYING_URL.equals(file_url)) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        holder.frameLayout.setBackgroundResource(R.drawable.ic_pause);
                    } else {
                        if(mediaPlayer.getCurrentPosition() > 30000){
                            Log.d(TAG, "onSwipedRight: pase "+ (mediaPlayer.getCurrentPosition() - 30000));
                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 30000);
                        }else{
                            mediaPlayer.seekTo(0);
                        }
                        mediaPlayer.start();
                        holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
                    }

                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
                    Constants.CURRENT_PLAYING_URL = file_url;
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    try {
                        mediaPlayer.setDataSource(Constants.CURRENT_PLAYING_URL);
                        mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }*/
                return true;
            }

            @Override
            public void onSwipeLeftComplete(@NonNull SwipeActionView swipeActionView) {
                Log.d(TAG, "onSwipeLeftComplete: ");
            }

            @Override
            public void onSwipeRightComplete(@NonNull SwipeActionView swipeActionView) {
                Log.d(TAG, "onSwipeRightComplete: ");
            }
        });

    }
      /*  holder.itemView.setOnTouchListener(new OnSwipeTouchListener(context){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Log.d(TAG, "onSwipeRight: ");
                if (!mediaPlayer.isPlaying()){
                    if (lastSelected == null) {
                        lastSelected = holder.frameLayout;
                    } else {
                        lastSelected.setBackgroundResource(R.drawable.ic_pause);
                        lastSelected = holder.frameLayout;
                    }
                    currentSelect = position;

                    if (!file.exists()) {
                        Log.d(TAG, "mp4: " + file.getPath());

                        file_url = Constants.AUDIO_URL + mainRow.getAudio();
                        PRDownloader.download(file_url, getFilePath(context), mainRow.getStoryName() + ".mp3")
                                .build()
                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {

                                    }
                                })
                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {

                                    }
                                })
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
                                        Log.d(TAG, "onProgress: " + progress.toString());
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {

                                    }

                                    @Override
                                    public void onError(Error error) {

                                    }
                                });
                    } else {
                        file_url = file.getPath();
                    }
                    if (!mp3file.exists()) {


                        PRDownloader.download(Constants.IMAGE_URL + mainRow.getStorypicture(), getImagePath(context), mainRow.getStoryName() + ".png")
                                .build()
                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {

                                    }
                                })
                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {

                                    }
                                })
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {

                                    }
                                })
                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
                                        Log.d(TAG, "onProgress: " + progress.toString());
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {

                                    }

                                    @Override
                                    public void onError(Error error) {

                                    }
                                });
                    }

                    if (Constants.CURRENT_PLAYING_URL.equals(file_url)) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            holder.frameLayout.setBackgroundResource(R.drawable.ic_pause);
                        } else {
                            mediaPlayer.start();
                            holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
                        }

                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        holder.frameLayout.setBackgroundResource(R.drawable.ic_play_green_bg);
                        Constants.CURRENT_PLAYING_URL = file_url;
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );
                        try {
                            mediaPlayer.setDataSource(Constants.CURRENT_PLAYING_URL);
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mediaPlayer.start();
                    }
                }
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Log.d(TAG, "onSwipeLeft: ");
            }
        });

    }*/

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void screenOff() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            // mediaPlayer.stop();
            if (lastSelected != null)
                lastSelected.setBackgroundResource(R.drawable.ic_pause);

        }
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        FrameLayout frameLayout;
        SwipeActionView swipeActionView;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.song_image);
            frameLayout = itemView.findViewById(R.id.frame);
            swipeActionView = itemView.findViewById(R.id.swipe_both);

        }
    }

    public static String getImagePath(Context context) {
        String location = createPath() + "/" + Constants.IMAGE_PATH + "/";
        File file = new File(location);
        if (!file.exists()) {
            file.mkdir();
        }
        return location;
    }

    public static String getFilePath(Context x) {
        context = x;
        String location = createPath() + "/" + Constants.FILE_LOCATION + "/";
        File file = new File(location);
        if (!file.exists()) {
            file.mkdir();
        }
        return location;
    }

    public void onActivtyDestory() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

        }
    }

    public static String createPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File[] directory = new File[0];
            directory = context.getExternalMediaDirs();
            for (File i : directory) {
                if (i.getName().contains(context.getPackageName())) {
                    return i.getAbsolutePath();
                }
            }
        } else {
            return getAndroidImageFolder().getAbsolutePath();
        }
        return getAndroidImageFolder().getAbsolutePath();
    }

    public static File getAndroidImageFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

}
