# Data Model Analysis & UML Diagram

## UML Diagram (Text Format)

```
┌─────────────────────────────────────────────────────────────┐
│                         School                               │
├─────────────────────────────────────────────────────────────┤
│ + id: UUID                                                  │
│ + name: String                                              │
│ + svg: String                                               │
│ + createdAt: TIME                                           │
├─────────────────────────────────────────────────────────────┤
│ + getClubs(): List<Club>                                    │
│ + getActiveListings(): List<CardListing>                    │
│ + getListingsByDate(date: Date): List<CardListing>         │
│ + getUpcomingListings(count: Int): List<CardListing>       │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │ 1
                          │
                          │ *
┌─────────────────────────────────────────────────────────────┐
│                          Club                                │
├─────────────────────────────────────────────────────────────┤
│ + id: UUID                                                  │
│ + name: String                                              │
│ + schoolId: UUID (FK)                                       │
│ + createdAt: TIME                                           │
├─────────────────────────────────────────────────────────────┤
│ + getListings(): List<CardListing>                         │
│ + getActiveListings(): List<CardListing>                   │
│ + getListingsCount(): Int                                   │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │ 1
                          │
                          │ *
┌─────────────────────────────────────────────────────────────┐
│                      CardListing                             │
├─────────────────────────────────────────────────────────────┤
│ + id: UUID                                                  │
│ + createdAt: TIME                                           │
│ + schoolId: UUID (FK)                                       │
│ + title: String                                             │
│ + room: String?                                             │
│ + address: String                                           │
│ + dietRestrictions: List<String>?                           │
│ + timeStart: TIME                                           │
│ + timeEnd: TIME                                             │
│ + costDown: Float?                                          │
│ + costUp: Float?                                             │
│ + fixedCost: Float?                                         │
│ + description: String?                                      │
│ + clubId: UUID? (FK, optional)                              │
│ + userId: UUID (FK)                                         │
├─────────────────────────────────────────────────────────────┤
│ + isFree(): Boolean                                         │
│ + hasFixedPrice(): Boolean                                  │
│ + hasPriceRange(): Boolean                                  │
│ + getFormattedPrice(): String                               │
│ + getPriceRange(): Pair<Float, Float>?                      │
│ + isActive(): Boolean                                       │
│ + isUpcoming(): Boolean                                     │
│ + isPast(): Boolean                                         │
│ + getDuration(): Duration                                   │
│ + getFormattedTimeRange(): String                           │
│ + isValid(): Boolean                                        │
│ + hasDietRestriction(diet: String): Boolean                 │
│ + matchesDietRestrictions(userRestrictions: List<String>): │
│   Boolean                                                   │
│ + getFullLocation(): String                                 │
└─────────────────────────────────────────────────────────────┘
        ▲                              │
        │ 1                            │ 1
        │                              │
        │ *                            │ *
┌─────────────────────────────────────────────────────────────┐
│                     Notification                            │
├─────────────────────────────────────────────────────────────┤
│ + id: UUID                                                  │
│ + time: TIME                                                │
│ + cardListingId: UUID (FK)                                 │
│ + userId: UUID (FK)                                         │
├─────────────────────────────────────────────────────────────┤
│ + isOverdue(): Boolean                                      │
│ + isUpcoming(): Boolean                                     │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │ *
                          │
                          │ 1
┌─────────────────────────────────────────────────────────────┐
│                          User                                │
├─────────────────────────────────────────────────────────────┤
│ + id: UUID                                                  │
│ + createdAt: TIME                                           │
│ + macAddr: String                                           │
│ - subscribedListings: Set<UUID> (private)                   │
├─────────────────────────────────────────────────────────────┤
│ + subscribeToListing(cardListingId: UUID): Boolean          │
│ + unsubscribeFromListing(cardListingId: UUID): Boolean      │
│ + isSubscribedToListing(cardListingId: UUID): Boolean      │
│ + getSubscribedListings(): Set<UUID>                        │
│ + getSubscribedListingsCount(): Int                         │
│ + clearAllSubscriptions(): Unit                             │
└─────────────────────────────────────────────────────────────┘
```

## Detailed Analysis

### 1. **CardListing** (Main Entity)
**Purpose**: Represents food/event listings at schools

**Strengths**:
- Comprehensive fields covering location, time, cost, and dietary needs
- Good use of nullable fields for optional data
- Supports both range pricing (CostDown/CostUp) and fixed pricing

