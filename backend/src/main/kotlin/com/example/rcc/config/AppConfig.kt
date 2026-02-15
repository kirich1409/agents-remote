package com.example.rcc.config

/**
 * Application configuration.
 *
 * Loads settings from environment variables.
 */
public object AppConfig {
    private const val DEFAULT_GATEWAY_PORT = 3000

    /**
     * Authentication token for Cloud Code sessions.
     */
    public val authToken: String =
        requireNotNull(System.getenv("AUTH_TOKEN")) {
            "Environment variable AUTH_TOKEN must be set"
        }

    /**
     * Gateway server port.
     */
    public val gatewayPort: Int = System.getenv("GATEWAY_PORT")?.toIntOrNull() ?: DEFAULT_GATEWAY_PORT

    /**
     * Logging level.
     */
    public val logLevel: String = System.getenv("LOG_LEVEL") ?: "INFO"
}
