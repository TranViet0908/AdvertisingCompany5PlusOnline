package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Partner;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.CompanyService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/partners")
public class PartnerController {
    @Autowired
    private PartnerService partnerService;

    @Autowired
    private CompanyService companyService;

    private List<Partner> searchPartners(String search) {
        try {
            Long id = Long.parseLong(search);
            Partner byId = partnerService.getPartnerById(id);
            return (byId != null) ? List.of(byId) : List.of();
        } catch (NumberFormatException e) {
            List<Partner> byName = partnerService.getPartnerByName(search);
            if (!byName.isEmpty()) return byName;

            return partnerService.getAllPartner().stream()
                    .filter(C2 -> C2.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
    }

    @GetMapping
    public String getAllPartner(@RequestParam(required = false) String search, Model model) {
        List<Partner> partners;

        if (search == null || search.isEmpty()) {
            partners = partnerService.getAllPartner();
        } else {
            partners = searchPartners(search);
        }

        model.addAttribute("partners", partners);
        model.addAttribute("search", search);
        return "/admin/partners/list";
    }

    @PostMapping
    public String createPartner(@ModelAttribute Partner partner) {
        // Gán thời gian tạo & cập nhật
        LocalDateTime now = LocalDateTime.now();
        partner.setCreated_at(now);
        partner.setUpdated_at(now);

        partnerService.createPartner(partner);
        return "redirect:/admin/partners";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("partner", new Partner());
        model.addAttribute("companies", companyService.getAllCompany());
        return "/admin/partners/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Partner partner = partnerService.getPartnerById(id);
        model.addAttribute("partner", partner);
        model.addAttribute("companies", companyService.getAllCompany());
        return "/admin/partners/edit";
    }

    @PostMapping("/{id}")
    public String updatePartner(@PathVariable Long id,
                                 @ModelAttribute Partner partner) {
        partnerService.updatePartner(id, partner);
        return "redirect:/admin/partners";
    }

    @GetMapping("/delete/{id}")
    public String deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return "redirect:/admin/partners";
    }
}
