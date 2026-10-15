package com.example.jacquesnoir.Vue.Utilitaires

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp

data class BottomEdgeShadowShape(
    val thickness: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val thicknessPx = with(density) { thickness.toPx() }
            .coerceIn(0f, size.height)

        val top = size.height - thicknessPx

        return Outline.Generic(
            Path().apply {
                moveTo(0f, top)
                lineTo(size.width, top)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
        )
    }
}
