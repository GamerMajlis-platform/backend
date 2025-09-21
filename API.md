# GamerMajlis Backend API Documentation

**Base URL:** `http://localhost:8080/api`

All authenticated endpoints require the `Authorization` header:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🔐 Authentication APIs

### 1. User Registration
**API:** `POST /api/auth/signup`

**Body (form-data):**
```
email: user@example.com
password: password123
displayName: GamerUser
```

**Expected Output:**
```json
{
  "success": true,
  "message": "User registered successfully. Please check your email for verification.",
  "userId": 1
}
```

### 2. User Login  
**API:** `POST /api/auth/login`

**Body (form-data):**
```
identifier: user@example.com (or displayName)
password: password123
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "displayName": "GamerUser",
    "email": "user@example.com",
    "roles": ["REGULAR_GAMER"]
  }
}
```

### 3. User Logout
**API:** `POST /api/auth/logout`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

### 4. Email Verification
**API:** `GET /api/auth/verify-email?token={verification_token}`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Email verified successfully"
}
```

### 5. Resend Verification Email
**API:** `POST /api/auth/resend-verification`

**Body (form-data):**
```
email: user@example.com
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Verification email sent successfully"
}
```

### 6. Get Current User
**API:** `GET /api/auth/me`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "user": {
    "id": 1,
    "displayName": "GamerUser",
    "email": "user@example.com",
    "bio": "Gaming enthusiast",
    "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg",
    "roles": ["REGULAR_GAMER"],
    "emailVerified": true,
    "createdAt": "2024-12-21T10:00:00"
  }
}
```

### 7. Validate Token
**API:** `GET /api/auth/validate-token`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "valid": true,
  "userId": 1,
  "username": "GamerUser",
  "message": "Token is valid"
}
```

---

## 👤 Profile Management APIs

### 8. Get My Profile
**API:** `GET /api/profile/me`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "user": {
    "id": 1,
    "displayName": "GamerUser",
    "email": "user@example.com",
    "bio": "Gaming enthusiast",
    "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg",
    "gamingPreferences": "{\"favoriteGames\": [\"Valorant\", \"CS2\"]}",
    "socialLinks": "{\"twitter\": \"@gameruser\"}",
    "gamingStatistics": "{\"totalGames\": 150, \"winRate\": 65}",
    "roles": ["REGULAR_GAMER"],
    "discordUsername": "GamerUser#1234",
    "lastLogin": "2024-12-21T09:30:00",
    "createdAt": "2024-12-21T10:00:00",
    "privacySettings": "{\"profileVisible\": true}",
    "emailVerified": true
  }
}
```

### 9. Get User Profile by ID
**API:** `GET /api/profile/{userId}`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "user": {
    "id": 2,
    "displayName": "ProGamer",
    "bio": "Professional esports player",
    "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg",
    "gamingPreferences": "{\"favoriteGames\": [\"Valorant\"]}",
    "socialLinks": "{\"twitch\": \"progamer_tv\"}",
    "gamingStatistics": "{\"totalGames\": 500, \"winRate\": 80}",
    "roles": ["TOURNAMENT_ORGANIZER"],
    "discordUsername": "ProGamer#5678",
    "createdAt": "2024-12-20T15:00:00"
  }
}
```

### 10. Update Profile
**API:** `PUT /api/profile/me`

**Body (form-data):**
```
displayName: UpdatedGamerUser (optional)
bio: Updated bio text (optional)
gamingPreferences: {"favoriteGames": ["Valorant", "Apex"]} (optional)
socialLinks: {"twitter": "@updateduser"} (optional)
privacySettings: {"profileVisible": false} (optional)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "user": {
    "id": 1,
    "displayName": "UpdatedGamerUser",
    "bio": "Updated bio text",
    // ... other updated fields
  }
}
```

### 11. Upload Profile Picture
**API:** `POST /api/profile/me/profile-picture`

**Body (multipart/form-data):**
```
file: [image file - JPG/PNG/GIF, max 10MB]
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Profile picture uploaded successfully",
  "profilePictureUrl": "/uploads/profile-pictures/profile_1_1703155200000.jpg"
}
```

### 12. Remove Profile Picture
**API:** `DELETE /api/profile/me/profile-picture`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Profile picture removed successfully"
}
```

### 13. Update Gaming Statistics
**API:** `POST /api/profile/me/gaming-stats`

**Body (form-data):**
```
gamingStatistics: {"totalGames": 200, "winRate": 70, "favoriteMap": "Dust2"}
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Gaming statistics updated successfully",
  "gamingStatistics": "{\"totalGames\": 200, \"winRate\": 70, \"favoriteMap\": \"Dust2\"}"
}
```

### 14. Search Profiles
**API:** `GET /api/profile/search?query=gamer&page=0&size=20`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Profiles found",
  "profiles": [
    {
      "id": 1,
      "displayName": "GamerUser",
      "bio": "Gaming enthusiast",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg",
      "roles": ["REGULAR_GAMER"],
      "createdAt": "2024-12-21T10:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 15. Get Profile Suggestions
**API:** `GET /api/profile/suggestions?limit=10`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Profile suggestions retrieved",
  "suggestions": [
    {
      "id": 3,
      "displayName": "SkillfulGamer",
      "bio": "Competitive player",
      "profilePictureUrl": "/uploads/profile-pictures/profile_3.jpg"
    }
  ]
}
```

---

## 📁 Media Management APIs

### 16. Upload Media
**API:** `POST /api/media/upload`

**Body (multipart/form-data):**
```
file: [media file - MP4/AVI/MOV/JPG/PNG/GIF, max 100MB for videos, 10MB for images]
title: My Gaming Clip
description: Amazing clutch moment (optional)
tags: ["gaming", "clutch", "valorant"] (optional)
gameCategory: FPS (optional)
visibility: PUBLIC (optional, default: PUBLIC)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Media uploaded successfully",
  "media": {
    "id": 1,
    "title": "My Gaming Clip",
    "description": "Amazing clutch moment",
    "originalFilename": "clip.mp4",
    "storedFilename": "media_1_1703155200000.mp4",
    "filePath": "/uploads/media/media_1_1703155200000.mp4",
    "mediaType": "VIDEO",
    "fileSize": 15728640,
    "compressedSize": 10485760,
    "compressionRatio": 0.67,
    "thumbnailPath": "/uploads/media/thumbnails/thumb_1.jpg",
    "duration": 30,
    "resolution": "1920x1080",
    "tags": "[\"gaming\", \"clutch\", \"valorant\"]",
    "gameCategory": "FPS",
    "visibility": "PUBLIC",
    "viewCount": 0,
    "downloadCount": 0,
    "createdAt": "2024-12-21T10:30:00"
  }
}
```

### 17. Get Media Details
**API:** `GET /api/media/{mediaId}`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Media retrieved successfully",
  "media": {
    "id": 1,
    "title": "My Gaming Clip",
    "description": "Amazing clutch moment",
    "filePath": "/uploads/media/media_1_1703155200000.mp4",
    "mediaType": "VIDEO",
    "fileSize": 15728640,
    "duration": 30,
    "resolution": "1920x1080",
    "thumbnailPath": "/uploads/media/thumbnails/thumb_1.jpg",
    "tags": "[\"gaming\", \"clutch\", \"valorant\"]",
    "gameCategory": "FPS",
    "visibility": "PUBLIC",
    "viewCount": 5,
    "downloadCount": 2,
    "uploader": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "createdAt": "2024-12-21T10:30:00"
  }
}
```

