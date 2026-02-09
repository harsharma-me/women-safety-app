package com.safety.women.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing an emergency contact in the database
 */
@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
