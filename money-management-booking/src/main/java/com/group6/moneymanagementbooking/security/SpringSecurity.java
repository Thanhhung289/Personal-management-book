package com.group6.moneymanagementbooking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.group6.moneymanagementbooking.exception.CustomAuthenticationFailureHandler;
import com.group6.moneymanagementbooking.repository.UsersRepository;
import com.group6.moneymanagementbooking.service.AccountsService;
import com.group6.moneymanagementbooking.service.UsersService;
import com.group6.moneymanagementbooking.service.impl.UsersServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SpringSecurity {
<<<<<<< HEAD
        private final CustomUserDetailsService userDetailsService;
        private final UsersRepository usersRepository;
        private final UsersServiceImpl usersServiceImpl;
        private final UsersService usersService;
        private final AccountsService accountsService;
=======

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private UsersRepository usersRepository;

        @Autowired
        private UsersServiceImpl usersServiceImpl;

        @Autowired
        private UsersService usersService;

        @Autowired
        private AccountsService accountsService;
>>>>>>> c8ef1a87016b02e4dcfc4b5f30f1d7894b08a518

        @Bean
        public static PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
        @Bean
        public CustomAuthenticationFailureHandler authenticationFailureHandler() {
                return new CustomAuthenticationFailureHandler(userDetailsService, passwordEncoder(), usersServiceImpl,usersRepository);
        }
        @Bean
        public BeforeAuthenticationFilter beforeAuthenticationFilter(AccountsService accountsService,                                                                     UsersService usersService) {
            return new BeforeAuthenticationFilter(accountsService, usersService);
        }
        @Bean 
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler(){
                return new CustomAuthenticationSuccessHandler(usersRepository);
        }
        @Bean
        public CaptchaAuthenticationFilter captchaValidationFilter() {
            return new CaptchaAuthenticationFilter();
        }
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.addFilterBefore(new BeforeAuthenticationFilter(accountsService, usersService),UsernamePasswordAuthenticationFilter.class)
                .csrf().disable().authorizeHttpRequests((authorize) -> authorize.mvcMatchers("/register").permitAll()
                                                .mvcMatchers("/check/**").permitAll()
                                                .mvcMatchers("/find-email").permitAll()
                                                .mvcMatchers("/otps/**").permitAll()
                                                .mvcMatchers("/").permitAll()
                                                .mvcMatchers("/homepage").permitAll()
                                                .mvcMatchers("/forgot-password").permitAll()
                                                .mvcMatchers("/login").permitAll()
                                                .mvcMatchers("/admins/**").hasRole("ADMIN")
                                                .anyRequest().authenticated()
<<<<<<< HEAD
                                ).formLogin(form -> form.loginPage("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .failureUrl("/login?error=true")
                                                .failureHandler(authenticationFailureHandler())
                                                .defaultSuccessUrl("/", false)
                                                .successHandler(customAuthenticationSuccessHandler()).permitAll())
                                                .addFilterBefore(captchaValidationFilter(), UsernamePasswordAuthenticationFilter.class)
                                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll())
                                .rememberMe().key("Axncmvi2002")
                                .tokenValiditySeconds(60 * 60 * 24 * 10 * 30);
=======

                                ).formLogin(
                                                form -> form
                                                                .loginPage("/login")
                                                                .usernameParameter("email")
                                                                .passwordParameter("password")
                                                                .failureUrl("/login?error=true")
                                                                .failureHandler(authenticationFailureHandler())
                                                                .defaultSuccessUrl("/", false)
                                                                .successHandler((request, response, authentication) -> {
                                                                        String email = request.getParameter("email");
                                                                        Users users = usersRepository.findByEmail(email)
                                                                                        .get();
                                                                        HttpSession session = request.getSession();
                                                                        session.setAttribute("userFullName", users
                                                                                        .getFirstName() + " "
                                                                                        + users.getLastName());
                                
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
                                                                }).permitAll())
                                .logout(
                                                logout -> logout
                                                                .logoutRequestMatcher(
                                                                                new AntPathRequestMatcher("/logout"))
                                                                .permitAll())
                                .rememberMe().key("Axncmvi2002")
                                .tokenValiditySeconds(60 * 60 * 24 * 10 * 30);
                http.addFilterBefore(new BeforeAuthenticationFilter(accountsService,usersService),
                                UsernamePasswordAuthenticationFilter.class);
>>>>>>> c8ef1a87016b02e4dcfc4b5f30f1d7894b08a518
                return http.build();
        }
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring().antMatchers("/assets/**", "/image/**");
        }

}
