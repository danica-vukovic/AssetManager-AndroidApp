package com.example.assetmanager.ui.assets.tasks;

import android.os.AsyncTask;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.ui.assets.AssetDetailFragment;
import java.lang.ref.WeakReference;
import java.util.List;

public class FetchAssetsByLocationTask extends AsyncTask<Void, Void, List<Asset>> {
    private final WeakReference<Fragment> weakReference;
    private final int id;

    public FetchAssetsByLocationTask(Fragment assetFragment, long id) {
        this.weakReference = new WeakReference<>(assetFragment);
        this.id = (int) id;
    }

    @Override
    protected List<Asset> doInBackground(Void... voids) {

        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getAssetDao().getAssetsByLocationId(id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Asset> assets) {
        super.onPostExecute(assets);
       AssetDetailFragment  fragment = (AssetDetailFragment) weakReference.get();
        if (fragment != null && assets != null) {
            fragment.setAssets(assets);
            fragment.onTaskCompleted();
        }
    }
}