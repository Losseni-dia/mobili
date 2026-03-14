package com.mobili.backend.module.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mobili.backend.module.admin.dto.AdminStatsResponse;
import com.mobili.backend.module.admin.dto.PartnerAdminResponse;
import com.mobili.backend.module.admin.dto.UserAdminResponse;
import com.mobili.backend.module.admin.service.AdminService;
import com.mobili.backend.module.partner.dto.mapper.PartnerMapper;
import com.mobili.backend.module.partner.entity.Partner;
import com.mobili.backend.module.partner.service.PartnerService;
import com.mobili.backend.module.user.dto.mapper.UserMapper;
import com.mobili.backend.module.user.entity.User;
import com.mobili.backend.module.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PartnerService partnerService;
    private final AdminService adminService;
    private final UserMapper userMapper; // Injection du mapper ici
    private final PartnerMapper partnerMapper;



    // 💡 AJOUT : Récupérer tous les utilisateurs pour le tableau Angular
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();

        // 💡 C'est ici que la magie opère : on transforme l'entité en DTO
        List<UserAdminResponse> response = users.stream()
                .map(userMapper::toAdminDto)
                .toList();

        return ResponseEntity.ok(response);
    }

  @GetMapping("/partners")
  public ResponseEntity<List<PartnerAdminResponse>> getAllPartners() {
         List<Partner> partners = partnerService.findAll();

        
        List<PartnerAdminResponse> response = partners.stream()
                .map(partnerMapper::toAdminDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    // Activer/Désactiver l'accès au site
    @PatchMapping("/users/{id}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        userService.toggleUserStatus(id, enabled);
        return ResponseEntity.ok().build();
    }

    // Activer/Désactiver le droit de publier des trajets
    @PatchMapping("/partners/{id}/toggle")
    public ResponseEntity<Void> togglePartnerStatus(@PathVariable Long id) {
        partnerService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getGlobalStats());
    }
}
