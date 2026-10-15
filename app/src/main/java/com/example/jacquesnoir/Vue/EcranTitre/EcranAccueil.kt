package com.example.jacquesnoir.Vue.EcranTitre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jacquesnoir.VueModele.VueModeleJacquesNoir

@Composable
fun EcranAccueil(
    vueModele: VueModeleJacquesNoir,
    auClicJouer: () -> Unit,
    auClicStatistiques: () -> Unit,
    dernierePartie: String = "—",
    statutDossier: String = "AUCUN DOSSIER"
) {
    val soldeDisponible by vueModele.jetonsDisponibles.collectAsStateWithLifecycle()
    val partieEnCours by vueModele.partieEnCours.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            text = "FORMULAIRE JN-A/01",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                SectionInfosGen(
                    soldeDisponible = soldeDisponible,
                    dernierePartie = dernierePartie,
                    statutDossier = statutDossier
                )

                Spacer(Modifier.height(64.dp))

                SectionChoixOperations(
                    texteJouer = if (partieEnCours) "CONTINUER LA PARTIE" else "NOUVELLE PARTIE",
                    auClicJouer = auClicJouer,
                    auClicStatistiques = auClicStatistiques
                )

                Spacer(Modifier.height(48.dp))

                Text(
                    text = "RÉF. JN / ACCUEIL / SERVICE DES JEUX",
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)
                )
            }
        }
    }
}
