package com.example.jacquesnoir.Room.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.jacquesnoir.Room.Entites.Parametre

@Dao
abstract class ParametresDAO {

    @Query("SELECT * FROM Parametres WHERE cle = :cle LIMIT 1")
    abstract suspend fun obtenir(cle: String): Parametre?

    @Query("SELECT * FROM Parametres WHERE cle = :cle LIMIT 1")
    abstract fun observer(cle: String): Flow<Parametre?>

    @Query("SELECT * FROM Parametres ORDER BY cle ASC")
    abstract suspend fun obtenirTous(): List<Parametre>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun enregistrer(parametre: Parametre)

    @Update
    abstract suspend fun modifier(parametre: Parametre): Int

    @Delete
    abstract suspend fun supprimer(parametre: Parametre): Int

    @Query("DELETE FROM Parametres")
    abstract suspend fun vider(): Int
}
