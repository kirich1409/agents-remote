package com.example.rcc.domain.util

import java.util.UUID

public actual fun randomUuid(): String = UUID.randomUUID().toString()
