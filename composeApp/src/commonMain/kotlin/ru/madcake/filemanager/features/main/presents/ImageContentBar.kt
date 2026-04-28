package ru.madcake.filemanager.features.main.presents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.madcake.filemanager.core.domain.ChromaKeyProcessor
import ru.madcake.filemanager.core.domain.ValueAnalysisMode
import ru.madcake.filemanager.core.domain.ValueFilterProcessor
import ru.madcake.filemanager.core.domain.ValueFilterSettings
import ru.madcake.filemanager.designsystem.theme.padding16

@Composable
fun ImageContentBar(
    filter: ImageFilter = ImageFilter.None,
    onFilter: (ImageFilter) -> Unit,
    chromaKeyTolerance: Float,
    onChromaKeyToleranceChange: (Float) -> Unit
) {
    var chromaKeySettings by remember { 
        mutableStateOf(
            if (filter is ImageFilter.ChromaKey) filter else ImageFilter.ChromaKey()
        ) 
    }
    var valueFilterSettings by remember {
        mutableStateOf(
            if (filter is ImageFilter.ValueFilter) filter.settings else ValueFilterSettings()
        )
    }
    var colorSimplificationSettings by remember {
        mutableStateOf(
            if (filter is ImageFilter.ColorSimplification) filter else ImageFilter.ColorSimplification()
        )
    }
    var munsellValueSettings by remember {
        mutableStateOf(
            if (filter is ImageFilter.MunsellValue) filter else ImageFilter.MunsellValue()
        )
    }
    var munsellColorSettings by remember {
        mutableStateOf(
            if (filter is ImageFilter.MunsellColor) filter else ImageFilter.MunsellColor()
        )
    }
    var showColorPicker by remember { mutableStateOf(false) }
    var showValuePresets by remember { mutableStateOf(false) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth().padding(padding16),
        horizontalAlignment = Alignment.End
    ) {
        // Filter Selection Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            // Advanced Settings Toggle Button
            if (filter is ImageFilter.ChromaKey || filter is ImageFilter.ValueFilter || filter is ImageFilter.ColorSimplification || filter is ImageFilter.MunsellValue || filter is ImageFilter.MunsellColor) {
                IconButton(
                    onClick = { showAdvancedSettings = !showAdvancedSettings }
                ) {
                    androidx.compose.material3.Icon(
                        if (showAdvancedSettings) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showAdvancedSettings) "Hide Settings" else "Show Settings"
                    )
                }
                
                Spacer(modifier = Modifier.width(ImageContentBar.VERTICAL_SPACING))
            }
            
            SingleChoiceSegmentedButtonRow {
                ImageFilter.filters.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = filter::class == item::class,
                        label = { Icon(item.icon, contentDescription = "") },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ImageFilter.filters.size
                        ),
                        icon = {},
                        onClick = { 
                            when (item) {
                                is ImageFilter.ChromaKey -> onFilter(chromaKeySettings)
                                is ImageFilter.ValueFilter -> onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                                is ImageFilter.ColorSimplification -> onFilter(colorSimplificationSettings)
                                is ImageFilter.MunsellValue -> onFilter(munsellValueSettings)
                                is ImageFilter.MunsellColor -> onFilter(munsellColorSettings)
                                else -> onFilter(item)
                            }
                        }
                    )
                }
            }
        }
        
        // Advanced Chroma Key Controls
        if (filter is ImageFilter.ChromaKey && showAdvancedSettings) {
            Spacer(modifier = Modifier.height(ImageContentBar.LARGE_SPACING))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ImageContentBar.CARD_CORNER_RADIUS)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Chroma Key Settings",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Target Color Selection
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Target Color:", modifier = Modifier.width(100.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(ImageContentBar.COLOR_PICKER_SIZE)
                                .clip(CircleShape)
                                .background(chromaKeySettings.targetColor)
                                .border(ImageContentBar.CIRCLE_BORDER_WIDTH, MaterialTheme.colorScheme.outline, CircleShape)
                                .clickable { showColorPicker = true }
                        )
                        
                        Spacer(modifier = Modifier.width(ImageContentBar.VERTICAL_SPACING))
                        
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = "Color Picker",
                            modifier = Modifier.clickable { showColorPicker = true }
                        )
                        
                        // Color Preset Dropdown
                        DropdownMenu(
                            expanded = showColorPicker,
                            onDismissRequest = { showColorPicker = false }
                        ) {
                            ChromaKeyProcessor.PresetColors.ALL_PRESETS.forEach { (name, color) ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(ImageContentBar.SMALL_ICON_SIZE)
                                                    .background(color, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(ImageContentBar.VERTICAL_SPACING))
                                            Text(name)
                                        }
                                    },
                                    onClick = {
                                        chromaKeySettings = chromaKeySettings.copy(targetColor = color)
                                        onFilter(chromaKeySettings)
                                        showColorPicker = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Tolerance Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tolerance:")
                            Text("${(chromaKeySettings.tolerance * 100).toInt()}%")
                        }
                        Slider(
                            value = chromaKeySettings.tolerance,
                            onValueChange = { newTolerance ->
                                chromaKeySettings = chromaKeySettings.copy(tolerance = newTolerance)
                                onFilter(chromaKeySettings)
                                onChromaKeyToleranceChange(newTolerance)
                            },
                            valueRange = 0f..1f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Spill Suppression Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Spill Suppression:")
                            Text("${(chromaKeySettings.spillSuppression * 100).toInt()}%")
                        }
                        Slider(
                            value = chromaKeySettings.spillSuppression,
                            onValueChange = { newSpill ->
                                chromaKeySettings = chromaKeySettings.copy(spillSuppression = newSpill)
                                onFilter(chromaKeySettings)
                            },
                            valueRange = 0f..1f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Edge Softness Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Edge Softness:")
                            Text("${(chromaKeySettings.softness * 100).toInt()}%")
                        }
                        Slider(
                            value = chromaKeySettings.softness,
                            onValueChange = { newSoftness ->
                                chromaKeySettings = chromaKeySettings.copy(softness = newSoftness)
                                onFilter(chromaKeySettings)
                            },
                            valueRange = 0f..1f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Feather Radius Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Feather Radius:")
                            Text("${chromaKeySettings.featherRadius.toInt()}px")
                        }
                        Slider(
                            value = chromaKeySettings.featherRadius,
                            onValueChange = { newFeather ->
                                chromaKeySettings = chromaKeySettings.copy(featherRadius = newFeather)
                                onFilter(chromaKeySettings)
                            },
                            valueRange = 0f..10f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Edge Smoothing Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edge Smoothing:")
                        Switch(
                            checked = chromaKeySettings.edgeSmoothing,
                            onCheckedChange = { enabled ->
                                chromaKeySettings = chromaKeySettings.copy(edgeSmoothing = enabled)
                                onFilter(chromaKeySettings)
                            }
                        )
                    }
                }
            }
        }
        
        // Advanced Value Filter Controls
        if (filter is ImageFilter.ValueFilter && showAdvancedSettings) {
            Spacer(modifier = Modifier.height(ImageContentBar.LARGE_SPACING))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ImageContentBar.CARD_CORNER_RADIUS)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Value Filter Settings",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Analysis Mode Selection
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Analysis Mode:", modifier = Modifier.width(ImageContentBar.SETTINGS_BUTTON_WIDTH))
                        
                        SingleChoiceSegmentedButtonRow {
                            ValueAnalysisMode.entries.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = valueFilterSettings.analysisMode == mode,
                                    label = { 
                            Text(mode.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) 
                        },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = ValueAnalysisMode.entries.size
                                    ),
                                    icon = {},
                                    onClick = {
                                        valueFilterSettings = valueFilterSettings.copy(analysisMode = mode)
                                        onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Preset Filters
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Presets:", modifier = Modifier.width(ImageContentBar.PRESET_BUTTON_WIDTH))
                        
                        Text(
                            text = "Select Preset",
                            modifier = Modifier
                                .clickable { showValuePresets = true }
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(ImageContentBar.CARD_CORNER_RADIUS)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        DropdownMenu(
                            expanded = showValuePresets,
                            onDismissRequest = { showValuePresets = false }
                        ) {
                            ValueFilterProcessor.Presets.ALL_PRESETS.forEach { (name, preset) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        valueFilterSettings = preset
                                        onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                                        showValuePresets = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Contrast Threshold Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Contrast Threshold:")
                            Text("${(valueFilterSettings.contrastThreshold * 100).toInt()}%")
                        }
                        Slider(
                            value = valueFilterSettings.contrastThreshold,
                            onValueChange = { newThreshold ->
                                valueFilterSettings = valueFilterSettings.copy(contrastThreshold = newThreshold)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            },
                            valueRange = 0f..1f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Brightness Threshold Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Brightness Threshold:")
                            Text("${(valueFilterSettings.brightnessThreshold * 100).toInt()}%")
                        }
                        Slider(
                            value = valueFilterSettings.brightnessThreshold,
                            onValueChange = { newThreshold ->
                                valueFilterSettings = valueFilterSettings.copy(brightnessThreshold = newThreshold)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            },
                            valueRange = 0f..1f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Contrast Boost Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Contrast Boost:")
                            Text("${valueFilterSettings.contrastBoost.toInt()}x")
                        }
                        Slider(
                            value = valueFilterSettings.contrastBoost,
                            onValueChange = { newBoost ->
                                valueFilterSettings = valueFilterSettings.copy(contrastBoost = newBoost)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            },
                            valueRange = 0.5f..5f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Advanced Options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Contrast Spots:")
                        Switch(
                            checked = valueFilterSettings.showContrastSpots,
                            onCheckedChange = { show ->
                                valueFilterSettings = valueFilterSettings.copy(showContrastSpots = show)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Highlight Bright Areas:")
                        Switch(
                            checked = valueFilterSettings.highlightBrightAreas,
                            onCheckedChange = { highlight ->
                                valueFilterSettings = valueFilterSettings.copy(highlightBrightAreas = highlight)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Highlight Dark Areas:")
                        Switch(
                            checked = valueFilterSettings.highlightDarkAreas,
                            onCheckedChange = { highlight ->
                                valueFilterSettings = valueFilterSettings.copy(highlightDarkAreas = highlight)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edge Detection:")
                        Switch(
                            checked = valueFilterSettings.edgeDetection,
                            onCheckedChange = { edge ->
                                valueFilterSettings = valueFilterSettings.copy(edgeDetection = edge)
                                onFilter(ImageFilter.ValueFilter(valueFilterSettings))
                            }
                        )
                    }
                }
            }
        }
        
        // Advanced Color Simplification Controls
        if (filter is ImageFilter.ColorSimplification && showAdvancedSettings) {
            Spacer(modifier = Modifier.height(ImageContentBar.LARGE_SPACING))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ImageContentBar.CARD_CORNER_RADIUS)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Color Simplification Settings",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Color Levels Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Color Levels:")
                            Text("${colorSimplificationSettings.colorLevels}")
                        }
                        Slider(
                            value = colorSimplificationSettings.colorLevels.toFloat(),
                            onValueChange = { newLevels ->
                                colorSimplificationSettings = colorSimplificationSettings.copy(colorLevels = newLevels.toInt())
                                onFilter(colorSimplificationSettings)
                            },
                            valueRange = 2f..32f,
                            steps = 29
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Quantization Strength Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Quantization Strength:")
                            Text("${(colorSimplificationSettings.quantizationStrength * 100).toInt()}%")
                        }
                        Slider(
                            value = colorSimplificationSettings.quantizationStrength,
                            onValueChange = { newStrength ->
                                colorSimplificationSettings = colorSimplificationSettings.copy(quantizationStrength = newStrength)
                                onFilter(colorSimplificationSettings)
                            },
                            valueRange = 0.1f..1f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Preserve Hue Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Preserve Hue:")
                        Switch(
                            checked = colorSimplificationSettings.preserveHue,
                            onCheckedChange = { preserve ->
                                colorSimplificationSettings = colorSimplificationSettings.copy(preserveHue = preserve)
                                onFilter(colorSimplificationSettings)
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Preserve Chroma Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Preserve Chroma:")
                        Switch(
                            checked = colorSimplificationSettings.preserveChroma,
                            onCheckedChange = { preserve ->
                                colorSimplificationSettings = colorSimplificationSettings.copy(preserveChroma = preserve)
                                onFilter(colorSimplificationSettings)
                            }
                        )
                    }
                }
            }
        }
        
        // Advanced Munsell Value Controls
        if (filter is ImageFilter.MunsellValue && showAdvancedSettings) {
            Spacer(modifier = Modifier.height(ImageContentBar.LARGE_SPACING))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ImageContentBar.CARD_CORNER_RADIUS)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Munsell Value Settings",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Value Range Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Value Range:")
                            Text("${(munsellValueSettings.valueRange * 100).toInt()}%")
                        }
                        Slider(
                            value = munsellValueSettings.valueRange,
                            onValueChange = { newRange ->
                                munsellValueSettings = munsellValueSettings.copy(valueRange = newRange)
                                onFilter(munsellValueSettings)
                            },
                            valueRange = 0.1f..2.0f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Contrast Boost Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Contrast Boost:")
                            Text("${(munsellValueSettings.contrastBoost * 100).toInt()}%")
                        }
                        Slider(
                            value = munsellValueSettings.contrastBoost,
                            onValueChange = { newBoost ->
                                munsellValueSettings = munsellValueSettings.copy(contrastBoost = newBoost)
                                onFilter(munsellValueSettings)
                            },
                            valueRange = 0.5f..2.0f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Gamma Correction Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gamma Correction:")
                            Text("${String.format("%.1f", munsellValueSettings.gammaCorrection)}")
                        }
                        Slider(
                            value = munsellValueSettings.gammaCorrection,
                            onValueChange = { newGamma ->
                                munsellValueSettings = munsellValueSettings.copy(gammaCorrection = newGamma)
                                onFilter(munsellValueSettings)
                            },
                            valueRange = 1.0f..3.0f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Preserve Color Tones Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Preserve Color Tones:")
                        Switch(
                            checked = munsellValueSettings.preserveColorTones,
                            onCheckedChange = { preserve ->
                                munsellValueSettings = munsellValueSettings.copy(preserveColorTones = preserve)
                                onFilter(munsellValueSettings)
                            }
                        )
                    }
                }
            }
        }
        
        // Advanced Munsell Color Controls
        if (filter is ImageFilter.MunsellColor && showAdvancedSettings) {
            Spacer(modifier = Modifier.height(ImageContentBar.LARGE_SPACING))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ImageContentBar.CARD_CORNER_RADIUS)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Munsell Color Settings",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Hue Shift Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hue Shift:")
                            Text("${munsellColorSettings.hueShift.toInt()}")
                        }
                        Slider(
                            value = munsellColorSettings.hueShift,
                            onValueChange = { newHue ->
                                munsellColorSettings = munsellColorSettings.copy(hueShift = newHue)
                                onFilter(munsellColorSettings)
                            },
                            valueRange = -50f..50f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Value Adjustment Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Value Adjustment:")
                            Text("${String.format("%.1f", munsellColorSettings.valueAdjustment)}")
                        }
                        Slider(
                            value = munsellColorSettings.valueAdjustment,
                            onValueChange = { newValue ->
                                munsellColorSettings = munsellColorSettings.copy(valueAdjustment = newValue)
                                onFilter(munsellColorSettings)
                            },
                            valueRange = -5f..5f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.VERTICAL_SPACING))
                    
                    // Chroma Multiplier Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Chroma Multiplier:")
                            Text("${String.format("%.1f", munsellColorSettings.chromaMultiplier)}x")
                        }
                        Slider(
                            value = munsellColorSettings.chromaMultiplier,
                            onValueChange = { newChroma ->
                                munsellColorSettings = munsellColorSettings.copy(chromaMultiplier = newChroma)
                                onFilter(munsellColorSettings)
                            },
                            valueRange = 0.1f..3.0f
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ImageContentBar.SECTION_SPACING))
                    
                    // Preserve Original Colors Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Preserve Original Colors:")
                        Switch(
                            checked = munsellColorSettings.preserveOriginalColors,
                            onCheckedChange = { preserve ->
                                munsellColorSettings = munsellColorSettings.copy(preserveOriginalColors = preserve)
                                onFilter(munsellColorSettings)
                            }
                        )
                    }
                }
            }
        }
    }
}

object ImageContentBar {
    val VERTICAL_SPACING = 8.dp
    val SECTION_SPACING = 12.dp
    val LARGE_SPACING = 16.dp
    val COLOR_PICKER_SIZE = 32.dp
    val SMALL_ICON_SIZE = 16.dp
    val CARD_CORNER_RADIUS = 8.dp
    val CIRCLE_BORDER_WIDTH = 2.dp
    val SETTINGS_BUTTON_WIDTH = 120.dp
    val PRESET_BUTTON_WIDTH = 100.dp
}

val ImageFilter.Companion.filters: List<ImageFilter>
    get() = listOf(
        ImageFilter.None,
        ImageFilter.Grayscale,
        ImageFilter.BlackAndWhite,
        ImageFilter.WhiteAndBlack,
        ImageFilter.ChromaKey(),
        ImageFilter.ValueFilter(),
        ImageFilter.ColorSimplification(),
        ImageFilter.MunsellValue(),
        ImageFilter.MunsellColor(),
    )
