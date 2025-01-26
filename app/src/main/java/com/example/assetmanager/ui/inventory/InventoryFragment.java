package com.example.assetmanager.ui.inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.assetmanager.R;
import com.example.assetmanager.adapter.InventoryAdapter;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.assets.tasks.FetchAssetsTask;
import com.example.assetmanager.ui.employees.tasks.FetchEmployeesTask;
import com.example.assetmanager.ui.inventory.tasks.DeleteInventoryItemTask;
import com.example.assetmanager.ui.inventory.tasks.FetchInventoryTask;
import com.example.assetmanager.ui.locations.tasks.FetchLocationsTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment implements InventoryAdapter.OnInventoryActionListener {
    private final List<Inventory> inventoryItems = new ArrayList<>();

    protected List<Asset> assets = new ArrayList<>();

    protected List<Location> locations = new ArrayList<>();

    protected List<Employee> employees = new ArrayList<>();
    protected InventoryAdapter adapter;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InventoryAdapter(inventoryItems, assets, locations, employees, getContext(), this);
        recyclerView.setAdapter(adapter);
        new FetchLocationsTask(this).execute();
        new FetchEmployeesTask(this).execute();
        new FetchAssetsTask(this).execute();
        new FetchInventoryTask(this).execute();
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setTooltipText(getString(R.string.search_by_barcode_employee_name_or_location));
        FloatingActionButton fabAddAsset = view.findViewById(R.id.fabAddInventory);
        fabAddAsset.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_inventoryFragment_to_addInventoryItemFragment);
        });
        searchView.setQueryHint(getString(R.string.search_by_barcode_employee_name_or_location));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onInventoryUpdate(Inventory inventory) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("inventory", inventory);
        AddInventoryItemFragment fragment = new AddInventoryItemFragment();
        fragment.setArguments(bundle);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_inventoryFragment_to_addInventoryItemFragment, bundle);
    }

    @Override
    public void onInventoryDelete(Inventory inventory) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirm_deletion))
                .setMessage(R.string.are_you_sure_you_want_to_delete_this_inventory_item)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {

                    new DeleteInventoryItemTask(this, inventory).execute();
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private boolean isAllDataFetched() {
        return !assets.isEmpty() && !employees.isEmpty() && !locations.isEmpty();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Inventory> inventoryList) {
        if (isAllDataFetched()) {
            adapter.updateInventoryList(inventoryList);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onLocationsFetched(List<Location> locations) {
        this.locations.clear();
        this.locations.addAll(locations);
        if (isAllDataFetched()) {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onAssetsFetched(List<Asset> assets) {
        this.assets.clear();
        this.assets.addAll(assets);
        if (isAllDataFetched()) {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onEmployeesFetched(List<Employee> employees) {
        this.employees.clear();
        this.employees.addAll(employees);
        if (isAllDataFetched()) {
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshInventoryList() {
        new FetchInventoryTask(this).execute();
    }
}