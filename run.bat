@echo off
chcp 65001 >nul

echo Old Dragon 2 RPG - Terminal Adventure
echo ====================================
echo.

REM Verificar se o Kotlin está instalado
kotlin -version >nul 2>&1
if errorlevel 1 (
    echo Erro: Kotlin não está instalado.
    echo Por favor, instale o Kotlin para executar o jogo.
    echo.
    echo Instruções de instalação:
    echo 1. Instale o Java 11 ou superior
    echo 2. Baixe e instale o Kotlin de https://kotlinlang.org/
    echo 3. Execute este script novamente
    pause
    exit /b 1
)

echo Compilando o projeto...

REM Criar diretório de build se não existir
if not exist "build\classes" mkdir build\classes

REM Compilar todos os arquivos Kotlin
for /r "src\main\kotlin" %%f in (*.kt) do (
    kotlinc "%%f" -d build\classes
    if errorlevel 1 (
        echo Erro na compilação de %%f
        pause
        exit /b 1
    )
)

echo Compilação concluída com sucesso!
echo.
echo Iniciando o jogo...
echo.

REM Executar o jogo
kotlin -cp build\classes com.rpg.MainKt

pause

