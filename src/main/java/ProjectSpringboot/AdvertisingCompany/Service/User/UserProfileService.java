package ProjectSpringboot.AdvertisingCompany.Service.User;

import ProjectSpringboot.AdvertisingCompany.Entity.User;
import ProjectSpringboot.AdvertisingCompany.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UserProfileService {
    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");
    private static final Pattern PHONE_VN_PATTERN =
            Pattern.compile("^0\\d{9,10}$"); // sau chuẩn hoá +84 -> 0, chấp nhận 10-11 số tổng
    private static final Set<String> GENDERS = Set.of("MALE","FEMALE","OTHER");

    public User findByUsername(String username) { return userRepository.findByUsername(username); }
    public List<User> findByRole(String role){ return userRepository.findByRole(role); }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /** Dùng cho form /user/edit: email, SDT, address, đổi mật khẩu tuỳ chọn. Username không đổi. */
    public User updateProfile(Long id,
                              String email,
                              String sdt,
                              String address,
                              String currentPassword,
                              String newPassword,
                              String confirmPassword) {

        User user = getUserById(id);

        String normEmail = requireAndNormalizeEmail(email);
        ensureEmailUnique(normEmail, id);

        String phone = normalizePhone(sdt);                 // optional
        String addr  = truncate(address, 255);              // optional

        // Đổi mật khẩu nếu có nhập newPassword hoặc confirmPassword
        boolean wantChangePwd =
                StringUtils.hasText(newPassword) || StringUtils.hasText(confirmPassword) || StringUtils.hasText(currentPassword);
        if (wantChangePwd) {
            if (!StringUtils.hasText(currentPassword)) throw bad("currentPassword is required");
            if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) throw bad("currentPassword is incorrect");
            if (!StringUtils.hasText(newPassword)) throw bad("newPassword is required");
            if (!newPassword.equals(confirmPassword)) throw bad("password confirmation mismatch");
            validatePassword(newPassword);
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }

        // Gán các field được phép sửa
        user.setEmail(normEmail);
        user.setSDT(phone);
        user.setAddress(addr);
        user.setUpdated_at(LocalDateTime.now());

        return userRepository.save(user);
    }

    /** Giữ để tương thích chỗ khác nếu đang gọi. Không dùng cho form /user/edit. */
    public User updateUser(Long id, User userDetails){
        User user = getUserById(id);

        // Username không cho đổi ở màn hình này, bỏ qua giá trị từ payload
        // user.setUsername(user.getUsername());

        String normEmail = requireAndNormalizeEmail(userDetails.getEmail());
        ensureEmailUnique(normEmail, id);
        String phone = normalizePhone(userDetails.getSDT());
        String addr  = truncate(userDetails.getAddress(), 255);
        String gender = normalizeGender(userDetails.getGender());

        user.setEmail(normEmail);
        user.setSDT(phone);
        user.setAddress(addr);
        user.setGender(gender);

        if (StringUtils.hasText(userDetails.getPassword())) {
            validatePassword(userDetails.getPassword());
            user.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        }

        user.setUpdated_at(LocalDateTime.now());
        return userRepository.save(user);
    }

    /* ================= Helpers ================= */

    private String requireAndNormalizeEmail(String email) {
        if (!StringUtils.hasText(email)) throw bad("email is required");
        String e = email.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(e).matches()) throw bad("email is invalid");
        return e;
    }

    private String normalizePhone(String sdt) {
        if (!StringUtils.hasText(sdt)) return null;
        String v = sdt.trim().replaceAll("[^0-9+]", "");
        if (v.startsWith("+84")) v = "0" + v.substring(3);
        else if (v.startsWith("84")) v = "0" + v.substring(2);
        v = v.replaceAll("\\D", "");
        if (!PHONE_VN_PATTERN.matcher(v).matches()) throw bad("phone is invalid");
        return v;
    }

    private String normalizeGender(String gender) {
        if (!StringUtils.hasText(gender)) return "OTHER";
        String g = gender.trim().toUpperCase(Locale.ROOT);
        if (!GENDERS.contains(g)) throw bad("gender is invalid");
        return g;
    }

    private void validatePassword(String raw) {
        int len = raw.length();
        if (len < 8 || len > 72) throw bad("password length must be 8-72");
        boolean strong = raw.matches(".*[A-Z].*") && raw.matches(".*[a-z].*") && raw.matches(".*\\d.*");
        if (!strong) throw bad("password must contain upper, lower, digit");
    }

    private void ensureEmailUnique(String email, Long excludeId) {
        List<User> all = userRepository.findAll();
        for (User u : all) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                if (excludeId == null || !u.getId().equals(excludeId)) throw bad("email already in use");
            }
        }
    }

    private String truncate(String s, int max) {
        if (!StringUtils.hasText(s)) return null;
        String v = s.trim();
        return v.length() <= max ? v : v.substring(0, max);
    }

    private RuntimeException bad(String msg) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg); }
}
