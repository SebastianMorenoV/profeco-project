@echo off
title ProFeCo - Iniciando Entorno de Desarrollo
color 0A

:: 0. Calcular rutas dinamicamente
set "FRONTEND_DIR=%~dp0"
cd /d "%FRONTEND_DIR%..\..\"
set "ROOT_DIR=%cd%"

echo ========================================================
echo         INICIANDO ARQUITECTURA DE PROFECO
echo ========================================================
echo.

:: 1. Levantar el Gateway (Envoy y red)
echo [1/4] Levantando Envoy Gateway...
cd "%ROOT_DIR%\backend\profeco-backend\infrastructure\envoy"
docker-compose up -d

:: 2. Levantar los Microservicios y BDs
echo.
echo [2/4] Levantando Microservicios y Bases de Datos...
cd "%ROOT_DIR%\backend\profeco-backend\microservices"
docker-compose up -d

:: 3. Pausa para que Spring Boot arranque
echo.
echo [3/4] Esperando 2 minutos a que Java se conecte a las BDs y se levanten correctamente los MS...
timeout /t 120 /nobreak > NUL

:: 4. Iniciar el Frontend (React/Vite)
echo.
echo [4/4] Iniciando la interfaz de React...
cd "%FRONTEND_DIR%"
call npm install
echo.
echo ========================================================
echo    ¡TODO LISTO! La pagina se abrira en tu navegador.
echo    (Deja esta ventana abierta mientras trabajas)
echo ========================================================
echo.
:: Inicia Vite y abre Chrome automaticamente
call npm run dev -- --open

pause