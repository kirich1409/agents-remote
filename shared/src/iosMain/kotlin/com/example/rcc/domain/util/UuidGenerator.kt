package com.example.rcc.domain.util

import platform.Foundation.NSUUID

public actual object UuidGenerator {
    public actual fun randomUuid(): String = NSUUID().UUIDString()
}
