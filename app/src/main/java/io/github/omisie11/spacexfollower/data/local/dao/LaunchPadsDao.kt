package io.github.omisie11.spacexfollower.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import kotlinx.coroutines.flow.Flow

@Dao
interface LaunchPadsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaunchPads(launchPads: List<LaunchPad>)

    @Transaction
    suspend fun replaceLaunchPads(launchPads: List<LaunchPad>) {
        deleteLaunchPadsData()
        insertLaunchPads(launchPads)
    }

    @Query("SELECT * FROM launch_pads_table")
    fun getLaunchPadsFlow(): Flow<List<LaunchPad>>

    @Query("DELETE FROM launch_pads_table")
    suspend fun deleteLaunchPadsData()
}
