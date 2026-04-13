package com.example.deliveryservice.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Embeddable
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    String city;
    String street;
    String house;
}
