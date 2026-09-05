package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE isInRotation = 1 AND (snoozeUntilTimestamp == 0 OR snoozeUntilTimestamp <= :currentTime) ORDER BY lastContactedTimestamp ASC")
    fun getRotationClients(currentTime: Long): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :clientId LIMIT 1")
    suspend fun getClientById(clientId: String): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clients: List<ClientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity)

    @Update
    suspend fun update(client: ClientEntity)

    @Query("UPDATE clients SET isInRotation = :isInRotation WHERE id = :clientId")
    suspend fun setRotationStatus(clientId: String, isInRotation: Boolean)

    @Query("UPDATE clients SET isInRotation = :isInRotation")
    suspend fun setAllRotationStatus(isInRotation: Boolean)

    @Query("UPDATE clients SET lastContactedTimestamp = :timestamp WHERE id = :clientId")
    suspend fun updateLastContacted(clientId: String, timestamp: Long)

    @Query("UPDATE clients SET snoozeUntilTimestamp = :snoozeUntil WHERE id = :clientId")
    suspend fun setSnooze(clientId: String, snoozeUntil: Long)

    @Query("SELECT number FROM clients")
    suspend fun getAllNumbers(): List<String>

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getCount(): Int

    @Query("DELETE FROM clients WHERE id LIKE 'client-%'")
    suspend fun removeSeedClients()
}
