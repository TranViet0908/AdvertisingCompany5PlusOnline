package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.Employee;
import ProjectSpringboot.AdvertisingCompany.Entity.ProgressReport;
import ProjectSpringboot.AdvertisingCompany.Repository.ProgressReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressReportService {
    @Autowired
    private ProgressReportRepository progressReportRepository;

    public List<ProgressReport> getAllReport(){
        return progressReportRepository.findAll();
    }

    public List<ProgressReport> getReportByEmployee(Employee employee){
        return progressReportRepository.findByEmployee(employee);
    }

    public ProgressReport getReportById(Long id){
        return progressReportRepository.findById(id).orElseThrow(() -> new RuntimeException("Report not found by id" + id));
    }

    public ProgressReport createReport(ProgressReport progressReport){
        return progressReportRepository.save(progressReport);
    }

    public ProgressReport updateReport(Long id, ProgressReport reportDetails){
        ProgressReport report = getReportById(id);
        report.setProject(reportDetails.getProject());
        report.setEmployee(reportDetails.getEmployee());
        report.setReportDate(reportDetails.getReportDate());
        report.setContent(reportDetails.getContent());
        return progressReportRepository.save(report);
    }
    public void deleteReport(Long id){
        ProgressReport report = getReportById(id);
        progressReportRepository.delete(report);
    }
}
