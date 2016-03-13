package com.zack.weatherforecast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Handler mHandler;
    TextView mAreaTextView;
    TextView mDateTextView;
    TextView mLabelTextView;
    TextView mTempTextView;
    ImageView mImageView;

    private static final int LOCATION_UPDATE_MIN_TIME = 0;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 1;

    private double latitude;
    private double longitude;

    LocationManager mLocationManager;
    Location location;

    static String name;
    static String country;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler();

        mAreaTextView = (TextView) findViewById(R.id.textViewArea);
        mDateTextView = (TextView) findViewById(R.id.textViewDate);
        mLabelTextView = (TextView) findViewById(R.id.textViewLabel);
        mTempTextView = (TextView) findViewById(R.id.textViewTemp);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //位置情報を取得
        requestLocationUpdates();
        //緯度経度
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
//            位置情報を保存

            Log.d("GPSSSSS", latitude + "");
            Log.d("GPSSSSS", longitude + "");

            //天気情報を取得
            getWeather(latitude, longitude);

        } else {
            Toast.makeText(this, "Cannot get location", Toast.LENGTH_LONG).show();
        }


    }


    private void getWeather(double latitude, double longitude) {
        Request request = new Request.Builder().url("http://api.openweathermap.org/data/2.5/forecast?lat=" + String.valueOf(latitude) + "&lon=" + String.valueOf(longitude) + "&APPID=d7689f4744a178cb7c399d8bf0e3c6f8").get().build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d("onResponse", response.toString());
                final String json = response.body().string();
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
            //国，現在地を拾う
            String city = jsonObject.getString("city");
            JSONObject locationObject = new JSONObject(city);
            name = locationObject.getString("name");
            country = locationObject.getString("country");
            mAreaTextView.setText("City:"+name + "  Country:"+country);
            //今日の情報
            /*JSONArray forecastsArray = jsonObject.getJSONArray("forecasts");
            JSONObject todayWeatherJson = forecastsArray.getJSONObject(0);*/

            JSONArray listArray = jsonObject.getJSONArray("list");
            JSONObject todayWeatherJson = listArray.getJSONObject(0);


            String date = todayWeatherJson.getString("dt_txt");
            Log.d("Date", date);
            mDateTextView.setText(date);

            /*
            String telop = todayWeatherJson.getString("telop");
            Log.d("Telop", telop);

            String dateLabel = todayWeatherJson.getString("dateLabel");
            Log.d("DateLabel", dateLabel);
            mLabelTextView.setText(telop + "\n" + dateLabel);*/

            JSONArray weatherArray = todayWeatherJson.getJSONArray("weather");
            JSONObject weatherInfoJson = weatherArray.getJSONObject(0);
            String label = weatherInfoJson.getString("description");
            String icon = weatherInfoJson.getString("icon");
            Log.d("icon",icon);
            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/"+icon+".png").into(mImageView);
            mLabelTextView.setText("Weather:"+label);

            JSONObject temperatureJson = todayWeatherJson.getJSONObject("temperature");
            JSONObject minJson = temperatureJson.get("min") != null ? temperatureJson.getJSONObject("min") : null;
            String min = "";
            if (minJson != null) {
                min = minJson.getString("celsius");
            }

            JSONObject maxJson = temperatureJson.get("max") != null ? temperatureJson.getJSONObject("max") : null;
            String max = "";
            if (maxJson != null) {
                max = maxJson.getString("celsius");
            }
            Log.d("Min ~ Max", min + "~" + max);
            mTempTextView.setText("最低気温" + min + "℃~最高気温" + max + "℃");

            //天気の画像をurl先から拾う
            /*JSONObject imageJson = todayWeatherJson.getJSONObject("image");
            String imageUrl = "http://openweathermap.org/img/w/"+icon+".png";
            Picasso.with(MainActivity.this).load(imageUrl).into(mImageView);*/

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

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                break;

            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;

            case LocationProvider.AVAILABLE:
                if (provider.equals(LocationManager.GPS_PROVIDER)) {
                    requestLocationUpdates();
                }
                break;
        }

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            requestLocationUpdates();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void requestLocationUpdates() {
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isNetworkEnabled) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE,
                    this
            );
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Toast.makeText(this, "GPS is disabled", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Get location from network", Toast.LENGTH_LONG).show();
        }

    }
}
