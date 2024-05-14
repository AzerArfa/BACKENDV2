package com.offer.controller.user;

import com.offer.entity.Offer;
import com.offer.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/offer/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Offer>> getAllOffers(){
        List<Offer> offers = userService.getAllOffers();
        return ResponseEntity.ok(offers);
    }

}
