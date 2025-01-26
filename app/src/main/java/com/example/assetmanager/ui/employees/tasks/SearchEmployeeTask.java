package com.example.assetmanager.ui.employees.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.employees.EmployeesFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SearchEmployeeTask extends AsyncTask<String, Void, List<Employee>> {
    private final WeakReference<EmployeesFragment> weakReference;
    private final List<Employee> employeeList;

    public SearchEmployeeTask(EmployeesFragment employeesFragment, List<Employee> employeeList) {
        this.weakReference = new WeakReference<>(employeesFragment);
        this.employeeList = employeeList;
    }

    @Override
    protected List<Employee> doInBackground(String... queries) {
        String query = queries[0];
        List<Employee> filtered = new ArrayList<>();

        for (Employee employee : employeeList) {
            if (employee.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    employee.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                    employee.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(employee);
            }
        }
        return filtered;
    }

    @Override
    protected void onPostExecute(List<Employee> result) {
        EmployeesFragment fragment = weakReference.get();
        if (fragment != null) {
            fragment.filterList(result);
        }
    }
}