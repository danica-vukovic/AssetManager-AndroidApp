package com.example.assetmanager.ui.locations.tasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Location;
import com.example.assetmanager.ui.assets.AssetDetailFragment;
import java.lang.ref.WeakReference;

public class FetchLocationTask extends AsyncTask<Void, Void, Location> {
    private final WeakReference<Fragment> weakReference;
    private final int id;

    public FetchLocationTask(Fragment fragment, long id) {
        this.weakReference = new WeakReference<>(fragment);
        this.id = (int) id;
    }

    @Override
    protected Location doInBackground(Void... voids) {

        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getLocationDao().getLocationById(id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Location location) {
        super.onPostExecute(location);
        Fragment fragment = weakReference.get();
        if (fragment != null && location != null) {
            if (fragment instanceof AssetDetailFragment) {
                ((AssetDetailFragment) fragment).setLocation(location);
                ((AssetDetailFragment) fragment).onTaskCompleted();
            }
        }
    }
}

