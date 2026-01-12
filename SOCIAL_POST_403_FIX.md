# Social Post 403 Error Fix

## Problem
- POST /api/posts returns 403 Forbidden
- via.placeholder.com images fail to load (ERR_CONNECTION_CLOSED)

## Root Cause
The `SecurityConfig` was allowing all `/api/**` requests through but the `@PreAuthorize("isAuthenticated()")` annotation on the `createPost` method was still enforcing authentication. This created a conflict where:
1. Spring Security's filter chain allowed the request
2. But method-level security rejected it because no authentication was set in SecurityContext

## Solution Applied

### 1. Updated SecurityConfig.java
Added JWT authentication filter to properly validate tokens and set authentication in SecurityContext:

```java
@Bean
public OncePerRequestFilter jwtAuthenticationFilter() {
    return new OncePerRequestFilter() {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    if (JwtUtil.validateToken(token)) {
                        String userId = JwtUtil.getUserIdFromToken(token);
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userId, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    // Invalid token, continue without authentication
                }
            }
            
            filterChain.doFilter(request, response);
        }
    };
}
```

### 2. Placeholder Image Issue
The via.placeholder.com connection error is a network issue (external service unavailable). This doesn't affect functionality - it's just mock data images failing to load.

## Testing

1. Restart the social-service:
```bash
cd backend/social-service
mvn spring-boot:run
```

2. Login to the frontend and try posting

3. The JWT token from localStorage will now be properly validated and authentication will be set

## How It Works Now

1. Frontend sends request with `Authorization: Bearer <token>` header
2. JWT filter intercepts the request
3. Validates the token using JwtUtil
4. Extracts userId and creates authentication object
5. Sets authentication in SecurityContext
6. `@PreAuthorize("isAuthenticated()")` now passes
7. Post is created successfully

## Files Modified
- `backend/social-service/src/main/java/com/jiyi/social/config/SecurityConfig.java`
