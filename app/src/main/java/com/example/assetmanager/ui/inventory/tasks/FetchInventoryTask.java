package com.example.assetmanager.ui.inventory.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.ui.inventory.InventoryFragment;
import java.lang.ref.WeakReference;
import java.util.List;

public class FetchInventoryTask extends AsyncTask<Void, Void, List<Inventory>> {
    private final WeakReference<InventoryFragment> weakReference;

    public FetchInventoryTask(InventoryFragment assetFragment) {
        this.weakReference = new WeakReference<>(assetFragment);
    }

    @Override
    protected List<Inventory> doInBackground(Void... voids) {
        InventoryFragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getInventoryDao().getInventories();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<Inventory> inventoryItems) {
        super.onPostExecute(inventoryItems);
        InventoryFragment fragment = weakReference.get();
        if (fragment != null && inventoryItems != null) {
            fragment.setData(inventoryItems);
        }
    }
}
