package com.example.assetmanager.ui.assets.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.ui.assets.AssetsFragment;
import com.example.assetmanager.ui.inventory.AddInventoryItemFragment;
import com.example.assetmanager.ui.inventory.InventoryFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class FetchAssetsTask extends AsyncTask<Void, Void, List<Asset>> {
    private final WeakReference<Fragment> weakReference;

    public FetchAssetsTask(Fragment assetFragment) {
        this.weakReference = new WeakReference<>(assetFragment);
    }

    @Override
    protected List<Asset> doInBackground(Void... voids) {
        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getAssetDao().getAssets();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<Asset> assets) {
        super.onPostExecute(assets);
        Fragment fragment = weakReference.get();
        if(fragment instanceof AssetsFragment){
            ((AssetsFragment)fragment).setData(assets);
        }else if(fragment instanceof InventoryFragment){
            ((InventoryFragment)fragment).onAssetsFetched(assets);
        }
    }
}