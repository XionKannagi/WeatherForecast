package com.zack.weatherforecast;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    TextView mNowTempTextView;
    TextView mHumidityTextView;
    TextView mWindSpeedTextView;
    ImageView mImageView;
    FrameLayout background;

    private static final int LOCATION_UPDATE_MIN_TIME = 0;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 1;

    private double latitude;
    private double longitude;

    LocationManager mLocationManager;
    Location location;

    static String name;
    static String country;


    private int[] skyStatus = {
            R.drawable.clear_sky,
            R.drawable.rainny,
            R.drawable.cloudy,
            R.drawable.snow
    };



    String mainWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHandler = new Handler();

        mAreaTextView = (TextView) findViewById(R.id.textViewArea);
        mDateTextView = (TextView) findViewById(R.id.textViewDate);
        mLabelTextView = (TextView) findViewById(R.id.textViewLabel);
        mTempTextView = (TextView) findViewById(R.id.textViewTemp);
        mNowTempTextView = (TextView) findViewById(R.id.textViewNowTemp);
        mHumidityTextView = (TextView) findViewById(R.id.textViewHumidity);
        mWindSpeedTextView = (TextView) findViewById(R.id.textViewWind);
        mImageView = (ImageView) findViewById(R.id.imageView);
        background = (FrameLayout) findViewById(R.id.main_activity_back);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


    }

    @Override
    public void onResume() {
        super.onResume();
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
            mAreaTextView.setText("Location:" + name + "  in: " + country);
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
            mainWeather = weatherInfoJson.getString("main");
            String icon = weatherInfoJson.getString("icon");
            Log.d("icon", icon);
            Log.d("mainWeather",mainWeather);
            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + icon + ".png").into(mImageView);
            mLabelTextView.setText("天気：" + label);

            if (mainWeather.equals("Clear")){
                background.setBackgroundResource(skyStatus[0]);
            } else if(mainWeather.equals("Rain")){
                background.setBackgroundResource(skyStatus[1]);
            }else if (mainWeather.equals("Clouds")){
                background.setBackgroundResource(skyStatus[2]);
            } else if(mainWeather.equals("Snow")){
                background.setBackgroundResource(skyStatus[3]);
            }

            /*JSONObject temperatureJson = todayWeatherJson.getJSONObject("temperature");
            JSONObject minJson = temperatureJson.get("min") != null ? temperatureJson.getJSONObject("min") : null;
            String min = "";
            if (minJson != null) {
                min = minJson.getString("celsius");
            }

            JSONObject maxJson = temperatureJson.get("max") != null ? temperatureJson.getJSONObject("max") : null;
            String max = "";
            if (maxJson != null) {
                max = maxJson.getString("celsius");
            }*/

            //気温と湿度オブジェクトを取得
            String temperature = todayWeatherJson.getString("main");
            JSONObject temperatureObject = new JSONObject(temperature);

            //最高気温を取得
            String maxTemp = temperatureObject.getString("temp_max");
            Log.d("最高気温", maxTemp);
            double parseMaxTemp = Double.parseDouble(maxTemp) - 273.15;
            int parseMaxTempInt = (int) parseMaxTemp;
            String max = String.valueOf(parseMaxTempInt);


            //最低気温を取得
            String minTemp = temperatureObject.getString("temp_min");
            Log.d("最低気温", minTemp);
            double parseMinTemp = Double.parseDouble(minTemp) - 273.15;
            int parseMinTempInt = (int) parseMinTemp;
            String min = String.valueOf(parseMinTempInt);

            Log.d("Min ~ Max", min + "~" + max);
            mTempTextView.setText("最低気温" + min + "℃~最高気温" + max + "℃");

            //現在の気温
            String nowTemp = temperatureObject.getString("temp");
            Log.d("気温", nowTemp);
            double parseNowTemp = Double.parseDouble(nowTemp) - 273.15;
            int parseNowTempInt = (int) parseNowTemp;
            String nowTempStr = String.valueOf(parseNowTempInt);
            //mNowTempTextView.setText("現在の気温：" + nowTempStr + "℃" );


            //湿度を取得
            String humidity = temperatureObject.getString("humidity");
            Log.d("湿度", humidity);
            double parseHumidity = Double.parseDouble(humidity);
            mHumidityTextView.setText("湿度：" + humidity + " ％");

            //風速
            String wind = todayWeatherJson.getString("wind");
            JSONObject windObject = new JSONObject(wind);
            String windSpeed = windObject.getString("speed");
            Log.d("wind_Speed", windSpeed);
            double parseWindSpeed = Double.parseDouble(windSpeed);
            mWindSpeedTextView.setText("風速：" + windSpeed + "m/s");

            String NETStr = String.valueOf(net(parseNowTemp, parseHumidity, parseWindSpeed));
            Log.d("体感気温：", NETStr);

            mNowTempTextView.setText("現在の気温：" + nowTempStr + "℃" + "(体感気温：" + NETStr + "℃)");

            SharedPreferences data = getSharedPreferences("saveData", MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString("weather", label);
            editor.putInt("minNET", net(parseMinTemp, parseHumidity, parseWindSpeed));
            editor.putInt("minTmp", parseMinTempInt);
            editor.putInt("maxTmp", parseMaxTempInt);
            editor.apply();


            //天気の画像をurl先から拾う
            /*JSONObject imageJson = todayWeatherJson.getJSONObject("image");
            String imageUrl = "http://openweathermap.org/img/w/"+icon+".png";
            Picasso.with(MainActivity.this).load(imageUrl).into(mImageView);*/

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public int net(double temp, double humidity, double windSpeed) {
        //体感気温を推定（グレゴルチュク）
        double NET = 37 - ((37 - temp) / (0.68 - (0.0014 * humidity) + (1 / (1.76 + 1.4 * Math.pow(windSpeed, 0.75))))) - 0.29 * temp * (1 - (humidity / 100));
        int NETInt = (int) NET;
        return NETInt;
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

        //服装recommendページへ
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_recommend) {
            Intent intent = new Intent(this, RecommendActivity.class);
            startActivity(intent);
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
