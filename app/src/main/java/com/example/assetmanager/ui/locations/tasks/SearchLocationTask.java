package com.example.assetmanager.ui.locations.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.locations.LocationsFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SearchLocationTask extends AsyncTask<String, Void, List<Location>> {

    private final WeakReference<LocationsFragment> weakReference;
    private final List<Location> allLocations;

    public SearchLocationTask(LocationsFragment locationsFragment, List<Location> allLocations) {
        this.weakReference = new WeakReference<>(locationsFragment);
        this.allLocations = allLocations;
    }

    @Override
    protected List<Location> doInBackground(String... queries) {
        String query = queries[0].toLowerCase();
        List<Location> filteredLocations = new ArrayList<>();

        for (Location location : allLocations) {
            if (location.getName().toLowerCase().contains(query)) {
                filteredLocations.add(location);
            }
        }
        return filteredLocations;
    }

    @Override
    protected void onPostExecute(List<Location> filteredLocations) {
        super.onPostExecute(filteredLocations);

        LocationsFragment fragment = weakReference.get();
        if (fragment != null && filteredLocations != null) {
            fragment.updateLocationList(filteredLocations);
        }
    }
}

