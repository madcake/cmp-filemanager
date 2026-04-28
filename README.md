# File Manager - Kotlin Multiplatform

A desktop file manager application built with Kotlin Multiplatform and Compose Multiplatform, featuring dual-pane interface for file system navigation and object storage management.

## Features

### 🖼️ **Content-Based Image Detection**
- **Smart image recognition** - Detects images based on file content, not extensions
- **MIME type detection** with magic number fallback
- **Comprehensive format support**: JPEG, PNG, GIF, BMP, WebP, TIFF, ICO
- **Works with any file** regardless of extension or naming

### 📁 **Dual-Panel Interface**
- **File system panel** - Browse and navigate local file system
- **Object store panel** - Manage stored objects with metadata
- **Tab switching** - Quick panel switching with Tab key
- **Split pane layout** - Adjustable sizing between panels

### 🎨 **Advanced Image Viewing**
- **Zoomable image viewer** with mouse wheel support
- **Image filters** including chroma key effects
- **Filter controls** with real-time preview
- **Full-screen capable** image display

### 💾 **Data Management**
- **Room database** with SQLite backend
- **Object metadata** storage with notes
- **Tree structure** for organizing stored objects
- **File hashing** for integrity verification

### 📥 **YouTube Integration**
- **YouTube video download** functionality via yt-dlp
- **Download progress** tracking
- **Video info extraction** before download

## Technology Stack

- **Kotlin Multiplatform** - Cross-platform development
- **Compose Multiplatform** - Modern UI framework
- **Room Database** - Local data persistence
- **Koin** - Dependency injection
- **Voyager** - Navigation library
- **Coil** - Image loading
- **Material 3** - Design system

## Getting Started

### Prerequisites
- JDK 17 or higher
- Gradle 8.x

### Building and Running

```bash
# Build the project
./gradlew build

# Run the application
./gradlew composeApp:run

# Run tests
./gradlew desktopTest

# Build distribution
./gradlew createDistributable
```

### Project Structure

```
kmp/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/
│   │   │   └── ru/madcake/filemanager/
│   │   │       ├── core/           # Business logic & domain
│   │   │       ├── data/           # Data layer & repositories
│   │   │       ├── designsystem/   # UI components & theming
│   │   │       ├── di/             # Dependency injection
│   │   │       └── features/       # Feature-specific UI & ViewModels
│   │   ├── desktopMain/kotlin/     # Desktop-specific code
│   │   └── commonTest/kotlin/      # Unit tests
│   └── schemas/                    # Room database schemas
├── build.gradle.kts
└── README.md
```

## Architecture

### Clean Architecture
- **Domain layer** - Business logic and entities
- **Data layer** - Repositories and data sources
- **Presentation layer** - UI components and ViewModels

### Key Components

#### Image Detection (`ImageUtils.kt`)
```kotlin
// Content-based image detection
val isImage = ImageUtils.isImageFile(file)

// Supports all major formats via sealed class
sealed class ImageFormat(val name: String, val magicBytes: ByteArray)
```

#### Dependency Injection
- Modular DI setup with Koin
- Feature-specific modules
- Repository and use case injection

#### Navigation
- Voyager-based screen navigation
- Screen transitions and state management
- Koin integration for ViewModels

## Development

### Running Tests
```bash
# Run all tests
./gradlew desktopTest

# Run specific test class
./gradlew desktopTest --tests "ImageUtilsTest"
```

### Code Style
- Follow Kotlin coding conventions
- Use existing patterns and libraries
- Maintain clean architecture principles

### Adding New Image Formats
1. Add new format to `ImageFormat` sealed class
2. Define magic bytes pattern
3. Add test cases in `ImageUtilsTest`
4. Update documentation

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built with [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- Image loading by [Coil](https://coil-kt.github.io/coil/)
- Database by [Room](https://developer.android.com/training/data-storage/room)
- Dependency injection by [Koin](https://insert-koin.io/)