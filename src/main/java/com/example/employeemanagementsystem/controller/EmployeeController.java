package com.example.employeemanagementsystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeemanagementsystem.bean.EmployeeBean;
import com.example.employeemanagementsystem.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@PostMapping
	public ResponseEntity<EmployeeBean> createEmployee(@Valid @RequestBody EmployeeBean employeeBean) {

		EmployeeBean createdEmployee = employeeService.createEmployee(employeeBean);

		return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
	}

	@GetMapping
	public ResponseEntity<List<EmployeeBean>> getAllEmployees() {

		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmployeeBean> getEmployeeById(@PathVariable Long id) {

		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmployeeBean> updateEmployee(@PathVariable Long id,
			@Valid @RequestBody EmployeeBean employeeBean) {

		return ResponseEntity.ok(employeeService.updateEmployee(id, employeeBean));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

		employeeService.deleteEmployee(id);

		return ResponseEntity.noContent().build();
	}
}