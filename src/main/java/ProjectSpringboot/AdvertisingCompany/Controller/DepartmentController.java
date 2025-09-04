package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.Department;
import ProjectSpringboot.AdvertisingCompany.Service.CompanyService;
import ProjectSpringboot.AdvertisingCompany.Service.DepartmentService;
import ProjectSpringboot.AdvertisingCompany.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String getAllDepartment(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Department> departments;

        if (search != null && !search.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(search.trim());
                Department department = departmentService.getDepartmentById(id);
                departments = (department != null) ? List.of(department) : List.of();
            } catch (NumberFormatException e) {
                List<Department> byName = departmentService.getDepartmentByName(search.trim());
                if (!byName.isEmpty()) {
                    departments = byName;
                } else {
                    departments = departmentService.getAllDepartment().stream()
                            .filter(d -> d.getName().toLowerCase().contains(search.trim().toLowerCase()))
                            .toList();
                }
            }
        } else {
            // Mặc định load tất cả
            departments = departmentService.getAllDepartment();
        }

        model.addAttribute("departments", departments);
        model.addAttribute("search", search);
        return "admin/departments/list"; // bỏ "/" đầu để thymeleaf resolve đúng
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("employees", employeeService.getAllEmployee());
        model.addAttribute("companies", companyService.getAllCompany());
        return "/admin/departments/create";
    }

    @PostMapping
    String createDepartment(@ModelAttribute Department department){
        departmentService.createDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        model.addAttribute("employees", employeeService.getAllEmployee());
        model.addAttribute("companies", companyService.getAllCompany());
        return "/admin/departments/edit";
    }

    @PostMapping("/{id}")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department){
        departmentService.updateDepartment(id, department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable Long id){
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }
}
