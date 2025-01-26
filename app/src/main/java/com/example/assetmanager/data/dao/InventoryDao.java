package com.example.assetmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.util.Constants;

import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    long insertInventory(Inventory inventory);
    @Update
    void updateInventory(Inventory inventory);
    @Delete
    void deleteInventory(Inventory inventory);
    @Delete
    void deleteInventories(Inventory... inventory);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_INVENTORY)
    List<Inventory> getInventories();
}
