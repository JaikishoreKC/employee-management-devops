package com.example.employeemanagementsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.employeemanagementsystem.bean.EmployeeBean;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeBean createEmployee(EmployeeBean employeeBean) {

		if (employeeRepository.existsByEmail(employeeBean.getEmail())) {
			throw new IllegalArgumentException("Employee with email " + employeeBean.getEmail() + " already exists");
		}

		Employee employee = toEntity(employeeBean);
		Employee savedEmployee = employeeRepository.save(employee);

		return toBean(savedEmployee);
	}

	public List<EmployeeBean> getAllEmployees() {
		return employeeRepository.findAll().stream().map(this::toBean).toList();
	}

	public EmployeeBean getEmployeeById(Long id) {

		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));

		return toBean(employee);
	}

	public EmployeeBean updateEmployee(Long id, EmployeeBean employeeBean) {

		Employee existingEmployee = employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));

		if (!existingEmployee.getEmail().equals(employeeBean.getEmail())
				&& employeeRepository.existsByEmail(employeeBean.getEmail())) {

			throw new IllegalArgumentException("Employee with email " + employeeBean.getEmail() + " already exists");
		}

		existingEmployee.setName(employeeBean.getName());
		existingEmployee.setEmail(employeeBean.getEmail());
		existingEmployee.setDepartment(employeeBean.getDepartment());
		existingEmployee.setSalary(employeeBean.getSalary());

		Employee updatedEmployee = employeeRepository.save(existingEmployee);

		return toBean(updatedEmployee);
	}

	public void deleteEmployee(Long id) {

		if (!employeeRepository.existsById(id)) {
			throw new EmployeeNotFoundException("Employee with id " + id + " not found");
		}

		employeeRepository.deleteById(id);
	}

	private Employee toEntity(EmployeeBean employeeBean) {

		return Employee.builder().id(employeeBean.getId()).name(employeeBean.getName()).email(employeeBean.getEmail())
				.department(employeeBean.getDepartment()).salary(employeeBean.getSalary()).build();
	}

	private EmployeeBean toBean(Employee employee) {

		return EmployeeBean.builder().id(employee.getId()).name(employee.getName()).email(employee.getEmail())
				.department(employee.getDepartment()).salary(employee.getSalary()).build();
	}
}