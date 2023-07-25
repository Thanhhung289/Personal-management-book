package com.group6.moneymanagementbooking.exception;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.group6.moneymanagementbooking.enity.Users;
import com.group6.moneymanagementbooking.repository.UsersRepository;
import com.group6.moneymanagementbooking.security.CustomUserDetailsService;
import com.group6.moneymanagementbooking.security.MyUserDetails;
import com.group6.moneymanagementbooking.service.impl.UsersServiceImpl;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UsersServiceImpl usersServiceImpl;

    public CustomAuthenticationFailureHandler(CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder, UsersServiceImpl usersServiceImpl) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.usersServiceImpl = usersServiceImpl;

    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            response.sendRedirect("/login?error=null");
            return;
        }
        try {
            MyUserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails.isEnabled()) {
                Users users = usersServiceImpl.getUserByEmail(username);
                if (users.isNonLocked()) {
                    if (!passwordEncoder.matches(password, users.getPassword())) {
                        usersServiceImpl.checkUnLockUser(users, response);
                    }
                } else {
                    if (passwordEncoder.matches(password, users.getPassword())) {
                        if (usersServiceImpl.unlock(users)) {
                            String rememberMe = request.getParameter("remember-me");
                            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            if (rememberMe != null && rememberMe.equalsIgnoreCase("on")) {
                                rememberMeServices().loginSuccess(request, response, authentication);
                            }
                            for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
                                if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                                    response.sendRedirect("/admins/home");
                                    return;
                                } else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                                    response.sendRedirect("/users/overview");
                                    return;
                                }
                            }
                        } else {
                            response.sendRedirect("/login?error=login-fail&turn=0");
                        }
                    } else {
                        response.sendRedirect("/login?error=login-fail&turn=0");
                    }
                }
            } else {
                response.sendRedirect("/login?error=disabled");
            }
        } catch (UsernameNotFoundException e) {
            response.sendRedirect("/login?error=true");
        } catch (InternalAuthenticationServiceException iase) {
            response.sendRedirect("/login?error=true");
        } catch (Exception ex) {
            response.sendRedirect("/login?error=true");
        }
    }

    public TokenBasedRememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("Axncmvi2002",
                userDetailsService);
        rememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 10 * 30);
        return rememberMeServices;
    }

}