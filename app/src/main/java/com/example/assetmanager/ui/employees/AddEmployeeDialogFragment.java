package com.example.assetmanager.ui.employees;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.ui.employees.tasks.AddEmployeeTask;

public class AddEmployeeDialogFragment extends DialogFragment {
    private EditText etFirstName, etLastName, etEmail;
    private final EmployeesFragment employeesFragment;
    private Button btnAdd;

    public AddEmployeeDialogFragment(EmployeesFragment fragment) {
        this.employeesFragment = fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_employee, container, false);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        btnAdd = view.findViewById(R.id.btnAdd);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnAdd.setEnabled(false);

        etFirstName.addTextChangedListener(inputWatcher);
        etLastName.addTextChangedListener(inputWatcher);
        etEmail.addTextChangedListener(inputWatcher);

        btnAdd.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String email = etEmail.getText().toString();

            Employee employee = new Employee(firstName, lastName, email);
            new AddEmployeeTask(employeesFragment, employee).execute();
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    private final TextWatcher inputWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            validateInputs();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void validateInputs() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        btnAdd.setEnabled(!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty());
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
        }
    }
}
