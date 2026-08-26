package com.example.employeemanagementsystem.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.employeemanagementsystem.bean.EmployeeBean;
import com.example.employeemanagementsystem.service.EmployeeService;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

	private static final String EMPLOYEES_REDIRECT = "redirect:/employees";

	private final EmployeeService employeeService;

	public EmployeeViewController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping
	public String listEmployees(Model model) {
		List<EmployeeBean> employees = employeeService.getAllEmployees();
		model.addAttribute("employees", employees);
		return "employees";
	}

	@GetMapping("/add")
	public String showAddEmployeeForm(Model model) {
		model.addAttribute("employee", new EmployeeBean());
		return "employee-form";
	}

	@PostMapping("/save")
	public String saveEmployee(EmployeeBean employee) {
		employeeService.createEmployee(employee);
		return EMPLOYEES_REDIRECT;
	}
}