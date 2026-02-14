package com.example.rcc.domain.error

/** Base class for all domain errors. */
public sealed class DomainError(
    open val message: String,
) : Exception(message)

/** Errors related to chat operations. */
public sealed class ChatError(
    override val message: String,
) : DomainError(message) {
    /** Chat not found error. */
    public data class NotFound(
        override val message: String = "Chat not found",
    ) : ChatError(message)

    /** Invalid input error. */
    public data class InvalidInput(
        override val message: String,
    ) : ChatError(message)

    /** Operation failed error. */
    public data class OperationFailed(
        override val message: String,
    ) : ChatError(message)
}

/** Errors related to authentication. */
public sealed class AuthError(
    override val message: String,
) : DomainError(message) {
    /** Unauthorized access error. */
    public data class Unauthorized(
        override val message: String = "Unauthorized",
    ) : AuthError(message)

    /** Token expired error. */
    public data class TokenExpired(
        override val message: String = "Token expired",
    ) : AuthError(message)
}

/** Errors related to network operations. */
public sealed class NetworkError(
    override val message: String,
) : DomainError(message) {
    /** Request timeout error. */
    public data class Timeout(
        override val message: String = "Request timeout",
    ) : NetworkError(message)

    /** Connection failed error. */
    public data class ConnectionFailed(
        override val message: String,
    ) : NetworkError(message)
}
