package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.AdProject;

import ProjectSpringboot.AdvertisingCompany.Service.AdProjectService;
import ProjectSpringboot.AdvertisingCompany.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/adProjects")
public class AdProjectController {
    @Autowired
    private AdProjectService adProjectService;

    @Autowired
    private ClientService clientService;

    private List<AdProject> searchAdProjects(String search){
        try{
            Long id = Long.parseLong(search);
            AdProject byId = adProjectService.getAdProjectById(id);
            return (byId != null) ? List.of(byId) : List.of();
        } catch (NumberFormatException e){
            List<AdProject> byName = adProjectService.getAdProjectsByName(search);
            if(!byName.isEmpty()) return byName;

            return adProjectService.getAllAdProject().stream()
                    .filter(a2 -> a2.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
    }
    @GetMapping
    public String getAdProjectService(@RequestParam(required = false) String search, Model model){
        List<AdProject> adProjects;

        if(search == null || search.isEmpty()){
            adProjects = adProjectService.getAllAdProject();
        } else {
            adProjects = searchAdProjects(search);
        }

        model.addAttribute("adProjects", adProjects);
        model.addAttribute("search", search);
        return "admin/adProjects/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("adProject", new AdProject());
        model.addAttribute("clients", clientService.getAllClient());
        return "admin/adProjects/create";
    }

    @PostMapping
    public String createAdProject(@ModelAttribute AdProject adProject){
        adProjectService.createAdProject(adProject);
        return "redirect:/admin/adProjects";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        AdProject adProject = adProjectService.getAdProjectById(id);
        model.addAttribute("adProject", adProject);
        model.addAttribute("clients", clientService.getAllClient());
        return "admin/adProjects/edit";
    }

    @PostMapping("/{id}")
    public String updateAdProject(@PathVariable Long id, @ModelAttribute AdProject adProject){
        adProjectService.updateAdProject(adProject, id);
        return "redirect:/admin/adProjects";
    }

    @GetMapping("/delete/{id}")
    public String deleteAdProject(@PathVariable Long id){
        adProjectService.deleteAdProject(id);
        return "redirect:/admin/adProjects";
    }
}
