package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.Company;
import ProjectSpringboot.AdvertisingCompany.Service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @GetMapping
    public String getAllCompany(@RequestParam(required = false) String search, Model model){
        List<Company> companies;

        if(search != null && !search.isEmpty()){
            try{
                Long id = Long.parseLong(search);
                Company company = companyService.getCompanyById(id);
                companies = (company != null) ? List.of(company) : List.of();
            } catch (NumberFormatException e){
                List<Company> byname = companyService.getCompanyByName(search);
                if(!byname.isEmpty()){
                    companies = byname;
                } else {
                    companies = companyService.getAllCompany().stream()
                            .filter(t -> t.getName().toLowerCase().contains(search.toLowerCase()))
                            .toList();
                }
            }
        } else {
            companies = companyService.getAllCompany();
        }

        model.addAttribute("companies", companies);
        model.addAttribute("search", search);
        return "/admin/companies/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("company", new Company());
        return "/admin/companies/create";
    }

    @PostMapping
    public String createCompany(@ModelAttribute Company company){
        companyService.createCompany(company);
        return "redirect:/admin/companies";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Company company = companyService.getCompanyById(id);
        model.addAttribute("company", company);
        return "/admin/companies/edit";
    }

    @PostMapping("/{id}")
    public String updateCompany(@PathVariable Long id, @ModelAttribute Company company){
        companyService.updateCompany(id, company);
        return "redirect:/admin/companies";
    }

    @GetMapping("/delete/{id}")
    public String deleteCompany(@PathVariable Long id){
        companyService.deleteCompany(id);
        return "redirect:/admin/companies";
    }
}
