package com.example.assetmanager.ui.employees.tasks;

import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.assets.AssetDetailFragment;
import com.example.assetmanager.ui.employees.EmployeesFragment;
import com.example.assetmanager.ui.inventory.AddInventoryItemFragment;

import java.lang.ref.WeakReference;

public class FetchEmployeeTask extends AsyncTask<Void, Void, Employee> {
    private final WeakReference<Fragment> weakReference;
    private final long id;

    public FetchEmployeeTask(Fragment fragment, long id) {
        this.weakReference = new WeakReference<>(fragment);
        this.id = id;
    }

    @Override
    protected Employee doInBackground(Void... voids) {

        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getEmployeeDao().getEmployeeById(id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Employee employee) {
        super.onPostExecute(employee);
        Fragment fragment = weakReference.get();
        if (fragment != null && employee != null) {
            if (fragment instanceof AssetDetailFragment) {
                ((AssetDetailFragment) fragment).setEmployee(employee);
                ((AssetDetailFragment) fragment).onTaskCompleted();
            }
        }
    }
}
