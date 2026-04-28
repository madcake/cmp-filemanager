package ru.madcake.filemanager.features.main.presents

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.MouseWheelZoom
import net.engawapg.lib.zoomable.ZoomState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import ru.madcake.filemanager.AppKeyEvent
import ru.madcake.filemanager.core.domain.ImageUtils
import ru.madcake.filemanager.designsystem.components.items.DataObjectInfo
import ru.madcake.filemanager.designsystem.components.items.FileObjectInfo
import ru.madcake.filemanager.designsystem.components.items.StoreBranchInfo
import ru.madcake.filemanager.designsystem.components.items.StoreObjectInfo
import ru.madcake.filemanager.features.dataobjectspanel.presents.StorePanel
import ru.madcake.filemanager.features.download.presents.YouTubeDownloadDialog
import ru.madcake.filemanager.features.filesystempanel.presents.FileSystemPanelScreen
import ru.madcake.filemanager.features.main.viewmodels.MainViewModel

@OptIn(ExperimentalSplitPaneApi::class, ExperimentalFoundationApi::class)
class MainScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = getScreenModel<MainViewModel>()

        var filePanelSelectedObject by remember { mutableStateOf<DataObjectInfo?>(null) }
        var storePanelSelectedObject by remember { mutableStateOf<DataObjectInfo?>(null) }

        var currentStoreBranch by remember { mutableStateOf<StoreBranchInfo?>(null) }

        var activePanelId by remember { mutableStateOf(FileSystemPanelScreen.panelId) }

        var isShowYouTubeDownloadDialog by remember { mutableStateOf(false) }

        val filePanel = remember {
            FileSystemPanelScreen(
                onPanelClick = { activePanelId = FileSystemPanelScreen.panelId },
                onCopy = { viewModel.onCopy(it, currentStoreBranch) },
                onSelect = { filePanelSelectedObject = it },
                onMove = viewModel::onMove
            )
        }
        val storePanel = remember {
            StorePanel(
                onPanelClick = { activePanelId = StorePanel.PANEL_ID },
                onCopy = { viewModel.onCopy(it) },
                onSelect = { storePanelSelectedObject = it },
                onMove = viewModel::onMove,
                onOpenDir = { currentStoreBranch = it as StoreBranchInfo }
            )
        }

        var imageFilter by remember { mutableStateOf<ImageFilter>(ImageFilter.None) }
        var chromaKeyTolerance by remember { mutableStateOf(0.1f) }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val panelWidth = 250.dp

            val firstSplitterPosition = (panelWidth / screenWidth).coerceIn(0f, 1f)
            val secondSplitterPosition = remember { 0.75f } // Remember the position to prevent jumping

            HorizontalSplitPane(
                splitPaneState = rememberSplitPaneState(firstSplitterPosition)
            ) {
                first(minSize = panelWidth) {
                    filePanel.isActive = activePanelId == FileSystemPanelScreen.panelId
                    filePanel.Content()
                }
                second(minSize = panelWidth * 2) {
                    HorizontalSplitPane(
                        splitPaneState = rememberSplitPaneState(secondSplitterPosition)
                    ) {
                        first(minSize = panelWidth) {
                            val selectedObject = when (activePanelId) {
                                StorePanel.PANEL_ID -> storePanelSelectedObject
                                else -> filePanelSelectedObject
                            }

                            val imageFile = when (selectedObject) {
                                is FileObjectInfo -> selectedObject.file
                                is StoreObjectInfo -> selectedObject.file
                                else -> null
                            }

                            if (imageFile != null && ImageUtils.isImageFile(imageFile.path)) {
                                val zoomState = rememberZoomState()
                                
                                // Track real pan position for rectangle movement
                                var panOffsetX by remember { mutableFloatStateOf(0f) }
                                var panOffsetY by remember { mutableFloatStateOf(0f) }
                                
                                // Reset pan when zoom changes
                                LaunchedEffect(zoomState.scale) {
                                    if (zoomState.scale <= 1f) {
                                        panOffsetX = 0f
                                        panOffsetY = 0f
                                    }
                                }
                                
                                Column(modifier = Modifier.fillMaxSize()) {
                                    ImageContentBar(
                                        filter = imageFilter,
                                        onFilter = { imageFilter = it },
                                        chromaKeyTolerance = chromaKeyTolerance,
                                        onChromaKeyToleranceChange = { chromaKeyTolerance = it }
                                    )
                                    
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = imageFile,
                                            contentDescription = "view image",
                                            colorFilter = imageFilter.getColorFilter(),
                                            modifier = Modifier.fillMaxSize().clipToBounds()
                                                .let { modifier ->
                                                    imageFilter.getRenderEffect()?.let { effect ->
                                                        modifier.graphicsLayer(renderEffect = effect)
                                                    } ?: modifier
                                                }
                                                .zoomable(
                                                    zoomState = zoomState,
                                                    mouseWheelZoom = MouseWheelZoom.Enabled,
                                                ),
                                        )
                                        
                                        // Invisible overlay to track pan gestures
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .pointerInput(Unit) {
                                                    detectDragGestures(
                                                        onDragStart = { /* Initial drag position */ },
                                                        onDrag = { change ->
                                                            if (zoomState.scale > 1.1f) {
                                                                // Accumulate pan offset
                                                                panOffsetX += change.x
                                                                panOffsetY += change.y
                                                                
                                                                // Constrain to reasonable bounds
                                                                val maxOffset = 200f * zoomState.scale
                                                                panOffsetX = panOffsetX.coerceIn(-maxOffset, maxOffset)
                                                                panOffsetY = panOffsetY.coerceIn(-maxOffset, maxOffset)
                                                            }
                                                        }
                                                    )
                                                }
                                        )
                                        
                                        // Navigation Controls
                                        ImageNavigationControls(
                                            zoomState = zoomState,
                                            panOffsetX = panOffsetX,
                                            panOffsetY = panOffsetY,
                                            onPanUpdate = { x, y ->
                                                panOffsetX = x
                                                panOffsetY = y
                                            },
                                            imageFile = imageFile,
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                                        )
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row {
                                            Text("Work area")
                                            IconButton(
                                                onClick = { isShowYouTubeDownloadDialog = true }
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = "Download")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        second(minSize = panelWidth) {
                            storePanel.isActive = activePanelId == StorePanel.PANEL_ID
                            storePanel.Content()
                        }
                    }
                }
            }
        }

        AppKeyEvent.onTabListener {
            activePanelId = when (activePanelId) {
                StorePanel.PANEL_ID -> FileSystemPanelScreen.panelId
                else -> StorePanel.PANEL_ID
            }
        }

        if (isShowYouTubeDownloadDialog) {
            Dialog(
                onDismissRequest = { isShowYouTubeDownloadDialog = false },
            ) {
                Card(
                    colors = CardDefaults.cardColors()
                ) {
                    YouTubeDownloadDialog().Content()
                }
            }
        }
    }
}

