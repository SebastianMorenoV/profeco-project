@echo off
setlocal

cd /d "%~dp0"

where node >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Node.js no esta instalado o no esta en el PATH.
    echo Descargalo desde https://nodejs.org/
    pause
    exit /b 1
)

if not exist "node_modules" (
    echo Instalando dependencias por primera vez...
    call npm install
    if errorlevel 1 (
        echo [ERROR] Fallo npm install.
        pause
        exit /b 1
    )
)

echo Iniciando ui-consumidor en http://localhost:5173 ...
start "" http://localhost:5173
call npm run dev

endlocal
