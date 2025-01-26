package com.example.assetmanager.ui.inventory.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Inventory;
import com.example.assetmanager.ui.inventory.InventoryFragment;

import java.lang.ref.WeakReference;

public class DeleteInventoryItemTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<InventoryFragment> fragmentWeakReference;
    private final Inventory inventory;

    public DeleteInventoryItemTask(InventoryFragment fragment, Inventory inventory) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.inventory = inventory;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        InventoryFragment fragment = fragmentWeakReference.get();
        boolean result = false;
        if (fragment != null && inventory != null) {
            try {
                AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
                db.getInventoryDao().deleteInventory(inventory);
                result = true;
            } catch (Exception e) {
                Log.e("DeleteInventoryTask", "Error deleting inventory", e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        InventoryFragment fragment = fragmentWeakReference.get();
        if (fragment != null) {
            if (success) {
                Toast.makeText(fragment.getContext(), R.string.inventory_item_deleted_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.failed_to_delete_inventory_item, Toast.LENGTH_SHORT).show();
            }
            fragment.refreshInventoryList();
        }
    }
}
