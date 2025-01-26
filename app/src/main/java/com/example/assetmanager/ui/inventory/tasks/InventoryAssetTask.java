package com.example.assetmanager.ui.inventory.tasks;

import android.os.AsyncTask;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.room.Database;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Asset;
import com.example.assetmanager.data.model.Inventory;
import java.lang.ref.WeakReference;

public class InventoryAssetTask extends AsyncTask<Void, Void, Boolean> {

    private final WeakReference<Fragment> weakReference;
    private final Inventory inventory;
    private final Asset asset;
    private final boolean isInsert;

    public InventoryAssetTask(Fragment fragment, Inventory inventory, Asset asset, boolean isInsert) {
        this.inventory = inventory;
        this.asset = asset;
        this.isInsert = isInsert;
        this.weakReference = new WeakReference<>(fragment);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Fragment fragment = weakReference.get();
        if (isInsert) {
            return AssetManagerDatabase.getInstance(fragment.getContext())
            .getInventoryAssetDao().insertInventoryAndUpdateAsset(inventory, asset);
        } else {
            return AssetManagerDatabase.getInstance(fragment.getContext())
                    .getInventoryAssetDao().updateInventoryAndUpdateAsset(inventory, asset);
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        Fragment fragment = weakReference.get();
        if (success) {
            Toast.makeText(fragment.getContext(), R.string.inventory_item_saved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(fragment.getContext(), R.string.failed_to_save_inventory_item, Toast.LENGTH_SHORT).show();
        }
    }
}
