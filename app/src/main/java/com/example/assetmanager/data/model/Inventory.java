package com.example.assetmanager.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.assetmanager.util.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_INVENTORY,
foreignKeys = {
        @ForeignKey(
                entity = Employee.class,
                parentColumns = "employee_id",
                childColumns = "current_employee_id",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Employee.class,
                parentColumns = "employee_id",
                childColumns = "new_employee_id",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Location.class,
                parentColumns = "location_id",
                childColumns = "current_location_id",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = Location.class,
                parentColumns = "location_id",
                childColumns = "new_location_id",
                onDelete = ForeignKey.CASCADE
        )
},
        indices = {
                @Index(value = {"current_employee_id"}),
                @Index(value = {"new_employee_id"}),
                @Index(value = {"current_location_id"}),
                @Index(value = {"new_location_id"})
        })
public class Inventory implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "inventory_id")
    private long inventoryId;

    @ColumnInfo(name = "asset_barcode")
    private long assetBarcode;

    @ColumnInfo(name = "current_employee_id")
    private long currentEmployeeId;

    @ColumnInfo(name = "new_employee_id")
    private long newEmployeeId;

    @ColumnInfo(name = "current_location_id")
    private long currentLocationId;

    @ColumnInfo(name = "new_location_id")
    private long newLocationId;

    @Ignore
    public Inventory(){}

    public Inventory(long assetBarcode, long currentEmployeeId, long newEmployeeId, long currentLocationId, long newLocationId) {
        this.assetBarcode = assetBarcode;
        this.currentEmployeeId = currentEmployeeId;
        this.newEmployeeId = newEmployeeId;
        this.currentLocationId = currentLocationId;
        this.newLocationId = newLocationId;
    }

    public long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public long getAssetBarcode() {
        return assetBarcode;
    }

    public void setAssetBarcode(long assetBarcode) {
        this.assetBarcode = assetBarcode;
    }

    public long getCurrentEmployeeId() {
        return currentEmployeeId;
    }

    public void setCurrentEmployeeId(long currentEmployeeId) {
        this.currentEmployeeId = currentEmployeeId;
    }

    public long getNewEmployeeId() {
        return newEmployeeId;
    }

    public void setNewEmployeeId(long newEmployeeId) {
        this.newEmployeeId = newEmployeeId;
    }

    public long getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(long currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public long getNewLocationId() {
        return newLocationId;
    }

    public void setNewLocationId(long newLocationId) {
        this.newLocationId = newLocationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return inventoryId == inventory.inventoryId && assetBarcode == inventory.assetBarcode && currentEmployeeId == inventory.currentEmployeeId && newEmployeeId == inventory.newEmployeeId && currentLocationId == inventory.currentLocationId && newLocationId == inventory.newLocationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId, assetBarcode, currentEmployeeId, newEmployeeId, currentLocationId, newLocationId);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId=" + inventoryId +
                ", assetBarcode=" + assetBarcode +
                ", currentEmployeeId=" + currentEmployeeId +
                ", newEmployeeId=" + newEmployeeId +
                ", currentLocationId=" + currentLocationId +
                ", newLocationId=" + newLocationId +
                '}';
    }
}
