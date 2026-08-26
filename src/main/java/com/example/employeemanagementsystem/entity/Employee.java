package com.example.employeemanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "employees", uniqueConstraints = { @UniqueConstraint(name = "uk_employee_email", columnNames = "email") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Name is required")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Department is required")
	@Column(nullable = false)
	private String department;

	@NotNull(message = "Salary is required")
	@Min(value = 0, message = "Salary must be greater than or equal to 0")
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal salary;
}