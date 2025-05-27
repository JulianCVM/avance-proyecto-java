@echo off
echo Iniciando contenedores Docker...
docker-compose down
docker-compose up --build -d
echo.
echo Los contenedores están iniciándose. Para ver los registros, ejecute:
echo docker-compose logs -f
echo.
echo Para acceder a la aplicación: http://localhost:8080
echo Para acceder a MySQL desde el host: localhost:3307 (usuario: root, contraseña: password) 