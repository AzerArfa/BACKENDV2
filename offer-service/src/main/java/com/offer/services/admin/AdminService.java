package com.offer.services.admin;

import com.offer.entity.Offer;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminService {

    Offer createOffer(Offer offer);

    List<Offer> getAllOffers();

    Offer updateOffer(Offer offer);
    List<Offer> getOffersByEntrepriseId(UUID entrepriseId);
    void deleteOffer(UUID id);

	Optional<Offer> getOfferById(UUID id);
}
