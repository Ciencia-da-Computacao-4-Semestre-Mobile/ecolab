@echo off
echo 🔄 Corrigindo incompatibilidade Kotlin/Compose Compiler...
echo.

:: Limpar builds
if exist app\build (
    rmdir /s /q app\build
    echo ✅ Build do app limpo
)
if exist build (
    rmdir /s /q build
    echo ✅ Build raiz limpo
)

:: Limpar cache do Gradle
if exist .gradle (
    rmdir /s /q .gradle
    echo ✅ Cache do Gradle limpo
)

echo.
echo 📝 Versões configuradas:
echo - Kotlin: 1.9.22 (libs.versions.toml)
echo - Compose Compiler: 1.5.8 (app/build.gradle.kts)
echo.
echo ✅ Compatibilidade corrigida!
echo.
echo 📱 Próximos passos:
echo 1. Abra o Android Studio
echo 2. Vá em File ^> Sync Project with Gradle Files
echo 3. Aguarde a sincronização completar
echo.
pause