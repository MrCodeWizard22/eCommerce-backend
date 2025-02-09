package com.varshneys.ecommerce.ecommerce_backend.security;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.varshneys.ecommerce.ecommerce_backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.varshneys.ecommerce.ecommerce_backend.Model.User;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    // constructor 
    public UserDetailServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // loader 
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
        
        return UserDetailImpl.build(user);
    }


}
