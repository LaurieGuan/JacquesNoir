package com.example.jacquesnoir.Room.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.jacquesnoir.Room.Entites.StatistiqueDB

@Dao
abstract class StatistiquesDAO {

    @Query("SELECT * FROM Statistiques ORDER BY couleur ASC, numero ASC")
    abstract suspend fun obtenirToutes(): List<StatistiqueDB>

    @Query("SELECT * FROM Statistiques ORDER BY couleur ASC, numero ASC")
    abstract fun observerToutes(): Flow<List<StatistiqueDB>>

    @Query(
        "SELECT * FROM Statistiques WHERE couleur = :couleur AND numero = :numero LIMIT 1"
    )
    abstract suspend fun obtenir(couleur: String, numero: Int): StatistiqueDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun enregistrer(statistique: StatistiqueDB)

    @Update
    abstract suspend fun modifier(statistique: StatistiqueDB): Int

    @Query("DELETE FROM Statistiques WHERE couleur = :couleur AND numero = :numero")
    abstract suspend fun supprimer(couleur: String, numero: Int): Int

    @Query("DELETE FROM Statistiques")
    abstract suspend fun vider(): Int

    @Query(
        """
        SELECT couleur, numero, COUNT(*) AS nombreApparition
        FROM Defausse
        GROUP BY couleur, numero
        """
    )
    protected abstract suspend fun compilerDefausse(): List<StatistiqueDB>

    @Query("DELETE FROM Defausse")
    protected abstract suspend fun viderDefausse(): Int

    /** Cumul les cartes de la défausse aux statistiques, puis vide la défausse. */
    @Transaction
    open suspend fun comptabiliserDefausse() {
        for (compte in compilerDefausse()) {
            val existant = obtenir(compte.couleur, compte.numero)
            val total = (existant?.nombreApparition ?: 0) + compte.nombreApparition
            enregistrer(compte.copy(nombreApparition = total))
        }
        viderDefausse()
    }
}
