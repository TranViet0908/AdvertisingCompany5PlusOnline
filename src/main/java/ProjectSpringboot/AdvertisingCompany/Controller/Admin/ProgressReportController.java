package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.ProgressReport;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.AdProjectService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.EmployeeService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.ProgressReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/progressReports")
public class ProgressReportController {
    @Autowired
    private ProgressReportService progressReportService;

    @Autowired
    private AdProjectService adProjectService;

    @Autowired
    private EmployeeService employeeService;

    private List<ProgressReport> searchProgressReports(String search) {
        try {
            Long id = Long.parseLong(search);
            ProgressReport byId = progressReportService.getReportById(id);
            return (byId != null) ? List.of(byId) : List.of();
        } catch (NumberFormatException e) {
            return progressReportService.getAllReport().stream()
                    .filter(report -> report.getContent().toLowerCase().contains(search.toLowerCase()) ||
                            report.getProject().getName().toLowerCase().contains(search.toLowerCase()) ||
                            report.getEmployee().getFullName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
    }

    @GetMapping
    public String getAllProgressReports(@RequestParam(required = false) String search, Model model) {
        List<ProgressReport> reports;

        if (search == null || search.isEmpty()) {
            reports = progressReportService.getAllReport();
        } else {
            reports = searchProgressReports(search);
        }

        model.addAttribute("reports", reports);
        model.addAttribute("search", search);
        return "/admin/progressReports/list";
    }

    @PostMapping
    public String createProgressReport(@ModelAttribute ProgressReport progressReport) {
        // Gán thời gian tạo & cập nhật
        LocalDateTime now = LocalDateTime.now();
        progressReport.setCreated_at(now);
        progressReport.setUpdated_at(now);

        progressReportService.createReport(progressReport);
        return "redirect:/admin/progressReports";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("progressReport", new ProgressReport());
        model.addAttribute("projects", adProjectService.getAllAdProject());
        model.addAttribute("employees", employeeService.getAllEmployee());
        return "/admin/progressReports/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProgressReport progressReport = progressReportService.getReportById(id);
        model.addAttribute("progressReport", progressReport);
        model.addAttribute("projects", adProjectService.getAllAdProject());
        model.addAttribute("employees", employeeService.getAllEmployee());
        return "/admin/progressReports/edit";
    }

    @PostMapping("/{id}")
    public String updateProgressReport(@PathVariable Long id,
                                       @ModelAttribute ProgressReport reportDetails) {
        ProgressReport report = progressReportService.getReportById(id);
        report.setProject(reportDetails.getProject());
        report.setEmployee(reportDetails.getEmployee());
        report.setReportDate(reportDetails.getReportDate());
        report.setContent(reportDetails.getContent());
        report.setUpdated_at(LocalDateTime.now());
        progressReportService.createReport(report);
        return "redirect:/admin/progressReports";
    }

    @GetMapping("/delete/{id}")
    public String deleteProgressReport(@PathVariable Long id) {
        progressReportService.deleteReport(id);
        return "redirect:/admin/progressReports";
    }
}