@Composable
fun ImageNavigationControls(
    zoomState: ZoomState,
    panOffsetX: Float,
    panOffsetY: Float,
    onPanUpdate: (Float, Float) -> Unit,
    imageFile: java.io.File,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Preview (only show when zoomed in)
            if (zoomState.scale > 1.1f) {
                ImagePreview(
                    imageFile = imageFile,
                    zoomState = zoomState,
                    panOffsetX = panOffsetX,
                    panOffsetY = panOffsetY,
                    modifier = Modifier
                        .width(90.dp)
                        .height(60.dp)
                        .padding(bottom = 8.dp)
                )
            }
            
            // Zoom level indicator
            Text(
                text = "${(zoomState.scale * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Zoom controls
            Row {
                IconButton(
                    onClick = { 
                        coroutineScope.launch {
                            zoomState.changeScale(zoomState.scale * 1.2f, Offset.Zero)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = { 
                        coroutineScope.launch {
                            zoomState.changeScale(zoomState.scale / 1.2f, Offset.Zero)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Row {
                IconButton(
                    onClick = { 
                        coroutineScope.launch {
                            zoomState.reset()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ZoomOutMap,
                        contentDescription = "Reset Zoom",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = { 
                        coroutineScope.launch {
                            zoomState.changeScale(1f, Offset.Zero)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.CenterFocusStrong,
                        contentDescription = "Actual Size",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ImagePreview(
    imageFile: java.io.File,
    zoomState: ZoomState,
    panOffsetX: Float,
    panOffsetY: Float,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Background thumbnail image
        AsyncImage(
            model = imageFile,
            contentDescription = "Image preview",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        
        // Visible area indicator overlay
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { 
                    coroutineScope.launch {
                        zoomState.reset()
                    }
                }
        ) {
            drawVisibleAreaIndicator(
                canvasSize = size,
                scale = zoomState.scale,
                panOffsetX = panOffsetX,
                panOffsetY = panOffsetY
            )
        }
    }
}

fun DrawScope.drawVisibleAreaIndicator(
    canvasSize: Size,
    scale: Float,
    panOffsetX: Float,
    panOffsetY: Float
) {
    if (scale <= 1f) return
    
    // Calculate visible area dimensions as fraction of full image (make smaller)
    val visibleWidth = 1f / scale * 0.8f  // Make rectangle smaller
    val visibleHeight = 1f / scale * 0.8f
    
    // Calculate pan offset as normalized coordinates
    val maxPanX = canvasSize.width * (scale - 1) / 2
    val maxPanY = canvasSize.height * (scale - 1) / 2
    
    // Normalize pan offset to 0-1 range (inverted because pan is opposite to view)
    val normalizedPanX = if (maxPanX > 0) -panOffsetX / (maxPanX * 2) else 0f
    val normalizedPanY = if (maxPanY > 0) -panOffsetY / (maxPanY * 2) else 0f
    
    // Calculate visible area center position
    val centerX = 0.5f + normalizedPanX
    val centerY = 0.5f + normalizedPanY
    
    // Calculate visible area bounds
    val visibleLeft = (centerX - visibleWidth / 2f).coerceIn(0f, 1f - visibleWidth)
    val visibleTop = (centerY - visibleHeight / 2f).coerceIn(0f, 1f - visibleHeight)
    val visibleRight = (visibleLeft + visibleWidth).coerceIn(visibleWidth, 1f)
    val visibleBottom = (visibleTop + visibleHeight).coerceIn(visibleHeight, 1f)
    
    // Convert to canvas coordinates
    val rectLeft = visibleLeft * canvasSize.width
    val rectTop = visibleTop * canvasSize.height
    val rectRight = visibleRight * canvasSize.width
    val rectBottom = visibleBottom * canvasSize.height
    
    // Draw lighter semi-transparent overlay for non-visible areas
    val overlayColor = Color.Black.copy(alpha = 0.3f)
    
    // Top overlay
    if (rectTop > 0) {
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, 0f),
            size = Size(canvasSize.width, rectTop)
        )
    }
    
    // Bottom overlay
    if (rectBottom < canvasSize.height) {
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, rectBottom),
            size = Size(canvasSize.width, canvasSize.height - rectBottom)
        )
    }
    
    // Left overlay
    if (rectLeft > 0) {
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, rectTop),
            size = Size(rectLeft, rectBottom - rectTop)
        )
    }
    
    // Right overlay
    if (rectRight < canvasSize.width) {
        drawRect(
            color = overlayColor,
            topLeft = Offset(rectRight, rectTop),
            size = Size(canvasSize.width - rectRight, rectBottom - rectTop)
        )
    }
    
    // Draw visible area border (thinner and brighter)
    drawRect(
        color = Color.Red.copy(alpha = 0.8f),
        topLeft = Offset(rectLeft, rectTop),
        size = Size(rectRight - rectLeft, rectBottom - rectTop),
        style = Stroke(width = 1.5.dp.toPx())
    )
}