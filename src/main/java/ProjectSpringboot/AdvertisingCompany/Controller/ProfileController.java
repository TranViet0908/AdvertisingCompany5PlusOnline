package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.User;
import ProjectSpringboot.AdvertisingCompany.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class ProfileController {

    @Autowired
    private UserService userService;

    // Hiển thị trang profile
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login"; // nếu chưa login thì quay lại login
        }

        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user/profile/profile"; // templates/user/profile.html
    }

    // Hiển thị form edit
    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user/profile/edit"; // tạo file templates/user/edit-profile.html
    }

    // Cập nhật profile
    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("user") User userDetailsForm) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User currentUser = userService.findByUsername(userDetails.getUsername());
        userService.updateUser(currentUser.getId(), userDetailsForm);

        return "redirect:/user/profile";
    }
}