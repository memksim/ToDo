package com.memksim.todo.data.local

import androidx.room.*
import com.memksim.todo.data.entity.Reminder

@Dao
interface ReminderDao {

    @Insert
    suspend fun insertReminder(vararg reminder: Reminder)

    @Update
    suspend fun updateReminder(vararg reminder: Reminder)

    @Delete
    suspend fun deleteReminder(vararg reminder: Reminder)

    @Query("select * from reminders where isCompleted = 0")
    suspend fun getUpcomingReminders(): List<Reminder>

    @Query("select * from reminders where isCompleted = 1")
    suspend fun getCompletedReminders(): List<Reminder>

    @Query("select * from reminders where id = :id")
    suspend fun getReminder(id: Int): Reminder
}