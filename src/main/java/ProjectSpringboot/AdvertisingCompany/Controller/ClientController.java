package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.Client;
import ProjectSpringboot.AdvertisingCompany.Entity.Client;
import ProjectSpringboot.AdvertisingCompany.Service.ClientService;
import ProjectSpringboot.AdvertisingCompany.Service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private CompanyService companyService;

    private List<Client> searchClients(String search) {
        try {
            Long id = Long.parseLong(search);
            Client byId = clientService.getClientById(id);
            return (byId != null) ? List.of(byId) : List.of();
        } catch (NumberFormatException e) {
            List<Client> byName = clientService.getClientByName(search);
            if (!byName.isEmpty()) return byName;

            Client byEmail = clientService.getClientByEmail(search);
            if (byEmail != null) return List.of(byEmail);

            return clientService.getAllClient().stream()
                    .filter(C2 -> C2.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
    }

    @GetMapping
    public String getAllClient(@RequestParam(required = false) String search, Model model) {
        List<Client> clients;

        if (search == null || search.isEmpty()) {
            clients = clientService.getAllClient();
        } else {
            clients = searchClients(search);
        }

        model.addAttribute("clients", clients);
        model.addAttribute("search", search);
        return "/admin/clients/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("client", new Client());
        model.addAttribute("companies", companyService.getAllCompany());
        return "/admin/clients/create";
    }

    @PostMapping
    public String createClient(@ModelAttribute Client client){
        clientService.createClient(client);
        return "redirect:/admin/clients";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Client client = clientService.getClientById(id);
        model.addAttribute("client", client);
        model.addAttribute("companies", companyService.getAllCompany());
        return "/admin/clients/edit";
    }

    @PostMapping("/{id}")
    public String updateClient(@PathVariable Long id, @ModelAttribute Client client){
        clientService.updateClient(id, client);
        return "redirect:/admin/clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id){
        clientService.deleteClient(id);
        return "redirect:/admin/clients";
    }
}
