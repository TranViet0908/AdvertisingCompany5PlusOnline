package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.AdProject;
import ProjectSpringboot.AdvertisingCompany.Repository.AdProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdProjectService {
    @Autowired
    private AdProjectRepository adProjectRepository;

    public List<AdProject> getAllAdProject(){
        return adProjectRepository.findAll();
    }

    public List<AdProject> getAdProjectsByName(String name){
        return adProjectRepository.getAdProjectByName(name);
    }

    public AdProject getAdProjectById(Long id){
        return adProjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad Project not found by ID: \" + id"));
    }

    public AdProject createAdProject(AdProject adProject){
        return adProjectRepository.save(adProject);
    }

    public AdProject updateAdProject(AdProject adProjectDetails, Long id){
        AdProject adProject = getAdProjectById(id);
        adProject.setName(adProjectDetails.getName());
        adProject.setClient(adProjectDetails.getClient());
        adProject.setStart_date(adProjectDetails.getStart_date());
        adProject.setEnd_date(adProjectDetails.getEnd_date());
        adProject.setBudget(adProjectDetails.getBudget());
        adProject.setActual_cost(adProjectDetails.getActual_cost());
        adProject.setStatus(adProjectDetails.getStatus());
        adProject.setCreated_at(adProjectDetails.getCreated_at());
        adProject.setUpdated_at(adProjectDetails.getUpdated_at());
        return adProjectRepository.save(adProject);
    }

    public void deleteAdProject(Long id){
        AdProject adProject = getAdProjectById(id);
        adProjectRepository.delete(adProject);
    }
    public long countAdProjects() {
        return adProjectRepository.count();
    }
}
