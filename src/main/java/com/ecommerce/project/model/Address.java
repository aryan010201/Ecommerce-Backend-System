package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min=5,message = "Street name must be atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min=5,message = "Building name must be atleast 5 characters")
    private String buildingame;

    @NotBlank
    @Size(min=3,message = "City name must be atleast 3 characters")
    private String city;

    @NotBlank
    @Size(min=2,message = "State name must be atleast 2 characters")
    private String state;

    @NotBlank
    @Size(min=2,message = "Country name must be atleast 2 characters")
    private String country;

    @NotBlank
    @Size(min=6,message = "Country name must be atleast 6 characters")
    private String zip;

    @ToString.Exclude
    @ManyToMany(mappedBy ="addresses")
    private List<User> users;

    public Address(String street, String buildingame, String city, String state, String country, String zip) {
        this.street = street;
        this.buildingame = buildingame;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
    }
}
