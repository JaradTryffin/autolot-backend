package com.autolot.autolotbackend.service.auth;

import com.autolot.autolotbackend.model.dto.AuthResponse;
import com.autolot.autolotbackend.model.dto.LoginRequest;
import com.autolot.autolotbackend.model.dto.SignupRequest;
import com.autolot.autolotbackend.model.entity.AdminRole;
import com.autolot.autolotbackend.model.entity.AdminUser;
import com.autolot.autolotbackend.model.entity.Dealership;
import com.autolot.autolotbackend.model.entity.SiteConfig;
import com.autolot.autolotbackend.repository.AdminUserRepository;
import com.autolot.autolotbackend.repository.DealershipRepository;
import com.autolot.autolotbackend.repository.SiteConfigRepository;
import com.autolot.autolotbackend.security.JwtUtil;
import com.autolot.autolotbackend.service.password.PasswordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final DealershipRepository dealershipRepository;
    private final AdminUserRepository adminUserRepository;
    private final SiteConfigRepository siteConfigRepository;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse signup(SignupRequest request)  {
       if(dealershipRepository.existsByEmail(request.email())){
           throw new RuntimeException("Email already exists");
       }
       if(dealershipRepository.findBySlug(request.slug()).isPresent()){
           throw new RuntimeException("Slug already taken");
       }

       Dealership dealership = Dealership.builder()
               .name(request.dealershipName())
               .slug(request.slug())
               .email(request.email())
               .active(true)
               .build();
       dealershipRepository.save(dealership);

        AdminUser adminUser = AdminUser.builder()
                .dealership(dealership)
                .email(request.email())
                .hashedPassword(passwordService.hashPassword(request.password()))
                .fullName(request.fullName())
                .adminRole(AdminRole.OWNER)
                .build();
        adminUserRepository.save(adminUser);

        SiteConfig siteConfig = SiteConfig.builder()
                .layoutJson("{\"sections\": []}")
                .theme("default")
                .primaryColor("#000000")
                .build();
        siteConfig.setDealership(dealership);
        siteConfigRepository.save(siteConfig);

        String token = jwtUtil.generateToken(
                adminUser.getId(),
                dealership.getId(),
                adminUser.getEmail(),
                adminUser.getAdminRole().name()
        );

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request){
        AdminUser adminUser = adminUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if(!passwordService.verifyPassword(request.password(), adminUser.getHashedPassword())){
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                adminUser.getId(),
                adminUser.getDealership().getId(),
                adminUser.getEmail(),
                adminUser.getAdminRole().name()
        );
        return new AuthResponse(token);
    }

}
