package ru.madcake.filemanager.core.cases.config

import java.io.File

interface GetCurrentDir {
    suspend fun getCurrentDir(): File
}