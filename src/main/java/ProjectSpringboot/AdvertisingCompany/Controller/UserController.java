package ProjectSpringboot.AdvertisingCompany.Controller;

import ProjectSpringboot.AdvertisingCompany.Entity.User;
import ProjectSpringboot.AdvertisingCompany.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllUser(@RequestParam(required = false) String search, Model model) {
        List<User> users;

        if (search != null && !search.isEmpty()) {
            try {
                Long id = Long.parseLong(search);
                User userById = userService.getUserById(id);
                users = (userById != null) ? List.of(userById) : List.of();
            } catch (NumberFormatException e) {
                // Tìm chính xác username
                User userByUsername = userService.findByUsername(search);
                if (userByUsername != null) {
                    users = List.of(userByUsername);
                } else {
                    // Tìm theo role (chính xác)
                    users = userService.findByRole(search);
                    // Nếu role ko có → tìm theo contains username
                    if (users.isEmpty()) {
                        users = userService.getAllUser().stream()
                                .filter(u -> u.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                        u.getRole().toLowerCase().contains(search.toLowerCase()))
                                .toList();
                    }
                }
            }
        } else {
            users = userService.getAllUser();
        }

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        return "/admin/user/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("user", new User());
        return "/admin/user/create";
    }

    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "/admin/user/edit";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user){
        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
