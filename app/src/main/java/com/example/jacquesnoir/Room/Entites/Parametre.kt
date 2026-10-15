package com.example.jacquesnoir.Room.Entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Entity(
    tableName = "Parametres"
)
data class Parametre(
    @PrimaryKey val cle: String,
    val valeur: String
) {
    init {
        check(valeur.isNotBlank())
        check(valeur.isNotEmpty())
        check(isValidJson(valeur))
    }
}


fun isValidJson(jsonString: String): Boolean {
    return try {
        Json.parseToJsonElement(jsonString)
        true
    } catch (e: Exception) {
        false
    }
}
