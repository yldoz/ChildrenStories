package com.example.alamaudioplayer;

import static com.example.alamaudioplayer.MainRecyclerAdapter.context;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.inputmethodservice.Keyboard;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.RenderProcessGoneDetail;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alamaudioplayer.model.RepoList;
import com.example.alamaudioplayer.model.Rows;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "main_act";
    private RecyclerView recyclerView;
    JSONPlaceHolder jsonPlaceholder;
    List<File> filesName = new ArrayList<>();
    List<File> imageName = new ArrayList<>();
    String lang = "english";
    String langCode = "en";
    TextView tverror;
    int age;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        erroLayout = findViewById(R.id.errorLiear);
        tverror = findViewById(R.id.txtErrormsg);
        trygain = findViewById(R.id.tryAgain);
        age = prefManager.getInt("age");
        lang = prefManager.getString("lang", "english");
        langCode = prefManager.getString("langcode", "en");
        dialog = new ProgressDialog(this);

        dialog.setMessage("Loading...");


        Dexter.withContext(this)
                .withPermissions(
                        // Manifest.permission.WRITE_SETTINGS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            getFilesFromDirectory();
                            //showAlertDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                })
                .check();

        recyclerView = findViewById(R.id.recycerlview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestStories();
        trygain.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (findMatch) {
                    requestStories();
                    getFilesFromDirectory();
                } else {
                    PrefManager prefManager = new PrefManager(MainActivity.this);
                   prefManager.setBoolean("save",false);
                    tverror.setText("Unable to find stories according to birthday or language, Try to change one of them.");
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }
        });


        // Toast.makeText(this, "brightness "+brightness, Toast.LENGTH_SHORT).show();
    }
/*
    private void showAlertDialog() {
        if (prefManager.getBoolean("wst")) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Control Brightness!");
        builder.setMessage("To control the automatic brightness of device. App need to write setting permission.");
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPerm();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Don't Ask again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                prefManager.setBoolean("wst", true);
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }*/

   /* private void requestPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //contorlBrightneess();
        }
    }
/*

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void contorlBrightneess() {
        try {
            Date date = new Date();   // given date
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);   // assigns calendar to given date
            int time = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
            int hour = calendar.get(Calendar.HOUR);
            Log.d(TAG, "onCreate: time : hour " + time + hour);
            if (!Settings.System.canWrite(this)) {
                return;
            }
            if(time >= 18 || time < 6 ){
            int brightness =
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, 0);
            float per = (brightness / 100);
            float limit = 20 * per;
             if (brightness > limit) {
                double d = brightness - limit;
                double inc = d / 14;
                final int[] currentBrith = {(int) inc};
                new CountDownTimer(1500, 100) {

                    @Override
                    public void onTick(long l) {
                        int fb = (int) d - currentBrith[0];

                        if (Settings.System.canWrite(MainActivity.this)) {

                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, fb);
                            currentBrith[0] = currentBrith[0] + (int) inc;
                        }

                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();
            }
            }else {
                int brightness =
                        Settings.System.getInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, 0);

                float per = (brightness / 100);
                float full = 50 * per;
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, (int) full);
                */
