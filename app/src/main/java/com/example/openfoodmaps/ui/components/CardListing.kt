package com.example.openfoodmaps.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt
import kotlinx.coroutines.delay

@Composable
fun CardListing(
    modifier: Modifier = Modifier,
    priceRange: String = "$44 - 48",
    timeRange: String = "15:30 - 16:00",
    title: String = "wolverine on wheels"
) {
    var isFlipped by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }
    var isAnimating by remember { mutableStateOf(false) }
    var cardSize by remember { mutableStateOf(Size.Zero) }
    var buttonPosition by remember { mutableStateOf(Offset.Zero) }
    
    // Animation progress (0f to 1f)
    val animationProgress by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "spreadAnimation"
    )
    
    // Calculate the maximum radius needed to cover the entire card from the button position
    val maxRadius = remember(cardSize, buttonPosition) {
        if (cardSize.width > 0 && cardSize.height > 0 && buttonPosition != Offset.Zero) {
            val distances = listOf(
                sqrt(buttonPosition.x * buttonPosition.x + buttonPosition.y * buttonPosition.y),
                sqrt((cardSize.width - buttonPosition.x) * (cardSize.width - buttonPosition.x) + buttonPosition.y * buttonPosition.y),
                sqrt(buttonPosition.x * buttonPosition.x + (cardSize.height - buttonPosition.y) * (cardSize.height - buttonPosition.y)),
                sqrt((cardSize.width - buttonPosition.x) * (cardSize.width - buttonPosition.x) + (cardSize.height - buttonPosition.y) * (cardSize.height - buttonPosition.y))
            )
            distances.maxOrNull() ?: 0f
        } else {
            0f
        }
    }
    
    val currentRadius = maxRadius * animationProgress
    
    // Determine colors based on flip state
    val baseCardColor = Color(0xFF424242) // Dark gray (not flipped)
    val flippedCardColor = Color(0xFFE0E0E0) // Light gray (flipped)
    
    val badgeColor = if (isFlipped) Color.Black else Color(0xFF424242)
    val badgeTextColor = Color.White
    val titleBadgeColor = if (isFlipped) Color(0xFFE0E0E0) else Color(0xFF424242)
    val titleTextColor = if (isFlipped) Color.Black else Color.White
    val buttonBgColor = if (isFlipped) Color.Black else Color.White
    val buttonIconColor = if (isFlipped) Color.White else Color.Black
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = baseCardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .onGloballyPositioned { coordinates ->
                    cardSize = Size(
                        coordinates.size.width.toFloat(),
                        coordinates.size.height.toFloat()
                    )
                }
        ) {
            // Base content (always visible)
            CardContent(
                isFlipped = false,
                cardColor = baseCardColor,
                badgeColor = Color(0xFF424242),
                badgeTextColor = badgeTextColor,
                titleBadgeColor = Color(0xFF424242),
                titleTextColor = Color.White,
                priceRange = priceRange,
                timeRange = timeRange,
                title = title
            )
            
            // Animated overlay that spreads from the button
            if (animationProgress > 0f && currentRadius > 0f && buttonPosition != Offset.Zero) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(
                            object : Shape {
                                override fun createOutline(
                                    size: Size,
                                    layoutDirection: LayoutDirection,
                                    density: Density
                                ): Outline {
                                    val path = Path().apply {
                                        addOval(
                                            Rect(
                                                offset = Offset(
                                                    buttonPosition.x - currentRadius,
                                                    buttonPosition.y - currentRadius
                                                ),
                                                size = Size(
                                                    currentRadius * 2,
                                                    currentRadius * 2
                                                )
                                            )
                                        )
                                    }
                                    return Outline.Generic(path)
                                }
                            }
                        )
                ) {
                    // Flipped content overlay (only visible within the circle)
                    CardContent(
                        isFlipped = isFlipped,
                        cardColor = if (isFlipped) flippedCardColor else baseCardColor,
                        badgeColor = if (isFlipped) Color.Black else Color(0xFF424242),
                        badgeTextColor = badgeTextColor,
                        titleBadgeColor = if (isFlipped) Color(0xFFE0E0E0) else Color(0xFF424242),
                        titleTextColor = if (isFlipped) Color.Black else Color.White,
                        priceRange = priceRange,
                        timeRange = timeRange,
                        title = title
                    )
                }
            }
            
            // Buttons on the top right edge
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Notification button
                CircularButton(
                    icon = Icons.Default.Notifications,
                    onClick = {
                        notificationsEnabled = !notificationsEnabled
                    },
                    backgroundColor = buttonBgColor,
                    iconColor = buttonIconColor,
                    size = 40.dp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Flip/Menu button (changes icon based on state)
// Flip/Menu button (changes icon based on state)
                Box(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        // Get the position of the button's center relative to its parent.
                        // The parent is the Column aligned to the TopEnd.
                        val positionInParent = coordinates.positionInParent()

                        // The parent Column is offset, so we need to account for that.
                        // The Column is inside the main Box, so we can find its position
                        // relative to the root of the layout.
                        val rootPosition = coordinates.positionInRoot()

                        // Since the main Box is the root of this component's layout,
                        // positionInRoot() gives us the coordinates relative to it.
                        buttonPosition = Offset(
                            rootPosition.x + coordinates.size.width / 2f,
                            rootPosition.y + coordinates.size.height / 2f
                        )
                    }
                )
 {
                    CircularButton(
                        icon = if (isFlipped) Icons.Default.Close else Icons.Default.Restaurant,
                        onClick = {
                            isAnimating = true
                            isFlipped = !isFlipped
                        },
                        backgroundColor = buttonBgColor,
                        iconColor = buttonIconColor,
                        size = 40.dp
                    )
                }
            }
        }
    }
    
    // Reset animation state after completion
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(800)
            isAnimating = false
        }
    }
}

@Composable
private fun CardContent(
    isFlipped: Boolean,
    cardColor: Color,
    badgeColor: Color,
    badgeTextColor: Color,
    titleBadgeColor: Color,
    titleTextColor: Color,
    priceRange: String,
    timeRange: String,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        // Large image/content area on the left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.75f)
                .background(
                    cardColor,
                    RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        ) {
            // Overlay badges in top-left
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                // Price badge
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = priceRange,
                        color = badgeTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Time badge
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = timeRange,
                        color = badgeTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Title badge at bottom-center
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-16).dp)
                    .background(titleBadgeColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    color = titleTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
