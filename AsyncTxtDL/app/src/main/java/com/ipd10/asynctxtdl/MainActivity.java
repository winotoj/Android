package com.ipd10.asynctxtdl;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    TextView tvProgress, tvResult;
    EditText etUrl;
    Button btStartStop;

    AsyncTextFileDownloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvProgress = findViewById(R.id.tvProgress);
        tvResult = findViewById(R.id.tvResult);
        etUrl = findViewById(R.id.etUrl);
        btStartStop = findViewById(R.id.btStartStop);

        btStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (downloader == null) {
                    try {
                        URL url = new URL(etUrl.getText().toString());
                        downloader = new AsyncTextFileDownloader();
                        downloader.execute(url); // this is where 2nd thread is started - doInBackground() executes
                    } catch (MalformedURLException e) {
                        Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    downloader.cancel(true);
                }
            }
        });

    }

    class AsyncTextFileDownloader extends AsyncTask<URL, Long, String> {

        @Override
        protected void onProgressUpdate(Long ... progress) {
            // this method is called every time you execute publishProgress() in doInBackground()
            long downloaded = progress[0];
            long total = progress[1];
            long perc = progress[2];
            // Display updated information in the UI
            tvProgress.setText(String.format("Downloaded %d / %d (%d%%)", downloaded, total, perc));
        }

        @Override
        protected void onPreExecute() {
            Log.v(TAG,"onPreExecute");
            btStartStop.setText("Cancel Download");
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "onPostExecute");
            tvResult.setText(result == null ? "DOWNLOAD FAILED" : result);
            btStartStop.setText("Download");
            downloader = null;
        }

        @Override
        protected void onCancelled() {
            Log.v(TAG, "onCancelled");
            tvResult.setText("DOWNLOAD CANCELLED");
            btStartStop.setText("Download");
            downloader = null;
        }

        @Override
        protected String doInBackground(URL... urls) {
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
                    return null;
                }
                inputStream = connection.getInputStream();
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
                    sb.append(new String(buffer, 0 , singleReadLength));
                  //  try {
                   //     Thread.sleep(200);
                  //  } catch (InterruptedException ex) { }
                }
                return sb.toString();
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
