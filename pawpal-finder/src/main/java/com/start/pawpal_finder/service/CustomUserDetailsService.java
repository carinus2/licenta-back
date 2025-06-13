package com.start.pawpal_finder.service;

import com.start.pawpal_finder.dto.CustomUserDetails;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PetSitterService petSitterService;
    private final PetOwnerService petOwnerService;

    @Autowired
    public CustomUserDetailsService(PetSitterService petSitterService,
                                    PetOwnerService petOwnerService) {
        this.petSitterService = petSitterService;
        this.petOwnerService = petOwnerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        PetSitterEntity petSitter = petSitterService.findByEmail(username).orElse(null);
        if (petSitter != null) {
            return new CustomUserDetails(
                    petSitter.getEmail(),
                    petSitter.getPassword(),
                    Collections.singleton(() -> "ROLE_PET_SITTER"),
                    petSitter.getId()
            );
        }

        PetOwnerEntity petOwner = petOwnerService.findByEmail(username).orElse(null);
        if (petOwner != null) {
            String authority = petOwner.getAdmin()
                    ? "ROLE_ADMIN"
                    : "ROLE_PET_OWNER";

            return new CustomUserDetails(
                    petOwner.getEmail(),
                    petOwner.getPassword(),
                    Collections.singleton(() -> authority),
                    petOwner.getId()
            );
        }

        throw new UsernameNotFoundException("No user found with email: " + username);
    }
}
