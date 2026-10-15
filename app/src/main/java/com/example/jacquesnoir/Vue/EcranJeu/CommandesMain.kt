package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommandesMain(
    commandesActives: Boolean,
    resterDisponible: Boolean,
    doublerDisponible: Boolean,
    separerDisponible: Boolean,
    croupierRevele: Boolean,
    auClicTirer: () -> Unit,
    auClicRester: () -> Unit,
    auClicDoubler: () -> Unit,
    auClicSeparer: () -> Unit,
    auClicChangerMise: () -> Unit,
    auClicNouvelleManche: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOuvert by remember { mutableStateOf(false) }

    if (croupierRevele) {
        Button(
            onClick = auClicNouvelleManche,
            modifier = modifier.fillMaxWidth().fillMaxHeight(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("NOUVELLE MANCHE", fontSize = 18.sp)
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = auClicTirer,
                enabled = commandesActives,
                modifier = Modifier.weight(1.2f).fillMaxHeight(),
                shape = RectangleShape,
                contentPadding = PaddingValues(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("TIRER", fontSize = 16.sp)
            }

            Button(
                onClick = auClicRester,
                enabled = commandesActives && resterDisponible,
                modifier = Modifier.weight(1.2f).fillMaxHeight(),
                shape = RectangleShape,
                contentPadding = PaddingValues(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("RESTER", fontSize = 16.sp)
            }

            Box {
                Button(
                    onClick = { menuOuvert = true },
                    enabled = commandesActives,
                    modifier = Modifier.width(54.dp).fillMaxHeight(),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("…", fontSize = 22.sp)
                }

                DropdownMenu(
                    expanded = menuOuvert,
                    onDismissRequest = { menuOuvert = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Doubler") },
                        enabled = doublerDisponible,
                        onClick = {
                            menuOuvert = false
                            auClicDoubler()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Séparer") },
                        enabled = separerDisponible,
                        onClick = {
                            menuOuvert = false
                            auClicSeparer()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Changer la mise suivante") },
                        onClick = {
                            menuOuvert = false
                            auClicChangerMise()
                        }
                    )
                }
            }
        }
    }
}
