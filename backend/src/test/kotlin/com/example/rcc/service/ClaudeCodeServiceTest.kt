package com.example.rcc.service

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

/**
 * Tests for ClaudeCodeService input sanitization.
 *
 * These tests directly verify the sanitizeInput function to ensure
 * command injection attacks are prevented.
 */
public class ClaudeCodeServiceTest {

    private val service = ClaudeCodeService()

    /**
     * Test: Verify that messages exceeding MAX_MESSAGE_LENGTH are rejected.
     */
    @Test
    public fun testMessageLengthValidation() {
        val longMessage = "a".repeat(50_001)

        val exception = assertThrows<IllegalArgumentException> {
            service.sanitizeInput(longMessage)
        }

        exception.message shouldContain "exceeds maximum length"
    }

    /**
     * Test: Verify that semicolon command separator is removed.
     */
    @Test
    public fun testSemicolonRemoval() {
        val maliciousInput = "hello; rm -rf /"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain ";"
        sanitized shouldBe "hello rm -rf /"
    }

    /**
     * Test: Verify that pipe operator is removed.
     */
    @Test
    public fun testPipeRemoval() {
        val maliciousInput = "hello | cat /etc/passwd"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain "|"
        sanitized shouldBe "hello  cat /etc/passwd"
    }

    /**
     * Test: Verify that ampersand is removed.
     */
    @Test
    public fun testAmpersandRemoval() {
        val maliciousInput = "hello & echo pwned"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain "&"
        sanitized shouldBe "hello  echo pwned"
    }

    /**
     * Test: Verify that backticks are removed.
     */
    @Test
    public fun testBacktickRemoval() {
        val maliciousInput = "hello `whoami` test"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain "`"
        sanitized shouldBe "hello whoami test"
    }

    /**
     * Test: Verify that dollar signs are escaped.
     */
    @Test
    public fun testDollarSignEscaping() {
        val maliciousInput = "hello \$USER test"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldContain "\\$"
        sanitized shouldBe "hello \\\$USER test"
    }

    /**
     * Test: Verify that newlines are replaced with spaces.
     */
    @Test
    public fun testNewlineReplacement() {
        val maliciousInput = "hello\nrm -rf /"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain "\n"
        sanitized shouldBe "hello rm -rf /"
    }

    /**
     * Test: Verify that carriage returns are removed.
     */
    @Test
    public fun testCarriageReturnRemoval() {
        val maliciousInput = "hello\rworld"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain "\r"
        sanitized shouldBe "helloworld"
    }

    /**
     * Test: Verify that multiple dangerous characters are handled.
     */
    @Test
    public fun testMultipleDangerousCharacters() {
        val maliciousInput = "test; echo \$HOME | cat & `ls`\nrm\r-rf"
        val sanitized = service.sanitizeInput(maliciousInput)

        sanitized shouldNotContain ";"
        sanitized shouldNotContain "|"
        sanitized shouldNotContain "&"
        sanitized shouldNotContain "`"
        sanitized shouldNotContain "\n"
        sanitized shouldNotContain "\r"
        sanitized shouldContain "\\$"
    }

    /**
     * Test: Verify that legitimate messages pass through safely.
     */
    @Test
    public fun testLegitimateMessage() {
        val legitInput = "Hello! How are you? I'm fine."
        val sanitized = service.sanitizeInput(legitInput)

        sanitized shouldBe legitInput
    }

    /**
     * Test: Verify that empty message is handled.
     */
    @Test
    public fun testEmptyMessage() {
        val emptyInput = ""
        val sanitized = service.sanitizeInput(emptyInput)

        sanitized shouldBe ""
    }

    /**
     * Test: Verify that whitespace is preserved.
     */
    @Test
    public fun testWhitespacePreservation() {
        val whitespaceInput = "hello   world"
        val sanitized = service.sanitizeInput(whitespaceInput)

        sanitized shouldBe "hello   world"
    }
}
