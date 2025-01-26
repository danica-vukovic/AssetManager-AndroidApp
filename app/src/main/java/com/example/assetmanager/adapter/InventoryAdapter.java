package com.example.assetmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.data.model.Location;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryHolder> {

    private final List<Inventory> inventoryList;
    private final List<Asset> assetList;
    private final List<Location> locationList;
    private final List<Employee> employeeList;
    private final List<Inventory> filteredInventoryList;
    private final LayoutInflater layoutInflater;
    private final OnInventoryActionListener onInventoryActionListener;

    public InventoryAdapter(List<Inventory> inventoryList,
                            List<Asset> assetList,
                            List<Location> locationList,
                            List<Employee> employeeList,
                            Context context,
                            OnInventoryActionListener listener) {
        this.inventoryList = inventoryList;
        this.assetList = assetList;
        this.locationList = locationList;
        this.employeeList = employeeList;
        this.layoutInflater = LayoutInflater.from(context);
        this.onInventoryActionListener = listener;
        this.filteredInventoryList = new ArrayList<>(inventoryList);

    }

    @NonNull
    @Override
    public InventoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_inventory, parent, false);
        return new InventoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryHolder holder, int position) {
        Inventory inventory = filteredInventoryList.get(position);

        Asset asset = findAssetByBarcode(inventory.getAssetBarcode());
        holder.textAssetName.setText(asset != null ? asset.getName() : "Unknown Asset");

        Employee employee = findEmployeeById(inventory.getNewEmployeeId());
        holder.textCurrentEmployee.setText(employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown Employee");

        Location location = findLocationById(inventory.getNewLocationId());
        holder.textCurrentLocation.setText(location != null ? location.getName() : "Unknown Location");

        holder.textAssetBarcode.setText(String.valueOf(inventory.getAssetBarcode()));

        holder.ivUpdate.setOnClickListener(v -> onInventoryActionListener.onInventoryUpdate(inventory));
        holder.ivDelete.setOnClickListener(v -> onInventoryActionListener.onInventoryDelete(inventory));
    }

    @Override
    public int getItemCount() {
        return filteredInventoryList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String query) {
        query = query.toLowerCase();
        filteredInventoryList.clear();

        if (query.isEmpty()) {
            filteredInventoryList.addAll(inventoryList);
        } else {
            for (Inventory inventory : inventoryList) {
                Asset asset = findAssetByBarcode(inventory.getAssetBarcode());
                Location location = findLocationById(inventory.getNewLocationId());
                Employee employee = findEmployeeById(inventory.getNewEmployeeId());

                if ((asset != null && String.valueOf(asset.getBarcode()).toLowerCase().contains(query)) ||
                        (location != null && location.getName().toLowerCase().contains(query)) ||
                        (employee != null && (employee.getFirstName().toLowerCase().contains(query) ||
                                employee.getLastName().toLowerCase().contains(query)))) {
                    filteredInventoryList.add(inventory);
                }
            }
        }
        notifyDataSetChanged();
    }


    private Asset findAssetByBarcode(long barcode) {
        for (Asset asset : assetList) {
            if (asset.getBarcode() == barcode) {
                return asset;
            }
        }
        return null;
    }

    private Employee findEmployeeById(long employeeId) {
        for (Employee employee : employeeList) {
            if (employee.getEmployeeId() == employeeId) {
                return employee;
            }
        }
        return null;
    }

    private Location findLocationById(long locationId) {
        for (Location location : locationList) {
            if (location.getLocationId() == locationId) {
                return location;
            }
        }
        return null;
    }

    public static class InventoryHolder extends RecyclerView.ViewHolder {

        TextView textAssetBarcode, textAssetName, textCurrentEmployee, textCurrentLocation;
        ImageView ivUpdate, ivDelete;

        public InventoryHolder(@NonNull View itemView) {
            super(itemView);

            textAssetBarcode = itemView.findViewById(R.id.textAssetBarcode);
            textAssetName = itemView.findViewById(R.id.textAssetName);
            textCurrentEmployee = itemView.findViewById(R.id.textCurrentEmployee);
            textCurrentLocation = itemView.findViewById(R.id.textCurrentLocation);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public interface OnInventoryActionListener {
        void onInventoryUpdate(Inventory inventory);
        void onInventoryDelete(Inventory inventory);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateInventoryList(List<Inventory> newInventoryList) {
        inventoryList.clear();
        inventoryList.addAll(newInventoryList);

        filteredInventoryList.clear();
        filteredInventoryList.addAll(newInventoryList);

        notifyDataSetChanged();
    }
}