### 18. Get Media List
**API:** `GET /api/media?page=0&size=20&category=FPS&type=VIDEO&visibility=PUBLIC&myMedia=false`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Media list retrieved",
  "media": [
    {
      "id": 1,
      "title": "My Gaming Clip",
      "thumbnailPath": "/uploads/media/thumbnails/thumb_1.jpg",
      "mediaType": "VIDEO",
      "duration": 30,
      "gameCategory": "FPS",
      "viewCount": 5,
      "uploader": {
        "id": 1,
        "displayName": "GamerUser"
      },
      "createdAt": "2024-12-21T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 19. Update Media
**API:** `PUT /api/media/{mediaId}`

**Body (form-data):**
```
title: Updated Gaming Clip (optional)
description: Updated amazing clutch moment (optional)
tags: ["gaming", "clutch", "valorant", "updated"] (optional)
gameCategory: MOBA (optional)
visibility: PRIVATE (optional)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Media updated successfully",
  "media": {
    "id": 1,
    "title": "Updated Gaming Clip",
    "description": "Updated amazing clutch moment",
    // ... other fields
  }
}
```

### 20. Delete Media
**API:** `DELETE /api/media/{mediaId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Media deleted successfully"
}
```

### 21. Increment Media View Count
**API:** `POST /api/media/{mediaId}/view`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "View count updated",
  "newViewCount": 6
}
```

### 22. Search Media
**API:** `GET /api/media/search?query=gaming&page=0&size=20&type=VIDEO`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Media search completed",
  "media": [
    {
      "id": 1,
      "title": "My Gaming Clip",
      "thumbnailPath": "/uploads/media/thumbnails/thumb_1.jpg",
      "mediaType": "VIDEO",
      "gameCategory": "FPS",
      "viewCount": 5,
      "createdAt": "2024-12-21T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 23. Get Trending Media
**API:** `GET /api/media/trending?limit=10&days=7`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Trending media retrieved",
  "media": [
    {
      "id": 2,
      "title": "Epic Gaming Moments",
      "thumbnailPath": "/uploads/media/thumbnails/thumb_2.jpg",
      "mediaType": "VIDEO",
      "gameCategory": "FPS",
      "viewCount": 150,
      "createdAt": "2024-12-20T15:00:00"
    }
  ]
}
```

---

## 📝 Post Management APIs

### 24. Create Post
**API:** `POST /api/posts`

**Body (form-data):**
```
title: My Gaming Experience
content: Just had an amazing gaming session with my squad!
type: TEXT (optional, default: TEXT)
gameTitle: Valorant (optional)
gameCategory: FPS (optional)
platform: PC (optional)
tags: ["gaming", "squad", "fun"] (optional)
hashtags: ["#gaming", "#valorant"] (optional)
mediaIds: [1, 2] (optional - array of media IDs to attach)
visibility: PUBLIC (optional, default: PUBLIC)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Post created successfully",
  "post": {
    "id": 1,
    "title": "My Gaming Experience",
    "content": "Just had an amazing gaming session with my squad!",
    "type": "TEXT",
    "gameTitle": "Valorant",
    "gameCategory": "FPS",
    "platform": "PC",
    "tags": "[\"gaming\", \"squad\", \"fun\"]",
    "hashtags": "[\"#gaming\", \"#valorant\"]",
    "visibility": "PUBLIC",
    "viewCount": 0,
    "likeCount": 0,
    "commentCount": 0,
    "shareCount": 0,
    "attachedMedia": [
      {
        "id": 1,
        "title": "My Gaming Clip",
        "thumbnailPath": "/uploads/media/thumbnails/thumb_1.jpg"
      }
    ],
    "author": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "createdAt": "2024-12-21T11:00:00"
  }
}
```

### 25. Get Post Details
**API:** `GET /api/posts/{postId}`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Post retrieved successfully",
  "post": {
    "id": 1,
    "title": "My Gaming Experience",
    "content": "Just had an amazing gaming session with my squad!",
    "type": "TEXT",
    "gameTitle": "Valorant",
    "gameCategory": "FPS",
    "platform": "PC",
    "tags": "[\"gaming\", \"squad\", \"fun\"]",
    "hashtags": "[\"#gaming\", \"#valorant\"]",
    "visibility": "PUBLIC",
    "viewCount": 15,
    "likeCount": 5,
    "commentCount": 3,
    "shareCount": 1,
    "attachedMedia": [],
    "author": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "createdAt": "2024-12-21T11:00:00",
    "updatedAt": "2024-12-21T11:05:00"
  }
}
```

### 26. Get Posts Feed
**API:** `GET /api/posts?page=0&size=20&gameCategory=FPS&type=TEXT&myPosts=false`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Posts feed retrieved",
  "posts": [
    {
      "id": 1,
      "title": "My Gaming Experience",
      "content": "Just had an amazing gaming session with my squad!",
      "gameCategory": "FPS",
      "likeCount": 5,
      "commentCount": 3,
      "author": {
        "id": 1,
        "displayName": "GamerUser",
        "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
      },
      "createdAt": "2024-12-21T11:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 27. Update Post
**API:** `PUT /api/posts/{postId}`

**Body (form-data):**
```
title: Updated Gaming Experience (optional)
content: Updated content about my gaming session (optional)
gameTitle: CS2 (optional)
gameCategory: FPS (optional)
platform: Steam (optional)
tags: ["gaming", "updated"] (optional)
hashtags: ["#updated"] (optional)
visibility: PRIVATE (optional)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Post updated successfully",
  "post": {
    "id": 1,
    "title": "Updated Gaming Experience",
    "content": "Updated content about my gaming session",
    // ... other updated fields
  }
}
```

### 28. Delete Post
**API:** `DELETE /api/posts/{postId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Post deleted successfully"
}
```

### 29. Like/Unlike Post
**API:** `POST /api/posts/{postId}/like`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Post liked successfully",
  "liked": true,
  "newLikeCount": 6
}
```

