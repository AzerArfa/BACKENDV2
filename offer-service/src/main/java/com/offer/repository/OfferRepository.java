package com.offer.repository;

import com.offer.entity.Offer;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {
	List<Offer> findByEntrepriseId(UUID entrepriseId);
}
