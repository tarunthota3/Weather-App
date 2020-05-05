/*
Assignment: Homework03
File name: MainActivity.java
Full name:
Akhil Madhamshetty-801165622
Tarun thota-801164383
 */
package com.example.homework03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CityAdapter.InteractWithMainActivity{

    static String CITY_KEY = "citykey";
    static String CITY_NAME_KEY = "city";
    static String COUNTRY_NAME_KEY = "country";



    Button buttonSetCurrentCity;
    private static final String TAG = "demo";
    EditText editTextCityName;
    EditText editTextCountryName;
    TextView textViewCurrentCityNotSet;
    TextView textViewCityAndCountry;
    TextView textViewWeatherText;
    ImageView imageViewWeatherIcon;
    TextView textViewTemperature;
    TextView textViewUpdated;
    TextView textViewNoCityDisplay;
    TextView textViewSearchCityAndSave;
    TextView textViewSavedCities;

    ProgressBar progressBar;
    EditText editTextCity;
    EditText editTextCountry;
    Button buttonSearchCity;
    String keys[] = null;
    public static ArrayList<City> savedCities = new ArrayList<>();
    public static WeatherData weatherData = new WeatherData();
    private static final int REQ_CODE =0x001;


    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather App");

        buttonSetCurrentCity = findViewById(R.id.buttonSetCurrentCity);
        textViewCurrentCityNotSet = findViewById(R.id.textViewCurrentCityNotSet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        buttonSearchCity = findViewById(R.id.buttonSearchCity);


        textViewCityAndCountry = findViewById(R.id.textViewCityandCountry);
        textViewWeatherText = findViewById(R.id.textViewWeatherText);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeatherIcon);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewUpdated = findViewById(R.id.textViewUpdated);

        textViewNoCityDisplay = findViewById(R.id.textViewNoCityDisplay);
        textViewSearchCityAndSave = findViewById(R.id.textViewSearchCityAndSave);
        textViewSavedCities = findViewById(R.id.textViewSavedCities);
        progressBar = findViewById(R.id.progressBar);




        recyclerView = findViewById(R.id.recyclerView1);
        rv_layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_layoutManager);

        View view = getLayoutInflater().from(this).inflate(R.layout.city_details,null, false);
        editTextCityName = view.findViewById(R.id.editTextCityName);
        editTextCountryName = view.findViewById(R.id.editTextCountryName);

        if(savedCities.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            textViewSavedCities.setVisibility(View.INVISIBLE);
            textViewNoCityDisplay.setVisibility(View.VISIBLE);
            textViewSearchCityAndSave.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewSavedCities.setVisibility(View.VISIBLE);
            textViewNoCityDisplay.setVisibility(View.INVISIBLE);
            textViewSearchCityAndSave.setVisibility(View.INVISIBLE);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter City Details")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new GetCityData().execute(editTextCityName.getText().toString(),
                                    editTextCountryName.getText().toString());
                    }
                });
        final AlertDialog alert = builder.create();

        buttonSetCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });

        buttonSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchCities().execute(editTextCity.getText().toString(),editTextCountry.getText().toString());
            }
        });


