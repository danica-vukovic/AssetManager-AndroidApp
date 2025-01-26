package com.example.assetmanager.ui.employees.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.assets.AddAssetFragment;
import com.example.assetmanager.ui.employees.EmployeesFragment;
import com.example.assetmanager.ui.inventory.AddInventoryItemFragment;
import com.example.assetmanager.ui.inventory.InventoryFragment;

import java.lang.ref.WeakReference;
import java.util.List;

public class FetchEmployeesTask extends AsyncTask<Void, Void, List<Employee>> {
    private final WeakReference<Fragment> weakReference;

    public FetchEmployeesTask(Fragment fragment) {
        this.weakReference = new WeakReference<>(fragment);
    }

    @Override
    protected List<Employee> doInBackground(Void... voids) {
        Fragment fragment = weakReference.get();
        if (fragment != null) {
            AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
            return db.getEmployeeDao().getEmployees();
        }
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<Employee> employees) {
        super.onPostExecute(employees);
        Fragment fragment = weakReference.get();
        if (fragment != null && employees != null) {
            if(fragment instanceof EmployeesFragment) {
                ((EmployeesFragment)fragment).getEmployeeList().clear();
                ((EmployeesFragment)fragment).getEmployeeList().addAll(employees);
                ((EmployeesFragment)fragment).filterList(employees);
            }else if(fragment instanceof AddAssetFragment){
                ((AddAssetFragment)fragment).onEmployeesFetched(employees);
            }else if(fragment instanceof AddInventoryItemFragment){
                ((AddInventoryItemFragment)fragment).onEmployeesFetched(employees);
            }else if(fragment instanceof InventoryFragment){
                ((InventoryFragment)fragment).onEmployeesFetched(employees);
            }
        }
    }
}
