package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Service.AdminApproveService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/approvals")
@PreAuthorize("hasRole('ADMIN')")
public class AdminApproveController {

    private final AdminApproveService service;

    public AdminApproveController(AdminApproveService service) {
        this.service = service;
    }

    // LIST
    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "status", required = false) String status,
                       @RequestParam(value = "approveType", required = false) String approveType,
                       Model model) {
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        model.addAttribute("approveType", approveType);
        service.bindFormLists(model);                         // để có allStatuses, allApproveTypes cho filter
        model.addAttribute("items", service.list(q, status, approveType));
        return "admin/approvals/list";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createForm(Model model) {
        service.bindFormLists(model);
        return "admin/approvals/create";
    }

    // CREATE SUBMIT
    @PostMapping
    public String create(@RequestParam Map<String, String> form,
                         RedirectAttributes ra) {
        try {
            Long id = service.create(form);
            ra.addFlashAttribute("success", "Đã tạo phê duyệt #" + id);
            return "redirect:/admin/approvals/edit/" + id;
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/approvals/create";
        }
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id,
                           Model model,
                           RedirectAttributes ra) {
        try {
            service.bindFormLists(model);
            model.addAttribute("header", service.loadHeaderMap(id));           // Map snake_case cho edit.html
            model.addAttribute("mediaGroups", service.loadChildrenGrouped(id)); // images/videos/texts/attaches/allChildren
            return "admin/approvals/edit";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/approvals";
        }
    }

    // UPDATE SUBMIT
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Map<String, String> form,
                         RedirectAttributes ra) {
        try {
            service.update(id, form);
            ra.addFlashAttribute("success", "Đã cập nhật phê duyệt #" + id);
            return "redirect:/admin/approvals"; // quay về list sau khi cập nhật
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/approvals/edit/" + id;
    }

    // DELETE HEADER (cascade)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra) {
        try {
            service.deleteCascade(id);
            ra.addFlashAttribute("success", "Đã xóa phê duyệt #" + id);
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/approvals";
    }

    // ========= MEDIA / COMMENT / ATTACH =========

    @PostMapping("/{id}/media")
    public String addMedia(@PathVariable Long id,
                           @RequestParam Map<String, String> form,
                           RedirectAttributes ra) {
        try {
            service.addMedia(id, form);
            ra.addFlashAttribute("success", "Đã thêm MEDIA");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/approvals/edit/" + id;
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam Map<String, String> form,
                             RedirectAttributes ra) {
        try {
            service.addComment(id, form);
            ra.addFlashAttribute("success", "Đã thêm ghi chú");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/approvals/edit/" + id;
    }

    @PostMapping("/{id}/attach")
    public String addAttach(@PathVariable Long id,
                            @RequestParam Map<String, String> form,
                            RedirectAttributes ra) {
        try {
            service.addAttach(id, form);
            ra.addFlashAttribute("success", "Đã thêm tài liệu");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/approvals/edit/" + id;
    }

    @GetMapping("/{id}/children/{childId}/delete")
    public String deleteChild(@PathVariable Long id,
                              @PathVariable Long childId,
                              RedirectAttributes ra) {
        try {
            service.deleteChild(id, childId);
            ra.addFlashAttribute("success", "Đã xóa nội dung");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/approvals/edit/" + id;
    }
}
