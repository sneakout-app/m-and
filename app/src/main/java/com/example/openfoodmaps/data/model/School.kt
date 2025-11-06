package com.example.openfoodmaps.data.model

import java.time.Instant

/**
 * School entity - represents an educational institution
 */
data class School(
    val id: String, // UUID
    val name: String,
    val svg: String, // SVG logo/icon as string
    val createdAt: Instant? = null
)



