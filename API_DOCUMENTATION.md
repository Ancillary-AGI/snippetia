# Snippetia API Documentation

## 📋 Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
- [Data Models](#data-models)
- [Error Handling](#error-handling)
- [Rate Limiting](#rate-limiting)
- [WebSocket API](#websocket-api)
- [SDK and Libraries](#sdk-and-libraries)

## 🚀 Overview

The Snippetia API is a RESTful web service that provides comprehensive access to all platform features. It follows REST principles, uses JSON for data exchange, and implements industry-standard security practices.

### Base URL

```
Production: https://api.snippetia.dev/v1
Staging: https://staging-api.snippetia.dev/v1
Development: http://localhost:8080/api/v1
```

### API Versioning

The API uses URL versioning with the version number in the path. The current version is `v1`.

### Content Type

All requests and responses use `application/json` content type unless otherwise specified.

## 🔐 Authentication

### JWT Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

### OAuth2 Providers

Supported OAuth2 providers:
- GitHub
- Google
- Discord
- Microsoft
- Twitch

### Authentication Endpoints

#### Login with Credentials

```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "user@example.com",
    "displayName": "John Doe",
    "avatarUrl": "https://example.com/avatar.jpg"
  }
}
```

#### Register New User

```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "user@example.com",
  "password": "securepassword",
  "displayName": "John Doe"
}
```

#### OAuth2 Login

```http
GET /auth/oauth2/{provider}?redirect_uri=https://app.snippetia.dev/callback
```

#### Refresh Token

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 📚 API Endpoints

### Snippets

#### Get All Public Snippets

```http
GET /snippets?page=0&size=20&language=kotlin&category=algorithm&tags=sorting,array&search=quicksort
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20, max: 100)
- `language` (optional): Programming language filter
- `category` (optional): Category filter
- `tags` (optional): Comma-separated list of tags
- `search` (optional): Search query for full-text search

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Quick Sort Algorithm",
      "description": "Efficient sorting algorithm implementation",
      "language": "kotlin",
      "tags": ["sorting", "algorithm"],
      "isPublic": true,
      "author": {
        "id": 1,
        "username": "johndoe",
        "displayName": "John Doe",
        "avatarUrl": "https://example.com/avatar.jpg"
      },
      "likeCount": 42,
      "viewCount": 156,
      "forkCount": 8,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "direction": "DESC",
      "property": "createdAt"
    }
  },
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

#### Get Snippet by ID

```http
GET /snippets/{id}
```

**Response:**
```json
{
  "id": 1,
  "title": "Quick Sort Algorithm",
  "description": "Efficient sorting algorithm implementation",
  "content": "fun quickSort(arr: IntArray, low: Int, high: Int) {\n    if (low < high) {\n        val pi = partition(arr, low, high)\n        quickSort(arr, low, pi - 1)\n        quickSort(arr, pi + 1, high)\n    }\n}",
  "language": "kotlin",
  "tags": ["sorting", "algorithm"],
  "isPublic": true,
  "author": {
    "id": 1,
    "username": "johndoe",
    "displayName": "John Doe",
    "avatarUrl": "https://example.com/avatar.jpg"
  },
  "likeCount": 42,
  "viewCount": 156,
  "forkCount": 8,
  "forkedFrom": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

#### Create Snippet

```http
POST /snippets
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Binary Search Implementation",
  "description": "Efficient binary search algorithm",
  "content": "fun binarySearch(arr: IntArray, target: Int): Int {\n    var left = 0\n    var right = arr.size - 1\n    \n    while (left <= right) {\n        val mid = left + (right - left) / 2\n        \n        when {\n            arr[mid] == target -> return mid\n            arr[mid] < target -> left = mid + 1\n            else -> right = mid - 1\n        }\n    }\n    \n    return -1\n}",
  "language": "kotlin",
  "tags": ["search", "algorithm"],
  "isPublic": true
}
```

#### Update Snippet

```http
PUT /snippets/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Binary Search",
  "description": "Optimized binary search with better error handling",
  "content": "// Updated implementation...",
  "language": "kotlin",
  "tags": ["search", "algorithm", "optimized"],
  "isPublic": true
}
```

#### Delete Snippet

```http
DELETE /snippets/{id}
Authorization: Bearer <token>
```

#### Like/Unlike Snippet

```http
POST /snippets/{id}/like
Authorization: Bearer <token>
```

**Response:**
```json
{
  "liked": true,
  "likeCount": 43
}
```

#### Fork Snippet

```http
POST /snippets/{id}/fork
Authorization: Bearer <token>
```

#### Upload Snippet from File

```http
POST /snippets/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

title: "Uploaded Algorithm"
description: "Algorithm from uploaded file"
language: "kotlin"
tags: "algorithm,upload"
isPublic: true
file: <binary-file-data>
```

### Users

#### Get Current User Profile

```http
GET /users/profile
Authorization: Bearer <token>
```

#### Get User Profile by ID

```http
GET /users/{userId}/profile
```

#### Get User Profile by Username

```http
GET /users/username/{username}
```

#### Check Username Availability

```http
GET /users/check/username/{username}
```

**Response:**
```json
{
  "available": true
}
```

#### Check Email Availability

```http
GET /users/check/email/{email}
```

### Comments

#### Get Comments for Snippet

```http
GET /snippets/{snippetId}/comments?page=0&size=20
```

#### Add Comment to Snippet

```http
POST /snippets/{snippetId}/comments
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Great implementation! Very clean and efficient."
}
```

### Analytics

#### Get User Analytics

```http
GET /analytics/user
Authorization: Bearer <token>
```

#### Get Snippet Analytics

```http
GET /analytics/snippet/{snippetId}
Authorization: Bearer <token>
```

#### Get Platform Analytics (Admin Only)

```http
GET /analytics/platform
Authorization: Bearer <admin-token>
```

### Search

#### Advanced Search

```http
POST /search
Content-Type: application/json

{
  "query": "sorting algorithm",
  "filters": {
    "language": ["kotlin", "java"],
    "tags": ["algorithm"],
    "dateRange": {
      "from": "2024-01-01T00:00:00Z",
      "to": "2024-12-31T23:59:59Z"
    },
    "author": "johndoe",
    "minLikes": 10,
    "maxResults": 50
  },
  "sort": {
    "field": "relevance",
    "direction": "DESC"
  }
}
```

## 📊 Data Models

### Snippet Model

```json
{
  "id": "number",
  "title": "string",
  "description": "string",
  "content": "string",
  "language": "string",
  "tags": ["string"],
  "isPublic": "boolean",
  "author": "UserSummary",
  "likeCount": "number",
  "viewCount": "number",
  "forkCount": "number",
  "forkedFrom": "SnippetSummary | null",
  "createdAt": "ISO 8601 datetime",
  "updatedAt": "ISO 8601 datetime"
}
```

### User Model

```json
{
  "id": "number",
  "username": "string",
  "email": "string",
  "displayName": "string",
  "bio": "string | null",
  "avatarUrl": "string | null",
  "location": "string | null",
  "website": "string | null",
  "githubUsername": "string | null",
  "twitterUsername": "string | null",
  "followerCount": "number",
  "followingCount": "number",
  "snippetCount": "number",
  "totalLikes": "number",
  "totalViews": "number",
  "joinedAt": "ISO 8601 datetime",
  "lastActiveAt": "ISO 8601 datetime"
}
```

### Comment Model

```json
{
  "id": "number",
  "content": "string",
  "author": "UserSummary",
  "createdAt": "ISO 8601 datetime",
  "updatedAt": "ISO 8601 datetime | null"
}
```

## ⚠️ Error Handling

### Error Response Format

```json
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "The requested snippet was not found",
    "details": "Snippet with ID 12345 does not exist or is not accessible",
    "timestamp": "2024-01-15T10:30:00Z",
    "path": "/api/v1/snippets/12345",
    "requestId": "req-123e4567-e89b-12d3-a456-426614174000"
  }
}
```

### HTTP Status Codes

- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `204 No Content`: Request successful, no content to return
- `400 Bad Request`: Invalid request parameters or body
- `401 Unauthorized`: Authentication required or invalid
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource conflict (e.g., duplicate username)
- `422 Unprocessable Entity`: Validation errors
- `429 Too Many Requests`: Rate limit exceeded
- `500 Internal Server Error`: Server error

### Common Error Codes

- `VALIDATION_ERROR`: Request validation failed
- `AUTHENTICATION_REQUIRED`: Authentication token required
- `INVALID_TOKEN`: Authentication token is invalid or expired
- `ACCESS_DENIED`: Insufficient permissions
- `RESOURCE_NOT_FOUND`: Requested resource not found
- `DUPLICATE_RESOURCE`: Resource already exists
- `RATE_LIMIT_EXCEEDED`: Too many requests
- `SERVER_ERROR`: Internal server error

## 🚦 Rate Limiting

### Rate Limits

- **Anonymous users**: 100 requests per hour
- **Authenticated users**: 1000 requests per hour
- **Premium users**: 5000 requests per hour
- **API key users**: 10000 requests per hour

### Rate Limit Headers

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642248000
X-RateLimit-Retry-After: 3600
```

### Rate Limit Response

```json
{
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Rate limit exceeded",
    "details": "You have exceeded the rate limit of 1000 requests per hour",
    "retryAfter": 3600
  }
}
```

## 🔌 WebSocket API

### Connection

```javascript
const ws = new WebSocket('wss://api.snippetia.dev/v1/ws');
```

### Authentication

```javascript
ws.send(JSON.stringify({
  type: 'auth',
  token: 'your-jwt-token'
}));
```

### Real-time Events

#### Snippet Updates

```json
{
  "type": "snippet.updated",
  "data": {
    "snippetId": 123,
    "changes": {
      "content": "new content",
      "title": "new title"
    },
    "author": {
      "id": 1,
      "username": "johndoe"
    },
    "timestamp": "2024-01-15T10:30:00Z"
  }
}
```

#### New Comments

```json
{
  "type": "comment.created",
  "data": {
    "snippetId": 123,
    "comment": {
      "id": 456,
      "content": "Great snippet!",
      "author": {
        "id": 2,
        "username": "janedoe"
      },
      "createdAt": "2024-01-15T10:30:00Z"
    }
  }
}
```

#### Live Collaboration

```json
{
  "type": "collaboration.cursor",
  "data": {
    "snippetId": 123,
    "userId": 2,
    "position": {
      "line": 10,
      "column": 5
    },
    "selection": {
      "start": { "line": 10, "column": 5 },
      "end": { "line": 10, "column": 15 }
    }
  }
}
```

## 📚 SDK and Libraries

### JavaScript/TypeScript SDK

```bash
npm install @snippetia/sdk
```

```javascript
import { SnippetiaClient } from '@snippetia/sdk';

const client = new SnippetiaClient({
  baseUrl: 'https://api.snippetia.dev/v1',
  apiKey: 'your-api-key'
});

// Get snippets
const snippets = await client.snippets.list({
  language: 'kotlin',
  page: 0,
  size: 20
});

// Create snippet
const newSnippet = await client.snippets.create({
  title: 'My Snippet',
  content: 'fun hello() = println("Hello, World!")',
  language: 'kotlin',
  isPublic: true
});
```

### Python SDK

```bash
pip install snippetia-sdk
```

```python
from snippetia import SnippetiaClient

client = SnippetiaClient(
    base_url='https://api.snippetia.dev/v1',
    api_key='your-api-key'
)

# Get snippets
snippets = client.snippets.list(language='python', page=0, size=20)

# Create snippet
new_snippet = client.snippets.create(
    title='My Python Snippet',
    content='def hello():\n    print("Hello, World!")',
    language='python',
    is_public=True
)
```

### Java/Kotlin SDK

```kotlin
// Gradle
implementation 'dev.snippetia:sdk:1.0.0'

// Usage
val client = SnippetiaClient.builder()
    .baseUrl("https://api.snippetia.dev/v1")
    .apiKey("your-api-key")
    .build()

// Get snippets
val snippets = client.snippets().list(
    language = "kotlin",
    page = 0,
    size = 20
)

// Create snippet
val newSnippet = client.snippets().create(
    CreateSnippetRequest(
        title = "My Kotlin Snippet",
        content = "fun hello() = println(\"Hello, World!\")",
        language = "kotlin",
        isPublic = true
    )
)
```

## 📖 Examples

### Complete Workflow Example

```javascript
// 1. Authenticate
const authResponse = await fetch('https://api.snippetia.dev/v1/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password'
  })
});

const { accessToken } = await authResponse.json();

// 2. Create a snippet
const createResponse = await fetch('https://api.snippetia.dev/v1/snippets', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${accessToken}`
  },
  body: JSON.stringify({
    title: 'Fibonacci Sequence',
    description: 'Generate Fibonacci numbers',
    content: 'fun fibonacci(n: Int): Long {\n    return if (n <= 1) n.toLong()\n    else fibonacci(n - 1) + fibonacci(n - 2)\n}',
    language: 'kotlin',
    tags: ['algorithm', 'recursion'],
    isPublic: true
  })
});

const snippet = await createResponse.json();

// 3. Get snippet details
const detailResponse = await fetch(`https://api.snippetia.dev/v1/snippets/${snippet.id}`);
const snippetDetails = await detailResponse.json();

// 4. Like the snippet
const likeResponse = await fetch(`https://api.snippetia.dev/v1/snippets/${snippet.id}/like`, {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${accessToken}` }
});

const likeResult = await likeResponse.json();
console.log(`Snippet ${likeResult.liked ? 'liked' : 'unliked'}. Total likes: ${likeResult.likeCount}`);
```

---

**Need Help?**

- 📧 Email: api-support@snippetia.dev
- 💬 Discord: [Snippetia Community](https://discord.gg/snippetia)
- 📚 Documentation: [docs.snippetia.dev](https://docs.snippetia.dev)
- 🐛 Issues: [GitHub Issues](https://github.com/snippetia/snippetia/issues)