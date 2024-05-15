package com.offer.controller.admin;

import com.offer.entity.Offer;
import com.offer.services.admin.AdminService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/offer/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Map<String, String>> createOffer(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("datelimitesoumission") @DateTimeFormat(pattern = "yyyy-MM-dd") Date datelimitesoumission,
            @RequestParam("entrepriseId") UUID entrepriseId,
            @RequestParam("localisation") String localisation,
            @RequestParam("document") MultipartFile document,
            @RequestParam("img") MultipartFile img) {

        Map<String, String> response = new HashMap<>();
        try {
            Offer offer = new Offer();
            offer.setTitre(titre);
            offer.setDescription(description);
            offer.setDatelimitesoumission(datelimitesoumission);
            offer.setEntrepriseId(entrepriseId);
            offer.setLocalisation(localisation);

            if (img != null && !img.isEmpty()) {
                byte[] returnedImg = img.getBytes();
                offer.setImg(returnedImg);
            }

            if (document != null && !document.isEmpty()) {
                if (!document.getContentType().equals("application/pdf")) {
                    response.put("error", "Document must be a PDF file.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                byte[] returnedDoc = document.getBytes();
                offer.setDocument(returnedDoc);
            }

            Offer createdOffer = adminService.createOffer(offer);
            if (createdOffer == null) {
                response.put("error", "Failed to create offer.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.put("message", "Offer created successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error creating offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers() {
        return ResponseEntity.ok(adminService.getAllOffers());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Map<String, String>> updateOffer(
            @PathVariable UUID id,
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String localisation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date datelimitesoumission,
            @RequestParam(required = false) MultipartFile img,
            @RequestParam(required = false) MultipartFile document) {

        Map<String, String> response = new HashMap<>();
        try {
            Optional<Offer> optionalOffer = adminService.getOfferById(id);
            if (!optionalOffer.isPresent()) {
                response.put("error", "Offer not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Offer existingOffer = optionalOffer.get();
            if (titre != null) existingOffer.setTitre(titre);
            if (description != null) existingOffer.setDescription(description);
            if (datelimitesoumission != null) existingOffer.setDatelimitesoumission(datelimitesoumission);
            if (localisation != null) existingOffer.setLocalisation(localisation);
            if (img != null && !img.isEmpty()) {
                byte[] returnedImg = img.getBytes();
                existingOffer.setImg(returnedImg);
            }

            if (document != null && !document.isEmpty()) {
                if (!document.getContentType().equals("application/pdf")) {
                    response.put("error", "Document must be a PDF file.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                byte[] returnedDoc = document.getBytes();
                existingOffer.setDocument(returnedDoc);
            }

            Offer updatedOffer = adminService.updateOffer(existingOffer);
            if (updatedOffer == null) {
                response.put("error", "Failed to update offer.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.put("message", "Offer updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating offer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable UUID id) {
        Optional<Offer> offer = adminService.getOfferById(id);
        return offer.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public void supprimerOffre(@PathVariable UUID id) {
        adminService.deleteOffer(id);
    }
    @GetMapping("/entreprises/{entrepriseId}/offers")
    public ResponseEntity<List<Offer>> getOffersByEntrepriseId(@PathVariable UUID entrepriseId) {
        List<Offer> offers = adminService.getOffersByEntrepriseId(entrepriseId);
        return ResponseEntity.ok(offers);
    }

}
