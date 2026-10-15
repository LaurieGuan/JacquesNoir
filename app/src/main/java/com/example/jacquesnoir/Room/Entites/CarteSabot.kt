package com.example.jacquesnoir.Room.Entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jacquesnoir.Modele.Carte
import com.example.jacquesnoir.Modele.Couleur
import com.example.jacquesnoir.Modele.Numero

@Entity (tableName = "Sabot")
data class CarteSabot(
    @PrimaryKey(
        autoGenerate = true
    )val id: Int,
    val couleur: String,
    val numero: Int,
    // L'ordre le plus petit correspond au dessus du sabot.
    // La pioche sélectionne donc la carte disponible ayant le plus petit ordre.
    val ordre: Int
) {
    init {
        validerCarte(couleur, numero)
    }

    fun enCarteEngin(): Carte =
        Carte(
            couleur = Couleur.valueOf(couleur), num = Numero.entries[numero - 2]
        )
}

/** Construit une carte de sabot; l'index fourni devient son ordre dans le sabot. */
fun Carte.enCarteSabot(ordre: Int): CarteSabot = CarteSabot(
    id = 0,
    couleur = couleur.name,
    numero = num.ordinal + 2,
    ordre = ordre
)
