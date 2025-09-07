package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Service.Admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private AdProjectService adProjectService;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public String dashboard(Model model, Authentication authentication) {

        // Kiểm tra role để hiển thị thông tin phù hợp
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        // Đếm tổng số lượng (card thống kê)
        model.addAttribute("visitorsCount", clientService.countClients());
        model.addAttribute("subscribersCount", employeeService.countEmployees());
        model.addAttribute("salesAmount", companyService.countCompanies());
        model.addAttribute("ordersCount", contractService.countContracts());

        // Tổng số lượng khác
        model.addAttribute("totalClients", clientService.countClients());
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("totalCompanies", companyService.countCompanies());
        model.addAttribute("totalContracts", contractService.countContracts());
        model.addAttribute("totalPartners", partnerService.countPartners());
        model.addAttribute("totalAdProjects", adProjectService.countAdProjects());

        model.addAttribute("recentContracts", contractService.getRecentContracts(5));

        Map<Integer, Long> contractsByMonth = contractService.getContractsCountByMonth();
        if (contractsByMonth == null) {
            contractsByMonth = new LinkedHashMap<>();
        }
        model.addAttribute("contractsByMonth", contractsByMonth);

        // Thêm thông tin role để hiển thị trong template
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userRole", isAdmin ? "ADMIN" : "EMPLOYEE");

        return "admin/dashboard";
    }
}
