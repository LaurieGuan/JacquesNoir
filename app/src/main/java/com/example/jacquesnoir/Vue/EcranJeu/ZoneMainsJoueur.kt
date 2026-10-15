package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jacquesnoir.Modele.Mains.MainDepassee
import com.example.jacquesnoir.Modele.Mains.MainJacquesNoir
import com.example.jacquesnoir.Modele.Mains.MainModele
import com.example.jacquesnoir.Modele.Mains.MainTerminee
import com.example.jacquesnoir.Modele.Mains.meilleurTotal

@Composable
fun ZoneMainsJoueur(
    etatPages: PagerState,
    mains: List<MainModele>,
    mainActiveId: Int?,
    auClicMain: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(etatPages.currentPage, mains) {
        mains.getOrNull(etatPages.currentPage)?.let { auClicMain(it.id) }
    }

    Column(
        modifier = modifier.padding(start = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalPager(
            state = etatPages,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val main = mains.getOrNull(page)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "VOTRE MAIN",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
                    )
                    Text(
                        text = main?.let { "MAIN ${it.numero + 1} · ${it.statut(mainActiveId)}" }
                            ?: "EN ATTENTE",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 11.sp)
                    )
                }

                if (main == null) {
                    Text("AUCUNE MAIN", modifier = Modifier.padding(vertical = 24.dp))
                } else {
                    RangeeCartesMain(
                        cartes = main.etat.cartes,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "TOTAL : ${main.etat.meilleurTotal()}   ·   MISE : ${main.etat.mise}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
                    )
                }
            }
        }

        if (mains.size > 1) {
            Text(
                text = "GLISSER POUR CONSULTER LES AUTRES MAINS",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 9.sp)
            )
        }
    }
}

private fun MainModele.statut(mainActiveId: Int?): String = when {
    etat is MainDepassee -> "DÉPASSÉE"
    etat is MainJacquesNoir -> "BLACKJACK"
    etat is MainTerminee -> "TERMINÉE"
    id == mainActiveId -> "ACTIVE"
    else -> "EN ATTENTE"
}
