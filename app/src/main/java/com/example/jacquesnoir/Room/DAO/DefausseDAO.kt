package com.example.jacquesnoir.Room.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.jacquesnoir.Room.Entites.CarteDefausse
import com.example.jacquesnoir.Room.Entites.CarteSabot

@Dao
abstract class DefausseDAO {

    @Query("SELECT * FROM Defausse ORDER BY id ASC")
    abstract suspend fun obtenirToutesLesCartes(): List<CarteDefausse>

    @Query("SELECT * FROM Defausse ORDER BY id ASC")
    abstract fun observerToutesLesCartes(): Flow<List<CarteDefausse>>

    @Query("SELECT * FROM Defausse WHERE id = :carteId LIMIT 1")
    abstract suspend fun obtenirCarte(carteId: Int): CarteDefausse?

    @Insert
    abstract suspend fun insererCartes(cartes: List<CarteDefausse>): List<Long>

    @Update
    abstract suspend fun modifierCarte(carte: CarteDefausse): Int

    @Delete
    abstract suspend fun supprimerCarte(carte: CarteDefausse): Int

    @Query("DELETE FROM Defausse")
    abstract suspend fun vider(): Int

    @Query(
        """
        SELECT DISTINCT Sabot.*
        FROM Sabot
        INNER JOIN CartesMains ON CartesMains.carteId = Sabot.id
        ORDER BY Sabot.id ASC
        """
    )
    protected abstract suspend fun chargerToutesLesCartesEnMain(): List<CarteSabot>

    @Insert
    protected abstract suspend fun ajouterCartes(cartes: List<CarteDefausse>): List<Long>

    @Query("DELETE FROM Sabot WHERE id IN (:ids)")
    protected abstract suspend fun supprimerDuSabot(ids: List<Int>): Int

    @Query("DELETE FROM Mains")
    protected abstract suspend fun supprimerToutesLesMains(): Int

    /** Défausse toutes les mains, supprime leurs cartes du sabot et leurs lignes de main. */
    @Transaction
    open suspend fun viderMainsDansDefausse() {
        val cartes = chargerToutesLesCartesEnMain()
        if (cartes.isNotEmpty()) {
            ajouterCartes(
                cartes.map { carte ->
                    CarteDefausse(
                        id = 0,
                        couleur = carte.couleur,
                        numero = carte.numero
                    )
                }
            )
            supprimerDuSabot(cartes.map { it.id })
        }
        supprimerToutesLesMains()
    }
}
