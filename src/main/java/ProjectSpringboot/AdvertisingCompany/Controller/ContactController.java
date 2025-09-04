package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.Contact;
import ProjectSpringboot.AdvertisingCompany.Service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/contacts")
public class ContactController {

    @Autowired
    public ContactService contactService;

    // LIST + SEARCH
    @GetMapping
    public String listContacts(@RequestParam(required = false) String search, Model model) {
        List<Contact> contacts = new ArrayList<>();

        if (search == null || search.isBlank()) {
            contacts = contactService.getAllContacts();
        } else if (isNumeric(search)) {
            // Tìm theo ID
            try {
                Long id = Long.parseLong(search);
                Contact c = contactService.getContactById(id);
                if (c != null) contacts = List.of(c);
            } catch (NumberFormatException ignore) {
                // không phải số hợp lệ -> rớt xuống tìm theo text
                contacts = searchFallback(search);
            } catch (RuntimeException ex) {
                // không tìm thấy id -> rớt xuống tìm theo text
                contacts = searchFallback(search);
            }
        } else {
            // Tìm theo text: fullName trước, nếu rỗng thì thử service
            contacts = searchFallback(search);
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("search", search);
        return "admin/contacts/list";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "admin/contacts/create";
    }

    // CREATE ACTION
    @PostMapping
    public String createContact(@ModelAttribute Contact contact) {
        LocalDateTime now = LocalDateTime.now();
        if (contact.getCreated_at() == null) contact.setCreated_at(now);
        contact.setUpdated_at(now);
        contactService.createContact(contact);
        return "redirect:/admin/contacts";
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Contact contact = contactService.getContactById(id);
        model.addAttribute("contact", contact);
        return "admin/contacts/edit";
    }

    // UPDATE ACTION (giữ đúng pattern POST /{id} như ContractController)
    @PostMapping("/{id}")
    public String updateContact(@PathVariable Long id, @ModelAttribute Contact contact) {
        contactService.updateContact(id, contact);
        return "redirect:/admin/contacts";
    }

    // DELETE (GET giống ContractController)
    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return "redirect:/admin/contacts";
    }

    // ===== Helpers =====
    private boolean isNumeric(String s) {
        if (s == null || s.isBlank()) return false;
        try { Long.parseLong(s.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }

    private List<Contact> searchFallback(String q) {
        List<Contact> byName = contactService.searchByFullName(q);
        if (!byName.isEmpty()) return byName;
        return contactService.searchByService(q);
    }
}
