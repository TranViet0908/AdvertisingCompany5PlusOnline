package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Department;
import ProjectSpringboot.AdvertisingCompany.Entity.Employee;
import ProjectSpringboot.AdvertisingCompany.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployee(){
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeeByName(String fullName){
        return employeeRepository.findEmployeeByFullName(fullName);
    }

    public Employee getEmployeeByEmail(String email){
        return employeeRepository.findEmployeeByEmail(email);
    }

    public List<Employee> getEmployeeByDepartmentId(Department department){
        return employeeRepository.findEmployeeByDepartmentId(department);
    }

    public Employee getEmployeeById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkTask not found with id: " + id));
    }

    public Employee createEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public Employee UpdateEmployee(Long id, Employee employeeDetails){
        Employee employee = getEmployeeById(id);
        employee.setFullName(employeeDetails.getFullName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhone(employeeDetails.getPhone());
        employee.setUpdated_at(LocalDateTime.now());
        employee.setRole(employeeDetails.getRole());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setPhotoUrl(employeeDetails.getPhotoUrl());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id){
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
    public long countEmployees() {
        return employeeRepository.count();
    }
}
