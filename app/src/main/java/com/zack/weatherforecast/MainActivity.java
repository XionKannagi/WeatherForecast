package com.zack.weatherforecast;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    TextView mDateTextView;
    TextView mLabelTextView;
    TextView mTempTextView;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler();

        mDateTextView = (TextView) findViewById(R.id.textViewDate);
        mLabelTextView = (TextView) findViewById(R.id.textViewLabel);
        mTempTextView = (TextView) findViewById(R.id.textViewTemp);
        mImageView = (ImageView) findViewById(R.id.imageView);

        getWeather();


    }


    private void getWeather() {
        Request request = new Request.Builder().url("http://weather.livedoor.com/forecast/webservice/json/v1?city=130010").get().build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d("onResponse", response.toString());
                final String json = response.body().toString();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parseJson(json);
                    }
                });

            }
        });


    }


    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray forecastsArray = jsonObject.getJSONArray("forecasts");

            JSONObject todayWeatherJson = forecastsArray.getJSONObject(0);

            String date = todayWeatherJson.getString("date");
            Log.d("Date", date);
            mDateTextView.setText(date);

            String telop = todayWeatherJson.getString("telop");
            Log.d("Telop", telop);

            String dateLabel = todayWeatherJson.getString("dateLabel");
            Log.d("DateLabel", dateLabel);
            mLabelTextView.setText(telop + "\n" + dateLabel);

            JSONObject temperatureJson = todayWeatherJson.getJSONObject("temperature");
            JSONObject minJson = temperatureJson.get("min") != null ? temperatureJson.getJSONObject("min") : null;
            String min = "";
            if (minJson == null) {
                min = minJson.getString("celsius");
            }

            JSONObject maxJson = temperatureJson.get("max") != null ? temperatureJson.getJSONObject("max") : null;
            String max = "";
            if (maxJson == null) {
                max = maxJson.getString("celsius");
            }
            Log.d("Min ~ Max", min + "~" + max);
            mTempTextView.setText(min + "~" + max);

            JSONObject imageJson = todayWeatherJson.getJSONObject("image");
            String imageUrl = imageJson.getString("url");
            Picasso.with(MainActivity.this).load(imageUrl).into(mImageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
