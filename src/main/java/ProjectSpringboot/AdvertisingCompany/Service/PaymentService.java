package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.Payment;
import ProjectSpringboot.AdvertisingCompany.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Lấy tất cả payment
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Lấy payment theo id
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    // Tạo payment mới
    public Payment createPayment(Payment payment) {
        payment.setCreated_at(LocalDateTime.now());
        payment.setUpdated_at(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    // Cập nhật payment
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = getPaymentById(id);

        payment.setContract(paymentDetails.getContract());
        payment.setClient(paymentDetails.getClient());
        payment.setCompany(paymentDetails.getCompany());
        payment.setEmployee(paymentDetails.getEmployee());
        payment.setProject(paymentDetails.getProject());

        payment.setName(paymentDetails.getName());
        payment.setSignDate(paymentDetails.getSignDate());
        payment.setTotalCost(paymentDetails.getTotalCost());
        payment.setAmountPaid(paymentDetails.getAmountPaid());
        payment.setStatus(paymentDetails.getStatus());

        payment.setUpdated_at(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    // Xóa payment
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }

    // ================= CUSTOM QUERY ================= //

    // Tìm theo name
    public List<Payment> getPaymentsByName(String name) {
        return paymentRepository.findByName(name);
    }

    // Tìm theo project id
    public List<Payment> getPaymentsByProject(Long projectId) {
        return paymentRepository.findByProject_Id(projectId);
    }

    // Tìm theo status
    public List<Payment> getPaymentsByStatus(Payment.Status status) {
        return paymentRepository.findByStatus(status);
    }

    // Tìm theo client id
    public List<Payment> getPaymentsByClient(Long clientId) {
        return paymentRepository.findByClient_Id(clientId);
    }

    // Kết hợp client + status
    public List<Payment> getPaymentsByClientAndStatus(Long clientId, Payment.Status status) {
        return paymentRepository.findByClient_IdAndStatus(clientId, status);
    }

    // Kết hợp project + status
    public List<Payment> getPaymentsByProjectAndStatus(Long projectId, Payment.Status status) {
        return paymentRepository.findByProject_IdAndStatus(projectId, status);
    }
}