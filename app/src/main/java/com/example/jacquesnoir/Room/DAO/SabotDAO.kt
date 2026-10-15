package com.example.jacquesnoir.Room.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.jacquesnoir.Room.Entites.CarteSabot

@Dao
abstract class SabotDAO {

    @Query(
        """
        SELECT COUNT(*)
        FROM Sabot
        WHERE NOT EXISTS (
            SELECT 1 FROM CartesMains
            WHERE CartesMains.carteId = Sabot.id
        )
        """
    )
    abstract suspend fun nombreCartesRestantes(): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM Sabot
        WHERE NOT EXISTS (
            SELECT 1 FROM CartesMains
            WHERE CartesMains.carteId = Sabot.id
        )
        """
    )
    abstract fun observerNombreCartesRestantes(): Flow<Int>


    @Query(
        """
        SELECT Sabot.*
        FROM Sabot
        WHERE NOT EXISTS (
            SELECT 1 FROM CartesMains
            WHERE CartesMains.carteId = Sabot.id
        )
        ORDER BY Sabot.ordre ASC
        """
    )
    abstract suspend fun obtenirCartesNonPigees(): List<CarteSabot>

    @Query(
        """
        SELECT Sabot.*
        FROM Sabot
        WHERE NOT EXISTS (
            SELECT 1 FROM CartesMains
            WHERE CartesMains.carteId = Sabot.id
        )
        ORDER BY Sabot.ordre ASC
        LIMIT 1
        """
    )
    abstract suspend fun obtenirCarteDuDessus(): CarteSabot?

    @Query("SELECT * FROM Sabot WHERE id = :carteId LIMIT 1")
    abstract suspend fun obtenirCarte(carteId: Int): CarteSabot?

    @Query("SELECT * FROM Sabot ORDER BY ordre ASC")
    abstract suspend fun obtenirToutesLesCartes(): List<CarteSabot>

    @Insert
    abstract suspend fun insererCartes(cartes: List<CarteSabot>): List<Long>

    @Update
    abstract suspend fun modifierCarte(carte: CarteSabot): Int

    @Delete
    abstract suspend fun supprimerCarte(carte: CarteSabot): Int

    @Delete
    abstract suspend fun supprimerCartes(cartes: List<CarteSabot>): Int

    @Query("DELETE FROM Sabot")
    abstract suspend fun vider(): Int

}
