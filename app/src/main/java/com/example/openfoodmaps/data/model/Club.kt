package com.example.openfoodmaps.data.model

import java.time.Instant

/**
 * Club entity - represents an organization within a school
 */
data class Club(
    val id: String, // UUID
    val name: String,
    val schoolId: String, // Foreign key to School
    val createdAt: Instant? = null
)



