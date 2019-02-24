package com.example.saquibmusicplayer;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Service;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    long queueid;
    DownloadManager downloadManager;
    MediaPlayer player;
    static final int PERMISSIONS_CODE=1000;
    View v;
    Button play, download, stop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v=this.getLayoutInflater().inflate(R.layout.activity_main,null);
        play = v.findViewById(R.id.Play);
        download = v.findViewById(R.id.Download);
        stop = v.findViewById(R.id.Stop);
        setContentView(v);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+"Sample" + ".mp3").getAbsolutePath());

        if(!file.exists())
        {
            play.setEnabled(false);
            stop.setEnabled(false);
        }
        else
        {
            stop.setEnabled(false);
        }
    }

    public void Download(View v)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,PERMISSIONS_CODE);
            }
            else
            {
                StartDownloading();
            }
        }

        else
        {
            StartDownloading();
        }
    }

    public void Play(View v)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+"Sample" + ".mp3").getAbsolutePath());
        if(!file.exists())
        {
            Toast.makeText(this,"The music file does'nt exists, Please download the file first",Toast.LENGTH_LONG).show();
            return;
        }

        if(player==null)
        {
            player = MediaPlayer.create(this,Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+"Sample" + ".mp3").getAbsolutePath()));
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Stop_player();
                }
            });
        }
        if(player!=null&&player.isPlaying()==false)
        {
            player.start();
            stop.setEnabled(true);
            play.setEnabled(false);
        }
    }

    public void Stop(View v)
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+"Sample" + ".mp3").getAbsolutePath());
        if(!file.exists())
        {
            Toast.makeText(this,"The music file does'nt exists, Please download the file first",Toast.LENGTH_LONG).show();
            return;
        }
        Stop_player();
    }

    public void Stop_player()
    {
        if(player!=null)
        {
            player.release();
            player=null;
            Toast.makeText(this,"Player is stoped is successfully", Toast.LENGTH_LONG).show();
            stop.setEnabled(false);
            play.setEnabled(true);
        }
    }

    public void StartDownloading()
    {
        if(!isConnected())
        {
            Toast.makeText(this,"Please make sure that your phone is connected to network",Toast.LENGTH_LONG).show();
        }
        else
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+"Sample" + ".mp3").getAbsolutePath());

            if(!file.exists())
            {
                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://ia802508.us.archive.org/5/items/testmp3testfile/mpthreetest.mp3"));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("Downloading");
                request.setDescription("Downloading File");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Sample" + ".mp3");
                queueid = downloadManager.enqueue(request);
                Toast.makeText(this, "Downloading...", Toast.LENGTH_LONG).show();
                play.setEnabled(true);
            }

            else
            {
                Toast.makeText(this,"File is already Downloaded",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSIONS_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    StartDownloading();
                }
                else
                {
                    Toast.makeText(this,"Sorry, permissions Denied",Toast.LENGTH_LONG).show();
                }
        }
    }

    public boolean isConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info!=null)
            {
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Stop_player();
    }
}
