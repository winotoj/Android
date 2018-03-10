package com.ipd10.imgdl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    EditText etUrl;
    Button btDownload, btCancel;
    TextView tvProgress;
    ProgressBar pbProgress;
    ImageView ivResult;
    public static final String URL_PREFERENCES = "UrlPrefs";
    public static final String Url = "urlKey";
    SharedPreferences sharedPreferences;

    AsyncImgFileDownloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = findViewById(R.id.etUrl);
        btDownload = findViewById(R.id.btDownload);
        btCancel = findViewById(R.id.btCancel);
        tvProgress = findViewById(R.id.tvProgress);
        pbProgress = findViewById(R.id.pbProgress);
        ivResult = findViewById(R.id.ivResult);
        sharedPreferences = getSharedPreferences(URL_PREFERENCES, Context.MODE_PRIVATE);

        pbProgress.setMax(100);

        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    URL url = new URL(etUrl.getText().toString());
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putString(Url,etUrl.getText().toString());
                    Toast.makeText(MainActivity.this, etUrl.getText().toString(), Toast.LENGTH_SHORT).show();
                   editor.commit();
                    downloader = new AsyncImgFileDownloader();
                    downloader.execute(url); // this is where 2nd thread is started - doInBackground() executes


                } catch (MalformedURLException e) {
                    Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();

                /*
                if (downloader == null) {
                    try {
                        URL url = new URL(etUrl.getText().toString());
                        downloader = new AsyncImgFileDownloader();
                        downloader.execute(url); // this is where 2nd thread is started - doInBackground() executes
                    } catch (MalformedURLException e) {
                        Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    downloader.cancel(true);
                }
                */
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloader.cancel(true);
            }
        });

    }
    class AsyncImgFileDownloader extends AsyncTask<URL, Long, Bitmap> {

        @Override
        protected void onProgressUpdate(Long ... progress) {
            // this method is called every time you execute publishProgress() in doInBackground()
            long downloaded = progress[0];
            long total = progress[1];
            long perc = progress[2];
            // Display updated information in the UI
            tvProgress.setText(String.format("Downloaded %d / %d (%d%%)", downloaded, total, perc));

                pbProgress.setProgress((int) perc);



        }


        @Override
        protected void onPreExecute() {
            Log.v(TAG,"onPreExecute");
            btDownload.setEnabled(false);
            btCancel.setEnabled(true);
            //btStartStop.setText("Cancel Download");
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.v(TAG, "onPostExecute");
            if(result == null){
                Toast.makeText(MainActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
            }
            ivResult.setImageBitmap(result);
           // tvProgress.setText(result == null? "DOWNLOAD FAILED" : result);
            btDownload.setEnabled(true);
            btCancel.setEnabled(false);
          //  btStartStop.setText("Download");
            downloader = null;
        }

        @Override
        protected void onCancelled() {
            Log.v(TAG, "onCancelled");
            tvProgress.setText("DOWNLOAD CANCELLED");
            btCancel.setEnabled(false);
            btDownload.setEnabled(true);
         //   tvResult.setText("DOWNLOAD CANCELLED");
          //  btStartStop.setText("Download");
            downloader = null;
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            Log.v(TAG, "doInBackground() started");
            StringBuilder sb = new StringBuilder();
            InputStream inputStream = null;
            if (urls.length != 1) {
                throw new RuntimeException("doInBackground requires one URL exactly");
            }
            URL url = urls[0];

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int httpCode = connection.getResponseCode();

                Log.d(TAG, String.format("HTTP code %d, GET %s", httpCode, url));
                if (httpCode / 100 != 2) {
                    Log.v(TAG, "HTTP code not 2xx, returning null");
                    return null;
                }
                String mimeType = connection.getContentType();
                if(!(mimeType.equals("image/png") || mimeType.equals("image/jpeg")) ){
                    Log.v(TAG, "Mime type invalid " + mimeType +", returning null");
                    return null;
                }
                Log.v(TAG, "Mime type is " + mimeType +", returning null");
                inputStream = connection.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // -1 if length is not known
                long totalContentLength = connection.getContentLength();
                byte [] buffer = new byte[1024];
                int singleReadLength;
                long downloadedLength = 0;
                while ((singleReadLength = inputStream.read(buffer)) != -1) {
                    if (isCancelled()) {
                        return null;
                    }
                    downloadedLength += singleReadLength;
                    long percentage = 100 * downloadedLength / totalContentLength;
                    percentage = totalContentLength < 0 ? -1 : percentage;
                    publishProgress(downloadedLength, totalContentLength, percentage);
                    out.write(buffer, 0, singleReadLength);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) { }
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(out.toByteArray() , 0, out.toByteArray().length);
                Log.v(TAG, "about to return bitmap=" + bitmap);
                return bitmap;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
