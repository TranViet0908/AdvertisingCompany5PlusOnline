package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.WorkTask;
import ProjectSpringboot.AdvertisingCompany.Repository.WorkTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkTaskService {
    @Autowired
    private WorkTaskRepository workTaskRepository;

    public List<WorkTask> getAllWorkTasks(){
        return workTaskRepository.findAll();
    }

    public WorkTask getWorkTaskById(Long id){
        return workTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkTask not found with id: " + id));
    }

    public List<WorkTask> getWorkTaskByName(String name){
        return workTaskRepository.findByName(name);
    }

    public WorkTask createWorkTask(WorkTask workTask){
        return workTaskRepository.save(workTask);
    }

    public WorkTask updateWorkTask(Long id, WorkTask workTaskDetails) {
        WorkTask workTask = getWorkTaskById(id);
        workTask.setAdProject(workTaskDetails.getAdProject());
        workTask.setEmployee(workTaskDetails.getEmployee());
        workTask.setParentTask(workTaskDetails.getParentTask());
        workTask.setName(workTaskDetails.getName());
        workTask.setDescription(workTaskDetails.getDescription());
        workTask.setStart_date(workTaskDetails.getStart_date());
        workTask.setEnd_date(workTaskDetails.getEnd_date());
        workTask.setStatus(workTaskDetails.getStatus());
        workTask.setCreated_at(workTaskDetails.getCreated_at());
        workTask.setUpdated_at(workTaskDetails.getUpdated_at());
        return workTaskRepository.save(workTask);
    }

    public void deleteWorkTask(Long id){
        WorkTask workTask = getWorkTaskById(id);
        workTaskRepository.delete(workTask);
    }

    public long countWorkTasks() {
        return workTaskRepository.count();
    }
}
