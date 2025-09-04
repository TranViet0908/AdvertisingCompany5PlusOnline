package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Employee;
import ProjectSpringboot.AdvertisingCompany.Entity.ProgressReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {
    long count();
    List<ProgressReport> findByEmployee(Employee employee);
}
