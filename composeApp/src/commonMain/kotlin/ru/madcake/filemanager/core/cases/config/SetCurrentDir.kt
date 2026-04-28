package ru.madcake.filemanager.core.cases.config

import java.io.File

interface SetCurrentDir {
    suspend fun setCurrentDir(dir: File)
}