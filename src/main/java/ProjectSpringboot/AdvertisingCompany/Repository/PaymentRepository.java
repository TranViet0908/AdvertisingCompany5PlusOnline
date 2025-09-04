package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Lấy danh sách Payment theo tên (name)
    List<Payment> findByName(String name);

    // Lấy danh sách Payment theo Project (dựa vào entity Project)
    List<Payment> findByProject_Id(Long projectId);

    // Lấy danh sách Payment theo Status
    List<Payment> findByStatus(Payment.Status status);

    // Lấy danh sách Payment theo Client
    List<Payment> findByClient_Id(Long clientId);

    // Kết hợp nhiều điều kiện
    List<Payment> findByClient_IdAndStatus(Long clientId, Payment.Status status);

    List<Payment> findByProject_IdAndStatus(Long projectId, Payment.Status status);

}
