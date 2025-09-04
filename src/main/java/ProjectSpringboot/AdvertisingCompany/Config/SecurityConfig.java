package ProjectSpringboot.AdvertisingCompany.Config;

import ProjectSpringboot.AdvertisingCompany.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Kích hoạt @PreAuthorize
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var user = userService.findByUsername(username);
            if (user == null) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found: " + username);
            }

            // Debug: In thông tin user
            System.out.println("Found user: " + user.getUsername() + ", Role: " + user.getRole());

            String role = user.getRole();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    true, // enabled
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    true, // accountNonLocked
                    org.springframework.security.core.authority.AuthorityUtils.createAuthorityList(role)
            );
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tạm thời disable CSRF để test
                .authorizeHttpRequests(auth -> auth
                        // Public resources
                        .requestMatchers("/", "/home", "/index", "/service", "/project", "/css/**", "/js/**", "/images/**", "/assets/**",
                                "/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        // Client
                        .requestMatchers("/user/**").hasRole("CLIENT")
                        // Admin only
                        .requestMatchers("/admin/users/**").hasRole("ADMIN")

                        // Admin và Employee
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "EMPLOYEE")

                        // Tất cả request khác cần authenticate
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // URL xử lý POST request
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            System.out.println("Login successful for user: " + authentication.getName());
                            System.out.println("Authorities: " + authentication.getAuthorities());

                            String redirectUrl = "/"; // mặc định client về trang chủ

                            if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE"))) {
                                redirectUrl = "/admin/dashboard";
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT"))) {
                                redirectUrl = "/";
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                                redirectUrl = "/admin/dashboard";
                            }
                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler((request, response, exception) -> {
                            // Custom failure handler để debug
                            System.out.println("Login failed: " + exception.getMessage());
                            response.sendRedirect("/login?error=true");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );
        return http.build();
    }
}
