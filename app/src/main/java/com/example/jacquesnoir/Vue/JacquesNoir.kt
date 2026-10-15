package com.example.jacquesnoir.Vue

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jacquesnoir.Vue.EcranJeu.EcranJeu
import com.example.jacquesnoir.Vue.EcranStatistiques.EcranStatistiques
import com.example.jacquesnoir.Vue.EcranTitre.EcranAccueil
import com.example.jacquesnoir.Vue.Utilitaires.BarreHautPrincipale
import com.example.jacquesnoir.VueModele.VueModeleJacquesNoir

@Composable
fun JacquesNoir(vueModele: VueModeleJacquesNoir) {
    val navController = rememberNavController()
    var confirmationFinPartieVisible by remember { mutableStateOf(false) }
    val confirmationFailliteVisible by
        vueModele.confirmationFailliteVisible.collectAsStateWithLifecycle()
    val estPauvre by vueModele.estPauvre.collectAsStateWithLifecycle()
    val destinationCourante = navController.currentBackStackEntryAsState().value
        ?.destination?.route

    LaunchedEffect(destinationCourante, estPauvre) {
        if (destinationCourante == "jeu" && estPauvre == true) {
            vueModele.afficherConfirmationFaillite()
        }
    }

    LaunchedEffect(vueModele) {
        vueModele.partieQuittee.collect {
            navController.navigate("accueil") {
                popUpTo("accueil") { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BarreHautPrincipale(
                vueModele = vueModele,
                onBackButton = {
                    if (destinationCourante == "jeu") {
                        confirmationFinPartieVisible = true
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "accueil",
            modifier = Modifier
                .padding(innerPadding)
        ) {
            composable("accueil") {
                EcranAccueil(
                    vueModele = vueModele,
                    auClicJouer = {
                        if (!vueModele.verifierFailliteAvantPartie()) {
                            navController.navigate("jeu")
                        }
                    },
                    auClicStatistiques = { navController.navigate("statistiques") }
                )
            }
            composable("jeu") { EcranJeu(vueModele) }
            composable("statistiques") {
                EcranStatistiques(
                    vueModele = vueModele,
                    auClicFermer = { navController.popBackStack() }
                )
            }
            composable("historique") { Text("Historique") }
        }
    }

    if (confirmationFailliteVisible) {
        val refuserFaillite = {
            vueModele.annulerConfirmationFaillite()
            if (destinationCourante == "jeu") {
                navController.popBackStack("accueil", false)
            }
        }

        AlertDialog(
            onDismissRequest = refuserFaillite,
            title = { Text("Faillite") },
            text = {
                Text(
                    "Vous avez perdu, êtes pauvre et personne ne vous aime. " +
                        "Voulez-vous faire faillite et recommencer à 1000 jetons?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vueModele.reinitialiserJetonsRestant {
                            if (destinationCourante != "jeu") {
                                navController.navigate("jeu")
                            }
                        }
                    }
                ) {
                    Text("OUI")
                }
            },
            dismissButton = {
                TextButton(onClick = refuserFaillite) {
                    Text("NON")
                }
            }
        )
    }

    if (confirmationFinPartieVisible) {
        AlertDialog(
            onDismissRequest = { confirmationFinPartieVisible = false },
            title = { Text("Terminer la partie ?") },
            text = {
                Text("La partie sera terminée et ses cartes seront comptabilisées dans les statistiques.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmationFinPartieVisible = false
                        vueModele.quitterPartie()
                    }
                ) {
                    Text("TERMINER LA PARTIE")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmationFinPartieVisible = false }) {
                    Text("CONTINUER À JOUER")
                }
            }
        )
    }
}
