package com.example.assetmanager.ui.locations;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.adapter.LocationAdapter;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.locations.tasks.AddLocationTask;
import com.example.assetmanager.ui.locations.tasks.DeleteLocationTask;
import com.example.assetmanager.ui.locations.tasks.FetchLocationsTask;
import com.example.assetmanager.ui.locations.tasks.SearchLocationTask;
import com.example.assetmanager.ui.locations.tasks.UpdateLocationTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationsFragment extends Fragment implements OnMapReadyCallback, LocationAdapter.OnLocationActionListener {
    private GoogleMap googleMap;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LocationAdapter locationAdapter;
    private double selectedLatitude, selectedLongitude;
    private TextView textViewLatitude, textViewLongitude;
    private EditText editTextLocationName;
    private Button btnSaveLocation;
    private Button btnUpdateLocation;
    private final List<Location> locations = new ArrayList<>();
    protected List<Location> allLocations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_locations, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLocations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        locationAdapter = new LocationAdapter(locations, this::onLocationClicked, this);
        recyclerView.setAdapter(locationAdapter);

        textViewLatitude = view.findViewById(R.id.textViewLatitude);
        textViewLongitude = view.findViewById(R.id.textViewLongitude);
        editTextLocationName = view.findViewById(R.id.editTextLocationName);
        btnSaveLocation = view.findViewById(R.id.btnSaveLocation);
        btnUpdateLocation = view.findViewById(R.id.btnUpdateLocation);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint((getString(R.string.search_by_name)));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search query here (optional action when search is submitted)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateLocationList(allLocations);
                } else {
                    new SearchLocationTask(LocationsFragment.this, locations).execute(newText);
                }
                return true;
            }
        });

        // Setup BottomSheet
        LinearLayout bottomSheet = view.findViewById(R.id.bottomSheet);
        ImageView arrowIcon = view.findViewById(R.id.iv_arrow_icon);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setPeekHeight(300);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    arrowIcon.animate().rotation(0).start();
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    arrowIcon.animate().rotation(180).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        btnSaveLocation.setOnClickListener(v -> {
            if (editTextLocationName.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_location_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            String locationName = editTextLocationName.getText().toString();
            Location location = new Location(locationName, selectedLatitude, selectedLongitude);
            new AddLocationTask(this, location).execute();
        });

        btnCancel.setOnClickListener(v -> clearData());

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        new FetchLocationsTask(this).execute();
        for (Location location : locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(location.getName()));
        }

        if (!locations.isEmpty()) {
            LatLng firstLocation = new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f));
        }

        googleMap.setOnMapClickListener(latLng -> {

            selectedLatitude = latLng.latitude;
            selectedLongitude = latLng.longitude;

            textViewLatitude.setText(getString(R.string.latitude) + selectedLatitude);
            textViewLongitude.setText(getString(R.string.longitude) + selectedLongitude);

            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String locationName = addresses.get(0).getLocality();
                    editTextLocationName.setText(locationName != null ? locationName : getString(R.string.unknown_location));
                } else {
                    editTextLocationName.setText(R.string.unknown_location);
                }
            } catch (Exception e) {
                editTextLocationName.setText(R.string.error_retrieving_location);
            }

            btnSaveLocation.setEnabled(!editTextLocationName.getText().toString().trim().isEmpty());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    private void onLocationClicked(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onLocationsFetched(List<Location> fetchedLocations) {
        locations.clear();
        allLocations.clear();
        locations.addAll(fetchedLocations);
        allLocations.addAll(fetchedLocations);
        locationAdapter.notifyDataSetChanged();

        for (Location location : locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title(location.getName()));
        }

        if (!locations.isEmpty()) {
            LatLng firstLocation = new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLocationList(List<Location> filteredLocations) {
        locations.clear();
        locations.addAll(filteredLocations);
        locationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationUpdate(Location location) {

        editTextLocationName.setText(location.getName());
        selectedLatitude = location.getLatitude();
        selectedLongitude = location.getLongitude();

        textViewLatitude.setText(getString(R.string.latitude) + selectedLatitude);
        textViewLongitude.setText(getString(R.string.longitude) + selectedLongitude);

        btnSaveLocation.setVisibility(GONE);
        btnUpdateLocation.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        btnUpdateLocation.setOnClickListener(v -> {
            String updatedLocationName = editTextLocationName.getText().toString();

            if (updatedLocationName.equals(getString(R.string.location_name))) {
                Toast.makeText(requireContext(), getString(R.string.no_location_selected), Toast.LENGTH_SHORT).show();
                return;
            }
            location.setName(updatedLocationName);
            location.setLatitude(selectedLatitude);
            location.setLongitude(selectedLongitude);

            new UpdateLocationTask(this, location).execute();
        });
    }

    @Override
    public void onLocationDelete(Location location) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirm_deletion))
                .setMessage(R.string.are_you_sure_you_want_to_delete_this_location)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    new DeleteLocationTask(this, location).execute();
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    public void clearData() {
        btnUpdateLocation.setVisibility(GONE);
        btnSaveLocation.setVisibility(View.VISIBLE);
        editTextLocationName.setText("");
        textViewLatitude.setText(getString(R.string.latitude));
        textViewLongitude.setText(getString(R.string.longitude));
    }
}
