package com.example.assetmanager.ui.assets.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.ui.assets.AssetsFragment;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SearchAssetTask extends AsyncTask<String, Void, List<Asset>> {
    private final WeakReference<AssetsFragment> weakReference;
    private final List<Asset> assetList;

    public SearchAssetTask(AssetsFragment fragment, List<Asset> assetList) {
        this.weakReference = new WeakReference<>(fragment);
        this.assetList = assetList;
    }

    @Override
    protected List<Asset> doInBackground(String... queries) {
        String query = queries[0];
        List<Asset> filtered = new ArrayList<>();

        for (Asset asset : assetList) {
            if (asset.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (String.valueOf(asset.getBarcode()).toLowerCase().contains(query.toLowerCase()))){
                filtered.add(asset);
            }
        }
        return filtered;
    }

    @Override
    protected void onPostExecute(List<Asset> result) {
        AssetsFragment fragment = weakReference.get();
        if (fragment != null) {
            fragment.updateAssetList(result);
        }
    }
}
