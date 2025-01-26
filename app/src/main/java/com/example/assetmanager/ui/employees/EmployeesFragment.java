package com.example.assetmanager.ui.employees;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.assetmanager.R;
import com.example.assetmanager.adapter.EmployeesAdapter;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.employees.tasks.DeleteEmployeeTask;
import com.example.assetmanager.ui.employees.tasks.FetchEmployeesTask;
import com.example.assetmanager.ui.employees.tasks.SearchEmployeeTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class EmployeesFragment extends Fragment implements EmployeesAdapter.OnEmployeeActionListener {
    protected EmployeesAdapter adapter;
    protected List<Employee> employeeList;
    protected List<Employee> filteredList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        employeeList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new EmployeesAdapter(filteredList, getContext(), this);
        recyclerView.setAdapter(adapter);

        new FetchEmployeesTask(this).execute();

        FloatingActionButton fabAddEmployee = view.findViewById(R.id.fabAddEmployee);
        fabAddEmployee.setOnClickListener(v -> {
            AddEmployeeDialogFragment addEmployeeDialog = new AddEmployeeDialogFragment(this);
            addEmployeeDialog.show(getParentFragmentManager(), "AddEmployeeDialog");
        });

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint( getString(R.string.search_by_name_surname_or_email));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    filterList(employeeList);
                } else {
                    new SearchEmployeeTask(EmployeesFragment.this, employeeList).execute(newText);
                }
                return true;
            }
        });
        return view;
    }


    @Override
    public void onEmployeeUpdate(Employee employee) {
        UpdateEmployeeDialogFragment dialogFragment = new UpdateEmployeeDialogFragment(this);

        Bundle bundle = new Bundle();
        bundle.putSerializable("employee", employee);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getParentFragmentManager(), "UpdateEmployeeDialog");
    }

    @Override
    public void onEmployeeDelete(Employee employee) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_deletion)
                .setMessage(R.string.are_you_sure_you_want_to_delete_this_employee)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.yes, (dialog, which) -> new DeleteEmployeeTask(this, employee).execute())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshEmployeeList() {
        new FetchEmployeesTask(this).execute();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Employee> employees) {
        filteredList.clear();
        filteredList.addAll(employees);
        adapter.notifyDataSetChanged();
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }
}