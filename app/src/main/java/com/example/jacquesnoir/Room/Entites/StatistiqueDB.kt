package com.example.jacquesnoir.Room.Entites

import androidx.room.Entity

@Entity(
    tableName = "Statistiques",
    primaryKeys = ["couleur", "numero"]
)
data class StatistiqueDB(
    val couleur: String,
    val numero: Int,
    val nombreApparition: Int
) {
    init {
        validerCarte(couleur, numero)
    }
}
