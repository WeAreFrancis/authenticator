package com.wearefrancis.authenticator.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.security.core.userdetails.UserDetails
import java.time.*

internal class JWTServiceTest {
    @Mock
    private lateinit var clock: Clock

    private lateinit var jwtService: JWTService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        jwtService = JWTService(clock)
    }

    @Test
    fun generateJWTShouldCreateJWT() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val expiration = 10L
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)

        // WHEN
        val jwt = jwtService.generateJWT(user, secret, expiration)

        // THEN
        assertThat(jwtService.getExpirationDateFromJwt(jwt, secret)).isEqualTo(LocalDate.from(now.atZone(zone)))
        assertThat(jwtService.getUsernameFromJWT(jwt, secret)).isEqualTo(username)
        verify(clock, times(2)).instant()
        verify(clock, times(2)).zone
        verify(user).username
    }

    @Test
    fun isValidJWTShouldReturnFalseIfTheJWTIsExpired() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)

        val jwt = jwtService.generateJWT(user, secret, -1L)

        // WHEN
        val isValidJWT = jwtService.isValidJWT(user, jwt, secret)

        // THEN
        assertThat(isValidJWT).isFalse()
        verify(clock, times(2)).instant()
        verify(clock).zone
        verify(user).username
    }

    @Test
    fun isValidJWTShouldReturnFalseIfUserAccountIsExpired() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)

        val jwt = jwtService.generateJWT(user, secret, 10L)

        // WHEN
        val isValidJWT = jwtService.isValidJWT(user, jwt, secret)

        // THEN
        assertThat(isValidJWT).isFalse()
        verify(clock, times(2)).instant()
        verify(clock).zone
        verify(user).username
        verify(user).isAccountNonExpired
    }

    @Test
    fun isValidJWTShouldReturnFalseIfUserIsLocked() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)
        whenever(user.isAccountNonExpired).thenReturn(true)

        val jwt = jwtService.generateJWT(user, secret, 10L)

        // WHEN
        val isValidJWT = jwtService.isValidJWT(user, jwt, secret)

        // THEN
        assertThat(isValidJWT).isFalse()
        verify(clock, times(2)).instant()
        verify(clock).zone
        verify(user).username
        verify(user).isAccountNonExpired
        verify(user).isAccountNonLocked
    }

    @Test
    fun isValidJWTShouldReturnFalseIfPasswordIsExpired() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)
        whenever(user.isAccountNonExpired).thenReturn(true)
        whenever(user.isAccountNonLocked).thenReturn(true)

        val jwt = jwtService.generateJWT(user, secret, 10L)

        // WHEN
        val isValidJWT = jwtService.isValidJWT(user, jwt, secret)

        // THEN
        assertThat(isValidJWT).isFalse()
        verify(clock, times(2)).instant()
        verify(clock).zone
        verify(user).username
        verify(user).isAccountNonExpired
        verify(user).isAccountNonLocked
        verify(user).isCredentialsNonExpired
    }

    @Test
    fun isValidJWTShouldReturnFalseIfUserIsNotEnabled() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)
        whenever(user.isAccountNonExpired).thenReturn(true)
        whenever(user.isAccountNonLocked).thenReturn(true)
        whenever(user.isCredentialsNonExpired).thenReturn(true)

        val jwt = jwtService.generateJWT(user, secret, 10L)

        // WHEN
        val isValidJWT = jwtService.isValidJWT(user, jwt, secret)

        // THEN
        assertThat(isValidJWT).isFalse()
        verify(clock, times(2)).instant()
        verify(clock).zone
        verify(user).username
        verify(user).isAccountNonExpired
        verify(user).isAccountNonLocked
        verify(user).isCredentialsNonExpired
        verify(user).isEnabled
    }

    @Test
    fun isValidJWTShouldReturnFalseIfUserUsernameIsNotEqualToUsernameInsideTheJWT() {
        // GIVEN
        val user = mock<UserDetails>()
        val secret = "secret"
        val username = "username"
        val now = Instant.now()
        val zone = ZoneOffset.UTC

        whenever(clock.instant()).thenReturn(now)
        whenever(clock.zone).thenReturn(zone)
        whenever(user.username).thenReturn(username)
        whenever(user.isAccountNonExpired).thenReturn(true)
        whenever(user.isAccountNonLocked).thenReturn(true)
        whenever(user.isCredentialsNonExpired).thenReturn(true)
        whenever(user.isEnabled).thenReturn(true)

        val jwt = jwtService.generateJWT(user, secret, 10L)

        whenever(user.username).thenReturn("${username}2")

        // WHEN
        val isValidJWT = jwtService.isValidJWT(user, jwt, secret)

        // THEN
        assertThat(isValidJWT).isFalse()
        verify(clock, times(2)).instant()
        verify(clock).zone
        verify(user, times(2)).username
        verify(user).isAccountNonExpired
        verify(user).isAccountNonLocked
        verify(user).isCredentialsNonExpired
        verify(user).isEnabled
    }
}