### 30. Add Comment to Post
**API:** `POST /api/posts/{postId}/comments`

**Body (form-data):**
```
content: Great post! Love your gaming content.
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Comment added successfully",
  "comment": {
    "id": 1,
    "content": "Great post! Love your gaming content.",
    "author": {
      "id": 2,
      "displayName": "CommenterUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
    },
    "createdAt": "2024-12-21T11:30:00"
  }
}
```

### 31. Get Post Comments
**API:** `GET /api/posts/{postId}/comments?page=0&size=20`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Comments retrieved successfully",
  "comments": [
    {
      "id": 1,
      "content": "Great post! Love your gaming content.",
      "author": {
        "id": 2,
        "displayName": "CommenterUser",
        "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
      },
      "createdAt": "2024-12-21T11:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 32. Delete Comment
**API:** `DELETE /api/posts/comments/{commentId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

### 33. Share Post
**API:** `POST /api/posts/{postId}/share`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Post shared successfully",
  "newShareCount": 2
}
```

### 34. Get Trending Posts
**API:** `GET /api/posts/trending?limit=10&days=7`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Trending posts retrieved",
  "posts": [
    {
      "id": 3,
      "title": "Epic Gaming Montage",
      "content": "Check out my best plays!",
      "likeCount": 50,
      "commentCount": 15,
      "shareCount": 8,
      "author": {
        "id": 3,
        "displayName": "ProGamer"
      },
      "createdAt": "2024-12-20T18:00:00"
    }
  ]
}
```

### 35. Search Posts
**API:** `GET /api/posts/search?query=gaming&page=0&size=20&gameCategory=FPS`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Posts search completed",
  "posts": [
    {
      "id": 1,
      "title": "My Gaming Experience",
      "content": "Just had an amazing gaming session with my squad!",
      "gameCategory": "FPS",
      "likeCount": 5,
      "author": {
        "id": 1,
        "displayName": "GamerUser"
      },
      "createdAt": "2024-12-21T11:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

---

## 🎉 Event Management APIs

### 36. Create Event
**API:** `POST /api/events`

**Body (form-data):**
```
title: Gaming Championship 2024
description: Annual gaming championship featuring multiple games
startDateTime: 2024-12-25T18:00:00
endDateTime: 2024-12-25T22:00:00 (optional)
eventType: TOURNAMENT (optional, default: COMMUNITY_GATHERING)
locationType: VIRTUAL (optional, default: VIRTUAL)
virtualLink: https://discord.gg/gaming (optional)
virtualPlatform: Discord (optional)
physicalAddress: 123 Gaming Street (optional)
physicalVenue: Gaming Arena (optional)
maxAttendees: 100 (optional)
requiresRegistration: true (optional, default: true)
registrationDeadline: 2024-12-24T18:00:00 (optional)
registrationRequirements: Must be 16+ years old (optional)
isPublic: true (optional, default: true)
gameTitle: Valorant (optional)
gameCategory: FPS (optional)
competitive: true (optional, default: false)
entryFee: 10.00 (optional)
ageRestriction: 16 (optional)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Event created successfully",
  "event": {
    "id": 1,
    "title": "Gaming Championship 2024",
    "description": "Annual gaming championship featuring multiple games",
    "startDateTime": "2024-12-25T18:00:00",
    "endDateTime": "2024-12-25T22:00:00",
    "eventType": "TOURNAMENT",
    "locationType": "VIRTUAL",
    "virtualLink": "https://discord.gg/gaming",
    "virtualPlatform": "Discord",
    "maxAttendees": 100,
    "currentAttendees": 0,
    "requiresRegistration": true,
    "registrationDeadline": "2024-12-24T18:00:00",
    "registrationRequirements": "Must be 16+ years old",
    "isPublic": true,
    "gameTitle": "Valorant",
    "gameCategory": "FPS",
    "competitive": true,
    "entryFee": 10.00,
    "currency": "USD",
    "ageRestriction": 16,
    "status": "DRAFT",
    "organizer": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "viewCount": 0,
    "interestedCount": 0,
    "createdAt": "2024-12-21T12:00:00"
  }
}
```

### 37. Get Event Details
**API:** `GET /api/events/{eventId}`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Event retrieved successfully",
  "event": {
    "id": 1,
    "title": "Gaming Championship 2024",
    "description": "Annual gaming championship featuring multiple games",
    "startDateTime": "2024-12-25T18:00:00",
    "endDateTime": "2024-12-25T22:00:00",
    "eventType": "TOURNAMENT",
    "locationType": "VIRTUAL",
    "virtualLink": "https://discord.gg/gaming",
    "virtualPlatform": "Discord",
    "maxAttendees": 100,
    "currentAttendees": 25,
    "requiresRegistration": true,
    "isPublic": true,
    "gameTitle": "Valorant",
    "gameCategory": "FPS",
    "competitive": true,
    "entryFee": 10.00,
    "currency": "USD",
    "ageRestriction": 16,
    "status": "REGISTRATION_OPEN",
    "organizer": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "viewCount": 50,
    "interestedCount": 15,
    "createdAt": "2024-12-21T12:00:00",
    "updatedAt": "2024-12-21T12:30:00"
  }
}
```

