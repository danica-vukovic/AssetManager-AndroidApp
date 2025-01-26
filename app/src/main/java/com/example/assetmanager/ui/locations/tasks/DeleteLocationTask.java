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

public class DeleteLocationTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<LocationsFragment> fragmentWeakReference;
    private final Location location;

    public DeleteLocationTask(LocationsFragment fragment, Location location) {
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
                db.getLocationDao().deleteLocation(location);
                result = true;
            } catch (Exception e) {
                Log.e("DeleteLocationTask", "Error deleting location", e);
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
                Toast.makeText(fragment.getContext(), R.string.location_deleted_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.failed_to_delete_location, Toast.LENGTH_SHORT).show();
            }
            new FetchLocationsTask(fragment).execute();
        }
    }
}