package ru.madcake.filemanager

import java.io.File

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getDefaultDir(): File

internal expect fun workspaceDir(): File

internal expect fun downloadDir(): File