package com.example.jacquesnoir.Room.Jointures

import androidx.room.Embedded
import com.example.jacquesnoir.Room.Entites.CarteSabot
import com.example.jacquesnoir.Room.Entites.MainDB

/** Ligne aplatie utilisée par le flux qui observe mains, liens et cartes. */
data class MainAvecCarteLigne(
    @Embedded(prefix = "main_") val main: MainDB,
    @Embedded(prefix = "carte_") val carte: CarteSabot?
)
