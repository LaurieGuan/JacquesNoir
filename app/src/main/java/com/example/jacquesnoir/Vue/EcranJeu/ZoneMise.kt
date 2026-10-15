package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ZoneMise(
    miseEnCours: Int,
    miseProchaine: Int,
    mancheEnCours: Boolean,
    auClic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.outline
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = auClic)
            .drawBehind {
                val x = size.width - 1.dp.toPx() / 2
                drawLine(
                    color = color,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(end = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (mancheEnCours) "MISE" else "PROCHAINE MISE",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
        )

        Text(
            text = (if (mancheEnCours) miseEnCours else miseProchaine).toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 36.sp)
        )

        PileJetons(
            montant = if (mancheEnCours) miseEnCours else miseProchaine
        )
    }
}
