package com.example.assetmanager.ui.employees.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.assetmanager.R;
import com.example.assetmanager.data.database.AssetManagerDatabase;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.employees.EmployeesFragment;

import java.lang.ref.WeakReference;

public class DeleteEmployeeTask extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<EmployeesFragment> fragmentWeakReference;
    private final Employee employee;

    public DeleteEmployeeTask(EmployeesFragment fragment, Employee employee) {
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
                db.getEmployeeDao().deleteEmployee(employee);
                result = true;
            } catch (Exception e) {
                Log.e("DeleteEmployeeTask", "Error deleting employee", e);
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
                Toast.makeText(fragment.getContext(), R.string.employee_deleted_successfully, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getContext(), R.string.failed_to_delete_employee, Toast.LENGTH_SHORT).show();
            }
            fragment.refreshEmployeeList();
        }
    }
}
