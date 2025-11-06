package com.example.openfoodmaps.data.model

import java.time.Instant

/**
 * User entity - represents an application user
 * 
 * Privacy-first design: No personal information collected.
 * Users are identified by their device's MAC address.
 */
data class User(
    val id: String, // UUID
    val createdAt: Instant? = null,
    val macAddr: String, // MAC address of user's device
    private val subscribedListings: MutableSet<String> = mutableSetOf() // Set of CardListing IDs
) {
    // Subscription management functions
    // These control access to the private subscribedListings field
    
    /**
     * Subscribe to a CardListing
     * @param cardListingId UUID of the CardListing to subscribe to
     * @return true if subscription was added, false if already subscribed
     */
    fun subscribeToListing(cardListingId: String): Boolean {
        return subscribedListings.add(cardListingId)
    }
    
    /**
     * Unsubscribe from a CardListing
     * @param cardListingId UUID of the CardListing to unsubscribe from
     * @return true if subscription was removed, false if not subscribed
     */
    fun unsubscribeFromListing(cardListingId: String): Boolean {
        return subscribedListings.remove(cardListingId)
    }
    
    /**
     * Check if user is subscribed to a CardListing
     * @param cardListingId UUID of the CardListing to check
     * @return true if subscribed, false otherwise
     */
    fun isSubscribedToListing(cardListingId: String): Boolean {
        return subscribedListings.contains(cardListingId)
    }
    
    /**
     * Get all subscribed listing IDs
     * @return Set of CardListing UUIDs the user is subscribed to
     */
    fun getSubscribedListings(): Set<String> {
        return subscribedListings.toSet() // Return immutable copy
    }
    
    /**
     * Get count of subscribed listings
     * @return Number of listings user is subscribed to
     */
    fun getSubscribedListingsCount(): Int {
        return subscribedListings.size
    }
    
    /**
     * Clear all subscriptions
     */
    fun clearAllSubscriptions() {
        subscribedListings.clear()
    }
    
    /**
     * Create a copy with modified subscribed listings
     * Useful for immutable updates
     */
    fun copyWithSubscriptions(newSubscriptions: Set<String>): User {
        return this.copy(subscribedListings = newSubscriptions.toMutableSet())
    }
}
