package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;


public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User userId);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User userId);

    String deleteAddressById(Long addressId);

    AddressDTO updateAddressById(AddressDTO addressDTO, Long addressId);
}