//        if(getIntent() != null || getIntent().getExtras() != null){
//            String key[];
//            key = getIntent().getExtras().getStringArray(CityWeather.KEY);
//            if(key[0].equals("1")) {
//                if (savedCities.size() == 0) {
//                    recyclerView.setVisibility(View.INVISIBLE);
//                    textViewSavedCities.setVisibility(View.INVISIBLE);
//                    textViewNoCityDisplay.setVisibility(View.VISIBLE);
//                    textViewSearchCityAndSave.setVisibility(View.VISIBLE);
//
//                } else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    textViewSavedCities.setVisibility(View.VISIBLE);
//                    textViewNoCityDisplay.setVisibility(View.INVISIBLE);
//                    textViewSearchCityAndSave.setVisibility(View.INVISIBLE);
//                    rv_adapter = new CityAdapter(savedCities);
//                    recyclerView.setAdapter(rv_adapter);
//
//                }
//            }
//        }
    }

    @Override
    public void deleteItem(int position) {
        savedCities.remove(position);
        rv_adapter.notifyDataSetChanged();

        if(savedCities.size() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            textViewSavedCities.setVisibility(View.INVISIBLE);
            textViewNoCityDisplay.setVisibility(View.VISIBLE);
            textViewSearchCityAndSave.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewSavedCities.setVisibility(View.VISIBLE);
            textViewNoCityDisplay.setVisibility(View.INVISIBLE);
            textViewSearchCityAndSave.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateFavourite(int position, boolean fav) {
        City city = savedCities.get(position);
        city.favourite = fav;
        savedCities.set(position,city);
        rv_adapter.notifyDataSetChanged();
    }


    class SearchCities extends AsyncTask<String, Void, String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            keys = null;
            progressBar.setVisibility(View.VISIBLE);
            editTextCity.setVisibility(View.INVISIBLE);
            editTextCountry.setVisibility(View.INVISIBLE);
            buttonSearchCity.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String city = strings[0];
            String country = strings[1];
            String baseURL = "http://dataservice.accuweather.com/locations/v1/cities/";


            HttpURLConnection connection = null;
            URL url1 = null;
            String[] data = null;
            try {
                String url = baseURL + URLEncoder.encode(country,"UTF-8") +"/search"+ "?" +
                        "apikey=" + getResources().getString(R.string.weather_key) +
                        "&" + "q=" + URLEncoder.encode(city,"UTF-8");
                url1 = new URL(url);
                connection = (HttpURLConnection) url1.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONArray root=new JSONArray(json);
                    data = new String[root.length()];
                    keys = new String[root.length()];
                    for(int i = 0;i<root.length();i++){
                        JSONObject object = root.getJSONObject(i);
                        String key = object.getString("Key");
                        String city_name = object.getString("EnglishName");
                        JSONObject AdministrativeArea = object.getJSONObject("AdministrativeArea");
                        String region = AdministrativeArea.getString("ID");
                        String city_and_region = city_name + ", " + region;
                        data[i] = city_and_region;
                        keys[i] = key;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(final String[] s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
            editTextCity.setVisibility(View.VISIBLE);
            editTextCountry.setVisibility(View.VISIBLE);
            buttonSearchCity.setVisibility(View.VISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final String[] items = s;
            builder.setTitle("Select City")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG,"selected: " + items[which] + " key: " + keys[which]);
                    Intent intent = new Intent(MainActivity.this, CityWeather.class);
                    intent.putExtra(CITY_KEY, keys[which]);
                    intent.putExtra(CITY_NAME_KEY, editTextCity.getText().toString());
                    intent.putExtra(COUNTRY_NAME_KEY, editTextCountry.getText().toString());
                    startActivityForResult(intent, REQ_CODE);

//                    new GetCityDataForSave().execute(keys[which],editTextCity.getText().toString(),editTextCountry.getText().toString());

                    }
                });
             AlertDialog alert = builder.create();
             alert.show();
        }
    }