**Issues & Improvements**:
1. **Pricing Logic** ✅ Clear design:
   - Either `fixedCost` is set (single price)
   - OR `costDown` + `costUp` are set (price range)
   - OR neither (free)
   - Fields are mutually exclusive by design
   - Validation: Ensure fixedCost and range pricing are not both set

2. **Missing Fields** (Optional):
   - `updatedAt: TIME` - for tracking modifications
   - `status: Enum` - ACTIVE, CANCELLED, EXPIRED
   - `capacity: Int?` - if event has limited spots
   - `tags: List<String>` - for categorization/search

3. **Naming Inconsistencies**:
   - Use `dietRestrictions` not `DietRest`
   - Use `timeStart`/`timeEnd` consistently

### 2. **Club**
**Purpose**: Organizations within schools that host events

**Strengths**:
- Simple, focused entity
- Clear relationship to School

**Issues & Improvements**:
1. **Missing Fields**:
   - `description: String?` - what the club does
   - `contactInfo: String?` - email/social media
   - `logoUrl: String?` - visual identity
   - `active: Boolean` - soft delete support

2. **No relationship tracking**: Add a computed property for CardListings count

### 3. **School**
**Purpose**: Educational institutions

**Strengths**:
- Basic structure is sound
- Includes SVG for logo/visual representation
- ✅ `createdAt: TIME` (fixed from original STR)

**Issues & Improvements**:
1. **Missing Fields** (Optional):
   - `location: Address?` - physical address
   - `website: String?` - official site
   - `active: Boolean` - for soft deletes

3. **SVG Storage**: Consider if SVG should be:
   - Stored as text in DB (current approach)
   - Stored as URL reference (better for large files)
   - Stored as file path (if hosting locally)

### 4. **User**
**Purpose**: Application users (privacy-focused, no personal information)

**Design Philosophy** ✅:
- **Privacy-first approach**: No personal information collected (email, name, phone)
- **Device-based identification**: Uses MAC address for user tracking
- **Intentional minimalism**: Only essential fields for functionality

**Field Design**:
1. **macAddr**: Device identifier for privacy-preserving user tracking
   - This is intentional - no personal data collection
   - MAC address identifies the device, not the person

2. **subscribedListings**: Private field (Set<UUID>)
   - ✅ **Controlled access**: Users cannot directly modify this field
   - ✅ **Accessor methods**: Use functions like `subscribeToListing()`, `unsubscribeFromListing()`
   - ✅ **Encapsulation**: Prevents unauthorized modifications
   - Stores IDs of CardListings the user wants notifications for

3. **CreatedAt**: Consistent with other entities (TIME type)

**Suggested Helper Functions** (see UML diagram):
- `subscribeToListing(cardListingId: UUID)` - Adds subscription
- `unsubscribeFromListing(cardListingId: UUID)` - Removes subscription
- `isSubscribedToListing(cardListingId: UUID)` - Checks subscription status
- `getSubscribedListings()` - Returns set of subscribed listing IDs

### 5. **Notification**
**Purpose**: User notifications about CardListings

**Fields** ✅:
1. ✅ **userId: UUID (FK)** - Added as requested
   - Links notification to the user it belongs to
   - Enables querying: "Get all notifications for this user"

2. **Existing fields**:
   - `id: UUID` - Unique identifier
   - `time: TIME` - When the notification should be delivered
   - `cardListingId: UUID (FK)` - Which CardListing the notification is about

**Design Notes**:
- Simple, focused entity - matches your minimal approach
- Can be extended later if needed (read status, types, etc.)
- Relationship: User has many Notifications, Notification belongs to one User and one CardListing

## Suggested Helper Functions/Extensions

### For CardListing:
1. **Pricing Helpers**:
   ```kotlin
   - isFree(): Boolean
   - getPriceRange(): PriceRange?
   - getFormattedPrice(): String
   - hasFixedPrice(): Boolean
   ```

2. **Time Helpers**:
   ```kotlin
   - isActive(): Boolean (current time between start/end)
   - isUpcoming(): Boolean
   - isPast(): Boolean
   - getDuration(): Duration
   - getFormattedTimeRange(): String
   ```

3. **Validation**:
   ```kotlin
   - isValid(): Boolean (checks all required fields)
   - hasDietRestriction(diet: String): Boolean
   ```

