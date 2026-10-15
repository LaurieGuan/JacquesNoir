package com.example.jacquesnoir.Vue.EcranJeu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DialoguePreparationMise(
    visible: Boolean,
    modeModification: Boolean,
    mancheEnCours: Boolean,
    miseProchaine: Int,
    texteMise: String,
    erreur: String?,
    actionEnCours: Boolean,
    auChangementTexteMise: (String) -> Unit,
    auClicMoins: () -> Unit,
    auClicPlus: () -> Unit,
    auClicModifier: () -> Unit,
    auClicContinuer: () -> Unit,
    auClicValider: () -> Unit,
    auClicAnnuler: () -> Unit
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = {
            if (mancheEnCours && !actionEnCours) auClicAnnuler()
        },
        title = {
            Text(
                if (modeModification) "MISE SUIVANTE"
                else "PRÉPARATION DE LA MANCHE"
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (modeModification) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "La mise modifiée s’appliquera à la prochaine manche.",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(onClick = auClicMoins) { Text("−") }
                            OutlinedTextField(
                                value = texteMise,
                                onValueChange = { saisie ->
                                    auChangementTexteMise(saisie.filter(Char::isDigit))
                                },
                                modifier = Modifier.width(150.dp),
                                singleLine = true,
                                suffix = { Text("jetons") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = auClicPlus) { Text("+") }
                        }
                    }
                } else {
                    Text(
                        "Mise prévue : $miseProchaine jetons. Voulez-vous la modifier ou continuer ?",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
                    )
                }
                erreur?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                enabled = !actionEnCours,
                onClick = if (modeModification) auClicAnnuler else auClicModifier
            ) {
                Text(
                    when {
                        modeModification -> "ANNULER"
                        else -> "CHANGER LA MISE"
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !actionEnCours,
                onClick = if (modeModification) auClicValider else auClicContinuer
            ) {
                Text(
                    when {
                        actionEnCours -> "DISTRIBUTION…"
                        modeModification && mancheEnCours -> "ENREGISTRER"
                        modeModification -> "VALIDER"
                        else -> "CONTINUER"
                    }
                )
            }
        }
    )
}
