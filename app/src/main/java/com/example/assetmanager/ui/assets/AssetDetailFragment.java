package com.example.assetmanager.ui.assets;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.assets.tasks.FetchAssetsByLocationTask;
import com.example.assetmanager.ui.employees.tasks.FetchEmployeeTask;
import com.example.assetmanager.ui.locations.tasks.FetchLocationTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AssetDetailFragment extends Fragment {

    private ImageView imageAsset;
    private Employee employee;
    private Location location;
    private int tasksRemaining = 3;
    private TextView textAssignedEmployee;
    private TextView textLocationName;
    private MapView mapView;
    private List<Asset> assetsInCity = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_detail, container, false);

        assert getArguments() != null;
        Asset asset = (Asset) getArguments().getSerializable("asset");

        TextView textAssetName = view.findViewById(R.id.textAssetName);
        TextView textAssetDescription = view.findViewById(R.id.textAssetDescription);
        TextView textAssetPrice = view.findViewById(R.id.textAssetPrice);
        imageAsset = view.findViewById(R.id.imageAsset);
        textAssignedEmployee = view.findViewById(R.id.textAssignedEmployee);
        textLocationName = view.findViewById(R.id.textLocationName);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        assert asset != null;
        textAssetName.setText(asset.getName());
        textAssetDescription.setText(asset.getDescription());
        textAssetPrice.setText(String.format("%s KM", asset.getPrice()));

        new FetchEmployeeTask(this, asset.getEmployeeId()).execute();
        new FetchLocationTask(this, asset.getLocationId()).execute();

        loadImageFromExternalStorage(asset.getImageUrl());

        new FetchAssetsByLocationTask(this, asset.getLocationId()).execute();

        return view;

    }

    private void loadImageFromExternalStorage(String fileName) {
        File externalDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(externalDir, fileName);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageAsset.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), getString(R.string.image_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private void showAssetsListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.assets_in) + location.getName());
        String[] assetNames = assetsInCity.stream().map(Asset::getName).toArray(String[]::new);
        builder.setItems(assetNames, (dialog, which) -> {
        });
        builder.setPositiveButton(getString(R.string.close), null);
        builder.show();
    }

    public void setAssets(List<Asset> assets) {
        this.assetsInCity = assets;
    }

    public void onTaskCompleted() {
        tasksRemaining--;
        if (tasksRemaining == 0) {
            updateUI();
        }
    }

    private void updateUI() {
        if (employee != null && location != null && !assetsInCity.isEmpty()) {

            textAssignedEmployee.setText(String.format("%s %s", employee.getFirstName(), employee.getLastName()));
            textLocationName.setText(location.getName());
            mapView.getMapAsync(googleMap -> {
                LatLng cityCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(cityCoordinates).title(getString(R.string.city)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCoordinates, 10));

                googleMap.setOnMarkerClickListener(marker -> {
                    showAssetsListDialog();
                    return true;
                });

            });

        } else {
            Toast.makeText(getContext(), getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
        }
    }
}