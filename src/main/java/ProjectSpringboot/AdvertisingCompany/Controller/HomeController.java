package ProjectSpringboot.AdvertisingCompany.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index"; // Render template index.html
    }

    @GetMapping("/home")
    public String homePage() {
        return "index"; // Render template index.html
    }
}