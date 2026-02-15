package com.example.rcc.service

import com.example.rcc.config.AppConfig
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Serializable
private data class ClaudeResponse(val result: String)

private val json = Json { ignoreUnknownKeys = true }

/**
 * Service for interacting with Claude Code CLI.
 *
 * Launches `claude` as a child process with `--session-id` / `--resume` to maintain
 * conversation context across messages within the same chat.
 */
@Single
public class ClaudeCodeService {

    private val claudePath: String = AppConfig.claudePath
    private val claudeModel: String = AppConfig.claudeModel
    private val knownSessions: MutableSet<String> = ConcurrentHashMap.newKeySet()

    /**
     * Sends a message to Claude Code CLI and returns the response.
     *
     * Uses `--session-id` for new sessions and `--resume` for existing ones.
     *
     * @param sessionId Session identifier (UUID) for conversation continuity.
     * @param content User message content.
     * @return Result containing Claude's response text, or failure on error.
     */
    public fun sendMessage(sessionId: String, content: String): Result<String> = runCatching {
        val isNewSession = sessionId !in knownSessions
        Napier.d("Sending message to Claude CLI, session=$sessionId, new=$isNewSession")

        val command = buildList {
            add(claudePath)
            add("-p")
            add("--output-format")
            add("json")
            if (isNewSession) {
                add("--session-id")
            } else {
                add("--resume")
            }
            add(sessionId)
            add("--model")
            add(claudeModel)
            add(content)
        }

        val process = ProcessBuilder(command).apply {
            environment().remove("CLAUDECODE")
            directory(java.io.File(System.getProperty("user.home")))
            redirectInput(ProcessBuilder.Redirect.from(java.io.File("/dev/null")))
            redirectErrorStream(true)
        }.start()

        // Read stdout before waitFor to avoid deadlock when output buffer fills up
        val stdout = process.inputStream.bufferedReader().readText()

        val completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!completed) {
            process.destroyForcibly()
            error("Claude CLI timed out after ${TIMEOUT_SECONDS}s")
        }

        val exitCode = process.exitValue()
        if (exitCode != 0) {
            Napier.e("Claude CLI failed (exit=$exitCode): $stdout")
            error("Claude CLI exited with code $exitCode: $stdout")
        }

        knownSessions.add(sessionId)
        val response = json.decodeFromString<ClaudeResponse>(stdout)
        response.result
    }

    private companion object {
        private const val TIMEOUT_SECONDS = 120L
    }
}
