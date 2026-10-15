package com.example.jacquesnoir.Room.Entites

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "CartesMains",
    primaryKeys = ["mainId", "carteId"],
    foreignKeys = [
        ForeignKey(
            entity = MainDB::class,
            parentColumns = ["id"],
            childColumns = ["mainId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CarteSabot::class,
            parentColumns = ["id"],
            childColumns = ["carteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CarteMain (
    val mainId: Int,
    val carteId: Int,
    // L'ordre le plus petit correspond au dessus de la main :
    // les cartes sont ainsi lues dans leur ordre de distribution.
    val ordre: Int
)
