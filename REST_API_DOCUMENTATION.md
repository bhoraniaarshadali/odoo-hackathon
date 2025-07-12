# SkillSwap REST API Documentation

## Base URL
```
http://localhost:8000/api/
```

## Authentication
All endpoints require Token Authentication. Include the token in the Authorization header:
```
Authorization: Token your_token_here
```

---

## 🔐 Authentication Endpoints

### Register User
**POST** `/auth/register/`

**Request Body:**
```json
{
    "username": "john_doe",
    "password": "secure_password",
    "email": "john@example.com",
    "first_name": "John",
    "last_name": "Doe"
}
```

**Response (201 Created):**
```json
{
    "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    },
    "token": "your_auth_token_here",
    "message": "User registered successfully"
}
```

### Login User
**POST** `/auth/login/`

**Request Body:**
```json
{
    "username": "john_doe",
    "password": "secure_password"
}
```

**Response (200 OK):**
```json
{
    "token": "your_auth_token_here",
    "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    }
}
```

---

## 👤 User Management

### Get Current User
**GET** `/users/me/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
{
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "first_name": "John",
    "last_name": "Doe"
}
```

---

## 👤 Profile Management

### Get User Profile
**GET** `/profiles/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "user": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "name": "John Doe",
        "location": "New York",
        "is_public": true,
        "availability": "Available",
        "skills_count": 3,
        "swaps_count": 5
    }
]
```

### Update Profile
**PUT** `/profiles/{id}/`

**Headers:** `Authorization: Token your_token_here`

**Request Body:**
```json
{
    "name": "John Doe",
    "location": "New York",
    "is_public": true,
    "availability": "Available"
}
```

**Response (200 OK):**
```json
{
    "id": 1,
    "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    },
    "name": "John Doe",
    "location": "New York",
    "is_public": true,
    "availability": "Available",
    "skills_count": 3,
    "swaps_count": 5
}
```

### Get Public Profiles
**GET** `/profiles/public_profiles/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "user": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "name": "John Doe",
        "location": "New York",
        "availability": "Available",
        "skills": [
            {
                "id": 1,
                "name": "Python Programming",
                "user": 1,
                "user_name": "john_doe",
                "is_offered": true,
                "created_at": "2024-01-01T10:00:00Z"
            }
        ]
    }
]
```

### Get Specific Public Profile
**GET** `/profiles/{id}/public_profile/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
{
    "id": 1,
    "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    },
    "name": "John Doe",
    "location": "New York",
    "availability": "Available",
    "skills": [
        {
            "id": 1,
            "name": "Python Programming",
            "user": 1,
            "user_name": "john_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        }
    ]
}
```

---

## 🛠 Skills Management

### Get User Skills
**GET** `/skills/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Python Programming",
        "user": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "user_name": "john_doe",
        "is_offered": true,
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

### Create Skill
**POST** `/skills/`

**Headers:** `Authorization: Token your_token_here`

**Request Body:**
```json
{
    "name": "Java Programming",
    "is_offered": true
}
```

**Response (201 Created):**
```json
{
    "id": 2,
    "name": "Java Programming",
    "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    },
    "user_name": "john_doe",
    "is_offered": true,
    "created_at": "2024-01-01T10:00:00Z"
}
```

### Get Available Skills (from other users)
**GET** `/skills/available_skills/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 3,
        "name": "JavaScript Programming",
        "user": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "user_name": "jane_doe",
        "is_offered": true,
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

