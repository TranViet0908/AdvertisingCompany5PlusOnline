package ProjectSpringboot.AdvertisingCompany.Controller.User;

import ProjectSpringboot.AdvertisingCompany.Service.User.ApproveService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/user/contracts")
@PreAuthorize("hasRole('CLIENT') or hasRole('EMPLOYEE')") // cho phép cả CLIENT/EMPLOYEE nếu cần
public class ApproveController {

    private final ApproveService approveService;

    public ApproveController(ApproveService approveService) {
        this.approveService = approveService;
    }

    // Danh sách phê duyệt (thông tin chung)
    @GetMapping("/approvals")
    public String approvals(Model model) {
        model.addAttribute("items", approveService.listHeadersForListPage());
        return "user/contracts/approvals"; // -> templates/user/contracts/approvals.html
    }

    // Chi tiết phê duyệt
    @GetMapping("/approvals/{id}")
    public String details(@PathVariable Long id, Model model) {
        Map<String, Object> data = approveService.loadDetails(id);
        model.addAllAttributes(data); // header, children, comments
        return "user/contracts/approvals/details"; // -> templates/user/contracts/approvals/details.html
    }

    // Quyết định: APPROVED / REJECTED / NEEDS_REVISION
    @PostMapping("/approvals/{id}/decision")
    public String decide(@PathVariable Long id,
                         @RequestParam("decision") String decision,
                         @RequestParam(value = "comment", required = false) String comment,
                         Authentication auth,
                         RedirectAttributes ra) {
        try {
            String actor = auth != null ? auth.getName() : null;
            approveService.submitDecision(id, decision, comment, actor);
            ra.addFlashAttribute("success", "Đã cập nhật quyết định: " + decision);
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/user/contracts/approvals/" + id;
    }

    // Bình luận/Thảo luận thêm
    @PostMapping("/approvals/{id}/comments")
    public String comment(@PathVariable Long id,
                          @RequestParam("content") String content,
                          Authentication auth,
                          RedirectAttributes ra) {
        try {
            String actor = auth != null ? auth.getName() : null;
            approveService.addComment(id, content, actor);
            ra.addFlashAttribute("success", "Đã thêm bình luận.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/user/contracts/approvals/" + id;
    }
}
