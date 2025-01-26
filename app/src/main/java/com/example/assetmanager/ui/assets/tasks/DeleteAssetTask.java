package com.example.assetmanager.ui.assets.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.ui.assets.AssetsFragment;

import java.lang.ref.WeakReference;

public class DeleteAssetTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<AssetsFragment> fragmentWeakReference;
    private final Asset asset;

    public DeleteAssetTask(AssetsFragment fragment, Asset asset) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.asset = asset;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        AssetsFragment fragment = fragmentWeakReference.get();
        boolean result = false;
        if (fragment != null && asset != null) {
            try {
                AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
                db.getAssetDao().deleteAssets(asset);
                result = true;
            } catch (Exception e) {
                Log.e("DeleteAssetTask", "Error deleting asset", e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        AssetsFragment fragment = fragmentWeakReference.get();
        if (fragment != null) {
            if (success) {
                Toast.makeText(fragment.getContext(), R.string.asset_deleted_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.asset_to_delete_employee, Toast.LENGTH_SHORT).show();
            }
            fragment.refreshAssetList();
        }
    }
}
