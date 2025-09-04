package ProjectSpringboot.AdvertisingCompany.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied"; // Render template error/access-denied.html
    }
}