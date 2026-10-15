package com.example.jacquesnoir.Vue.EcranTitre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElementInfoGenerale(
    libelle: String,
    valeur: String,
    afficherMarqueur: Boolean = false,
    couleurMarqueur: Color? = null
) {
    val color = MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val epaisseur = 1.dp.toPx()
                val y = size.height - epaisseur / 2

                drawLine(
                    color = color,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = epaisseur
                )
            }
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = libelle,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (afficherMarqueur) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(couleurMarqueur ?: MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.width(12.dp))
            }

            Text(
                text = valeur,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
            )
        }
    }
}
