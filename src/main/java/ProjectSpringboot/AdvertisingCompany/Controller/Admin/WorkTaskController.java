package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.WorkTask;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.AdProjectService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.EmployeeService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.WorkTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/work-tasks")
public class WorkTaskController {

    @Autowired
    private WorkTaskService workTaskService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdProjectService adProjectService;

    @GetMapping
    public String getAllWorkTasks(@RequestParam(required = false) String search, Model model) {
        List<WorkTask> tasks;

        if (search != null && !search.isEmpty()) {
            try {
                Long id = Long.parseLong(search);
                WorkTask task = workTaskService.getWorkTaskById(id);
                tasks = (task != null) ? List.of(task) : List.of();
            } catch (NumberFormatException e) {
                List<WorkTask> byName = workTaskService.getWorkTaskByName(search);
                if (!byName.isEmpty()) {
                    tasks = byName;
                } else {
                    tasks = workTaskService.getAllWorkTasks().stream()
                            .filter(t -> t.getName().toLowerCase().contains(search.toLowerCase()))
                            .toList();
                }
            }
        } else {
            tasks = workTaskService.getAllWorkTasks();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("search", search);
        return "/admin/work-tasks/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new WorkTask());
        model.addAttribute("projects", adProjectService.getAllAdProject());
        model.addAttribute("employees", employeeService.getAllEmployee());
        model.addAttribute("parentTasks", workTaskService.getAllWorkTasks());
        return "/admin/work-tasks/create";
    }

    @PostMapping
    public String createWorkTask(@ModelAttribute WorkTask workTask) {
        workTaskService.createWorkTask(workTask);
        return "redirect:/admin/work-tasks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        WorkTask task = workTaskService.getWorkTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("projects", adProjectService.getAllAdProject());
        model.addAttribute("employees", employeeService.getAllEmployee());
        model.addAttribute("parentTasks", workTaskService.getAllWorkTasks());
        return "/admin/work-tasks/edit";
    }

    @PostMapping("/{id}")
    public String updateWorkTask(@PathVariable Long id, @ModelAttribute WorkTask workTask) {
        workTaskService.updateWorkTask(id, workTask);
        return "redirect:/admin/work-tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteWorkTask(@PathVariable Long id) {
        workTaskService.deleteWorkTask(id);
        return "redirect:/admin/work-tasks";
    }
}