### 38. Get Events List
**API:** `GET /api/events?page=0&size=20&eventType=TOURNAMENT&gameCategory=FPS&locationType=VIRTUAL&myEvents=false&upcoming=true`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Events list retrieved",
  "events": [
    {
      "id": 1,
      "title": "Gaming Championship 2024",
      "description": "Annual gaming championship featuring multiple games",
      "startDateTime": "2024-12-25T18:00:00",
      "eventType": "TOURNAMENT",
      "locationType": "VIRTUAL",
      "gameCategory": "FPS",
      "currentAttendees": 25,
      "maxAttendees": 100,
      "competitive": true,
      "organizer": {
        "id": 1,
        "displayName": "GamerUser"
      },
      "createdAt": "2024-12-21T12:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 39. Update Event
**API:** `PUT /api/events/{eventId}`

**Body (form-data):**
```
title: Updated Gaming Championship 2024 (optional)
description: Updated description (optional)
startDateTime: 2024-12-25T19:00:00 (optional)
maxAttendees: 150 (optional)
// ... other optional fields
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Event updated successfully",
  "event": {
    "id": 1,
    "title": "Updated Gaming Championship 2024",
    "startDateTime": "2024-12-25T19:00:00",
    "maxAttendees": 150,
    // ... other fields
  }
}
```

### 40. Delete Event
**API:** `DELETE /api/events/{eventId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Event deleted successfully"
}
```

### 41. Register for Event
**API:** `POST /api/events/{eventId}/register`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Successfully registered for event",
  "attendance": {
    "id": 1,
    "eventId": 1,
    "userId": 2,
    "status": "REGISTERED",
    "registeredAt": "2024-12-21T13:00:00"
  }
}
```

### 42. Unregister from Event
**API:** `POST /api/events/{eventId}/unregister`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Successfully unregistered from event"
}
```

### 43. Get Event Attendees
**API:** `GET /api/events/{eventId}/attendees?page=0&size=20`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Event attendees retrieved",
  "attendees": [
    {
      "id": 1,
      "user": {
        "id": 2,
        "displayName": "AttendeeUser",
        "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
      },
      "status": "REGISTERED",
      "registeredAt": "2024-12-21T13:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 44. Check-in to Event
**API:** `POST /api/events/{eventId}/check-in`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Successfully checked in to event",
  "checkedInAt": "2024-12-25T17:55:00"
}
```

### 45. Search Events
**API:** `GET /api/events/search?query=gaming&page=0&size=20&eventType=TOURNAMENT&locationType=VIRTUAL`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Events search completed",
  "events": [
    {
      "id": 1,
      "title": "Gaming Championship 2024",
      "description": "Annual gaming championship featuring multiple games",
      "startDateTime": "2024-12-25T18:00:00",
      "eventType": "TOURNAMENT",
      "locationType": "VIRTUAL",
      "currentAttendees": 25,
      "maxAttendees": 100,
      "organizer": {
        "id": 1,
        "displayName": "GamerUser"
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 46. Get Trending Events
**API:** `GET /api/events/trending?limit=10`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Trending events retrieved",
  "events": [
    {
      "id": 2,
      "title": "Mega Gaming Tournament",
      "startDateTime": "2024-12-30T20:00:00",
      "eventType": "TOURNAMENT",
      "currentAttendees": 150,
      "maxAttendees": 200,
      "competitive": true,
      "organizer": {
        "id": 3,
        "displayName": "TournamentOrg"
      }
    }
  ]
}
```

---

## 🛒 Marketplace APIs

### 47. Create Product Listing
**API:** `POST /api/products`

**Body (form-data):**
```
name: Gaming Headset Pro
description: High-quality gaming headset with 7.1 surround sound
price: 99.99
currency: USD (optional, default: USD)
category: ACCESSORIES
subcategory: Headsets (optional)
condition: NEW
conditionDescription: Brand new in box (optional)
brand: SteelSeries (optional)
model: Arctis 7 (optional)
gameCompatibility: ["PC", "PlayStation", "Xbox"] (optional)
quantityAvailable: 5 (optional, default: 1)
shippingMethod: STANDARD (optional, default: STANDARD)
shippingCost: 5.99 (optional, default: 0)
freeShipping: false (optional, default: false)
shippingRegions: ["US", "CA"] (optional)
estimatedDeliveryDays: 3 (optional)
specifications: {"drivers": "40mm", "frequency": "20Hz-20kHz"} (optional)
dimensions: 20x15x10 cm (optional)
weight: 350.5 (optional)
color: Black (optional)
tags: ["gaming", "headset", "wireless"] (optional)
returnPolicy: 30-day return policy (optional)
warrantyPeriodDays: 365 (optional)
warrantyDescription: 1-year manufacturer warranty (optional)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Product created successfully",
  "product": {
    "id": 1,
    "name": "Gaming Headset Pro",
    "description": "High-quality gaming headset with 7.1 surround sound",
    "price": 99.99,
    "currency": "USD",
    "category": "ACCESSORIES",
    "subcategory": "Headsets",
    "condition": "NEW",
    "conditionDescription": "Brand new in box",
    "brand": "SteelSeries",
    "model": "Arctis 7",
    "gameCompatibility": "[\"PC\", \"PlayStation\", \"Xbox\"]",
    "quantityAvailable": 5,
    "quantitySold": 0,
    "isAvailable": true,
    "status": "DRAFT",
    "seller": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "sellerVerified": false,
    "shippingMethod": "STANDARD",
    "shippingCost": 5.99,
    "freeShipping": false,
    "estimatedDeliveryDays": 3,
    "specifications": "{\"drivers\": \"40mm\", \"frequency\": \"20Hz-20kHz\"}",
    "dimensions": "20x15x10 cm",
    "weight": 350.5,
    "color": "Black",
    "tags": "[\"gaming\", \"headset\", \"wireless\"]",
    "averageRating": 0.0,
    "totalReviews": 0,
    "viewCount": 0,
    "wishlistCount": 0,
    "inquiryCount": 0,
    "returnPolicy": "30-day return policy",
    "warrantyPeriodDays": 365,
    "warrantyDescription": "1-year manufacturer warranty",
    "moderationStatus": "PENDING",
    "createdAt": "2024-12-21T14:00:00"
  }
}
```

### 48. Upload Product Images
**API:** `POST /api/products/{productId}/images`

**Body (multipart/form-data):**
```
images: [array of image files - JPG/PNG, max 5MB each]
setMainImage: true (optional, default: false)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Product images uploaded successfully",
  "images": [
    {
      "id": 1,
      "url": "/uploads/products/product_1_image_1.jpg",
      "isMainImage": true
    },
    {
      "id": 2,
      "url": "/uploads/products/product_1_image_2.jpg",
      "isMainImage": false
    }
  ]
}
```

### 49. Get Product Details
**API:** `GET /api/products/{productId}`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "product": {
    "id": 1,
    "name": "Gaming Headset Pro",
    "description": "High-quality gaming headset with 7.1 surround sound",
    "price": 99.99,
    "currency": "USD",
    "category": "ACCESSORIES",
    "condition": "NEW",
    "brand": "SteelSeries",
    "model": "Arctis 7",
    "quantityAvailable": 5,
    "isAvailable": true,
    "status": "ACTIVE",
    "seller": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg",
      "sellerVerified": true
    },
    "mainImageUrl": "/uploads/products/product_1_image_1.jpg",
    "imageUrls": [
      "/uploads/products/product_1_image_1.jpg",
      "/uploads/products/product_1_image_2.jpg"
    ],
    "shippingCost": 5.99,
    "freeShipping": false,
    "estimatedDeliveryDays": 3,
    "specifications": "{\"drivers\": \"40mm\", \"frequency\": \"20Hz-20kHz\"}",
    "averageRating": 4.5,
    "totalReviews": 10,
    "viewCount": 50,
    "wishlistCount": 8,
    "warrantyPeriodDays": 365,
    "returnPolicy": "30-day return policy",
    "createdAt": "2024-12-21T14:00:00",
    "listedAt": "2024-12-21T14:30:00"
  }
}
```

### 50. Get Products List
**API:** `GET /api/products?page=0&size=20&category=ACCESSORIES&condition=NEW&minPrice=50&maxPrice=200&brand=SteelSeries&sortBy=price&sortOrder=asc&myProducts=false`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Products list retrieved",
  "products": [
    {
      "id": 1,
      "name": "Gaming Headset Pro",
      "price": 99.99,
      "currency": "USD",
      "category": "ACCESSORIES",
      "condition": "NEW",
      "brand": "SteelSeries",
      "mainImageUrl": "/uploads/products/product_1_image_1.jpg",
      "averageRating": 4.5,
      "totalReviews": 10,
      "seller": {
        "id": 1,
        "displayName": "GamerUser"
      },
      "freeShipping": false,
      "shippingCost": 5.99,
      "createdAt": "2024-12-21T14:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 51. Update Product
**API:** `PUT /api/products/{productId}`

**Body (form-data):**
```
name: Updated Gaming Headset Pro (optional)
description: Updated high-quality gaming headset (optional)
price: 89.99 (optional)
condition: USED_LIKE_NEW (optional)
quantityAvailable: 3 (optional)
status: ACTIVE (optional)
// ... other optional fields
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Product updated successfully",
  "product": {
    "id": 1,
    "name": "Updated Gaming Headset Pro",
    "price": 89.99,
    "condition": "USED_LIKE_NEW",
    "quantityAvailable": 3,
    "status": "ACTIVE",
    // ... other fields
  }
}
```

### 52. Delete Product
**API:** `DELETE /api/products/{productId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Product deleted successfully"
}
```

### 53. Add Product Review
**API:** `POST /api/products/{productId}/reviews`

**Body (form-data):**
```
rating: 5 (required, 1-5)
comment: Excellent headset! Great sound quality and comfortable.
verified: true (optional, default: false)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Review added successfully",
  "review": {
    "id": 1,
    "rating": 5,
    "comment": "Excellent headset! Great sound quality and comfortable.",
    "verified": true,
    "reviewer": {
      "id": 2,
      "displayName": "ReviewerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
    },
    "createdAt": "2024-12-21T15:00:00"
  }
}
```

### 54. Get Product Reviews
**API:** `GET /api/products/{productId}/reviews?page=0&size=10`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Product reviews retrieved",
  "reviews": [
    {
      "id": 1,
      "rating": 5,
      "comment": "Excellent headset! Great sound quality and comfortable.",
      "verified": true,
      "reviewer": {
        "id": 2,
        "displayName": "ReviewerUser",
        "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
      },
      "createdAt": "2024-12-21T15:00:00"
    }
  ],
  "averageRating": 4.5,
  "totalReviews": 1,
  "ratingDistribution": {
    "5": 1,
    "4": 0,
    "3": 0,
    "2": 0,
    "1": 0
  },
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10
}
```

