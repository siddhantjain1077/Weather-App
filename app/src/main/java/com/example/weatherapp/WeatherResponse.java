package com.example.weatherapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    @SerializedName("list")
    private List<ForecastItem> list;

    @SerializedName("city")
    private City city;

    public List<ForecastItem> getList() { return list; }
    public City getCity() { return city; }

    public static class ForecastItem {
        @SerializedName("dt")
        private long dt;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        @SerializedName("clouds")
        private Clouds clouds;

        @SerializedName("wind")
        private Wind wind;

        @SerializedName("visibility")
        private int visibility;

        @SerializedName("dt_txt")
        private String dt_txt;

        public long getDt() { return dt; }
        public Main getMain() { return main; }
        public List<Weather> getWeather() { return weather; }
        public Clouds getClouds() { return clouds; }
        public Wind getWind() { return wind; }
        public int getVisibility() { return visibility; }
        public String getDt_txt() { return dt_txt; }
    }

    public static class Main {
        private float temp;
        private float feels_like;
        private float temp_min;
        private float temp_max;
        private int pressure;
        private int humidity;

        public float getTemp() { return temp; }
        public float getFeelsLike() { return feels_like; }
        public float getTempMin() { return temp_min; }
        public float getTempMax() { return temp_max; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class Weather {
        private String description;
        private String icon;

        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class Clouds {
        private int all;
        public int getAll() { return all; }
    }

    public static class Wind {
        private float speed;
        public float getSpeed() { return speed; }
    }

    public static class City {
        private String name;
        private long sunrise;
        private long sunset;

        public String getName() { return name; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }
}
