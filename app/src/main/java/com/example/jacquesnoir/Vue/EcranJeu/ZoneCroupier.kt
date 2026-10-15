package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.example.jacquesnoir.R
import com.example.jacquesnoir.Modele.Mains.MainModele
import com.example.jacquesnoir.Modele.Mains.meilleurTotal

@Composable
fun ZoneCroupier(
    mainCroupier: MainModele?,
    carteCacheeRevelee: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CROUPIER",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
        )

        val cartes = mainCroupier?.etat?.cartes.orEmpty()
        Row(verticalAlignment = Alignment.CenterVertically) {
            cartes.forEachIndexed { index, carte ->
                if (index > 0) Spacer(Modifier.width(8.dp))
                if (index == 1 && !carteCacheeRevelee) {
                    CarteJeu(
                        ressource = R.drawable.carte_dos,
                        description = "Carte cachée du croupier",
                        modifier = Modifier.width(62.dp),
                        echelleContenu = ContentScale.Crop
                    )
                } else {
                    CarteJeu(
                        carte = carte,
                        description = "Carte ${index + 1} du croupier",
                        modifier = Modifier.width(62.dp)
                    )
                }
            }
        }

        Text(
            text = when {
                mainCroupier == null -> "EN ATTENTE"
                !carteCacheeRevelee -> "TOTAL : ?"
                else -> "TOTAL : ${mainCroupier.etat.meilleurTotal()}"
            },
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
        )
    }
}
