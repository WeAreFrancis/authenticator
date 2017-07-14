package com.wearefrancis.authenticator.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidJWTException(
        jwt: String
): Exception("Invalid JWT: $jwt")