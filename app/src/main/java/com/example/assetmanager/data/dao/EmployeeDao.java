package com.example.assetmanager.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assetmanager.data.model.Employee;
import com.example.assetmanager.util.Constants;

import java.util.List;

@Dao
public interface EmployeeDao {
    @Insert
    long insertEmployee(Employee employee);
    @Update
    void updateEmployee(Employee employee);
    @Delete
    void deleteEmployee(Employee employee);
    @Delete
    void deleteEmployees(Employee ... employee);
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE)
    List<Employee> getEmployees();
    @Query("SELECT * FROM " + Constants.TABLE_NAME_EMPLOYEE + " WHERE employee_id = :id LIMIT 1")
    Employee getEmployeeById(long id);

}
