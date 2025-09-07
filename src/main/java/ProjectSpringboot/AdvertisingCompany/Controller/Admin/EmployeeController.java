package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Employee;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.DepartmentService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    private final String UPLOAD_DIR = "src/main/resources/static/images/avatar/";

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String search, Model model) {
        List<Employee> employees;

        if (search == null || search.isEmpty()) {
            employees = employeeService.getAllEmployee();
        } else {
            try {
                Long id = Long.parseLong(search);
                Employee e = employeeService.getEmployeeById(id);
                employees = (e != null) ? List.of(e) : List.of();
            } catch (NumberFormatException e) {
                employees = employeeService.getAllEmployee().stream()
                        .filter(emp -> emp.getFullName().toLowerCase().contains(search.toLowerCase()))
                        .toList();
            }
        }

        model.addAttribute("employees", employees);
        model.addAttribute("search", search);
        return "/admin/employees/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentService.getAllDepartment());
        return "/admin/employees/create";
    }

    @PostMapping
    public String createEmployee(@ModelAttribute Employee employee,
                                 @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path uploadPath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(uploadPath, imageFile.getBytes());
                employee.setPhotoUrl("/images/avatar/" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    // Gán thời gian tạo & cập nhật
        LocalDateTime now = LocalDateTime.now();
        employee.setCreated_at(now);
        employee.setUpdated_at(now);

        employeeService.createEmployee(employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentService.getAllDepartment());
        return "/admin/employees/edit";
    }

    @PostMapping("/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @ModelAttribute Employee employee,
                                 @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                Path uploadPath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(uploadPath, imageFile.getBytes());
                employee.setPhotoUrl("/images/avatar/" + fileName);
            } else {
                String existingPhoto = employeeService.getEmployeeById(id).getPhotoUrl();
                employee.setPhotoUrl(existingPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        employeeService.UpdateEmployee(id, employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }
}