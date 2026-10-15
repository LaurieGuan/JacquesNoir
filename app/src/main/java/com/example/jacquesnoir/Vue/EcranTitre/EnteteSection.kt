package com.example.jacquesnoir.Vue.EcranTitre

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun EnteteSection(
    texte: String,
    centrer: Boolean = false,
    taille: TextUnit = 26.sp
) {
    Text(
        text = texte,
        modifier = Modifier.fillMaxWidth(),
        textAlign = if (centrer) TextAlign.Center else TextAlign.Start,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = taille)
    )
}
