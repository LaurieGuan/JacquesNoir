package com.example.jacquesnoir.Vue.EcranStatistiques

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jacquesnoir.VueModele.VueModeleJacquesNoir
import com.example.jacquesnoir.ui.theme.AppColors
import java.text.NumberFormat
import java.util.Locale

private val rangsCartes = listOf(
    14 to "A",
    13 to "K",
    12 to "Q",
    11 to "J",
    10 to "10",
    9 to "9",
    8 to "8",
    7 to "7",
    6 to "6",
    5 to "5",
    4 to "4",
    3 to "3",
    2 to "2"
)

@Composable
fun EcranStatistiques(
    vueModele: VueModeleJacquesNoir,
    auClicFermer: () -> Unit
) {
    val statistiques by vueModele.statistiques.collectAsStateWithLifecycle()

    val apparitionsParRang = statistiques
        .groupBy { it.numero }
        .mapValues { (_, cartes) -> cartes.sumOf { it.nombreApparition } }

    val totalApparitions = statistiques.sumOf { it.nombreApparition }
    val rangDominant = apparitionsParRang.maxByOrNull { it.value }
        ?.takeIf { it.value > 0 }
    val rangDominantEtPart = rangDominant?.let { (rang, nombre) ->
        val nomRang = rangsCartes.firstOrNull { it.first == rang }?.second ?: "—"
        val pourcentage = nombre * 100.0 / totalApparitions
        nomRang to pourcentage
    }

    val formatEntier = NumberFormat.getIntegerInstance(Locale.CANADA_FRENCH)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                Text(
                    text = "RAPPORT DE STATISTIQUES",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "APPARITIONS DES CARTES",
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 20.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IndicateurStatistique(
                    valeur = formatEntier.format(totalApparitions),
                    libelle = "CARTES COMPTÉES",
                    modifier = Modifier.weight(1f)
                )
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                IndicateurStatistique(
                    valeur = rangDominantEtPart?.first ?: "—",
                    libelle = "RANG DOMINANT",
                    modifier = Modifier.weight(1f)
                )
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                IndicateurStatistique(
                    valeur = rangDominantEtPart?.second?.let {
                        String.format(Locale.CANADA_FRENCH, "%.0f %%", it)
                    } ?: "—",
                    libelle = "PART DU TOTAL",
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CARTE",
                    modifier = Modifier.weight(0.7f),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "APPARITIONS",
                    modifier = Modifier.weight(1.2f),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "%",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }

        items(rangsCartes) { (numero, nom) ->
            val apparitions = apparitionsParRang[numero] ?: 0
            val proportion = if (totalApparitions == 0) {
                0f
            } else {
                apparitions.toFloat() / totalApparitions
            }
            val pourcentage = proportion * 100
            val estRangDominant = rangDominant?.key == numero

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nom,
                    modifier = Modifier.weight(0.7f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatEntier.format(apparitions),
                    modifier = Modifier.weight(1.2f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier.weight(2f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { proportion },
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp),
                        color = if (estRangDominant) {
                            AppColors.Futur
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = String.format(
                            Locale.CANADA_FRENCH,
                            "%.0f %%",
                            pourcentage
                        ),
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        item {
            Button(
                onClick = auClicFermer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 28.dp)
                    .height(56.dp)
            ) {
                Text("FERMER LE RAPPORT")
            }
        }
    }
}

@Composable
private fun IndicateurStatistique(
    valeur: String,
    libelle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valeur,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = libelle,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
