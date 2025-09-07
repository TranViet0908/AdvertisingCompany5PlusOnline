package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Payment;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdProjectService adProjectService;

    // Tìm kiếm Payment (theo id hoặc text)
    private List<Payment> searchPayments(String search) {
        try {
            Long id = Long.parseLong(search);
            Payment byId = paymentService.getPaymentById(id);
            return (byId != null) ? List.of(byId) : List.of();
        } catch (NumberFormatException e) {
            return paymentService.getAllPayments().stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase())
                            || p.getStatus().name().toLowerCase().contains(search.toLowerCase())
                            || p.getClient().getName().toLowerCase().contains(search.toLowerCase())
                            || p.getProject().getName().toLowerCase().contains(search.toLowerCase())
                            || p.getEmployee().getFullName().toLowerCase().contains(search.toLowerCase())
                            || p.getCompany().getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
    }

    @GetMapping
    public String getAllPayments(@RequestParam(required = false) String search, Model model) {
        List<Payment> payments;

        if (search == null || search.isEmpty()) {
            payments = paymentService.getAllPayments();
        } else {
            payments = searchPayments(search);
        }

        model.addAttribute("payments", payments);
        model.addAttribute("search", search);
        return "/admin/payments/list";
    }

    @PostMapping
    public String createPayment(@ModelAttribute Payment payment) {
        LocalDateTime now = LocalDateTime.now();
        payment.setCreated_at(now);
        payment.setUpdated_at(now);
        paymentService.createPayment(payment);
        return "redirect:/admin/payments";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("payment", new Payment());

        // Lấy dữ liệu từ khóa ngoại
        model.addAttribute("contracts", contractService.getAllContract());
        model.addAttribute("clients", clientService.getAllClient());
        model.addAttribute("companies", companyService.getAllCompany());
        model.addAttribute("employees", employeeService.getAllEmployee());
        model.addAttribute("projects", adProjectService.getAllAdProject());

        return "/admin/payments/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        model.addAttribute("payment", payment);

        // Lấy dữ liệu từ khóa ngoại
        model.addAttribute("contracts", contractService.getAllContract());
        model.addAttribute("clients", clientService.getAllClient());
        model.addAttribute("companies", companyService.getAllCompany());
        model.addAttribute("employees", employeeService.getAllEmployee());
        model.addAttribute("projects", adProjectService.getAllAdProject());

        return "/admin/payments/edit";
    }

    @PostMapping("/{id}")
    public String updatePayment(@PathVariable Long id, @ModelAttribute Payment paymentDetails) {
        Payment payment = paymentService.getPaymentById(id);
        payment.setName(paymentDetails.getName());
        payment.setAmountPaid(paymentDetails.getAmountPaid());
        payment.setStatus(paymentDetails.getStatus());

        // cập nhật khóa ngoại
        payment.setContract(paymentDetails.getContract());
        payment.setClient(paymentDetails.getClient());
        payment.setCompany(paymentDetails.getCompany());
        payment.setEmployee(paymentDetails.getEmployee());
        payment.setProject(paymentDetails.getProject());

        payment.setUpdated_at(LocalDateTime.now());
        paymentService.createPayment(payment);
        return "redirect:/admin/payments";
    }

    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/admin/payments";
    }
}
