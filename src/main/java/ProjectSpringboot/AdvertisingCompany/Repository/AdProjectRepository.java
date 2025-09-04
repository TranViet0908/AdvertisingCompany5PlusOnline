package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.AdProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdProjectRepository extends JpaRepository<AdProject, Long> {
    public List<AdProject> getAdProjectByName (String name);
    long count();

}
