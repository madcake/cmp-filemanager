# Claude Code Development Notes

## Project: Kotlin Multiplatform File Manager

### User Preferences and Choices

#### Image Filtering System
- **Value Filter Design Choice**: User prefers grayscale contrast analysis over HSV/HSL color range filtering
- **Purpose**: Value filter should convert images to grayscale and show contrast spots to help understand where light and dark areas are located
- **Target Result**: Should produce extreme contrast posterization similar to high-contrast grayscale images with distinct light/dark zones
- **Implementation**: Use luminance-based contrast detection with professional ITU-R BT.709 coefficients + aggressive contrast multiplication and brightness clamping
- **Rejection**: User explicitly rejected the initial HSV/HSL color range filtering approach

#### Code Quality Standards
- **Magic Numbers**: Always move magic numbers to constants, enums, or sealed classes
- **File Extensions**: Prefer content-based detection over file extension checking
- **Type Safety**: Use sealed classes for type-safe definitions (e.g., ImageFormat)

#### Development Patterns
- **Testing**: Always include comprehensive unit tests for core functionality
- **Architecture**: Follow existing patterns in the codebase
- **UI**: Use Material 3 components with real-time parameter adjustment

## Build & Run Commands

```bash
# Build the project
./gradlew composeApp:build

# Run the desktop application
./gradlew composeApp:run

# Run tests
./gradlew test
```

## Image Navigation System

### Custom Zoom Implementation
- **Problem**: The zoomable library had issues with filters and limited pan tracking
- **Solution**: Implemented custom zoom using `detectTransformGestures` with proper pan tracking
- **Benefits**: 
  - Full compatibility with image filters
  - Real-time pan position tracking for preview rectangle
  - Smooth zoom and pan gestures
  - Proper coordinate mapping for minimap

### Preview Rectangle Tracking
- **Feature**: Minimap-style preview shows exact visible area when zoomed
- **Implementation**: Canvas-based drawing with real pan coordinates
- **Calculation**: Converts screen pan offsets to normalized coordinates (0-1) for accurate rectangle positioning
- **Visual**: Semi-transparent overlays for non-visible areas with red border for current view

## Key Implementation Details

### Image Detection
- Uses content-based MIME type detection with magic number fallback
- Supports JPEG, PNG, GIF, BMP, WebP, TIFF, SVG formats
- Located in: `core/domain/ImageUtils.kt`

### Value Filter (Contrast Analysis)
- Four analysis modes: Luminance Contrast, Brightness Zones, Edge Detection, High Contrast Spots
- Professional controls: Contrast Threshold, Brightness Threshold, Contrast Boost
- Preset filters for common analysis tasks
- Located in: `core/domain/ValueFilterProcessor.kt`

### Chroma Key Filter
- Professional-grade chroma key with spill suppression and feathering
- Multiple color space support (RGB, HSL, YUV)
- Advanced controls for tolerance, edge smoothing, and feather radius
- Located in: `core/domain/ChromaKeyProcessor.kt`


## Architecture Notes

- **Database**: Room with SQLite for data persistence
- **DI**: Koin dependency injection
- **UI**: Compose Multiplatform with Material 3
- **Image Processing**: ColorMatrix-based filters for real-time performance
- **Testing**: Comprehensive unit tests with 100% pass rate

## Git Information
- Repository initialized with comprehensive .gitignore
- Commit format includes Claude Code attribution
- User credentials: MadCake <wdiabloster@gmail.com>