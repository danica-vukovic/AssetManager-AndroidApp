package com.example.assetmanager.ui.assets.tasks;

import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.ui.inventory.AddInventoryItemFragment;

import java.lang.ref.WeakReference;

public class FetchAssetByBarcode extends AsyncTask<Void, Void, Asset> {
    private final WeakReference<Fragment> weakReference;
    private final long id;

    public FetchAssetByBarcode(Fragment fragment, long id) {
        this.weakReference = new WeakReference<>(fragment);
        this.id = id;
    }

    @Override
    protected Asset doInBackground(Void... voids) {

        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getAssetDao().getAssetByBarcode(id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Asset asset) {
        super.onPostExecute(asset);
        Fragment fragment = weakReference.get();
        if (fragment != null && asset != null) {
            if (fragment instanceof AddInventoryItemFragment) {
                ((AddInventoryItemFragment) fragment).setAsset(asset);
            }
        }
    }
}