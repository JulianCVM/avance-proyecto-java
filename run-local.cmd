@echo off
echo Ejecutando la aplicación con el perfil 'local' (H2 en memoria)...
mvn spring-boot:run -Dspring-boot.run.profiles=local 