### Get Wanted Skills (from other users)
**GET** `/skills/wanted_skills/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 4,
        "name": "React Development",
        "user": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "user_name": "jane_doe",
        "is_offered": false,
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

---

## 🔄 Swap Requests

### Get User Swap Requests
**GET** `/swaps/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "sender": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "sender_name": "john_doe",
        "receiver": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "receiver_name": "jane_doe",
        "skill_requested": {
            "id": 3,
            "name": "JavaScript Programming",
            "user": 2,
            "user_name": "jane_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "skill_offered": {
            "id": 1,
            "name": "Python Programming",
            "user": 1,
            "user_name": "john_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "status": "pending",
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

### Create Swap Request
**POST** `/swaps/`

**Headers:** `Authorization: Token your_token_here`

**Request Body:**
```json
{
    "receiver": 2,
    "skill_requested": 3,
    "skill_offered": 1
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "sender": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    },
    "sender_name": "john_doe",
    "receiver": {
        "id": 2,
        "username": "jane_doe",
        "email": "jane@example.com",
        "first_name": "Jane",
        "last_name": "Doe"
    },
    "receiver_name": "jane_doe",
    "skill_requested": {
        "id": 3,
        "name": "JavaScript Programming",
        "user": 2,
        "user_name": "jane_doe",
        "is_offered": true,
        "created_at": "2024-01-01T10:00:00Z"
    },
    "skill_offered": {
        "id": 1,
        "name": "Python Programming",
        "user": 1,
        "user_name": "john_doe",
        "is_offered": true,
        "created_at": "2024-01-01T10:00:00Z"
    },
    "status": "pending",
    "created_at": "2024-01-01T10:00:00Z"
}
```

### Accept Swap Request
**POST** `/swaps/{id}/accept/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
{
    "message": "Swap request accepted"
}
```

### Reject Swap Request
**POST** `/swaps/{id}/reject/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
{
    "message": "Swap request rejected"
}
```

### Get Sent Requests
**GET** `/swaps/sent_requests/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "sender": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "sender_name": "john_doe",
        "receiver": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "receiver_name": "jane_doe",
        "skill_requested": {
            "id": 3,
            "name": "JavaScript Programming",
            "user": 2,
            "user_name": "jane_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "skill_offered": {
            "id": 1,
            "name": "Python Programming",
            "user": 1,
            "user_name": "john_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "status": "pending",
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

### Get Received Requests
**GET** `/swaps/received_requests/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 2,
        "sender": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "sender_name": "jane_doe",
        "receiver": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "receiver_name": "john_doe",
        "skill_requested": {
            "id": 1,
            "name": "Python Programming",
            "user": 1,
            "user_name": "john_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "skill_offered": {
            "id": 3,
            "name": "JavaScript Programming",
            "user": 2,
            "user_name": "jane_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "status": "pending",
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

---

## 💬 Feedback System

### Get User Feedback
**GET** `/feedback/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "swap": {
            "id": 1,
            "sender": {
                "id": 1,
                "username": "john_doe",
                "email": "john@example.com",
                "first_name": "John",
                "last_name": "Doe"
            },
            "sender_name": "john_doe",
            "receiver": {
                "id": 2,
                "username": "jane_doe",
                "email": "jane@example.com",
                "first_name": "Jane",
                "last_name": "Doe"
            },
            "receiver_name": "jane_doe",
            "skill_requested": {
                "id": 3,
                "name": "JavaScript Programming",
                "user": 2,
                "user_name": "jane_doe",
                "is_offered": true,
                "created_at": "2024-01-01T10:00:00Z"
            },
            "skill_offered": {
                "id": 1,
                "name": "Python Programming",
                "user": 1,
                "user_name": "john_doe",
                "is_offered": true,
                "created_at": "2024-01-01T10:00:00Z"
            },
            "status": "accepted",
            "created_at": "2024-01-01T10:00:00Z"
        },
        "from_user": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "from_user_name": "john_doe",
        "comments": "Great experience!",
        "rating": 5,
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

### Create Feedback
**POST** `/feedback/`

**Headers:** `Authorization: Token your_token_here`

**Request Body:**
```json
{
    "swap": 1,
    "comments": "Great experience!",
    "rating": 5
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "swap": {
        "id": 1,
        "sender": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com",
            "first_name": "John",
            "last_name": "Doe"
        },
        "sender_name": "john_doe",
        "receiver": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "receiver_name": "jane_doe",
        "skill_requested": {
            "id": 3,
            "name": "JavaScript Programming",
            "user": 2,
            "user_name": "jane_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "skill_offered": {
            "id": 1,
            "name": "Python Programming",
            "user": 1,
            "user_name": "john_doe",
            "is_offered": true,
            "created_at": "2024-01-01T10:00:00Z"
        },
        "status": "accepted",
        "created_at": "2024-01-01T10:00:00Z"
    },
    "from_user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "first_name": "John",
        "last_name": "Doe"
    },
    "from_user_name": "john_doe",
    "comments": "Great experience!",
    "rating": 5,
    "created_at": "2024-01-01T10:00:00Z"
}
```

### Get Received Feedback
**GET** `/feedback/received_feedback/`

**Headers:** `Authorization: Token your_token_here`

**Response (200 OK):**
```json
[
    {
        "id": 2,
        "swap": {
            "id": 1,
            "sender": {
                "id": 1,
                "username": "john_doe",
                "email": "john@example.com",
                "first_name": "John",
                "last_name": "Doe"
            },
            "sender_name": "john_doe",
            "receiver": {
                "id": 2,
                "username": "jane_doe",
                "email": "jane@example.com",
                "first_name": "Jane",
                "last_name": "Doe"
            },
            "receiver_name": "jane_doe",
            "skill_requested": {
                "id": 3,
                "name": "JavaScript Programming",
                "user": 2,
                "user_name": "jane_doe",
                "is_offered": true,
                "created_at": "2024-01-01T10:00:00Z"
            },
            "skill_offered": {
                "id": 1,
                "name": "Python Programming",
                "user": 1,
                "user_name": "john_doe",
                "is_offered": true,
                "created_at": "2024-01-01T10:00:00Z"
            },
            "status": "accepted",
            "created_at": "2024-01-01T10:00:00Z"
        },
        "from_user": {
            "id": 2,
            "username": "jane_doe",
            "email": "jane@example.com",
            "first_name": "Jane",
            "last_name": "Doe"
        },
        "from_user_name": "jane_doe",
        "comments": "Excellent teaching!",
        "rating": 5,
        "created_at": "2024-01-01T10:00:00Z"
    }
]
```

---

## 📊 Status Codes

- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Authentication required
- **403 Forbidden** - Permission denied
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

---

## 🔧 Error Responses

All error responses follow this format:
```json
{
    "error": "Error message here"
}
```

---

## 🚀 Quick Start

1. **Register a user:**
   ```bash
   curl -X POST http://localhost:8000/api/auth/register/ \
     -H "Content-Type: application/json" \
     -d '{"username":"testuser","password":"password123","email":"test@example.com"}'
   ```

2. **Login and get token:**
   ```bash
   curl -X POST http://localhost:8000/api/auth/login/ \
     -H "Content-Type: application/json" \
     -d '{"username":"testuser","password":"password123"}'
   ```

3. **Use the token for authenticated requests:**
   ```bash
   curl -X GET http://localhost:8000/api/profiles/ \
     -H "Authorization: Token your_token_here"
   ```

---

## 📝 Notes

- All timestamps are in ISO 8601 format
- All IDs are integers
- Authentication is required for most endpoints
- The API supports CORS for cross-origin requests
- Rate limiting is not currently implemented 