@echo off
echo 🔄 Corrigindo todos os erros de configuração...
echo.

:: Parar qualquer processo Gradle em execução
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo 🧹 Limpando builds...
if exist app\build (
    rmdir /s /q app\build
    echo ✅ Build do app limpo
)
if exist core\common\build (
    rmdir /s /q core\common\build
    echo ✅ Build do common limpo
)
if exist core\data\build (
    rmdir /s /q core\data\build
    echo ✅ Build do data limpo
)
if exist core\domain\build (
    rmdir /s /q core\domain\build
    echo ✅ Build do domain limpo
)
if exist core\ui\build (
    rmdir /s /q core\ui\build
    echo ✅ Build do ui limpo
)
if exist build (
    rmdir /s /q build
    echo ✅ Build raiz limpo
)

echo.
echo 🔄 Removendo cache Gradle...
if exist .gradle (
    rmdir /s /q .gradle
    echo ✅ Cache do Gradle limpo
)

echo.
echo ✅ Configurações corrigidas:
echo - Kotlin: 1.9.22
echo - Compose Compiler: 1.5.8
echo - Kotlin compiler strategy: in-process (sem aspas)
echo.
echo 📱 Prontos para sincronizar no Android Studio!
echo.
pause