package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.User;
import ProjectSpringboot.AdvertisingCompany.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model, @RequestParam("confirmPassword") String confirmPassword) {

        // ✅ Kiểm tra username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("error", "Username is required");
            return "register";
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            model.addAttribute("error", "Username must be between 3 and 50 characters");
            return "register";
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        // ✅ Kiểm tra password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "Password is required");
            return "register";
        }
        if (user.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            return "register";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "register";
        }
        // ✅ Kiểm tra email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Email is required");
            return "register";
        }

        // ✅ Kiểm tra số điện thoại
        if (user.getSDT() == null || user.getSDT().trim().isEmpty()) {
            model.addAttribute("error", "Phone number is required");
            return "register";
        }

        // ✅ Giới tính (optional, có thể để trống)
        if (user.getGender() == null || user.getGender().trim().isEmpty()) {
            user.setGender("UNKNOWN"); // mặc định
        }

        // ✅ Gán role mặc định
        user.setRole("ROLE_CLIENT");

        // ✅ Set thời gian tạo & cập nhật
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());

        // ✅ Lưu user
        userService.createUser(user);

        return "redirect:/login?success";
    }
}