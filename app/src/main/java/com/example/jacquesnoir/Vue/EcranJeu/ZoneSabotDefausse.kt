package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.R

@Composable
fun ZoneSabotDefausse(
    nombreCartesRestantes: Int,
    derniereCarteDefaussee: Carte?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .height(108.dp)
            ) {
                repeat(3) { index ->
                    CarteJeu(
                        ressource = R.drawable.carte_dos,
                        description = "Paquet de cartes",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (index * 3).dp, y = (-index * 3).dp)
                            .width(72.dp),
                        echelleContenu = ContentScale.Crop
                    )
                }
            }
            Text(
                text = "$nombreCartesRestantes CARTES",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
            )
        }

        Spacer(Modifier.width(48.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(108.dp),
                contentAlignment = Alignment.Center
            ) {
                if (derniereCarteDefaussee == null) {
                    Text("—", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 32.sp))
                } else {
                    CarteJeu(
                        carte = derniereCarteDefaussee,
                        description = "Dernière carte de la défausse",
                        modifier = Modifier.width(72.dp)
                    )
                }
            }
            Text(
                text = "DÉFAUSSE",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
            )
        }
    }
}
