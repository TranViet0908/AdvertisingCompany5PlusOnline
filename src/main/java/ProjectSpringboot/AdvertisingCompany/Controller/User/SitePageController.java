package ProjectSpringboot.AdvertisingCompany.Controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import ProjectSpringboot.AdvertisingCompany.Entity.Department;
import ProjectSpringboot.AdvertisingCompany.Repository.DepartmentRepository;

@Controller
public class SitePageController {

    // Cho phép null để không chặn khởi động nếu bạn chưa tạo repo
    @Autowired(required = false)
    private DepartmentRepository departmentRepository;

    @GetMapping({"/service", "/services"})
    public String servicePage(Model model) {
        List<Department> services = List.of();
        if (departmentRepository != null) {
            services = departmentRepository.findAll();
        }
        model.addAttribute("services", services);
        return "service";
    }

    @GetMapping({"/project", "/projects"})
    public String projectPage(Model model) {
        // Điền URL TẠM CỦA BẠN ở đây (có thể thay ngay không cần DB)
        List<MediaVM> media = List.of(
                MediaVM.video(
                        "TVC Tết 60s",
                        "https://cdn.example.com/videos/tvc-tet-60s.mp4",
                        "https://i.ytimg.com/vi/ysz5S6PUM-U/hqdefault.jpg",
                        "Chiến dịch Tết 2025", "Client A", 11L
                ),
                MediaVM.youtube(
                        "TVC Mùa Hè",
                        // Dùng link embed để hiển thị trong iframe
                        "https://www.youtube.com/embed/ysz5S6PUM-U",
                        null,
                        "Summer Campaign", "Client B", 12L
                ),
                MediaVM.image(
                        "Key Visual Lễ Hội",
                        "https://images.pexels.com/photos/3184405/pexels-photo-3184405.jpeg",
                        null,
                        "Festive 2025", "Client C", 13L
                )
        );

        model.addAttribute("media", media); // project.html sẽ ưu tiên render theo 'media'
        return "project";
    }

    public static class MediaVM {
        public enum Type { VIDEO_MP4, YOUTUBE, IMAGE }
        public String title;
        public String url;
        public String thumb;
        public String projectName;
        public String clientName;
        public Long projectId;
        public Type type;

        public static MediaVM video(String title, String url, String thumb,
                                    String projectName, String clientName, Long projectId) {
            MediaVM m = new MediaVM();
            m.type = Type.VIDEO_MP4;
            m.title = title; m.url = url; m.thumb = thumb;
            m.projectName = projectName; m.clientName = clientName; m.projectId = projectId;
            return m;
        }

        public static MediaVM youtube(String title, String embedUrl, String thumb,
                                      String projectName, String clientName, Long projectId) {
            MediaVM m = new MediaVM();
            m.type = Type.YOUTUBE;
            m.title = title; m.url = embedUrl; m.thumb = thumb;
            m.projectName = projectName; m.clientName = clientName; m.projectId = projectId;
            return m;
        }

        public static MediaVM image(String title, String url, String thumb,
                                    String projectName, String clientName, Long projectId) {
            MediaVM m = new MediaVM();
            m.type = Type.IMAGE;
            m.title = title; m.url = url; m.thumb = thumb;
            m.projectName = projectName; m.clientName = clientName; m.projectId = projectId;
            return m;
        }
    }
}
