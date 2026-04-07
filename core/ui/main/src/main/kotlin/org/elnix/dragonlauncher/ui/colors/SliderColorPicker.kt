package org.elnix.dragonlauncher.ui.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.base.ColorUtils.alphaMultiplier
import org.elnix.dragonlauncher.base.ColorUtils.randomColor
import org.elnix.dragonlauncher.ui.dragon.components.DragonIconButton
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel

@Composable
fun SliderColorPicker(
    actualColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableFloatStateOf(actualColor.red) }
    var green by remember { mutableFloatStateOf(actualColor.green) }
    var blue by remember { mutableFloatStateOf(actualColor.blue) }
    var alpha by remember { mutableFloatStateOf(actualColor.alpha) }

    val previousColors = remember { mutableStateListOf<Color>() }
    val color = Color(red, green, blue, alpha)
    val canPopLastColor = previousColors.isNotEmpty()



    fun pushCurrentColor() {
        previousColors.add(color)
    }

    fun popLastColor() {
        if (canPopLastColor) {
            val last = previousColors.removeAt(previousColors.lastIndex)
            red = last.red
            green = last.green
            blue = last.blue
            onColorSelected(color)
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            DragonIconButton(
                onClick = { popLastColor() },
                modifier = Modifier.weight(1f),
                enabled = { canPopLastColor },
                imageVector = Icons.Default.Replay,
                contentDescription = stringResource(R.string.undo),
            )

            DragonIconButton(
                onClick = {
                    pushCurrentColor()
                    val color = randomColor()
                    red = color.red
                    green = color.green
                    blue = color.blue
                    onColorSelected(color)
                },
                modifier = Modifier.weight(1f),
                imageVector =  Icons.Default.Shuffle,
                contentDescription = "Random Color",
            )
        }

        SliderWithLabel(
            label = "Red :",
            value = red,
            color = Color.Red,
            backgroundColor = Color.Red.alphaMultiplier(0.5f),
            valueRange = 0f..1f
        ) {
            red = it
            pushCurrentColor()
            onColorSelected(color)
        }
        SliderWithLabel(
            label = "Green :",
            value = green,
            color = Color.Green.alphaMultiplier(0.5f),
            valueRange = 0f..1f
        ) {
            green = it
            pushCurrentColor()
            onColorSelected(color)
        }
        SliderWithLabel(
            label = "Blue :",
            value = blue,
            color = Color.Blue,
            backgroundColor = Color.Blue.alphaMultiplier(0.5f),
            valueRange = 0f..1f
        ) {
            blue = it
            pushCurrentColor()
            onColorSelected(color)
        }
    }
}