/* if (brightness > limit) {
                    double d = brightness - limit;
                    double inc = d / 14;
                    Log.d(TAG, "onCreate: inc " + inc);
                    final int[] currentBrith = {(int) inc};
                    new CountDownTimer(1500, 100) {

                        @Override
                        public void onTick(long l) {
                            Log.d(TAG, "onTick:d " + d);
                            int fb = (int) d - currentBrith[0];
                            Log.d(TAG, "onTick: fb " + fb);
                            if (Settings.System.canWrite(MainActivity.this)) {

                                Settings.System.putInt(getContentResolver(),
                                        Settings.System.SCREEN_BRIGHTNESS, fb);
                                currentBrith[0] = currentBrith[0] + (int) inc;
                                Log.d(TAG, "onTick: " + currentBrith[0]);
                            }

                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();
                }*//*

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    LinearLayout erroLayout;
    TextView trygain;
    MainRecyclerAdapter adapter;

    private void requestStories() {
        recyclerView.setVisibility(View.VISIBLE);
        erroLayout.setVisibility(View.GONE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceholder = retrofit.create(JSONPlaceHolder.class);
        Call<RepoList> call = jsonPlaceholder.getMainList();
        dialog.show();
        dialog.setCancelable(false);
        call.enqueue(new Callback<RepoList>() {
            @Override
            public void onResponse(Call<RepoList> call, Response<RepoList> response) {
                Log.d(TAG, "onResponse: " + response.toString());
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                RepoList post = response.body();
                if (post.getRows() == null || post.getRows().size() == 0) {
                    return;
                }
                remoteList = currentList(post.getRows());
                List<Rows> rows = filterList();
                Log.d(TAG, "onResponse: " + rows.size());
                Collections.shuffle(rows);
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter = new MainRecyclerAdapter(rows, MainActivity.this, new PlayerListener() {
                    @Override
                    public void noDataFound() {
                        erroLayout.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void dataFound() {
                        erroLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });

                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<RepoList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
                Log.d(TAG, "onFailure: " + call.request().toString());
                MainRecyclerAdapter adapter = new MainRecyclerAdapter(localList, MainActivity.this, new PlayerListener() {
                    @Override
                    public void noDataFound() {
                        erroLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void dataFound() {
                        erroLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });

                recyclerView.setAdapter(adapter);
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    List<Rows> remoteList = new ArrayList<>();
    List<Rows> localList = new ArrayList<>();

    private void getAllLang() {
        SortedSet<String> allLanguages = new TreeSet<String>();
        String[] languages = Locale.getISOLanguages();
        for (int i = 0; i < languages.length; i++) {
            Locale loc = new Locale(languages[i]);
            Log.d(TAG, "getAllLang: " + loc.getLanguage());
            allLanguages.add(loc.getDisplayLanguage());
        }
    }

    ProgressDialog dialog;

    boolean findMatch = true;

    private List<Rows> currentList(List<Rows> currentList) {
        List<Rows> filterList = new ArrayList<>();
        try {
            for (Rows row : currentList) {
                int fa = 0;
                int ta = 0;
                String l = "english";
                if (row.getAgeFrom().isEmpty()) {
                    fa = 0;
                } else {
                    fa = Integer.parseInt(row.getAgeFrom());
                }
                if (row.getAgeTo().isEmpty()) {
                    ta = 0;
                } else {
                    ta = Integer.parseInt(row.getAgeTo());
                }
                if (row.getLanguage().isEmpty()) {
                } else {
                    l = row.getLanguage().toLowerCase(Locale.ROOT);
                }
                Log.d(TAG, "currentList: " + l.equals(lang));
                Log.d(TAG, "currentList:lang " + l);
                Log.d(TAG, "currentList: " + langCode);
                Log.d(TAG, "age: " + age);
                Log.d(TAG, "fa: " + fa);
                Log.d(TAG, "ta: " + ta);
                if ((age >= fa && age <= ta) && (l.equals(lang) || l.equals(langCode))) {
                    findMatch = true;
                    filterList.add(row);
                } else {
                    findMatch = false;
                }

            }
            return filterList;
        } catch (Exception e) {
            e.printStackTrace();
            return currentList;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("de_mai", "onPause: ");
        if (adapter != null) {
            adapter.screenOff();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.onActivtyDestory();
        }
    }

    private List<Rows> filterList() {
        List<Rows> finalList = new ArrayList<>();
        finalList.clear();
        Log.d(TAG, "filterList: " + localList.size());
        if (localList.size() > 0) {
            for (Rows item : remoteList) {
                boolean b = isExistFile(item.getStoryName());
                Log.d(TAG, "isContainFile: " + b);
                if (!b) {
                    Log.d(TAG, "filterList: added intofinal");
                    finalList.add(item);
                }
            }
            finalList.addAll(localList);
        } else {
            finalList.addAll(remoteList);
        }
        for (Rows rows : localList) {
            Log.d(TAG, "localFiler: " + rows.getStoryName());
        }
        return finalList;
    }

    private boolean isExistFile(String name) {
        String path = MainRecyclerAdapter.getFilePath(MainActivity.this);
        File file = new File(path, name + ".mp3");
        Log.d(TAG, "isContainFile: " + file.getPath() + " : " + file.exists());
        if (file.exists())
            return true;
        else
            return false;
    }

    // public Rows(String id, String storyName, String language, String ageFrom, String ageTo, String audio, String storypicture, String producerID, String verified, String status, String date) {
//
    private void getFilesFromDirectory() {

        String path = MainRecyclerAdapter.getFilePath(MainActivity.this);
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        if (files.length > 0) {
            for (File file : files) {
                localList.add(new Rows("0", file.getName().replace(".mp3", ""), "default", "12", "18", file.getAbsolutePath(), file.getAbsolutePath(), "8df897df89d", "1", "1", String.valueOf(file.lastModified())));
            }
        }
        //filesName.addAll(Arrays.asList(files));
    }

    private void getImagesFromDirectory() {
        String path = MainRecyclerAdapter.getImagePath(this);
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        imageName.addAll(Arrays.asList(files));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, new SettingsContentObserver(this, new Handler()));

    }
    public class SettingsContentObserver extends ContentObserver {
        private AudioManager audioManager;

        public SettingsContentObserver(Context context, Handler handler) {
            super(handler);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolumePercentage = 100 * currentVolume / maxVolume;
            if (currentVolumePercentage < 20) {
                Log.d("de_naqvi", "onChange: volume is:" + currentVolumePercentage);
                MainRecyclerAdapter.stopMedia();
            } else {
                Log.d("de_naqvi", "onChange: volume is:" + currentVolumePercentage);
                MainRecyclerAdapter.startMedia();
            }
            Log.d("de_naqvi", "Volume now " + currentVolume);
            Log.d("de_naqvi", "max now " + maxVolume);
        }
    }

    @Override
    protected void onStop() {
        getApplicationContext().getContentResolver().unregisterContentObserver(new SettingsContentObserver(this, new Handler()));
        super.onStop();
    }
}