package com.bibliotheque.bibliotheque_api.security.config;

import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MembreRepository membreRepository;

    public CustomUserDetailsService(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return membreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun membre trouvé avec cet email : " + email));
    }
}
