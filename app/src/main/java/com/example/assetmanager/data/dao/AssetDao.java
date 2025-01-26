package com.example.assetmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.util.Constants;

import java.util.List;

@Dao
public interface AssetDao {
    @Insert
    long insertAsset(Asset asset);
    @Update
    void updateAsset(Asset asset);
    @Delete
    void deleteAsset(Asset asset);
    @Delete
    void deleteAssets(Asset ... asset);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET)
    List<Asset> getAssets();
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET + " WHERE location_id = :id")
    List<Asset> getAssetsByLocationId(int id);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ASSET + " WHERE barcode = :id LIMIT 1")
    Asset getAssetByBarcode(long id);
}
