package ru.madcake.filemanager.data.repository.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.madcake.filemanager.core.cases.config.GetCurrentDir
import ru.madcake.filemanager.core.cases.config.SetCurrentDir
import ru.madcake.filemanager.core.domain.ConfigValueType
import ru.madcake.filemanager.data.store.room.dao.DaoConfig
import ru.madcake.filemanager.data.store.room.entity.DbConfig
import ru.madcake.filemanager.getDefaultDir
import java.io.File

class ConfigRepository(
    private val daoConfig: DaoConfig,
) : GetCurrentDir,
    SetCurrentDir {

    override suspend fun getCurrentDir(): File {
        val entry = daoConfig.get(Keys.CurrentDir.name)
        return entry?.takeIf { it.value.isNotEmpty() }?.let { File(it.value) } ?: getDefaultDir()
    }

    override suspend fun setCurrentDir(dir: File) = withContext(Dispatchers.IO) {
        daoConfig.set(DbConfig(Keys.CurrentDir.name, dir.absolutePath, ConfigValueType.String))
    }

    private enum class Keys {
        CurrentDir,
    }
}