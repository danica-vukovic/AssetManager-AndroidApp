package com.example.assetmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Transaction;

import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Inventory;

@Dao
public interface InventoryAssetDao {

    @Insert
    long insertInventory(Inventory inventory);

    @Update
    int updateInventory(Inventory inventory);

    @Update
    int updateAsset(Asset asset);

    @Transaction
    default boolean insertInventoryAndUpdateAsset(Inventory inventory, Asset asset) {
        long inventoryInsertResult = insertInventory(inventory);
        int assetUpdateResult = updateAsset(asset);

        return inventoryInsertResult > 0 && assetUpdateResult > 0;
    }

    @Transaction
    default boolean updateInventoryAndUpdateAsset(Inventory inventory, Asset asset) {
        int inventoryUpdateResult = updateInventory(inventory);
        int assetUpdateResult = updateAsset(asset);

        return inventoryUpdateResult > 0 && assetUpdateResult > 0;
    }
}
