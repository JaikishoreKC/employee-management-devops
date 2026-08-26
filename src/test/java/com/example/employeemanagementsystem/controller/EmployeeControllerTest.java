package com.example.employeemanagementsystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.employeemanagementsystem.bean.EmployeeBean;
import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import com.example.employeemanagementsystem.exception.GlobalExceptionHandler;
import com.example.employeemanagementsystem.service.EmployeeService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private EmployeeService employeeService;

	@Test
	void createEmployee_ShouldReturnCreatedEmployee() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().id(1L).name("John Doe").email("john@example.com")
				.department("IT").salary(new BigDecimal("55000.00")).build();

		when(employeeService.createEmployee(any(EmployeeBean.class))).thenReturn(employee);

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("John Doe"))
				.andExpect(jsonPath("$.email").value("john@example.com"))
				.andExpect(jsonPath("$.department").value("IT"));

		verify(employeeService).createEmployee(any(EmployeeBean.class));
	}

	@Test
	void createEmployee_ShouldReturnBadRequest_WhenValidationFails() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().name("").email("invalid-email").department("")
				.salary(new BigDecimal("-100")).build();

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isBadRequest());

		verify(employeeService, never()).createEmployee(any(EmployeeBean.class));
	}

	@Test
	void createEmployee_ShouldReturnBadRequest_WhenEmailExists() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().name("John Doe").email("john@example.com").department("IT")
				.salary(new BigDecimal("55000.00")).build();

		when(employeeService.createEmployee(any(EmployeeBean.class)))
				.thenThrow(new IllegalArgumentException("Employee with email john@example.com already exists"));

		mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.error").value("Bad Request"));

		verify(employeeService).createEmployee(any(EmployeeBean.class));
	}

	@Test
	void getAllEmployees_ShouldReturnEmployees() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().id(1L).name("John Doe").email("john@example.com")
				.department("IT").salary(new BigDecimal("55000.00")).build();

		when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

		mockMvc.perform(get("/api/employees")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(1))
				.andExpect(jsonPath("$[0].name").value("John Doe"))
				.andExpect(jsonPath("$[0].email").value("john@example.com"));

		verify(employeeService).getAllEmployees();
	}

	@Test
	void getEmployeeById_ShouldReturnEmployee() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().id(1L).name("John Doe").email("john@example.com")
				.department("IT").salary(new BigDecimal("55000.00")).build();

		when(employeeService.getEmployeeById(1L)).thenReturn(employee);

		mockMvc.perform(get("/api/employees/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("John Doe"));

		verify(employeeService).getEmployeeById(1L);
	}

	@Test
	void getEmployeeById_ShouldReturnNotFound() throws Exception {

		when(employeeService.getEmployeeById(99L))
				.thenThrow(new EmployeeNotFoundException("Employee with id 99 not found"));

		mockMvc.perform(get("/api/employees/99")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.error").value("Employee Not Found"));

		verify(employeeService).getEmployeeById(99L);
	}

	@Test
	void updateEmployee_ShouldReturnUpdatedEmployee() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().id(1L).name("John Updated").email("john.updated@example.com")
				.department("Engineering").salary(new BigDecimal("65000.00")).build();

		when(employeeService.updateEmployee(eq(1L), any(EmployeeBean.class))).thenReturn(employee);

		mockMvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("John Updated"))
				.andExpect(jsonPath("$.department").value("Engineering"));

		verify(employeeService).updateEmployee(eq(1L), any(EmployeeBean.class));
	}

	@Test
	void updateEmployee_ShouldReturnBadRequest_WhenValidationFails() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().name("").email("invalid").department("")
				.salary(new BigDecimal("-1")).build();

		mockMvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isBadRequest());

		verify(employeeService, never()).updateEmployee(eq(1L), any(EmployeeBean.class));
	}

	@Test
	void updateEmployee_ShouldReturnBadRequest_WhenEmailExists() throws Exception {

		EmployeeBean employee = EmployeeBean.builder().name("John Updated").email("existing@example.com")
				.department("Engineering").salary(new BigDecimal("65000.00")).build();

		when(employeeService.updateEmployee(eq(1L), any(EmployeeBean.class)))
				.thenThrow(new IllegalArgumentException("Employee with email existing@example.com already exists"));

		mockMvc.perform(put("/api/employees/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));

		verify(employeeService).updateEmployee(eq(1L), any(EmployeeBean.class));
	}

	@Test
	void deleteEmployee_ShouldReturnNoContent() throws Exception {

		doNothing().when(employeeService).deleteEmployee(1L);

		mockMvc.perform(delete("/api/employees/1")).andExpect(status().isNoContent());

		verify(employeeService).deleteEmployee(1L);
	}

	@Test
	void deleteEmployee_ShouldReturnNotFound() throws Exception {

		doThrow(new EmployeeNotFoundException("Employee with id 99 not found")).when(employeeService)
				.deleteEmployee(99L);

		mockMvc.perform(delete("/api/employees/99")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.error").value("Employee Not Found"));

		verify(employeeService).deleteEmployee(99L);
	}
}