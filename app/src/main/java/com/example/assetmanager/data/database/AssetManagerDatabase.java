package com.example.assetmanager.data.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.assetmanager.data.dao.AssetDao;
import com.example.assetmanager.data.dao.EmployeeDao;
import com.example.assetmanager.data.dao.InventoryAssetDao;
import com.example.assetmanager.data.dao.InventoryDao;
import com.example.assetmanager.data.dao.LocationDao;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.util.Constants;
import com.example.assetmanager.util.DateRoomConverter;

@Database(entities = { Asset.class, Employee.class, Inventory.class, Location.class }, version = 1, exportSchema = false)
@TypeConverters({DateRoomConverter.class})
public abstract class AssetManagerDatabase extends RoomDatabase {
    public abstract AssetDao getAssetDao();
    public abstract EmployeeDao getEmployeeDao();
    public abstract InventoryDao getInventoryDao();
    public abstract LocationDao getLocationDao();
    public abstract InventoryAssetDao getInventoryAssetDao();
    private static AssetManagerDatabase assetManagerDatabase;

    public static AssetManagerDatabase getInstance(Context context) {
        if (null == assetManagerDatabase) {
            assetManagerDatabase = buildDatabaseInstance(context);
        }
        return assetManagerDatabase;
    }
    private static AssetManagerDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AssetManagerDatabase.class,
                Constants.DB_NAME).fallbackToDestructiveMigration().build();
    }
    public  void cleanUp(){
        assetManagerDatabase = null;
    }
}