//    class GetCityDataForSave extends AsyncTask<String, Void, City>{
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected City doInBackground(String... strings) {
//            City city = new City();
//
//            String cityKey = strings[0];
//            String cityName = strings[1];
//            String countryName = strings[2];
//            String currentConditionsURL = "http://dataservice.accuweather.com/currentconditions/v1/";
//            HttpURLConnection connection1 = null;
//            URL url2 = null;
//            try {
//                String url3 = currentConditionsURL + URLEncoder.encode(cityKey,"UTF-8") + "?" +
//                        "apikey=" + getResources().getString(R.string.weather_key);
//                url2 = new URL(url3);
//                connection1 = (HttpURLConnection) url2.openConnection();
//                connection1.connect();
//                if (connection1.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                    String json = IOUtils.toString(connection1.getInputStream(), "UTF8");
//
//                    JSONArray root=new JSONArray(json);
//                    for(int i = 0; i<root.length(); i++){
//                        JSONObject firstObject = root.getJSONObject(i);
//                        city.cityKey = cityKey;
//                        city.cityName = cityName;
//                        city.country = countryName;
//                        city.date = firstObject.getString("LocalObservationDateTime");
//                        JSONObject temperature = firstObject.getJSONObject("Temperature");
//                        JSONObject metric = temperature.getJSONObject("Metric");
//                        Double value = metric.getDouble("Value");
//                        String unit = metric.getString("Unit");
//                        city.temperature = value + " " + unit;
//                        city.favourite = false;
//                    }
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return city;
//        }
//
//        @Override
//        protected void onPostExecute(City c) {
//            super.onPostExecute(c);
//            savedCities.add(c);
//            Log.d(TAG, "onPostExecute: cities length: " + savedCities.size());
//            rv_adapter = new CityAdapter(savedCities, MainActivity.this);
////            rv_adapter.notifyDataSetChanged();
//            recyclerView.setAdapter(rv_adapter);
//
//            if(savedCities.size() == 0){
//                recyclerView.setVisibility(View.INVISIBLE);
//                textViewSavedCities.setVisibility(View.INVISIBLE);
//                textViewNoCityDisplay.setVisibility(View.VISIBLE);
//                textViewSearchCityAndSave.setVisibility(View.VISIBLE);
//            }
//            else{
//                recyclerView.setVisibility(View.VISIBLE);
//                textViewSavedCities.setVisibility(View.VISIBLE);
//                textViewNoCityDisplay.setVisibility(View.INVISIBLE);
//                textViewSearchCityAndSave.setVisibility(View.INVISIBLE);
//            }
//        }
//    }


    class GetCityData extends AsyncTask<String, Void, String> {

        String baseURL = "http://dataservice.accuweather.com/locations/v1/cities/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            editTextCity.setVisibility(View.INVISIBLE);
            editTextCountry.setVisibility(View.INVISIBLE);
            buttonSearchCity.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String city = strings[0];
            String country = strings[1];
            String key="";
            Log.d(TAG, "doInBackground city and country: " + city + " " + country);

            HttpURLConnection connection = null;
            URL url1 = null;
            try {
                String url = baseURL + URLEncoder.encode(country,"UTF-8") +"/search"+ "?" +
                        "apikey=" + getResources().getString(R.string.weather_key) +
                        "&" + "q=" + URLEncoder.encode(city,"UTF-8");
                url1 = new URL(url);
                connection = (HttpURLConnection) url1.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONArray root=new JSONArray(json);
                    JSONObject firstObject = root.getJSONObject(0);
                    key = firstObject.getString("Key");
                    Log.d(TAG, "doInBackground key: " + key);
//                    for(int i = 0;i<root.length();i++){
//                        Log.d(TAG, "doInBackground: " + root.getJSONObject(i));
//                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return key;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            if(str == null || str == ""){
                progressBar.setVisibility(View.INVISIBLE);
                editTextCity.setVisibility(View.VISIBLE);
                editTextCountry.setVisibility(View.VISIBLE);
                buttonSearchCity.setVisibility(View.VISIBLE);
                editTextCityName.setText("");
                editTextCountryName.setText("");
                editTextCountryName.clearFocus();
                Toast.makeText(MainActivity.this,"City not found", Toast.LENGTH_SHORT).show();
            }
            else{
                new getCurrentConditionsData().execute(str);
            }

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
            Log.d(TAG, "onPostExecute weatherdata: " + weatherData.toString());
            progressBar.setVisibility(View.INVISIBLE);
            editTextCity.setVisibility(View.VISIBLE);
            editTextCountry.setVisibility(View.VISIBLE);
            buttonSearchCity.setVisibility(View.VISIBLE);

            textViewCurrentCityNotSet.setVisibility(View.INVISIBLE);
            buttonSetCurrentCity.setVisibility(View.INVISIBLE);

            editTextCity.setText(editTextCityName.getText().toString());
            editTextCountry.setText(editTextCountryName.getText().toString());
            textViewCityAndCountry.setText(editTextCityName.getText().toString()+", " +editTextCountryName.getText().toString());
            textViewWeatherText.setText(weatherData.weatherText);

            String weatherImageURL = "http://developer.accuweather.com/sites/default/files/";
            if (weatherData.weatherIcon < 10){
                weatherImageURL = weatherImageURL + "0" +weatherData.weatherIcon + "-s.png";
            }
            else{
                weatherImageURL = weatherImageURL + weatherData.weatherIcon + "-s.png";
            }
            Picasso.get().load(weatherImageURL).into(imageViewWeatherIcon);
            textViewTemperature.setText("Temperature: " + weatherData.metricInTemperatureValue + " " + weatherData.metricInTemperatureUnit);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = new Date();

            try {
                date = dateFormat.parse(weatherData.localObservationDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            PrettyTime p = new PrettyTime();

            String date_time = "Updated: " + p.format(date);

            textViewUpdated.setText(date_time);
            textViewCityAndCountry.setVisibility(View.VISIBLE);
            textViewWeatherText.setVisibility(View.VISIBLE);
            imageViewWeatherIcon.setVisibility(View.VISIBLE);
            textViewTemperature.setVisibility(View.VISIBLE);
            textViewUpdated.setVisibility(View.VISIBLE);

            Toast.makeText(MainActivity.this,"Current City details saved",Toast.LENGTH_SHORT).show();


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: !!!!!!!!!!!!!!!!!!!!");

        if(requestCode ==REQ_CODE && resultCode == RESULT_OK && data !=null)
        {
            Log.d(TAG, "onActivityResult: !!!!! " + savedCities.size());

            String v = data.getExtras().getString(CityWeather.KEY);


            Log.d("demo","update: " + v);


            if(v.equals("saveCity")){
                if(savedCities.size() == 0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    textViewSavedCities.setVisibility(View.INVISIBLE);
                    textViewNoCityDisplay.setVisibility(View.VISIBLE);
                    textViewSearchCityAndSave.setVisibility(View.VISIBLE);

                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewSavedCities.setVisibility(View.VISIBLE);
                    textViewNoCityDisplay.setVisibility(View.INVISIBLE);
                    textViewSearchCityAndSave.setVisibility(View.INVISIBLE);
                    rv_adapter = new CityAdapter(savedCities, this);
                    recyclerView.setAdapter(rv_adapter);

                }
            }
            else if(v.equals("setAsCurrent")){
                String city = data.getExtras().getString(CityWeather.CITY_NAME_KEY);
                Log.d(TAG, "onActivityResult setasCurrent: " + city + editTextCityName.getText().toString() + editTextCity.getText().toString());
                String cityBefore = textViewCityAndCountry.getText().toString().split(",")[0];
                if(city.equals(cityBefore)){
                    Toast.makeText(MainActivity.this, "Current City Updated", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Current City Saved", Toast.LENGTH_SHORT).show();
                }
                textViewCityAndCountry.setText(editTextCity.getText().toString() + ", " + editTextCountry.getText().toString());
                textViewWeatherText.setText(weatherData.weatherText);

                String weatherImageURL = "http://developer.accuweather.com/sites/default/files/";
                if (weatherData.weatherIcon < 10){
                    weatherImageURL = weatherImageURL + "0" +weatherData.weatherIcon + "-s.png";
                }
                else{
                    weatherImageURL = weatherImageURL + weatherData.weatherIcon + "-s.png";
                }
                Picasso.get().load(weatherImageURL).into(imageViewWeatherIcon);
                textViewTemperature.setText("Temperature: " + weatherData.metricInTemperatureValue + " " + weatherData.metricInTemperatureUnit);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = new Date();

                try {
                    date = dateFormat.parse(weatherData.localObservationDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                PrettyTime p = new PrettyTime();

                String date_time = "Updated: " + p.format(date);
                textViewUpdated.setText(date_time);
            }



        }

    }
}