### 55. Toggle Product Wishlist
**API:** `POST /api/products/{productId}/wishlist`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Product added to wishlist",
  "inWishlist": true,
  "newWishlistCount": 9
}
```

### 56. Record Product View
**API:** `POST /api/products/{productId}/view`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "View recorded successfully",
  "newViewCount": 51
}
```

### 57. Search Products
**API:** `GET /api/products/search?query=gaming+headset&page=0&size=20&category=ACCESSORIES&minPrice=50&maxPrice=200`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Product search completed",
  "products": [
    {
      "id": 1,
      "name": "Gaming Headset Pro",
      "price": 99.99,
      "category": "ACCESSORIES",
      "brand": "SteelSeries",
      "mainImageUrl": "/uploads/products/product_1_image_1.jpg",
      "averageRating": 4.5,
      "totalReviews": 10,
      "seller": {
        "id": 1,
        "displayName": "GamerUser"
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 58. Get Product Categories
**API:** `GET /api/products/categories`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Categories retrieved successfully",
  "categories": [
    {
      "name": "ACCESSORIES",
      "displayName": "Gaming Accessories",
      "subcategories": ["Headsets", "Keyboards", "Mice", "Controllers"]
    },
    {
      "name": "HARDWARE",
      "displayName": "Gaming Hardware",
      "subcategories": ["Graphics Cards", "Processors", "Memory"]
    },
    {
      "name": "GAMES",
      "displayName": "Games",
      "subcategories": ["PC Games", "Console Games", "Digital Keys"]
    }
  ]
}
```

### 59. Get Featured Products
**API:** `GET /api/products/featured?limit=10`

**Body:** None

**Expected Output:**
```json
{
  "success": true,
  "message": "Featured products retrieved",
  "products": [
    {
      "id": 2,
      "name": "Gaming Laptop Elite",
      "price": 1299.99,
      "category": "HARDWARE",
      "mainImageUrl": "/uploads/products/product_2_image_1.jpg",
      "averageRating": 4.8,
      "totalReviews": 25,
      "isFeatured": true,
      "seller": {
        "id": 3,
        "displayName": "TechSeller"
      }
    }
  ]
}
```

---

## 💬 Chat System APIs

### 60. Create Chat Room
**API:** `POST /api/chat/rooms`

**Body (form-data):**
```
name: Gaming Squad Chat
description: Chat room for our gaming squad (optional)
type: GROUP (optional, default: GROUP)
isPrivate: false (optional, default: false)
maxMembers: 50 (optional)
gameTitle: Valorant (optional)
tournamentId: 1 (optional)
eventId: 1 (optional)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Chat room created successfully",
  "chatRoom": {
    "id": 1,
    "name": "Gaming Squad Chat",
    "description": "Chat room for our gaming squad",
    "type": "GROUP",
    "isPrivate": false,
    "maxMembers": 50,
    "currentMembers": 1,
    "gameTitle": "Valorant",
    "tournamentId": 1,
    "eventId": 1,
    "creator": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "isActive": true,
    "allowFileSharing": true,
    "allowEmojis": true,
    "messageHistoryDays": 30,
    "totalMessages": 0,
    "createdAt": "2024-12-21T16:00:00",
    "lastActivity": "2024-12-21T16:00:00"
  }
}
```

### 61. Get User Chat Rooms
**API:** `GET /api/chat/rooms?page=0&size=20`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Chat rooms retrieved successfully",
  "chatRooms": [
    {
      "id": 1,
      "name": "Gaming Squad Chat",
      "type": "GROUP",
      "isPrivate": false,
      "currentMembers": 5,
      "gameTitle": "Valorant",
      "lastActivity": "2024-12-21T16:30:00",
      "lastMessage": {
        "id": 10,
        "content": "GG everyone!",
        "sender": {
          "id": 2,
          "displayName": "TeamMate"
        },
        "createdAt": "2024-12-21T16:30:00"
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 62. Get Chat Room Details
**API:** `GET /api/chat/rooms/{roomId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Chat room details retrieved",
  "chatRoom": {
    "id": 1,
    "name": "Gaming Squad Chat",
    "description": "Chat room for our gaming squad",
    "type": "GROUP",
    "isPrivate": false,
    "maxMembers": 50,
    "currentMembers": 5,
    "gameTitle": "Valorant",
    "creator": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "moderatorIds": [1],
    "members": [
      {
        "id": 1,
        "user": {
          "id": 1,
          "displayName": "GamerUser",
          "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
        },
        "role": "ADMIN",
        "joinedAt": "2024-12-21T16:00:00"
      }
    ],
    "allowFileSharing": true,
    "allowEmojis": true,
    "slowModeSeconds": null,
    "totalMessages": 15,
    "createdAt": "2024-12-21T16:00:00",
    "lastActivity": "2024-12-21T16:30:00"
  }
}
```

### 63. Join Chat Room
**API:** `POST /api/chat/rooms/{roomId}/join`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Successfully joined chat room",
  "membership": {
    "id": 2,
    "user": {
      "id": 2,
      "displayName": "NewMember"
    },
    "role": "MEMBER",
    "joinedAt": "2024-12-21T17:00:00"
  }
}
```

### 64. Leave Chat Room
**API:** `POST /api/chat/rooms/{roomId}/leave`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Successfully left chat room"
}
```

### 65. Send Message
**API:** `POST /api/chat/rooms/{roomId}/messages`

**Body (multipart/form-data):**
```
content: Hello everyone! Ready for the tournament?
messageType: TEXT (optional, default: TEXT)
replyToMessageId: 5 (optional)
file: [optional file attachment - images/videos/audio/pdf/text, max 10MB]
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Message sent successfully",
  "chatMessage": {
    "id": 16,
    "content": "Hello everyone! Ready for the tournament?",
    "messageType": "TEXT",
    "sender": {
      "id": 1,
      "displayName": "GamerUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
    },
    "chatRoom": {
      "id": 1,
      "name": "Gaming Squad Chat"
    },
    "replyToMessageId": 5,
    "fileUrl": null,
    "fileName": null,
    "fileSize": null,
    "createdAt": "2024-12-21T17:15:00",
    "updatedAt": "2024-12-21T17:15:00"
  }
}
```

### 66. Get Chat Messages
**API:** `GET /api/chat/rooms/{roomId}/messages?page=0&size=50&beforeMessageId=20&afterMessageId=5`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Messages retrieved successfully",
  "messages": [
    {
      "id": 16,
      "content": "Hello everyone! Ready for the tournament?",
      "messageType": "TEXT",
      "sender": {
        "id": 1,
        "displayName": "GamerUser",
        "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
      },
      "replyToMessage": {
        "id": 5,
        "content": "When does the tournament start?",
        "sender": {
          "id": 2,
          "displayName": "TeamMate"
        }
      },
      "fileUrl": null,
      "createdAt": "2024-12-21T17:15:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 50
}
```

