package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Service.UserContractService;
import ProjectSpringboot.AdvertisingCompany.Service.UserContractService.ContractView;
import ProjectSpringboot.AdvertisingCompany.Service.UserContractService.PaymentSummary;
import ProjectSpringboot.AdvertisingCompany.Service.UserContractService.ProgressStep;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user/contracts")
@PreAuthorize("hasRole('CLIENT')")
public class UserContractController {

    private final UserContractService service;

    public UserContractController(UserContractService service) {
        this.service = service;
    }

    private String username(Authentication auth) { return auth.getName(); }

    // ===== HỢP ĐỒNG =====
    @GetMapping
    public String contracts(Model model, Authentication auth) {
        List<ContractView> list = service.contractsOfUser(username(auth));
        model.addAttribute("contracts", list);
        return "user/contracts/contracts";
    }

    // ===== THANH TOÁN =====
    @GetMapping("/payments")
    public String payments(@RequestParam(required = false) Long contractId,
                           Model model,
                           Authentication auth) {
        var rows = service.paymentsOfUser(username(auth), Optional.ofNullable(contractId));
        PaymentSummary sum = service.paymentSummaryOfUser(username(auth), Optional.ofNullable(contractId));
        model.addAttribute("payments", rows);
        model.addAttribute("summary", sum);
        return "user/contracts/payments";
    }

    @PostMapping("/payments/{paymentId}/pay")
    public String pay(@PathVariable Long paymentId,
                      @RequestParam("pay_amount") BigDecimal payAmount,
                      Authentication auth,
                      RedirectAttributes ra) {
        try {
            service.payMore(username(auth), paymentId, payAmount);
            ra.addFlashAttribute("success", "Thanh toán thành công.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể thanh toán lúc này.");
        }
        return "redirect:/user/contracts/payments";
    }

    // ===== TIẾN TRÌNH =====
    @GetMapping("/progress")
    public String progress(@RequestParam(required = false) Long contractId,
                           Model model,
                           Authentication auth) {
        List<ProgressStep> steps = service.progressOfUser(username(auth), Optional.ofNullable(contractId));
        model.addAttribute("steps", steps);
        return "user/contracts/progress";
    }
}
