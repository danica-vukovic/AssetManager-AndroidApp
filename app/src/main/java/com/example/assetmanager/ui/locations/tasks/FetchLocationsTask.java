package com.example.assetmanager.ui.locations.tasks;
import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.assets.AddAssetFragment;
import com.example.assetmanager.ui.inventory.AddInventoryItemFragment;
import com.example.assetmanager.ui.inventory.InventoryFragment;
import com.example.assetmanager.ui.locations.LocationsFragment;
import java.lang.ref.WeakReference;
import java.util.List;

public class FetchLocationsTask extends AsyncTask<Void, Void, List<Location>> {
    private final WeakReference<Fragment> weakReference;

    public FetchLocationsTask(Fragment fragment) {
        this.weakReference = new WeakReference<>(fragment);
    }

    @Override
    protected List<Location> doInBackground(Void... voids) {
        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getLocationDao().getLocations();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<Location> locations) {
        super.onPostExecute(locations);
        Fragment fragment = weakReference.get();
        if (fragment != null && locations != null) {
            if(fragment instanceof AddAssetFragment) {
                ((AddAssetFragment) fragment).onLocationsFetched(locations);
            }else if(fragment instanceof LocationsFragment){
                ((LocationsFragment) fragment).onLocationsFetched(locations);
            }else if(fragment instanceof AddInventoryItemFragment){
                ((AddInventoryItemFragment) fragment).onLocationsFetched(locations);
            }else if(fragment instanceof InventoryFragment) {
                ((InventoryFragment) fragment).onLocationsFetched(locations);
            }
        }
    }
}


