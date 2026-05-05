@echo off
title Iniciando Interfaz UI Profeco
color 0A

echo ===================================================
echo Iniciando el frontend de UI Profeco...
echo ===================================================
echo.

:: Navegar al directorio exacto donde se encuentra este archivo .bat
cd /d "%~dp0"

:: Verificar si la carpeta node_modules existe. Si no, instalar dependencias.
IF NOT EXIST "node_modules\" (
    echo Detectando que faltan dependencias... Ejecutando npm install.
    npm install
)

:: Esperar 2 segundos para dar tiempo a que la terminal procese
timeout /t 2 /nobreak > NUL

:: Abrir el navegador en el puerto por defecto de Vite (5173)
start http://localhost:5173

:: Ejecutar el servidor de desarrollo de Vite
echo Levantando servidor de Vite...
npm run dev

pause