### For School:
1. **Relationship Helpers**:
   ```kotlin
   - getClubs(): List<Club>
   - getActiveListings(): List<CardListing>
   ```

### For User:
1. **Subscription Management** (see UML diagram):
   ```kotlin
   - subscribeToListing(cardListingId: UUID): Boolean
   - unsubscribeFromListing(cardListingId: UUID): Boolean
   - isSubscribedToListing(cardListingId: UUID): Boolean
   - getSubscribedListings(): Set<UUID>
   - getSubscribedListingsCount(): Int
   - clearAllSubscriptions(): Unit
   ```
   Note: These functions control access to the private `subscribedListings` field

### For Notification:
1. **Time Helpers** (see UML diagram):
   ```kotlin
   - isOverdue(): Boolean (if time has passed)
   - isUpcoming(): Boolean (if time is in the future)
   ```

## Architectural Improvements

### 1. **Pricing Logic** ✅
Your current approach is good - mutually exclusive optional fields:
- `fixedCost: Float?` OR `costDown: Float?` + `costUp: Float?` OR neither (free)
- Add validation in your ORM to ensure only one pricing type is set
- Helper functions in CardListing will make this easy to work with

### 2. **Soft Deletes** (Optional)
Add `deletedAt: TIME?` to all entities for soft delete support instead of hard deletes.

### 3. **Audit Trail**
Add `updatedAt: TIME` to all entities for change tracking.

### 4. **Enums for Status Fields**
- CardListing.status: ACTIVE, CANCELLED, EXPIRED
- Notification.type: REMINDER, UPDATE, NEW_LISTING
- Notification.read: Boolean (but enum could be UNREAD, READ, ARCHIVED)

### 5. **Indexes for Performance**
Consider database indexes on:
- CardListing: schoolId, userId, timeStart, timeEnd
- Notification: userId, cardListingId, time
- Club: schoolId

### 6. **Data Validation Rules**
Document business rules:
- `timeEnd` must be after `timeStart`
- `costUp` must be >= `costDown` (if both present)
- ✅ `fixedCost` and range pricing (`costDown` + `costUp`) are mutually exclusive:
  - Either `fixedCost` is set AND `costDown` + `costUp` are null
  - OR `costDown` + `costUp` are set AND `fixedCost` is null
  - OR all three are null (free)

## Relationship Summary

```
School (1) ──→ (*) Club
School (1) ──→ (*) CardListing
Club (1) ──→ (*) CardListing (optional)
User (1) ──→ (*) CardListing (creator)
User (1) ──→ (*) Notification
CardListing (1) ──→ (*) Notification
```

## Updates Applied ✅

1. ✅ **Added `userId: UUID` to Notification entity** - Now properly linked to users
2. ✅ **Fixed School.createdAt type** - Changed from STR to TIME (as noted in requirements)
3. ✅ **Pricing logic clarified** - Mutually exclusive: fixedCost OR (costDown + costUp) OR neither
4. ✅ **User entity design validated** - Minimal by design for privacy, MAC address for device tracking
5. ✅ **SubscribedListings as private field** - Controlled via methods, users can't directly edit
6. ✅ **Standardized time types** - All use TIME consistently

All critical fixes have been addressed based on your design requirements.

## Recommended Additional Entities

1. **UserPreferences**: Separate entity for user settings
   - dietary restrictions
   - notification preferences
   - subscribed schools/clubs

2. **Tag/Category**: For categorizing CardListings
   - breakfast, lunch, dinner, snack
   - vegan, gluten-free, etc.

3. **Rating/Review**: Users can rate CardListings
   - rating: Int (1-5)
   - comment: String?

## Summary

Your model structure is well-designed with a clear privacy-first approach:

✅ **Strengths**:
- Clean, focused entities without unnecessary complexity
- Privacy-preserving User model (MAC address only, no personal data)
- Mutually exclusive pricing fields (fixedCost OR range, not both)
- Encapsulated subscription management via private field + methods
- Proper relationships with foreign keys

✅ **Design Decisions Validated**:
- User entity intentionally minimal (privacy-focused)
- SubscribedListings as private field with controlled access
- Pricing as simple optional fields (mutually exclusive by validation)
- Notification now includes userId for proper relationship tracking

**UML Diagram** shows all suggested helper functions for each class to make working with these entities easier and more maintainable.

The model is ready for ORM implementation with Supabase!


