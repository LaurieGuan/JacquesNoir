package com.example.jacquesnoir.Room.Entites

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(
    tableName = "Mains",
)
data class MainDB (
    @PrimaryKey(
        autoGenerate = true
    )val id: Int,
    val etat: String,
    val numero: Int,
    val estJoueur: Int,
    @ColumnInfo(defaultValue = "0") val mise: Int
) {
    init {
        check(
            etat == "CREEE" ||
            etat == "INITIALISEE" ||
            etat == "ENGAGEE" ||
            etat == "SEPAREE" ||
            etat == "DOUBLEE" ||
            etat == "TERMINEE" ||
            etat == "JACQUES_NOIR" ||
            etat == "DEPASSEE"
        )
        check(numero in 0..3)
        check(mise >= 0)
    }
}
