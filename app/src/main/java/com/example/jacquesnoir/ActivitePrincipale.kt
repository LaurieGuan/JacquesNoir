package com.example.jacquesnoir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.jacquesnoir.Depot.DepotJacquesNoir
import com.example.jacquesnoir.Depot.GestionnairePartie
import com.example.jacquesnoir.Room.BDJacquesNoir
import com.example.jacquesnoir.Vue.JacquesNoir
import com.example.jacquesnoir.VueModele.VueModeleJacquesNoir
import com.example.jacquesnoir.ui.theme.ThemeJacqueNoir

class ActivitePrincipale : ComponentActivity() {
    private lateinit var vueModele: VueModeleJacquesNoir

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val fabrique = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(VueModeleJacquesNoir::class.java))
                val baseDeDonnees = Room.databaseBuilder(
                    applicationContext,
                    BDJacquesNoir::class.java,
                    "jacques-noir.db"
                )
                    .addMigrations(BDJacquesNoir.MIGRATION_1_2)
                    .build()
                val depot = DepotJacquesNoir(baseDeDonnees)
                val gestionnairePartie = GestionnairePartie(depot)
                return VueModeleJacquesNoir(depot, gestionnairePartie) as T
            }
        }
        vueModele = ViewModelProvider(this, fabrique)[VueModeleJacquesNoir::class.java]

        setContent {
            ThemeJacqueNoir() {
                JacquesNoir(vueModele)
            }
        }
    }
}
