package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.User;
import ProjectSpringboot.AdvertisingCompany.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(String role){
        return userRepository.findByRole(role);
    }

    public User createUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User updateUser(Long id, User userDetails){
        User user = getUserById(id);
        user.setUsername(userDetails.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        user.setRole(userDetails.getRole());
        user.setEmail(userDetails.getEmail());
        user.setSDT(userDetails.getSDT());
        user.setGender(userDetails.getGender());
        user.setAddress(userDetails.getAddress());
        user.setCreated_at(userDetails.getCreated_at());
        user.setUpdated_at(userDetails.getUpdated_at());
        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public long countUsers() {
        return userRepository.count();
    }
}
