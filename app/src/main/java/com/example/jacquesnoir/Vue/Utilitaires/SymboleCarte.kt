package com.example.jacquesnoir.Vue.Utilitaires

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SymboleCarte(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 18.dp, height = 24.dp)
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(3.dp)
            )
    ) {
        Text(
            text = "A",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
