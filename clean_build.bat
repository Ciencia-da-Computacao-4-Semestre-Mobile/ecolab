@echo off
echo Limpando build...
if exist app\build (
    rmdir /s /q app\build
    echo Build do app limpo
)
if exist core\common\build (
    rmdir /s /q core\common\build
    echo Build do common limpo
)
if exist core\data\build (
    rmdir /s /q core\data\build
    echo Build do data limpo
)
if exist core\domain\build (
    rmdir /s /q core\domain\build
    echo Build do domain limpo
)
if exist core\ui\build (
    rmdir /s /q core\ui\build
    echo Build do ui limpo
)
if exist build (
    rmdir /s /q build
    echo Build raiz limpo
)
echo Limpeza completa!
pause