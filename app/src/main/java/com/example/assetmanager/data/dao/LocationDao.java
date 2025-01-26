package com.example.assetmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.util.Constants;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    long insertLocation(Location location);
    @Update
    void updateLocation(Location location);
    @Delete
    void deleteLocation(Location location);
    @Delete
    void deleteLocations(Location ... location);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION)
    List<Location> getLocations();
    @Query("SELECT * FROM " + Constants.TABLE_NAME_LOCATION + " WHERE location_id = :id LIMIT 1")
    Location getLocationById(int id);
}
