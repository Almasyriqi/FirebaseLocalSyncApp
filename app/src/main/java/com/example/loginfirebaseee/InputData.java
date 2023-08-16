package com.example.loginfirebaseee;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "cable")
public class InputData {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String data_Id;
    @ColumnInfo(name = "data")
    private String data_Data;
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;

    public InputData(){

    }
    public InputData(String dataId, String dataData, double latitude, double longitude){
        this.data_Id = dataId;
        this.data_Data = dataData;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setData_Id(String data_Id) {
        this.data_Id = data_Id;
    }

    public void setData_Data(String data_Data) {
        this.data_Data = data_Data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getData_Id() {
        return data_Id;
    }

    public String getData_Data() {
        return data_Data;
    }
    public long getTimestamp(){
        return timestamp;
    }

    public String getFormattedTimestamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        return  dateFormat.format(new Date(timestamp));
    }
}

