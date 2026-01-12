@echo off
echo Testing Experience Service API...
echo.

echo 1. Testing GET /api/experience/scenes
curl -X GET "http://localhost:8084/api/experience/scenes?userId=1"
echo.
echo.

echo 2. Testing GET /api/experience/scenes/1
curl -X GET "http://localhost:8084/api/experience/scenes/1?userId=1"
echo.
echo.

echo 3. Testing GET /api/experience/certificates
curl -X GET "http://localhost:8084/api/experience/certificates?userId=1"
echo.
echo.

echo Done!
pause
