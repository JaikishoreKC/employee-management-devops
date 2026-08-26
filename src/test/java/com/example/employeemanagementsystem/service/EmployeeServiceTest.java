package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.employeemanagementsystem.bean.EmployeeBean;
import com.example.employeemanagementsystem.entity.Employee;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private EmployeeService employeeService;

	private Employee employee;
	private EmployeeBean employeeBean;

	@BeforeEach
	void setUp() {

		employee = Employee.builder().id(1L).name("John Doe").email("john@example.com").department("IT")
				.salary(new BigDecimal("55000.00")).build();

		employeeBean = EmployeeBean.builder().id(1L).name("John Doe").email("john@example.com").department("IT")
				.salary(new BigDecimal("55000.00")).build();
	}

	@Test
	void createEmployee_ShouldCreateEmployee() {

		when(employeeRepository.existsByEmail(employeeBean.getEmail())).thenReturn(false);

		when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

		EmployeeBean result = employeeService.createEmployee(employeeBean);

		assertEquals(1L, result.getId());
		assertEquals("John Doe", result.getName());
		assertEquals("john@example.com", result.getEmail());
		assertEquals("IT", result.getDepartment());
		assertEquals(new BigDecimal("55000.00"), result.getSalary());

		verify(employeeRepository).existsByEmail("john@example.com");
		verify(employeeRepository).save(any(Employee.class));
	}

	@Test
	void createEmployee_ShouldThrowException_WhenEmailExists() {

		when(employeeRepository.existsByEmail(employeeBean.getEmail())).thenReturn(true);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> employeeService.createEmployee(employeeBean));

		assertEquals("Employee with email john@example.com already exists", exception.getMessage());

		verify(employeeRepository).existsByEmail("john@example.com");
		verify(employeeRepository, never()).save(any(Employee.class));
	}

	@Test
	void getAllEmployees_ShouldReturnEmployees() {

		when(employeeRepository.findAll()).thenReturn(List.of(employee));

		List<EmployeeBean> result = employeeService.getAllEmployees();

		assertEquals(1, result.size());
		assertEquals("John Doe", result.get(0).getName());
		assertEquals("john@example.com", result.get(0).getEmail());

		verify(employeeRepository).findAll();
	}

	@Test
	void getEmployeeById_ShouldReturnEmployee() {

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		EmployeeBean result = employeeService.getEmployeeById(1L);

		assertEquals(1L, result.getId());
		assertEquals("John Doe", result.getName());

		verify(employeeRepository).findById(1L);
	}

	@Test
	void getEmployeeById_ShouldThrowException_WhenNotFound() {

		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.getEmployeeById(99L));

		assertEquals("Employee with id 99 not found", exception.getMessage());

		verify(employeeRepository).findById(99L);
	}

	@Test
	void updateEmployee_ShouldUpdateEmployee() {

		EmployeeBean updateBean = EmployeeBean.builder().name("John Updated").email("john.updated@example.com")
				.department("Engineering").salary(new BigDecimal("65000.00")).build();

		Employee updatedEmployee = Employee.builder().id(1L).name("John Updated").email("john.updated@example.com")
				.department("Engineering").salary(new BigDecimal("65000.00")).build();

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		when(employeeRepository.existsByEmail("john.updated@example.com")).thenReturn(false);

		when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

		EmployeeBean result = employeeService.updateEmployee(1L, updateBean);

		assertEquals(1L, result.getId());
		assertEquals("John Updated", result.getName());
		assertEquals("john.updated@example.com", result.getEmail());
		assertEquals("Engineering", result.getDepartment());
		assertEquals(new BigDecimal("65000.00"), result.getSalary());

		verify(employeeRepository).findById(1L);
		verify(employeeRepository).existsByEmail("john.updated@example.com");
		verify(employeeRepository).save(any(Employee.class));
	}

	@Test
	void updateEmployee_ShouldThrowException_WhenEmployeeNotFound() {

		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.updateEmployee(99L, employeeBean));

		assertEquals("Employee with id 99 not found", exception.getMessage());

		verify(employeeRepository).findById(99L);
		verify(employeeRepository, never()).save(any(Employee.class));
	}

	@Test
	void updateEmployee_ShouldThrowException_WhenEmailAlreadyExists() {

		EmployeeBean updateBean = EmployeeBean.builder().name("John Updated").email("existing@example.com")
				.department("Engineering").salary(new BigDecimal("65000.00")).build();

		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		when(employeeRepository.existsByEmail("existing@example.com")).thenReturn(true);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> employeeService.updateEmployee(1L, updateBean));

		assertEquals("Employee with email existing@example.com already exists", exception.getMessage());

		verify(employeeRepository).findById(1L);
		verify(employeeRepository).existsByEmail("existing@example.com");
		verify(employeeRepository, never()).save(any(Employee.class));
	}

	@Test
	void deleteEmployee_ShouldDeleteEmployee() {

		when(employeeRepository.existsById(1L)).thenReturn(true);

		employeeService.deleteEmployee(1L);

		verify(employeeRepository).existsById(1L);
		verify(employeeRepository).deleteById(1L);
	}

	@Test
	void deleteEmployee_ShouldThrowException_WhenNotFound() {

		when(employeeRepository.existsById(99L)).thenReturn(false);

		EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.deleteEmployee(99L));

		assertEquals("Employee with id 99 not found", exception.getMessage());

		verify(employeeRepository).existsById(99L);
		verify(employeeRepository, never()).deleteById(99L);
	}
}