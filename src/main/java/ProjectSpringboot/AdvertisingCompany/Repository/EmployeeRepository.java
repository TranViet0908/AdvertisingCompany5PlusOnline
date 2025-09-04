package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Department;
import ProjectSpringboot.AdvertisingCompany.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findEmployeeByFullName(String fullName);

    Employee findEmployeeByEmail(String email);

    List<Employee> findEmployeeByDepartmentId(Department department);
    long count();

}
