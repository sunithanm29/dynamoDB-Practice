package com.example.demo.dao;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.demo.entity.Employee;

@Component
public class Operations {

	@Autowired
	DynamoDBMapper dynamoDBMapper;
	
	@Autowired
	AmazonDynamoDB amazonDynamoDB;
	
	public void init()
	{
		CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Employee.class);
		tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L,1L));
		amazonDynamoDB.createTable(tableRequest);
	}
	
	
	public Employee createEmployee(Employee employee) {
			dynamoDBMapper.save(employee);
			return employee;
	}

	
	public Employee readEmployee(String employeeId) {
		return dynamoDBMapper.load(Employee.class, employeeId);
	}

	
	public Employee updateEmployee(Employee employee) {
		Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
		expectedAttributeValueMap.put("employeeId", new ExpectedAttributeValue(new AttributeValue().withS(employee.getEmployeeId())));
		DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression().withExpected(expectedAttributeValueMap);
		dynamoDBMapper.save(employee, saveExpression);
		return employee;
	}

	
	public Employee deleteEmployee(String employeeId) {
		Map<String, ExpectedAttributeValue> expectedAttributeValueMap = new HashMap<>();
		expectedAttributeValueMap.put("employeeId", new ExpectedAttributeValue(new AttributeValue().withS(employeeId)));
		DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression().withExpected(expectedAttributeValueMap);
		Employee employee = readEmployee(employeeId);
		dynamoDBMapper.delete(employee, deleteExpression);
		return null;
	}

	
	public List<Employee> readAllEmployees() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		List<Employee> employees = new ArrayList<>();
		dynamoDBMapper.scan(Employee.class, scanExpression).forEach(u -> employees.add(u));
		return employees;
	}

}
