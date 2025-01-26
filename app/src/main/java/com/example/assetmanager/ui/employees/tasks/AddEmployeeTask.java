package com.example.assetmanager.ui.employees.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.employees.EmployeesFragment;

import java.lang.ref.WeakReference;

public class AddEmployeeTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<EmployeesFragment> fragmentWeakReference;
    private final Employee employee;

    public AddEmployeeTask(EmployeesFragment fragment, Employee employee) {
        this.fragmentWeakReference = new WeakReference<>(fragment);
        this.employee = employee;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        EmployeesFragment fragment = fragmentWeakReference.get();
        boolean result = false;
        if (fragment != null && employee != null) {
            try {
                AssetManagerDatabase db = AssetManagerDatabase.getInstance(fragment.getContext());
                long res = db.getEmployeeDao().insertEmployee(employee);
                result = res > 0;
            } catch (Exception e) {
                Log.e("AddEmployeeTask", "Error adding employee", e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        EmployeesFragment fragment = fragmentWeakReference.get();
        if (fragment != null) {
            if (success) {
                Toast.makeText(fragment.getContext(), R.string.employee_added_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.failed_to_add_employee, Toast.LENGTH_SHORT).show();
            }
            fragment.refreshEmployeeList();
        }
    }
}
