package com.example.rcc.domain.repository

/**
 * Repository interface for session management.
 *
 * Handles authentication token storage and retrieval for Cloud Code sessions.
 */
public interface SessionRepository {
    /**
     * Saves the session authentication token.
     *
     * @param token Authentication token to save.
     * @return Result success or failure.
     */
    suspend fun saveSessionToken(token: String): Result<Unit>

    /**
     * Retrieves the saved session token.
     *
     * @return Result containing the token if exists, null if not found, or failure if operation failed.
     */
    suspend fun getSessionToken(): Result<String?>

    /**
     * Clears the saved session token.
     *
     * @return Result success or failure.
     */
    suspend fun clearSessionToken(): Result<Unit>

    /**
     * Checks if user is authenticated.
     *
     * @return Result containing true if authenticated, false otherwise, or failure if operation failed.
     */
    suspend fun isAuthenticated(): Result<Boolean>
}
