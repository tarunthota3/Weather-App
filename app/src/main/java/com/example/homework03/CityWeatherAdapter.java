package com.example.homework03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.ViewHolder> {
    public static InteractWithCityWeather interact;
    private static final String TAG = "demo";


    JSONArray dailyforcasts = null;
    Context ctx;

    public CityWeatherAdapter(JSONArray dailyforcasts, Context ctx) {
        this.dailyforcasts = dailyforcasts;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public CityWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(rv_layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CityWeatherAdapter.ViewHolder holder, final int position) {
        interact = (InteractWithCityWeather) ctx;

        try {
            SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date= new Date();
            try {
                date=dateFormat.parse(dailyforcasts.getJSONObject(position).getString("Date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat1 =new SimpleDateFormat("dd MMM''yy");
            String s=dateFormat1.format(date.getTime());
            holder.textViewDate.setText(s);

            JSONObject dayObject = dailyforcasts.getJSONObject(position).getJSONObject("Day");
            int dayIcon = dayObject.getInt("Icon");
            String weatherDayImageURL = "http://developer.accuweather.com/sites/default/files/";
            if (dayIcon < 10){
                weatherDayImageURL = weatherDayImageURL + "0" +dayIcon + "-s.png";
            }
            else{
                weatherDayImageURL = weatherDayImageURL + dayIcon + "-s.png";
            }
            Picasso.get().load(weatherDayImageURL).into(holder.imageViewWeatherIcon);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick card position: " + position);
                    interact.updateItem(position);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return dailyforcasts.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewWeatherIcon;
        TextView textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewWeatherIcon = itemView.findViewById(R.id.imageViewWeatherIcon);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
    public interface InteractWithCityWeather {
        void updateItem(int position);
    }
}
