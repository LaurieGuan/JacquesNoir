package com.example.jacquesnoir.Room.Entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Defausse")
data class CarteDefausse(
    @PrimaryKey(
        autoGenerate = true
    )val id: Int,
    val couleur: String,
    val numero: Int
) {
    init {
        validerCarte(couleur, numero)
    }
}
