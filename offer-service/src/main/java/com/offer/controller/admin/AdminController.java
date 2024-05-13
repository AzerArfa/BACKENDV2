package com.offer.controller.admin;

import com.offer.entity.Offer;
import com.offer.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/offer/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
        Offer createdOffer = adminService.createOffer(offer);
        if (createdOffer == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
    }

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(adminService.getAllOffers());
    }

    @PutMapping
    public ResponseEntity<Offer> updateOffer(@RequestBody Offer offer) {
        Offer updatedOffer = adminService.updateOffer(offer);
        if (updatedOffer == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.OK).body(updatedOffer);
    }

    @GetMapping("/{id}")
    public void getAllOffers(@PathVariable UUID id) {
        adminService.deleteOffer(id);
    }
    @GetMapping("/entreprises/{entrepriseId}/offers")
    public ResponseEntity<List<Offer>> getOffersByEntrepriseId(@PathVariable UUID entrepriseId) {
        List<Offer> offers = adminService.getOffersByEntrepriseId(entrepriseId);
        return ResponseEntity.ok(offers);
    }

}
