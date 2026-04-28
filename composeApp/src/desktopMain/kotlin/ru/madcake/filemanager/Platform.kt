package ru.madcake.filemanager

import java.io.File

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun getDefaultDir(): File = File(System.getProperty("user.home") + "/Downloads")

actual fun workspaceDir(): File {
    return File("${System.getProperty("user.home")}/Library/Application Support/max.rasputin.file_manager")
}

actual fun downloadDir(): File {
    return File("${System.getProperty("user.home")}/Library/Application Support/max.rasputin.file_manager/Downloads")
}