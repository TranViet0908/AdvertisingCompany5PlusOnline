package ProjectSpringboot.AdvertisingCompany.Controller.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Contract;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.AdProjectService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.ClientService;
import ProjectSpringboot.AdvertisingCompany.Service.Admin.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/contracts")
public class ContractController {
    @Autowired
    public ContractService contractService;

    @Autowired
    public ClientService clientService;

    @Autowired
    public AdProjectService adProjectService;

    @GetMapping
    public String listContracts(@RequestParam(required = false) String search, Model model) {
        List<Contract> contracts;
        if (search == null || search.isEmpty()) {
            contracts = contractService.getAllContract();
        } else {
            Long id = Long.parseLong(search);
            Contract e = contractService.getContractById(id);
            contracts = (e != null) ? List.of(e) : List.of();
        }

        model.addAttribute("contracts", contracts);
        model.addAttribute("search", search);
        return "admin/contracts/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("contract", new Contract());
        model.addAttribute("clients", clientService.getAllClient());
        model.addAttribute("adProjects", adProjectService.getAllAdProject());
        return "admin/contracts/create";
    }

    @PostMapping
    public String createContract(@ModelAttribute Contract contract) {
        // Gán thời gian tạo & cập nhật
        LocalDateTime now = LocalDateTime.now();
        contract.setCreated_at(now);
        contract.setUpdated_at(now);

        contractService.createContract(contract);
        return "redirect:/admin/contracts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Contract contract = contractService.getContractById(id);
        model.addAttribute("contract", contract);
        model.addAttribute("clients", clientService.getAllClient());
        model.addAttribute("adProjects", adProjectService.getAllAdProject());
        return "admin/contracts/edit";
    }

    @PostMapping("/{id}")
    public String updateContract(@PathVariable Long id,
                                 @ModelAttribute Contract contract,
                                 @RequestParam("imageFile") MultipartFile imageFile) {
        contractService.updateContract(id, contract);
        return "redirect:/admin/contracts";
    }

    @GetMapping("/delete/{id}")
    public String deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return "redirect:/admin/contracts";
    }
}