### 67. Delete Message
**API:** `DELETE /api/chat/messages/{messageId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Message deleted successfully"
}
```

### 68. Add Chat Room Member
**API:** `POST /api/chat/rooms/{roomId}/members/{memberId}`

**Body (form-data):**
```
role: MEMBER (optional, default: MEMBER)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Member added successfully",
  "membership": {
    "id": 3,
    "user": {
      "id": 3,
      "displayName": "NewPlayer"
    },
    "role": "MEMBER",
    "joinedAt": "2024-12-21T17:30:00"
  }
}
```

### 69. Remove Chat Room Member
**API:** `DELETE /api/chat/rooms/{roomId}/members/{memberId}`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Member removed successfully"
}
```

### 70. Get Chat Room Members
**API:** `GET /api/chat/rooms/{roomId}/members?page=0&size=20`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Members retrieved successfully",
  "members": [
    {
      "id": 1,
      "user": {
        "id": 1,
        "displayName": "GamerUser",
        "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
      },
      "role": "ADMIN",
      "joinedAt": "2024-12-21T16:00:00",
      "lastSeen": "2024-12-21T17:30:00",
      "isOnline": true
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20
}
```

### 71. Start Direct Message
**API:** `POST /api/chat/direct`

**Body (form-data):**
```
recipientId: 2
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Direct message conversation started",
  "chatRoom": {
    "id": 10,
    "name": "Direct Message",
    "type": "DIRECT_MESSAGE",
    "isPrivate": true,
    "currentMembers": 2,
    "members": [
      {
        "id": 1,
        "displayName": "GamerUser"
      },
      {
        "id": 2,
        "displayName": "FriendUser"
      }
    ],
    "createdAt": "2024-12-21T18:00:00"
  }
}
```

