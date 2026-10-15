package com.example.jacquesnoir.Vue.EcranTitre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoutonOperation(
    texte: String,
    selectionnee: Boolean,
    auClic: () -> Unit
) {
    val color = MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selectionnee,
                role = Role.RadioButton,
                onClick = auClic
            )
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
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(40.dp)
                .background(
                    if (selectionnee) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                )
        )

        Spacer(Modifier.width(12.dp))

        RadioButton(
            selected = selectionnee,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = texte,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
        )
    }
}
