package com.wearefrancis.authenticator.service

import com.wearefrancis.authenticator.exception.InvalidJWTException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate
import java.util.*

const val CLAIM_KEY_CREATED = "created"
const val CLAIM_KEY_USERNAME = "sub"

@Service
class JWTService(
        private val clock: Clock
) {
    companion object {
        val logger = LoggerFactory.getLogger(JWTService::class.java)!!
    }

    fun generateJWT(user: UserDetails, secret: String, expiration: Long): String = Jwts
            .builder()
            .setClaims(
                    mapOf(
                            CLAIM_KEY_USERNAME to user.username,
                            CLAIM_KEY_CREATED to LocalDate.from(clock.instant().atZone(clock.zone))
                    )
            )
            .setExpiration(Date.from(clock.instant().plusSeconds(expiration)))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()

    fun getExpirationDateFromJwt(jwt: String, secret: String): LocalDate {
        val claims = getClaimsFromJwt(jwt, secret) ?: throw InvalidJWTException(jwt)
        return LocalDate.from(claims.expiration.toInstant().atZone(clock.zone))
    }

    fun getUsernameFromJWT(jwt: String, secret: String): String {
        val claims = getClaimsFromJwt(jwt, secret) ?: throw InvalidJWTException(jwt)
        return claims.subject
    }

    fun isValidJWT(user: UserDetails, jwt: String, secret: String): Boolean {
        val claims = getClaimsFromJwt(jwt, secret) ?: return false
        return user.isAccountNonExpired
                && user.isAccountNonLocked
                && user.isCredentialsNonExpired
                && user.isEnabled
                && user.username == claims.subject
    }

    private fun getClaimsFromJwt(jwt: String, secret: String): Claims? {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .body
        } catch (exception: Exception) {
            logger.debug("$jwt is invalid", exception)
            return null
        }
    }
}