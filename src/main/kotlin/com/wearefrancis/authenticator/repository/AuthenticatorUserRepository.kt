package com.wearefrancis.authenticator.repository

import com.wearefrancis.authenticator.domain.AuthenticatorUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthenticatorUserRepository: JpaRepository<AuthenticatorUser, Long> {
    fun findOneByUsername(username: String): AuthenticatorUser?
}