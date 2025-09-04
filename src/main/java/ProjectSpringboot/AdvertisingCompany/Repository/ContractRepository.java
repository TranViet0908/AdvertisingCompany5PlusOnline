package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Contract;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    long count();
    // Lấy N hợp đồng mới nhất
    @Query("SELECT c FROM Contract c ORDER BY c.created_at DESC")
    List<Contract> findRecentContracts(Pageable pageable);

    // Đếm số lượng hợp đồng theo tháng
    @Query("SELECT FUNCTION('MONTH', c.created_at) AS month, COUNT(c) " +
            "FROM Contract c " +
            "GROUP BY FUNCTION('MONTH', c.created_at) " +
            "ORDER BY month")
    List<Object[]> countContractsByMonth();
}
