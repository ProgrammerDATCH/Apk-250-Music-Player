package com.ntakomisiyo.musicplayer250;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        runtimePermission();
    }

    public void runtimePermission()
    {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

//    public ArrayList<File> findSong(File file)
//    {
//        ArrayList<File> arrayList = new ArrayList<>();
//        File[] files = file.listFiles();
//
//        if(files == null)
//        {
//            return arrayList;
//        }
//
//        for(File singleFile : files)
//        {
//            if(singleFile.isDirectory() && !singleFile.isHidden())
//            {
//                findSong(singleFile);
//            }
//            else
//            {
//                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
//                {
//                    arrayList.add(singleFile);
//                }
//            }
//        }
//        return arrayList;
//    }

    public void displaySong()
    {
//        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        final ArrayList<File> mySongs = getAllAudioFiles();
        items = new String[mySongs.size()];
        for(int i=0;i<mySongs.size();i++)
        {
            items[i]=mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySongs)
                                .putExtra("songname", songName)
                                .putExtra("pos", i)
                        );

            }
        });
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewSong = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView txtSong = viewSong.findViewById(R.id.txtSong);
            txtSong.setSelected(true);
            txtSong.setText(items[i]);

            return viewSong;
        }
    }

    public ArrayList<File> getAllAudioFiles() {
        ArrayList<File> arrayList = new ArrayList<>();
        String[] storageDirectories = {"/storage/emulated/0/", "/storage/sdcard1/"};
        for (String directory : storageDirectories) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                arrayList.addAll(getAudioFilesInDirectory(file));
            }
        }
        return arrayList;
    }

    private ArrayList<File> getAudioFilesInDirectory(File directory) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    arrayList.addAll(getAudioFilesInDirectory(file));
                } else {
                    String fileName = file.getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                    if (fileExtension.equalsIgnoreCase("mp3") || fileExtension.equalsIgnoreCase("m4a")) {
                        arrayList.add(file);
                    }
                }
            }
        }
        return arrayList;
    }


}