### 72. Get Online Users
**API:** `GET /api/chat/online-users`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Online users retrieved",
  "onlineUsers": [
    {
      "id": 2,
      "displayName": "FriendUser",
      "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg",
      "status": "ONLINE",
      "lastSeen": "2024-12-21T18:00:00"
    },
    {
      "id": 3,
      "displayName": "GamerFriend",
      "profilePictureUrl": "/uploads/profile-pictures/profile_3.jpg",
      "status": "IN_GAME",
      "currentGame": "Valorant",
      "lastSeen": "2024-12-21T17:45:00"
    }
  ]
}
```

### 73. Send Typing Indicator
**API:** `POST /api/chat/typing`

**Body (form-data):**
```
roomId: 1
isTyping: true (optional, default: true)
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Typing indicator sent",
  "roomId": 1,
  "isTyping": true
}
```

---

## 🏆 Tournament Management APIs

### 74. Create Tournament
**API:** `POST /api/tournaments`

**Body (JSON):**
```json
{
  "name": "Valorant Championship 2024",
  "description": "Annual Valorant tournament with cash prizes",
  "gameTitle": "Valorant",
  "gameMode": "Competitive",
  "tournamentType": "ELIMINATION",
  "maxParticipants": 64,
  "entryFee": 25.00,
  "prizePool": 1000.00,
  "currency": "USD",
  "startDate": "2024-12-30T10:00:00",
  "endDate": "2024-12-30T18:00:00",
  "registrationDeadline": "2024-12-29T23:59:59",
  "rules": "Standard competitive rules apply",
  "status": "REGISTRATION_OPEN",
  "isPublic": true,
  "requiresApproval": false
}
```

**Expected Output:**
```json
{
  "id": 1,
  "name": "Valorant Championship 2024",
  "description": "Annual Valorant tournament with cash prizes",
  "gameTitle": "Valorant",
  "gameMode": "Competitive",
  "tournamentType": "ELIMINATION",
  "maxParticipants": 64,
  "currentParticipants": 0,
  "entryFee": 25.00,
  "prizePool": 1000.00,
  "currency": "USD",
  "startDate": "2024-12-30T10:00:00",
  "endDate": "2024-12-30T18:00:00",
  "registrationDeadline": "2024-12-29T23:59:59",
  "rules": "Standard competitive rules apply",
  "status": "REGISTRATION_OPEN",
  "isPublic": true,
  "requiresApproval": false,
  "organizer": {
    "id": 1,
    "displayName": "TournamentOrg",
    "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
  },
  "viewCount": 0,
  "createdAt": "2024-12-21T19:00:00",
  "updatedAt": "2024-12-21T19:00:00"
}
```

### 75. Get Tournament Details
**API:** `GET /api/tournaments/{id}`

**Body:** None

**Expected Output:**
```json
{
  "id": 1,
  "name": "Valorant Championship 2024",
  "description": "Annual Valorant tournament with cash prizes",
  "gameTitle": "Valorant",
  "gameMode": "Competitive",
  "tournamentType": "ELIMINATION",
  "maxParticipants": 64,
  "currentParticipants": 25,
  "entryFee": 25.00,
  "prizePool": 1000.00,
  "currency": "USD",
  "startDate": "2024-12-30T10:00:00",
  "endDate": "2024-12-30T18:00:00",
  "registrationDeadline": "2024-12-29T23:59:59",
  "rules": "Standard competitive rules apply",
  "status": "REGISTRATION_OPEN",
  "isPublic": true,
  "organizer": {
    "id": 1,
    "displayName": "TournamentOrg",
    "profilePictureUrl": "/uploads/profile-pictures/profile_1.jpg"
  },
  "moderators": [
    {
      "id": 2,
      "displayName": "ModeratorUser"
    }
  ],
  "viewCount": 150,
  "createdAt": "2024-12-21T19:00:00",
  "updatedAt": "2024-12-21T19:30:00"
}
```

### 76. Update Tournament
**API:** `PUT /api/tournaments/{id}`

**Body (JSON):**
```json
{
  "name": "Updated Valorant Championship 2024",
  "maxParticipants": 128,
  "prizePool": 1500.00,
  "registrationDeadline": "2024-12-29T20:00:00"
}
```

**Expected Output:**
```json
{
  "id": 1,
  "name": "Updated Valorant Championship 2024",
  "maxParticipants": 128,
  "prizePool": 1500.00,
  "registrationDeadline": "2024-12-29T20:00:00",
  "updatedAt": "2024-12-21T20:00:00"
  // ... other fields remain unchanged
}
```

### 77. Delete Tournament
**API:** `DELETE /api/tournaments/{id}`

**Body:** None (requires Authorization header)

**Expected Output:**
```
HTTP 204 No Content
```

### 78. Get All Tournaments
**API:** `GET /api/tournaments`

**Body:** None

**Expected Output:**
```json
[
  {
    "id": 1,
    "name": "Valorant Championship 2024",
    "gameTitle": "Valorant",
    "status": "REGISTRATION_OPEN",
    "currentParticipants": 25,
    "maxParticipants": 64,
    "startDate": "2024-12-30T10:00:00",
    "prizePool": 1000.00,
    "organizer": {
      "id": 1,
      "displayName": "TournamentOrg"
    }
  }
]
```

### 79. Get Tournaments by Organizer
**API:** `GET /api/tournaments/organizer/{organizerId}`

**Body:** None

**Expected Output:**
```json
[
  {
    "id": 1,
    "name": "Valorant Championship 2024",
    "status": "REGISTRATION_OPEN",
    "currentParticipants": 25,
    "maxParticipants": 64,
    "startDate": "2024-12-30T10:00:00",
    "createdAt": "2024-12-21T19:00:00"
  }
]
```

### 80. Add Tournament Moderator
**API:** `POST /api/tournaments/{id}/moderators?moderatorId=2`

**Body:** None (requires Authorization header)

**Expected Output:**
```
HTTP 200 OK
```

### 81. Increment Tournament View Count
**API:** `POST /api/tournaments/{id}/view`

**Body:** None

**Expected Output:**
```
HTTP 200 OK
```

---

## 🎮 Tournament Participation APIs

### 82. Register for Tournament
**API:** `POST /api/tournaments/{tournamentId}/participants/register?participantId=2`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "id": 1,
  "tournament": {
    "id": 1,
    "name": "Valorant Championship 2024"
  },
  "participant": {
    "id": 2,
    "displayName": "CompetitivePlayer",
    "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
  },
  "status": "REGISTERED",
  "registeredAt": "2024-12-21T20:30:00",
  "checkedIn": false,
  "disqualified": false
}
```

