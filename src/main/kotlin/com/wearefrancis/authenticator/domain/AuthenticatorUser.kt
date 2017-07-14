package com.wearefrancis.authenticator.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "authenticator_user")
data class AuthenticatorUser(
        @GeneratedValue
        @Id
        val id: Long,

        @Column
        @get:JvmName("getPassword_")
        val password: String,

        @Enumerated(EnumType.STRING)
        val role: Role,

        @Column
        @get:JvmName("getUsername_")
        val username: String
): UserDetails {
    enum class Role: GrantedAuthority {
        USER,
        ADMIN,
        SUPER_ADMIN;

        override fun getAuthority(): String = name
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(role)

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}