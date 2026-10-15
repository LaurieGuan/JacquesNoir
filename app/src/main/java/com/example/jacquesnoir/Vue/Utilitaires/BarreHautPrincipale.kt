package com.example.jacquesnoir.Vue.Utilitaires

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.jacquesnoir.R
import com.example.jacquesnoir.VueModele.VueModeleJacquesNoir

@Composable
fun BarreHautPrincipale(
    vueModele: VueModeleJacquesNoir,
    onBackButton: () -> Unit
) {
    val tokens by vueModele.jetonsDisponibles.collectAsStateWithLifecycle()
    val nombrePaquets by vueModele.nombrePaquetsParDefaut.collectAsStateWithLifecycle()
    var menuPaquetsOuvert by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 12.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    offset = DpOffset(x = 0.dp, y = 5.dp),
                    spread = (-4).dp
                )
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "back"
                    )
                },
                onClick = onBackButton
            )
            Text(
                "Jacques Noir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$tokens jetons")

            Box {
                IconButton(onClick = { menuPaquetsOuvert = true }) {
                    SymboleCarte()
                }

                DropdownMenu(
                    expanded = menuPaquetsOuvert,
                    onDismissRequest = { menuPaquetsOuvert = false }
                ) {
                    (1..4).forEach { nombre ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "$nombre paquet${if (nombre > 1) "s" else ""}" +
                                            if (nombre == nombrePaquets) " ✓" else ""
                                )
                            },
                            onClick = {
                                vueModele.choisirNombrePaquets(nombre)
                                menuPaquetsOuvert = false
                            }
                        )
                    }
                }
            }
        }
    }
}
