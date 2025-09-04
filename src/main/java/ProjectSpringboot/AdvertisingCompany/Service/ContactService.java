package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.Contact;
import ProjectSpringboot.AdvertisingCompany.Repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    public ContactRepository contactRepository;

    // Lấy tất cả contact
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    // Lấy contact theo id (ném RuntimeException nếu không thấy)
    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
    }

    // Tạo mới contact
    public Contact createContact(Contact contact) {
        // created_at / updated_at nếu bạn để @CreationTimestamp/@UpdateTimestamp
        // thì không cần set ở đây. Nếu không dùng annotation, có thể set thủ công:
        if (contact.getCreated_at() == null) {
            contact.setCreated_at(LocalDateTime.now());
        }
        contact.setUpdated_at(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    // Cập nhật contact
    public Contact updateContact(Long id, Contact contactDetails) {
        Contact contact = getContactById(id);
        contact.setFullName(contactDetails.getFullName());
        contact.setEmail(contactDetails.getEmail());
        contact.setPhone(contactDetails.getPhone());
        contact.setService(contactDetails.getService());
        contact.setDescription(contactDetails.getDescription());
        contact.setUpdated_at(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    // Xoá contact
    public void deleteContact(Long id) {
        Contact contact = getContactById(id);
        contactRepository.delete(contact);
    }

    // Đếm tổng số contact
    public long countContacts() {
        return contactRepository.count();
    }

    // Tìm kiếm theo service (LIKE, không phân biệt hoa thường)
    public List<Contact> searchByService(String q) {
        return contactRepository.findByServiceContainingIgnoreCase(q);
    }

    // Tìm kiếm theo fullName (LIKE, không phân biệt hoa thường)
    public List<Contact> searchByFullName(String q) {
        return contactRepository.findByFullNameContainingIgnoreCase(q);
    }

    // Lấy N contact mới nhất (dựa vào created_at DESC)
    public List<Contact> getRecentContacts(int limit) {
        return contactRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "created_at"))
        ).getContent();
    }
}
