package com.offer.services.admin;

import com.offer.entity.Offer;
import com.offer.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final OfferRepository offerRepository;

    @Override
    public Offer createOffer(Offer offer) {
        try {
            Offer newOffer = new Offer();
            newOffer.setTitre(offer.getTitre());
            newOffer.setDescription(offer.getDescription());
            newOffer.setDatelimitesoumission(offer.getDatelimitesoumission());
            newOffer.setEntrepriseId(offer.getEntrepriseId());
            return offerRepository.save(offer);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @Override
    public Offer updateOffer(Offer offer) {
        Optional<Offer> optionalOffer = offerRepository.findById(offer.getId());
        if (optionalOffer.isPresent()) {
            Offer existingOffer = optionalOffer.get();
            existingOffer.setTitre(offer.getTitre());
            existingOffer.setDescription(offer.getDescription());
            existingOffer.setDatelimitesoumission(offer.getDatelimitesoumission());
            return offerRepository.save(existingOffer);
        }
        return null;
    }

    @Override
    public void deleteOffer(UUID id) {
        offerRepository.deleteById(id);
    }
    @Override
    public List<Offer> getOffersByEntrepriseId(UUID entrepriseId) {
        return offerRepository.findByEntrepriseId(entrepriseId);
    }
}
