package com.example.loginfirebaseee;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Entity(tableName = "datacable")
public class InputData {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String data_Id;
    @ColumnInfo(name = "datacable")
    private String data_Data;
    @ColumnInfo(name = "user")
    private String userId;
    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;



    public InputData(){

    }
    public InputData(String dataId, String dataData, String userId,double latitude, double longitude){
        this.data_Id = dataId;
        this.data_Data = dataData;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = System.currentTimeMillis();

    }

    public void setData_Id(@NonNull String data_Id) {
        this.data_Id = data_Id;
    }

    public void setData_Data(String data_Data) {
        this.data_Data = data_Data;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getData_Id() {
        return data_Id;
    }

    public String getData_Data() {
        return data_Data;
    }
    public long getTimestamp(){ return timestamp; }
    public String getUserId() {
        return userId;
    }

    public String getFormattedTimestamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        return  dateFormat.format(new Date(timestamp));
    }


}

