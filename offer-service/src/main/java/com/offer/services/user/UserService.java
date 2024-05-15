package com.offer.services.user;

import com.offer.entity.Offer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
	Optional<Offer> getOfferById(UUID id);
    List<Offer> getAllOffers();
}
