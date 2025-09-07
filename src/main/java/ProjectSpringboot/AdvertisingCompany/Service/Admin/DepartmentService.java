package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Department;
import ProjectSpringboot.AdvertisingCompany.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartment(){
        return departmentRepository.findAll();
    }

    public List<Department> getDepartmentByName(String name){
        return departmentRepository.getDepartmentByName(name);
    }

    public Department getDepartmentById(Long id){
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: \" + id"));
    }

    public Department createDepartment(Department department){
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department departmentDetails){
        Department department = getDepartmentById(id);
        department.setName(departmentDetails.getName());
        department.setFunction(departmentDetails.getFunction());
        department.setManager(departmentDetails.getManager());
        department.setCompany(departmentDetails.getCompany());
        department.setCreated_at(departmentDetails.getCreated_at());
        department.setUpdated_at(departmentDetails.getUpdated_at());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id){
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }

    public long countDepartments() {
        return departmentRepository.count();
    }
}
