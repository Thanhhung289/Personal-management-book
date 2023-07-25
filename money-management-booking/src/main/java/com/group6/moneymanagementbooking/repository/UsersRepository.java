package com.group6.moneymanagementbooking.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group6.moneymanagementbooking.enity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

        public Optional<Users> findByEmail(String email);

        public Page<Users> findByEmailContaining(String email, Pageable pageable);

        public List<Users> findByEmailContaining(String email);

        public Page<Users> findByRole(String role, Pageable pageable);

        public Page<Users> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName,
                        Pageable pageable);

        public List<Users> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

        public Page<Users> findByNonLockedAndRole(boolean nonLocked,String role ,Pageable pageable);


        public Page<Users> findByActiveAndRole(boolean active, String role ,Pageable pageable);


        public List<Users> findByPhoneContaining(String phone);

        public Page<Users> findByPhoneContaining(String phone, Pageable pageable);

        public Optional<Users> findByPhone(String phone);

        // account-non-lock
        @Query(value = "SELECT * FROM users u WHERE u.email LIKE %?1% AND u.account_non_locked = ?2 AND u.role = 'ROLE_USER'", nativeQuery = true)
        public List<Users> findByEmailContainingAndNonLocked(String email, boolean nonLocked);

        @Query(value = "SELECT * FROM users u WHERE u.email LIKE %?1% AND u.account_non_locked = ?2 AND u.role = 'ROLE_USER'", nativeQuery = true)
        public Page<Users> findByEmailContainingAndNonLocked(String email, boolean nonLocked, Pageable pageable);

        @Query(value = "SELECT * FROM users u WHERE u.phone LIKE %?1% AND u.account_non_locked = ?2 AND u.role = 'ROLE_USER'", nativeQuery = true)
        public Page<Users> findByPhoneContainingAndNonLocked(String phone, boolean nonLocked, Pageable pageable);

        @Query(value = "SELECT * FROM users u WHERE u.phone LIKE %?1% AND u.account_non_locked = ?2 AND u.role = 'ROLE_USER'", nativeQuery = true)
        public List<Users> findByPhoneContainingAndNonLocked(String phone, boolean nonLocked);

        @Query(value = "SELECT * FROM users u WHERE (u.first_name LIKE %?1% or last_name LIKE %?2%) AND u.account_non_locked = ?3 AND u.role = 'ROLE_USER'", nativeQuery = true)
        public Page<Users> findByFirstNameContainingOrLastNameContainingAndNonLocked(String firstName,
                        String lastName, boolean nonLocked, Pageable pageable);

        @Query(value = "SELECT * FROM users u WHERE (u.first_name LIKE %?1% or last_name LIKE %?2%) AND u.account_non_locked = ?3 AND u.role = 'ROLE_USER'", nativeQuery = true)
        public List<Users> findByFirstNameContainingOrLastNameContainingAndNonLocked(String firstName,
                        String lastName, boolean nonLocked);

        // is_active

        @Query(value = "SELECT * FROM users u WHERE u.email LIKE %?1% AND u.account_non_locked = ?2 AND u.role like 'ROLE_USER'", nativeQuery = true)
        public List<Users> findByEmailContainingAndActive(String email, boolean nonLocked);

        @Query(value = "SELECT * FROM users u WHERE u.email LIKE %?1% AND u.account_non_locked = ?2 AND u.role like 'ROLE_USER'", nativeQuery = true)
        public Page<Users> findByEmailContainingAndActive(String email, boolean nonLocked, Pageable pageable);

        @Query(value = "SELECT * FROM users u WHERE u.phone LIKE %?1% AND u.is_active = ?2 AND u.role like 'ROLE_USER'", nativeQuery = true)
        public Page<Users> findByPhoneContainingAndActive(String phone, boolean isActive, Pageable pageable);

        @Query(value = "SELECT * FROM users u WHERE u.phone LIKE %?1% AND u.is_active = ?2 AND u.role like 'ROLE_USER'", nativeQuery = true)
        public List<Users> findByPhoneContainingAndActive(String phone, boolean isActive);

        @Query(value = "SELECT * FROM users u WHERE (u.first_name LIKE %?1% or last_name LIKE %?2%) AND u.is_active = ?3 AND u.role like 'ROLE_USER'", nativeQuery = true)
        public Page<Users> findByFirstNameContainingOrLastNameContainingAndActive(String firstName,
                        String lastName, boolean nonLocked, Pageable pageable);

        @Query(value = "SELECT * FROM users u WHERE (u.first_name LIKE %?1% or last_name LIKE %?2%) AND u.is_active = ?3 AND u.role like 'ROLE_USER'", nativeQuery = true)
        public List<Users> findByFirstNameContainingOrLastNameContainingAndActive(String firstName,
                        String lastName, boolean nonLocked);
}
