package com.smart.dashmate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform