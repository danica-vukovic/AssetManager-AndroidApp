package com.example.assetmanager.ui.locations.tasks;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.locations.LocationsFragment;
import java.lang.ref.WeakReference;

public class UpdateLocationTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<LocationsFragment> fragmentWeakReference;
    private final Location location;

    public UpdateLocationTask(LocationsFragment fragment, Location location) {
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
                db.getLocationDao().updateLocation(location);
                result = true;
            } catch (Exception e) {
                Log.e("UpdateLocationTask", "Error updating location", e);
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
                Toast.makeText(fragment.getContext(), R.string.location_updated_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.failed_to_update_location, Toast.LENGTH_SHORT).show();
            }
            fragment.clearData();
            new FetchLocationsTask(fragment).execute();
        }
    }
}
