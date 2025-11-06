package com.example.openfoodmaps.data.model

import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * CardListing entity - represents a food/event listing at a school
 * 
 * Pricing logic:
 * - Either fixedCost is set (single price) AND costDown + costUp are null
 * - OR costDown + costUp are set (price range) AND fixedCost is null
 * - OR all three are null (free)
 */
data class CardListing(
    val id: String, // UUID
    val createdAt: Instant? = null,
    val schoolId: String, // Foreign key to School
    val title: String,
    val room: String? = null,
    val address: String,
    val dietRestrictions: List<String>? = null, // e.g., ["vegan", "gluten-free"]
    val timeStart: Instant,
    val timeEnd: Instant,
    val costDown: Float? = null, // Minimum price in range
    val costUp: Float? = null, // Maximum price in range
    val fixedCost: Float? = null, // Fixed price (mutually exclusive with costDown/costUp)
    val description: String? = null,
    val clubId: String? = null, // Foreign key to Club (optional)
    val userId: String // Foreign key to User (creator)
) {
    // Pricing helper functions
    fun isFree(): Boolean = fixedCost == null && costDown == null && costUp == null
    
    fun hasFixedPrice(): Boolean = fixedCost != null && costDown == null && costUp == null
    
    fun hasPriceRange(): Boolean = costDown != null && costUp != null && fixedCost == null
    
    fun getFormattedPrice(): String {
        return when {
            isFree() -> "Free"
            hasFixedPrice() -> "$${String.format("%.2f", fixedCost)}"
            hasPriceRange() -> "$${String.format("%.2f", costDown)} - $${String.format("%.2f", costUp)}"
            else -> "Price TBD"
        }
    }
    
    fun getPriceRange(): Pair<Float, Float>? {
        return if (hasPriceRange() && costDown != null && costUp != null) {
            Pair(costDown, costUp)
        } else null
    }
    
    // Time helper functions
    fun isActive(): Boolean {
        val now = Instant.now()
        return now.isAfter(timeStart) && now.isBefore(timeEnd)
    }
    
    fun isUpcoming(): Boolean {
        return Instant.now().isBefore(timeStart)
    }
    
    fun isPast(): Boolean {
        return Instant.now().isAfter(timeEnd)
    }
    
    fun getDuration(): Duration {
        return Duration.between(timeStart, timeEnd)
    }
    
    fun getFormattedTimeRange(): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return "${formatter.format(timeStart)} - ${formatter.format(timeEnd)}"
    }
    
    // Validation
    fun isValid(): Boolean {
        return title.isNotBlank() 
            && address.isNotBlank()
            && timeEnd.isAfter(timeStart)
            && (isFree() || hasFixedPrice() || hasPriceRange())
    }
    
    // Diet restriction helpers
    fun hasDietRestriction(diet: String): Boolean {
        return dietRestrictions?.contains(diet.lowercase()) ?: false
    }
    
    fun matchesDietRestrictions(userRestrictions: List<String>): Boolean {
        if (dietRestrictions.isNullOrEmpty()) return true
        if (userRestrictions.isEmpty()) return false
        return dietRestrictions.any { it.lowercase() in userRestrictions.map { r -> r.lowercase() } }
    }
    
    // Location helper
    fun getFullLocation(): String {
        return if (room != null) {
            "$address, Room $room"
        } else {
            address
        }
    }
}

