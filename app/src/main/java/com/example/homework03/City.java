package com.example.homework03;

public class City {
    String cityKey, cityName, country, temperature, date;
    boolean favourite;

    public City() {
    }

    @Override
    public String toString() {
        return "City{" +
                "cityKey='" + cityKey + '\'' +
                ", cityName='" + cityName + '\'' +
                ", country='" + country + '\'' +
                ", temperature='" + temperature + '\'' +
                ", date='" + date + '\'' +
                ", favourite=" + favourite +
                '}';
    }
}
