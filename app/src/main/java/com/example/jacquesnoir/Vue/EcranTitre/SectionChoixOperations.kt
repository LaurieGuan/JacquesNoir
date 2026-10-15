package com.example.jacquesnoir.Vue.EcranTitre

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionChoixOperations(
    texteJouer: String = "Jouer",
    auClicJouer: () -> Unit,
    auClicStatistiques: () -> Unit
) {
    var operationSelectionnee by remember { mutableIntStateOf(0) }

    Column {
        EnteteSection(
            texte = "OPÉRATIONS POSSIBLES",
            centrer = true
        )

        Spacer(Modifier.height(16.dp))

        BoutonOperation(
            texte = texteJouer,
            selectionnee = operationSelectionnee == 0,
            auClic = {
                operationSelectionnee = 0
                auClicJouer()
            }
        )

        Spacer(Modifier.height(20.dp))

        BoutonOperation(
            texte = "Consulter les statistiques",
            selectionnee = operationSelectionnee == 1,
            auClic = {
                operationSelectionnee = 1
                auClicStatistiques()
            }
        )
    }
}
