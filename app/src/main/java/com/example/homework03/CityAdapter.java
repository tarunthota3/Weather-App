package com.example.homework03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    public static InteractWithMainActivity interact;
    Context ctx;
    ArrayList<City> cities = new ArrayList<>();
    private static final String TAG = "demo";


    public CityAdapter(ArrayList<City> cities, Context mainActivity) {
        this.cities = cities;
        this.ctx = mainActivity;
    }

    @NonNull
    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(rv_layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.ViewHolder holder, final int position) {
        interact = (InteractWithMainActivity) ctx;
        final City city = cities.get(position);
        holder.textViewCityAndCountry.setText(cities.get(position).cityName + ", " + cities.get(position).country);
        holder.textViewTemperature.setText("Temperature: " + cities.get(position).temperature);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = new Date();

        try {
            date = dateFormat.parse(cities.get(position).date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PrettyTime p = new PrettyTime();

        String date_time = "Updated: " + p.format(date);

        holder.textViewUpdated.setText(date_time);
        holder.city = city;
        if(city.favourite == true){
            holder.imageButtonStar.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else{
            holder.imageButtonStar.setImageResource(android.R.drawable.btn_star_big_off);
        }
        holder.imageButtonStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(city.favourite == true){
                    interact.updateFavourite(position,false);
                }
                else{
                    interact.updateFavourite(position,true);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.deleteItem(position);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCityAndCountry;
        TextView textViewTemperature;
        TextView textViewUpdated;
        ImageButton imageButtonStar;
        City city;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCityAndCountry = itemView.findViewById(R.id.textViewCityAndCountry);
            textViewTemperature = itemView.findViewById(R.id.textViewTemperature);
            textViewUpdated = itemView.findViewById(R.id.textViewUpdated);
            imageButtonStar = itemView.findViewById(R.id.imageButtonStar);
            this.city = city;

        }
    }

    public interface InteractWithMainActivity {
        void deleteItem(int position);
        void updateFavourite(int position, boolean fav);
    }
}