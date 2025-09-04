package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.User;
import ProjectSpringboot.AdvertisingCompany.Entity.WorkTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkTaskRepository extends JpaRepository<WorkTask, Long> {
    List<WorkTask> findByName(String name);
    long count();

}
