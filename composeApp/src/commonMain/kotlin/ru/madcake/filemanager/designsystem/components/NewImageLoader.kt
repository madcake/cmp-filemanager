package ru.madcake.filemanager.designsystem.components

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.util.DebugLogger
import okio.FileSystem

fun newImageLoader(
    context: PlatformContext,
    debug: Boolean,
): ImageLoader {
    return ImageLoader.Builder(context)
//        .components {
//            add(SQliteFetcher.Factory())
//        }
        .memoryCache {
            MemoryCache.Builder()
                // Set the max size to 25% of the app's available memory.
                .maxSizePercent(context, percent = 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                .maxSizeBytes(512L * 1024 * 1024) // 512MB
                .build()
        }
        // Show a short crossfade when loading images asynchronously.
        .crossfade(true)
        // Enable logging if this is a debug build.
        .apply {
            if (debug) {
                logger(DebugLogger())
            }
        }
        .build()
}

//class SQliteFetcher(
//    private val data: Uri,
//    private val options: Options,
//    private val diskCache: Lazy<DiskCache?>,
//) : Fetcher, KoinComponent {
//
//    val dataDao: BlockDataDao by inject()
//
//    override suspend fun fetch(): FetchResult? {
//        val blockKey = data.authority ?: return null
//        val dataKey = data.pathSegments.lastOrNull() ?: return null
//        val result = dataDao.getData(dataKey, blockKey) ?: return null
//        return SourceFetchResult(
//            source = ImageSource(
//                source = Buffer().apply { write(result.value) },
//                fileSystem = fileSystem,
//            ),
//            mimeType = result.mimetype,
//            dataSource = DataSource.NETWORK,
//        )
//    }
//
//    private val fileSystem: FileSystem
//        get() = diskCache.value?.fileSystem ?: options.fileSystem
//
//    class Factory : Fetcher.Factory<Uri> {
//        override fun create(
//            data: Uri,
//            options: Options,
//            imageLoader: ImageLoader
//        ): Fetcher? {
//            if (data.scheme != "maxnotes-data") return null
//            return SQliteFetcher(data, options, diskCache = lazy { imageLoader.diskCache },)
//        }
//
//    }
//}