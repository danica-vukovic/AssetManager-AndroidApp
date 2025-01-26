package com.example.assetmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Employee;

import java.util.List;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.EmployeeHolder> {

    private final List<Employee> list;
    private final LayoutInflater layoutInflater;
    private final OnEmployeeActionListener onEmployeeActionListener;

    public EmployeesAdapter(List<Employee> list, Context context, OnEmployeeActionListener listener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.onEmployeeActionListener = listener;
    }

    @NonNull
    @Override
    public EmployeeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_employee, parent, false);
        return new EmployeeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeHolder holder, int position) {
        Employee employee = list.get(position);

        holder.tvFullName.setText(String.format("%s %s", employee.getFirstName(), employee.getLastName()));
        holder.tvEmail.setText(employee.getEmail());

        holder.ivUpdate.setOnClickListener(v -> onEmployeeActionListener.onEmployeeUpdate(employee));
        holder.ivDelete.setOnClickListener(v -> onEmployeeActionListener.onEmployeeDelete(employee));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class EmployeeHolder extends RecyclerView.ViewHolder {

        TextView tvFullName, tvEmail;
        ImageView ivUpdate;
        ImageView ivDelete;

        public EmployeeHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public interface OnEmployeeActionListener {
        void onEmployeeUpdate(Employee employee);
        void onEmployeeDelete(Employee employee);
    }
}
