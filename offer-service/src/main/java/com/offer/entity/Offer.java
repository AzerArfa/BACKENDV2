package com.offer.entity;


import java.util.Date;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Offer {

	@Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date datecreation = new Date();
    
    private String titre;

    private String description;
    private Date datelimitesoumission;
    private UUID entrepriseId ;
    

}
