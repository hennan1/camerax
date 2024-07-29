package com.truid.android.ui.carddetection.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect

@Composable
fun Overlay(
    verticalOffset: Int,
    indicatorColor: Color,
    onGloballyPositioned: (topLeft: Offset, bottomRight: Offset) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val rectWidth = size.width * 0.95f
        val rectHeight = rectWidth / 1.57f

//        val rectLeft = size.center.x - rectWidth / 2f
//        val rectTop = size.center.y - (rectHeight / 2f) - verticalOffset
//        val rectRight = rectLeft + rectWidth
//        val rectBottom = rectTop + rectHeight
        val rectLeft = size.center.x - rectWidth / 2f
        val rectTop = 0f
        val rectRight = rectLeft + rectWidth
        val rectBottom = rectTop + rectHeight

        val rectPath = Path().apply {
            addRect(Rect(rectLeft, rectTop, rectRight, rectBottom))
        }

        clipPath(path = rectPath, clipOp = ClipOp.Difference) {
            drawRect(
                color = Color.Black.copy(alpha = 0.64f),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height)
            )
        }

        clipRect(
            rectLeft + 64, rectTop - 64,
            rectRight - 64, rectBottom + 64,
            clipOp = ClipOp.Difference
        ) {
            clipRect(
                rectLeft - 64, rectTop + 64,
                rectRight + 64, rectBottom - 64,
                clipOp = ClipOp.Difference
            ) {
                drawRect(
                    color = indicatorColor,
                    topLeft = Offset(rectLeft + 12f, rectTop + 12),
                    size = Size(rectWidth - 24f, rectHeight - 24),
                    style = Stroke(24f)
                )
            }
        }

        onGloballyPositioned(Offset(rectLeft, rectTop), Offset(rectRight, rectBottom))
    }
}