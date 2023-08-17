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
    @ColumnInfo(name = "user_email")
    private String user_email;

    public InputData(){

    }
    public InputData(String dataId, String dataData, double latitude, double longitude, String email){
        this.data_Id = dataId;
        this.data_Data = dataData;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_email = email;
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

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_email() {
        return user_email;
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

