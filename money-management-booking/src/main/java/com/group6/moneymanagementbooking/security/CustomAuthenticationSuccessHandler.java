package com.group6.moneymanagementbooking.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.repository.UsersRepository;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsersRepository usersRepository;

    public CustomAuthenticationSuccessHandler(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
            String email = request.getParameter("email");
            Users users = usersRepository.findByEmail(email)
                    .get();
            users.setFailed_attempt(0);
            users.setLockTime(null);
            users.setLockTime(null);
            usersRepository.save(users);
            Collection<? extends GrantedAuthority> authorities = authentication
                    .getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority()
                        .equals("ROLE_ADMIN")) {
                    response.sendRedirect(
                            "/admins/home");
                }
                if (grantedAuthority.getAuthority()
                        .equals("ROLE_USER")) {
                    response.sendRedirect(
                            "/users/overview");
                }
            }
            super.onAuthenticationSuccess(request, response, authentication);
    }
}
