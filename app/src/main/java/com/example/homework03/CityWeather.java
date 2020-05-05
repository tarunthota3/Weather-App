package com.example.homework03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.homework03.MainActivity.savedCities;
import static com.example.homework03.MainActivity.weatherData;

public class CityWeather extends AppCompatActivity implements CityWeatherAdapter.InteractWithCityWeather {
    private static final String TAG = "demo";
    TextView textViewCityAndCountry;
    TextView textViewWeatherText;
    TextView textViewForecastDate;
    TextView textViewTemperature;
    TextView textViewDay;
    TextView textViewNight;
    ImageView imageViewDay;
    ImageView imageViewNight;
    TextView textViewDayCondition;
    TextView textViewNightCondition;
    TextView textViewClickHereForMoreDetails;
    Button buttonSaveCity;
    Button buttonSetAsCurrent;

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    String cityKey = "";
    String cityAndCountry = "";
    String city = "";
    String country = "";
    JSONArray dailyForecasts = null;
    static String CITY_KEY = "citykey";
    static String CITY_NAME_KEY = "city";
    static String COUNTRY_NAME_KEY = "country";
    static String KEY="key";
    String key_pass = "";
    int position = 0;
    private static final int REQ_CODE =0x001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        setTitle("City Weather");
        textViewCityAndCountry = findViewById(R.id.textViewCityAndCountry);
        textViewWeatherText = findViewById(R.id.textViewWeatherText);
        textViewForecastDate = findViewById(R.id.textViewForecastDate);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewDay = findViewById(R.id.textViewDay);
        textViewNight = findViewById(R.id.textViewNight);
        imageViewDay = findViewById(R.id.imageViewDay);
        imageViewNight = findViewById(R.id.imageViewNight);
        textViewDayCondition = findViewById(R.id.textViewDayCondition);
        textViewNightCondition = findViewById(R.id.textViewNightCondition);
        textViewClickHereForMoreDetails = findViewById(R.id.textViewClickHereForMoreDetails);
        buttonSaveCity = findViewById(R.id.buttonSaveCity);
        buttonSetAsCurrent = findViewById(R.id.buttonSetAsCurrent);


        buttonSaveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + cityKey + city + country);

                new GetCityDataForSave().execute(cityKey, city,country);

            }
        });

        buttonSetAsCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getCurrentConditionsData().execute(cityKey);
            }
        });

        recyclerView = findViewById(R.id.recyclerView1);

        rv_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(rv_layoutManager);

        if(getIntent() != null && getIntent().getExtras() != null ){
            cityKey = getIntent().getExtras().getString(MainActivity.CITY_KEY);
            city = getIntent().getExtras().getString(MainActivity.CITY_NAME_KEY);
            country = getIntent().getExtras().getString(MainActivity.COUNTRY_NAME_KEY);
            cityAndCountry = city + ", " +country;
            new getForecasrApi().execute(cityKey);
        }
    }

    @Override
    public void updateItem(int position) {
        Log.d(TAG, "updateItem!!!!!!!!!!!!!: ");
        this.position = position;
        JSONObject jsonObject = null;
        try {

            jsonObject = dailyForecasts.getJSONObject(position);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = new Date();

            try {
                date = dateFormat.parse(jsonObject.getString("Date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMMM dd, yyyy");
            String s = dateFormat1.format(date.getTime());
            textViewForecastDate.setText("Forecast on " + s);

            JSONObject maxTemperature = jsonObject.getJSONObject("Temperature").getJSONObject("Maximum");
            textViewTemperature.setText("Temperature " + maxTemperature.getInt("Value") + " " + maxTemperature.getString("Unit"));

            int dayIcon = jsonObject.getJSONObject("Day").getInt("Icon");
            String weatherDayImageURL = "http://developer.accuweather.com/sites/default/files/";
            if (dayIcon < 10){
                weatherDayImageURL = weatherDayImageURL + "0" +dayIcon + "-s.png";
            }
            else{
                weatherDayImageURL = weatherDayImageURL + dayIcon + "-s.png";
            }
            Picasso.get().load(weatherDayImageURL).into(imageViewDay);
            textViewDayCondition.setText(jsonObject.getJSONObject("Day").getString("IconPhrase"));

            int nightIcon = jsonObject.getJSONObject("Night").getInt("Icon");
            String weatherNightImageURL = "http://developer.accuweather.com/sites/default/files/";
            if (nightIcon < 10){
                weatherNightImageURL = weatherNightImageURL + "0" +nightIcon + "-s.png";
            }
            else{
                weatherNightImageURL = weatherNightImageURL + nightIcon + "-s.png";
            }
            Picasso.get().load(weatherNightImageURL).into(imageViewNight);
            textViewNightCondition.setText(jsonObject.getJSONObject("Night").getString("IconPhrase"));
            final JSONObject finalJsonObject = jsonObject;
            textViewClickHereForMoreDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mobileLink = null;
                    try {
                        mobileLink = finalJsonObject.getString("MobileLink");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(mobileLink));
                    startActivity(intent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class getForecasrApi extends AsyncTask<String, Void, JSONObject>{

        String baseURL = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String cityKey1 = strings[0];
            HttpURLConnection connection = null;
            JSONObject root = null;
            URL url1 = null;
            try {
                String url = baseURL + URLEncoder.encode(cityKey1,"UTF-8") + "?" +
                        "apikey=" + getResources().getString(R.string.weather_key);
                url1 = new URL(url);
                connection = (HttpURLConnection) url1.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    root = new JSONObject(json);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return root;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            JSONObject headlines = null;

            try {
                headlines = jsonObject.getJSONObject("Headline");
                dailyForecasts = jsonObject.getJSONArray("DailyForecasts");
                textViewCityAndCountry.setText(cityAndCountry);
                textViewWeatherText.setText(headlines.getString("Text"));

                final JSONObject today = dailyForecasts.getJSONObject(position);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = new Date();

                try {
                    date = dateFormat.parse(today.getString("Date"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMMM dd, yyyy");
                String s = dateFormat1.format(date.getTime());

                textViewForecastDate.setText("Forecast on " + s);
                JSONObject maxTemperature = today.getJSONObject("Temperature").getJSONObject("Maximum");
                textViewTemperature.setText("Temperature " + maxTemperature.getInt("Value") + " " + maxTemperature.getString("Unit"));

                int dayIcon = today.getJSONObject("Day").getInt("Icon");
                String weatherDayImageURL = "http://developer.accuweather.com/sites/default/files/";
                if (dayIcon < 10){
                    weatherDayImageURL = weatherDayImageURL + "0" +dayIcon + "-s.png";
                }
                else{
                    weatherDayImageURL = weatherDayImageURL + dayIcon + "-s.png";
                }
                Picasso.get().load(weatherDayImageURL).into(imageViewDay);
                textViewDayCondition.setText(today.getJSONObject("Day").getString("IconPhrase"));

                int nightIcon = today.getJSONObject("Night").getInt("Icon");
                String weatherNightImageURL = "http://developer.accuweather.com/sites/default/files/";
                if (nightIcon < 10){
                    weatherNightImageURL = weatherNightImageURL + "0" +nightIcon + "-s.png";
                }
                else{
                    weatherNightImageURL = weatherNightImageURL + nightIcon + "-s.png";
                }
                Picasso.get().load(weatherNightImageURL).into(imageViewNight);
                textViewNightCondition.setText(today.getJSONObject("Night").getString("IconPhrase"));
                textViewClickHereForMoreDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            String mobileLink = today.getString("MobileLink");
                            Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(mobileLink));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            rv_adapter = new CityWeatherAdapter(dailyForecasts, CityWeather.this);
            recyclerView.setAdapter(rv_adapter);

        }
    }


    class GetCityDataForSave extends AsyncTask<String, Void, City>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected City doInBackground(String... strings) {
            City city = new City();

            String cityKey = strings[0];
            String cityName = strings[1];
            String countryName = strings[2];
            String currentConditionsURL = "http://dataservice.accuweather.com/currentconditions/v1/";
            HttpURLConnection connection1 = null;
            URL url2 = null;
            try {
                String url3 = currentConditionsURL + URLEncoder.encode(cityKey,"UTF-8") + "?" +
                        "apikey=" + getResources().getString(R.string.weather_key);
                url2 = new URL(url3);
                connection1 = (HttpURLConnection) url2.openConnection();
                connection1.connect();
                if (connection1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection1.getInputStream(), "UTF8");

                    JSONArray root=new JSONArray(json);
                    for(int i = 0; i<root.length(); i++){
                        JSONObject firstObject = root.getJSONObject(i);
                        city.cityKey = cityKey;
                        city.cityName = cityName;
                        city.country = countryName;
                        city.date = firstObject.getString("LocalObservationDateTime");
                        JSONObject temperature = firstObject.getJSONObject("Temperature");
                        JSONObject metric = temperature.getJSONObject("Metric");
                        Double value = metric.getDouble("Value");
                        String unit = metric.getString("Unit");
                        city.temperature = value + " " + unit;
                        city.favourite = false;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return city;
        }

        @Override
        protected void onPostExecute(City c) {
            super.onPostExecute(c);

            String city_key = c.cityKey;
            int temp = 1;
            if(savedCities.size()>0){
                for(int i =0; i<MainActivity.savedCities.size();i++){
                    City city = MainActivity.savedCities.get(i);
                    Log.d(TAG, "onPostExecute: " + city_key + city.cityKey);
                    Log.d(TAG, "City c: " + c.cityKey);

                    if(city.cityKey.equals(city_key)){
                        savedCities.get(i).temperature = c.temperature;
                        Toast.makeText(CityWeather.this,"City Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        savedCities.add(c);
                        Toast.makeText(CityWeather.this,"City Saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                savedCities.add(c);
            }

            Log.d(TAG, "City Weather!!!!!: " + savedCities.size());
            Intent i= new Intent();
            key_pass = "saveCity";
            Log.d("demo","key_pass: " + key_pass);
            i.putExtra(KEY,key_pass);
            setResult(RESULT_OK,i);
            finish();
        }
    }

    class getCurrentConditionsData extends AsyncTask<String, Void, WeatherData>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected WeatherData doInBackground(String... strings) {
            WeatherData weatherData = new WeatherData();
            String cityKey = strings[0];
            String currentConditionsURL = "http://dataservice.accuweather.com/currentconditions/v1/";
            HttpURLConnection connection1 = null;
            URL url2 = null;
            try {
//                349818?apikey=H6Wlc5b1hnHPqDSQYh17C61rEGJkXPxH
                String url3 = currentConditionsURL + URLEncoder.encode(cityKey,"UTF-8") + "?" +
                        "apikey=" + getResources().getString(R.string.weather_key);
                url2 = new URL(url3);
                connection1 = (HttpURLConnection) url2.openConnection();
                connection1.connect();
                if (connection1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection1.getInputStream(), "UTF8");

                    JSONArray root=new JSONArray(json);
                    JSONObject firstObject = root.getJSONObject(0);
                    weatherData.localObservationDateTime = firstObject.getString("LocalObservationDateTime");
                    weatherData.weatherText = firstObject.getString("WeatherText");
                    weatherData.weatherIcon = firstObject.getInt("WeatherIcon");
                    JSONObject temperature = firstObject.getJSONObject("Temperature");
                    JSONObject metric = temperature.getJSONObject("Metric");
                    weatherData.metricInTemperatureValue = metric.getDouble("Value");
                    weatherData.metricInTemperatureUnit = metric.getString("Unit");
                    weatherData.metricInTemperatureUnitType = metric.getInt("UnitType");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return weatherData;
        }

        @Override
        protected void onPostExecute(WeatherData weatherData) {
            super.onPostExecute(weatherData);

            MainActivity.weatherData = weatherData;


            Intent i= new Intent();
            key_pass = "setAsCurrent";
            Log.d("demo","key_pass: " + key_pass);
            i.putExtra(KEY,key_pass);
            i.putExtra(CITY_NAME_KEY, city);
            setResult(RESULT_OK,i);
            finish();
        }
    }

}
