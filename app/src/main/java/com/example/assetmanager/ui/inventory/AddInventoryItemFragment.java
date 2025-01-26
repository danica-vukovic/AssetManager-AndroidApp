package com.example.assetmanager.ui.inventory;

import static android.app.Activity.RESULT_OK;
import static com.example.assetmanager.util.Constants.REQUEST_PERMISSION_CODE_SCAN;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.assets.tasks.FetchAssetByBarcode;
import com.example.assetmanager.ui.employees.tasks.FetchEmployeesTask;
import com.example.assetmanager.ui.inventory.tasks.InventoryAssetTask;
import com.example.assetmanager.ui.locations.tasks.FetchLocationsTask;
import com.example.assetmanager.util.Constants;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AddInventoryItemFragment extends Fragment {

    private EditText editBarcode;
    private TextView textAssetName, textCurrentEmployee, textCurrentLocation;
    private Spinner spinnerNewEmployee, spinnerNewLocation;

    protected List<Location> locations = new ArrayList<>();

    protected List<Employee> employees = new ArrayList<>();

    private Inventory currentInventoryItem;
    private Asset asset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_inventory_item, container, false);

        if (getArguments() != null && getArguments().containsKey("inventory")) {
            currentInventoryItem = (Inventory) getArguments().getSerializable("inventory");
            if (getActivity() != null)
                Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.edit_inventory);
        }
        new FetchLocationsTask(this).execute();
        new FetchEmployeesTask(this).execute();

        editBarcode = view.findViewById(R.id.editBarcode);
        textAssetName = view.findViewById(R.id.textAssetName);
        textCurrentEmployee = view.findViewById(R.id.textCurrentEmployee);
        textCurrentLocation = view.findViewById(R.id.textCurrentLocation);
        TextView textScanBarcode = view.findViewById(R.id.textScanBarcode);
        spinnerNewEmployee = view.findViewById(R.id.spinnerNewEmployee);
        spinnerNewLocation = view.findViewById(R.id.spinnerNewLocation);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button search = view.findViewById(R.id.btnSearch);
        ImageView imageScanBarcode = view.findViewById(R.id.imageScanBarcode);


        imageScanBarcode.setOnClickListener(v -> scanBarcode());

        editBarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                new FetchAssetByBarcode(AddInventoryItemFragment.this,
                        Long.parseLong(editBarcode.getText().toString().trim())).execute();
                return true;
            }
            return false;
        });
        search.setOnClickListener(v -> new FetchAssetByBarcode(AddInventoryItemFragment.this,
                Long.parseLong(editBarcode.getText().toString().trim())).execute());
        btnSave.setOnClickListener(v -> saveInventoryItem());

        if (currentInventoryItem != null) {
            editBarcode.setText(String.valueOf(currentInventoryItem.getAssetBarcode()));
            editBarcode.setEnabled(false);
            imageScanBarcode.setVisibility(View.GONE);
            textScanBarcode.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            new FetchAssetByBarcode(AddInventoryItemFragment.this,
                    Long.parseLong(editBarcode.getText().toString().trim())).execute();
        }
        return view;
    }

    public void scanBarcode() {
        Intent intent = new Intent(getContext(), CaptureActivity.class);
        startActivityForResult(intent, REQUEST_PERMISSION_CODE_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_PERMISSION_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String barcode = data.getStringExtra("SCAN_RESULT");
                if (barcode != null) {
                    editBarcode.setText(barcode);
                    new FetchAssetByBarcode(AddInventoryItemFragment.this,
                            Long.parseLong(editBarcode.getText().toString().trim())).execute();
                }
            }
        }
    }

    private void setAssetDetails() {

        if (asset != null) {
            Location location = findLocationById(asset.getLocationId());
            Employee employee = findEmployeeById(asset.getEmployeeId());
            textAssetName.setText(String.format("%s:  %s", getString(R.string.asset_name), asset.getName()));
            textCurrentEmployee.setText(String.format("%s  %s", getString(R.string.currently_assigned_employee), employee));
            textCurrentLocation.setText(String.format("%s  %s", getString(R.string.current_location), location));
        }
    }

    private Location findLocationById(long locationId) {
        for (Location location : locations) {
            if (location.getLocationId() == locationId) {
                return location;
            }
        }
        return null;
    }

    private Employee findEmployeeById(long employeeId) {
        for (Employee employee : employees) {
            if (employee.getEmployeeId() == employeeId) {
                return employee;
            }
        }
        return null;
    }

    public void onLocationsFetched(List<Location> locations) {
        if (locations != null) {
            this.locations = locations;

            ArrayAdapter<Location> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    locations
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNewLocation.setAdapter(adapter);

        }
    }

    public void onEmployeesFetched(List<Employee> employees) {
        if (employees != null) {
            this.employees = employees;
            ArrayAdapter<Employee> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    employees
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNewEmployee.setAdapter(adapter);

        }
    }

    private void saveInventoryItem() {
        if (editBarcode.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), R.string.barcode_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        long newEmployee = ((Employee) spinnerNewEmployee.getSelectedItem()).getEmployeeId();
        long newLocation = ((Location) spinnerNewLocation.getSelectedItem()).getLocationId();
        long currentEmployee = asset.getEmployeeId();
        long currentLocation = asset.getLocationId();
        long barcode = Long.parseLong(editBarcode.getText().toString());
        asset.setLocationId(newLocation);
        asset.setEmployeeId(newEmployee);
        if (currentInventoryItem != null) {
            if (currentInventoryItem.getNewEmployeeId() == newEmployee && currentInventoryItem.getNewLocationId() == newLocation) {
                Toast.makeText(getContext(), R.string.no_changes_detected, Toast.LENGTH_SHORT).show();
                return;
            }

            currentInventoryItem.setNewEmployeeId(newEmployee);
            currentInventoryItem.setNewLocationId(newLocation);
            new InventoryAssetTask(AddInventoryItemFragment.this, currentInventoryItem, asset, false).execute();
        } else {
            Inventory inventory = new Inventory(barcode, currentEmployee, newEmployee, currentLocation, newLocation);

            new InventoryAssetTask(AddInventoryItemFragment.this, inventory, asset, true).execute();
            clearFields();
        }
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
        setAssetDetails();
    }

    private void clearFields() {
        editBarcode.setText("");
        spinnerNewEmployee.setSelection(0);
        spinnerNewLocation.setSelection(0);
        textAssetName.setText("");
        textCurrentEmployee.setText("");
        textCurrentLocation.setText("");
    }
}