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

    public InputData(){

    }
    public InputData(String dataId, String dataData){
        this.data_Id = dataId;
        this.data_Data = dataData;
        this.timestamp = System.currentTimeMillis();
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

