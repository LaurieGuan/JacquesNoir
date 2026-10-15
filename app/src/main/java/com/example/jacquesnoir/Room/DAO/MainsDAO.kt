package com.example.jacquesnoir.Room.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.jacquesnoir.Room.Entites.CarteMain
import com.example.jacquesnoir.Room.Entites.CarteSabot
import com.example.jacquesnoir.Room.Entites.MainDB
import com.example.jacquesnoir.Room.Jointures.MainAvecCartes
import com.example.jacquesnoir.Room.Jointures.MainAvecCarteLigne

@Dao
abstract class MainsDAO {

    @Query("SELECT * FROM Mains WHERE id = :mainId LIMIT 1")
    abstract suspend fun obtenirMain(mainId: Int): MainDB?

    @Query("SELECT * FROM Mains ORDER BY numero ASC")
    abstract suspend fun obtenirToutesLesMains(): List<MainDB>

    @Query(
        """
        SELECT
            Mains.id AS main_id,
            Mains.etat AS main_etat,
            Mains.numero AS main_numero,
            Mains.estJoueur AS main_estJoueur,
            Mains.mise AS main_mise,
            Sabot.id AS carte_id,
            Sabot.couleur AS carte_couleur,
            Sabot.numero AS carte_numero,
            Sabot.ordre AS carte_ordre
        FROM Mains
        LEFT JOIN CartesMains ON CartesMains.mainId = Mains.id
        LEFT JOIN Sabot ON Sabot.id = CartesMains.carteId
        ORDER BY Mains.numero ASC, Mains.estJoueur DESC, CartesMains.ordre ASC
        """
    )
    abstract fun observerMainsAvecCartes(): Flow<List<MainAvecCarteLigne>>

    @Transaction
    open suspend fun obtenirMainAvecCartes(mainId: Int): MainAvecCartes? {
        val main = obtenirMain(mainId) ?: return null
        val cartes = chargerCartes(mainId)
        return MainAvecCartes(main, cartes)
    }

    @Transaction
    open suspend fun obtenirToutesLesMainsAvecCartes(): List<MainAvecCartes> =
        obtenirToutesLesMains().map { main ->
            MainAvecCartes(main, chargerCartes(main.id))
        }

    @Insert
    abstract suspend fun insererMain(main: MainDB): Long

    @Update
    abstract suspend fun modifierMain(main: MainDB): Int

    @Delete
    abstract suspend fun supprimerMain(main: MainDB): Int

    @Query("DELETE FROM Mains")
    abstract suspend fun supprimerToutesLesMains(): Int

    @Query(
        """
        SELECT Sabot.*
        FROM Sabot
        INNER JOIN CartesMains ON CartesMains.carteId = Sabot.id
        WHERE CartesMains.mainId = :mainId
        ORDER BY CartesMains.ordre ASC
        """
    )
    abstract suspend fun chargerCartes(mainId: Int): List<CarteSabot>

    @Query("SELECT * FROM CartesMains WHERE mainId = :mainId ORDER BY ordre ASC")
    abstract suspend fun obtenirLiens(mainId: Int): List<CarteMain>

    @Insert
    abstract suspend fun ajouterCartesMain(cartes: List<CarteMain>): List<Long>

    @Update
    abstract suspend fun modifierLien(lien: CarteMain): Int

    @Delete
    abstract suspend fun supprimerLien(lien: CarteMain): Int

    @Query("DELETE FROM CartesMains WHERE mainId = :mainId")
    abstract suspend fun supprimerCartesDeMain(mainId: Int): Int

    @Query("DELETE FROM CartesMains")
    abstract suspend fun supprimerTousLesLiens(): Int
}
