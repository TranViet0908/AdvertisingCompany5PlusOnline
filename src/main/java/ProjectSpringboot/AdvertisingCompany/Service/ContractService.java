package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.Contract;
import ProjectSpringboot.AdvertisingCompany.Repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContractService {
    @Autowired
    public ContractRepository contractRepository;

    public List<Contract> getAllContract(){
        return contractRepository.findAll();
    }

    public Contract getContractById(Long id){
        return contractRepository.findById(id).orElseThrow(() -> new RuntimeException("Contract not found with id/ + id"));
    }
    public Contract createContract(Contract contract){
        return contractRepository.save(contract);
    }

    public Contract updateContract(Long id, Contract contractDetails){
        Contract contract = getContractById(id);
        contract.setClient(contractDetails.getClient());
        contract.setProject(contractDetails.getProject());
        contract.setValue(contractDetails.getValue());
        contract.setSignDate(contractDetails.getSignDate());
        contract.setUpdated_at(LocalDateTime.now());
        return contractRepository.save(contract);
    }

    public void deleteContract(Long id){
        Contract contract = getContractById(id);
        contractRepository.delete(contract);
    }

    public long countContracts() {
        return contractRepository.count();
    }

    // Lấy N hợp đồng mới nhất
    public List<Contract> getRecentContracts(int limit) {
        return contractRepository.findRecentContracts(PageRequest.of(0, limit));
    }

    // Đếm số lượng hợp đồng theo tháng
    public Map<Integer, Long> getContractsCountByMonth() {
        List<Object[]> results = contractRepository.countContractsByMonth();
        Map<Integer, Long> data = new LinkedHashMap<>();
        if (results != null) {
            for (Object[] row : results) {
                Integer month = ((Number) row[0]).intValue();
                Long count = ((Number) row[1]).longValue();
                data.put(month, count);
            }
        }
        return data;
    }
}
