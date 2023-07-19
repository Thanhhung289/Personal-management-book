package com.group6.moneymanagementbooking.security;



import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.repository.UsersRepository;


import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UsersRepository userRepository;

    public CustomUserDetailsService(UsersRepository accountRepository) {
        this.userRepository = accountRepository;
    }

    @Override
    public MyUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            Users account = user.get();
            return new MyUserDetails(account);
        }else{
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

}
