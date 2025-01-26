package com.example.assetmanager.ui.locations.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.employees.EmployeesFragment;
import com.example.assetmanager.ui.locations.LocationsFragment;

import java.lang.ref.WeakReference;

public class AddLocationTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<LocationsFragment> fragmentWeakReference;
    private final Location location;

    public AddLocationTask(LocationsFragment fragment, Location location) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.location = location;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        LocationsFragment fragment = fragmentWeakReference.get();
        boolean result = false;
        if (fragment != null && location != null) {
            try {
                AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
                long res = db.getLocationDao().insertLocation(location);
                result = res > 0;
            } catch (Exception e) {
                Log.e("AddLocationTask", "Error adding location", e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        LocationsFragment fragment = fragmentWeakReference.get();
        if (fragment != null) {
            if (success) {
                new FetchLocationsTask(fragment).execute();
                Toast.makeText(fragment.getContext(), R.string.location_saved, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.failed_to_save_location, Toast.LENGTH_SHORT).show();
            }
            fragment.clearData();
        }
    }
}
