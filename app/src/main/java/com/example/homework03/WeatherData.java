package com.example.homework03;

public class WeatherData {
    String localObservationDateTime,weatherText;
    int weatherIcon;
    double metricInTemperatureValue;
    String metricInTemperatureUnit;
    int metricInTemperatureUnitType;

    public WeatherData() {
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "LocalObservationDateTime='" + localObservationDateTime + '\'' +
                ", WeatherText='" + weatherText + '\'' +
                ", WeatherIcon=" + weatherIcon +
                ", metricInTemperatureValue=" + metricInTemperatureValue +
                ", metricInTemperatureUnit='" + metricInTemperatureUnit + '\'' +
                ", metricInTemperatureUnitType=" + metricInTemperatureUnitType +
                '}';
    }
}
