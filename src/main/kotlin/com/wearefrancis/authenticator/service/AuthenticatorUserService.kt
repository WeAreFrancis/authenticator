package com.wearefrancis.authenticator.service

import com.wearefrancis.authenticator.repository.AuthenticatorUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticatorUserService(
        @Autowired
        private val authenticatorUserRepository: AuthenticatorUserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails
            = authenticatorUserRepository.findOneByUsername(username)
                    ?: throw UsernameNotFoundException("$username not found")
}