package com.example.assetmanager.data.model;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.assetmanager.util.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_ASSET,
        foreignKeys = {
            @ForeignKey(
                    entity = Employee.class,
                    parentColumns = "employee_id",
                    childColumns = "employee_id",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Location.class,
                    parentColumns = "location_id",
                    childColumns = "location_id",
                    onDelete = ForeignKey.CASCADE
            )
        },
        indices = {
                @Index(value = {"location_id"}),
                @Index(value = {"employee_id"})
        })
public class Asset implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "barcode")
    private long barcode;
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "creation_date")
    private Date creationDate;

    @ColumnInfo(name = "employee_id")
    private long employeeId;

    @ColumnInfo(name = "location_id")
    private long locationId;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @Ignore
    public Asset() {
    }

    public Asset(long barcode, String name, String description, double price, long employeeId, long locationId, String imageUrl) {
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.employeeId = employeeId;
        this.locationId = locationId;
        this.imageUrl = imageUrl;
        this.creationDate = new Date();
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return barcode == asset.barcode && Double.compare(price, asset.price) == 0 && employeeId == asset.employeeId && locationId == asset.locationId && Objects.equals(name, asset.name) && Objects.equals(description, asset.description) && Objects.equals(creationDate, asset.creationDate) && Objects.equals(imageUrl, asset.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode, name, description, price, creationDate, employeeId, locationId, imageUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "Asset{" +
                "barcode=" + barcode +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", creationDate=" + creationDate +
                ", employeeId=" + employeeId +
                ", locationId=" + locationId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
