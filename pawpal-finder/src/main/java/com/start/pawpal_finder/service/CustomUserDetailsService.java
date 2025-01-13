package com.start.pawpal_finder.service;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PetSitterService petSitterService;

    @Autowired
    private PetOwnerService petOwnerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        PetSitterEntity petSitter = petSitterService.findByEmail(username).orElse(null);
        if (petSitter != null) {
            return new User(
                    petSitter.getEmail(),
                    petSitter.getPassword(),
                    Collections.singleton(() -> "ROLE_USER"));
        }
        PetOwnerEntity petOwner = petOwnerService.findByEmail(username).orElse(null);
        if (petOwner != null) {
            return new User(
                    petOwner.getEmail(),
                    petOwner.getPassword(),
                    Collections.singleton(() -> "ROLE_USER"));
        }
        return new User(
                "admin@email.com",
                "admin1234",
                Collections.singleton(() -> "ROLE_ADMIN"));
    }

}
