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
    public val authToken: String? = System.getenv("AUTH_TOKEN")

    /**
     * Gateway server port.
     */
    public val gatewayPort: Int = System.getenv("GATEWAY_PORT")?.toIntOrNull() ?: DEFAULT_GATEWAY_PORT

    /**
     * Logging level.
     */
    public val logLevel: String = System.getenv("LOG_LEVEL") ?: "INFO"

    /**
     * Path to the Claude Code CLI binary.
     */
    public val claudePath: String = System.getenv("CLAUDE_PATH") ?: "claude"

    /**
     * Claude model to use.
     */
    public val claudeModel: String = System.getenv("CLAUDE_MODEL") ?: "sonnet"
}
