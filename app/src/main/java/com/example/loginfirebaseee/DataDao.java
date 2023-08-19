package com.example.loginfirebaseee;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao {
    @Insert
    void insert(InputData inputData);

    @Query("SELECT * FROM datacable ORDER BY timestamp ASC")
    List<InputData> getAllData();

    @Query("DELETE FROM datacable")
    void deleteTable();
}