### 83. Check-in Participant
**API:** `POST /api/tournaments/{tournamentId}/participants/check-in?participantId=2`

**Body:** None (requires Authorization header)

**Expected Output:**
```
HTTP 200 OK
```

### 84. Disqualify Participant
**API:** `POST /api/tournaments/{tournamentId}/participants/disqualify?participantId=2&reason=Violation of rules`

**Body:** None (requires Authorization header)

**Expected Output:**
```
HTTP 200 OK
```

### 85. Submit Match Result
**API:** `POST /api/tournaments/{tournamentId}/participants/submit-result?participantId=2&won=true`

**Body:** None (requires Authorization header)

**Expected Output:**
```
HTTP 200 OK
```

### 86. Get Tournament Participants
**API:** `GET /api/tournaments/{tournamentId}/participants`

**Body:** None

**Expected Output:**
```json
[
  {
    "id": 1,
    "participant": {
      "id": 2,
      "displayName": "CompetitivePlayer",
      "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
    },
    "status": "REGISTERED",
    "registeredAt": "2024-12-21T20:30:00",
    "checkedIn": true,
    "disqualified": false,
    "wins": 0,
    "losses": 0
  }
]
```

### 87. Get Specific Participation Details
**API:** `GET /api/tournaments/{tournamentId}/participants/{participantId}`

**Body:** None

**Expected Output:**
```json
{
  "id": 1,
  "tournament": {
    "id": 1,
    "name": "Valorant Championship 2024"
  },
  "participant": {
    "id": 2,
    "displayName": "CompetitivePlayer",
    "profilePictureUrl": "/uploads/profile-pictures/profile_2.jpg"
  },
  "status": "REGISTERED",
  "registeredAt": "2024-12-21T20:30:00",
  "checkedIn": true,
  "disqualified": false,
  "disqualificationReason": null,
  "wins": 2,
  "losses": 1,
  "currentRound": 3
}
```

---

## 🎯 Discord OAuth APIs

### 88. Initiate Discord OAuth
**API:** `GET /api/auth/discord/login`

**Body:** None

**Expected Output:**
```
HTTP 302 Redirect to Discord OAuth URL:
https://discord.com/api/oauth2/authorize?client_id=1416218898063429724&redirect_uri=http://localhost:8080/api/auth/discord/callback&response_type=code&scope=identify%20email&state=abc123
```

### 89. Discord OAuth Callback
**API:** `GET /api/auth/discord/callback?code=xyz789&state=abc123`

**Body:** None

**Expected Output:**
```
HTTP 302 Redirect to frontend:
http://localhost:3000/auth/success?token=eyJhbGciOiJIUzI1NiJ9...

OR JSON Response (fallback):
{
  "success": true,
  "message": "Discord authentication successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "displayName": "DiscordUser#1234",
    "email": "user@discord.com",
    "discordId": "123456789012345678",
    "profilePictureUrl": "https://cdn.discordapp.com/avatars/123456789012345678/avatar_hash.png",
    "roles": ["REGULAR_GAMER"]
  }
}
```

### 90. Link Discord Account
**API:** `POST /api/auth/discord/link`

**Body (form-data):**
```
code: xyz789
```

**Expected Output:**
```json
{
  "success": true,
  "message": "Discord account linked successfully",
  "discordUser": {
    "id": "123456789012345678",
    "username": "DiscordUser",
    "discriminator": "1234",
    "avatar": "avatar_hash",
    "email": "user@discord.com"
  }
}
```

### 91. Unlink Discord Account
**API:** `POST /api/auth/discord/unlink`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Discord account unlinked successfully"
}
```

### 92. Get Discord User Info
**API:** `GET /api/auth/discord/user-info`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Discord user info retrieved",
  "discordUser": {
    "id": "123456789012345678",
    "username": "DiscordUser",
    "discriminator": "1234",
    "avatar": "avatar_hash",
    "email": "user@discord.com",
    "verified": true,
    "linkedAt": "2024-12-21T21:00:00"
  }
}
```

### 93. Refresh Discord Token
**API:** `POST /api/auth/discord/refresh`

**Body:** None (requires Authorization header)

**Expected Output:**
```json
{
  "success": true,
  "message": "Discord token refreshed successfully",
  "expiresAt": "2024-12-22T21:00:00"
}
```

---

## 🌐 WebSocket Endpoints

For real-time features, connect to WebSocket at:
**WebSocket URL:** `ws://localhost:8080/api/ws`

### WebSocket Topics:
- `/topic/chat/room/{roomId}` - Chat room messages
- `/topic/notifications/{userId}` - User notifications  
- `/topic/tournament/{tournamentId}` - Tournament updates
- `/topic/typing/{roomId}` - Typing indicators
- `/user/queue/private` - Private user messages

### WebSocket Message Examples:

**Chat Message:**
```json
{
  "type": "CHAT_MESSAGE",
  "roomId": 1,
  "message": {
    "id": 16,
    "content": "Hello everyone!",
    "sender": {
      "id": 1,
      "displayName": "GamerUser"
    },
    "timestamp": "2024-12-21T17:15:00"
  }
}
```

**Typing Indicator:**
```json
{
  "type": "TYPING_INDICATOR", 
  "roomId": 1,
  "user": {
    "id": 2,
    "displayName": "TeamMate"
  },
  "isTyping": true
}
```

---

## 🔒 Error Responses

All APIs may return these common error formats:

### Authentication Error (401):
```json
{
  "success": false,
  "message": "Authentication required",
  "errorCode": "AUTH_REQUIRED"
}
```

### Authorization Error (403):
```json
{
  "success": false,
  "message": "Access denied. Insufficient permissions",
  "errorCode": "ACCESS_DENIED"
}
```

### Validation Error (400):
```json
{
  "success": false,
  "message": "Validation failed",
  "errorCode": "VALIDATION_ERROR",
  "errors": {
    "email": "Invalid email format",
    "password": "Password must be at least 6 characters"
  }
}
```

### Not Found Error (404):
```json
{
  "success": false,
  "message": "Resource not found",
  "errorCode": "NOT_FOUND"
}
```

### Server Error (500):
```json
{
  "success": false,
  "message": "Internal server error",
  "errorCode": "INTERNAL_ERROR"
}
```


