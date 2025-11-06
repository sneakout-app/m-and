package com.example.openfoodmaps.data.model

import java.time.Instant

/**
 * Notification entity - represents a user notification about a CardListing
 */
data class Notification(
    val id: String, // UUID
    val time: Instant, // When the notification should be delivered
    val cardListingId: String, // Foreign key to CardListing
    val userId: String // Foreign key to User
) {
    // Time helper functions
    fun isOverdue(): Boolean {
        return Instant.now().isAfter(time)
    }
    
    fun isUpcoming(): Boolean {
        return Instant.now().isBefore(time)
    }
}



