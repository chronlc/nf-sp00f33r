package com.mag_sp00f.app.data

data class ApduLogEntry(
    val timestamp: String,
    val command: String,
    val response: String,
    val statusWord: String,
    val description: String,
    val executionTimeMs: Long
)
