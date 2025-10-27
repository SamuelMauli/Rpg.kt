# Dockerfile para Old Dragon 2 RPG - Sistema Completo
FROM openjdk:17-slim

# Instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Criar diretório de trabalho
WORKDIR /app

# Copiar projeto completo
COPY . .

# Dar permissão de execução ao gradlew
RUN chmod +x gradlew

# Compilar o projeto
RUN ./gradlew clean build -x test

# Expor porta (caso precise de API REST no futuro)
EXPOSE 8080

# Comando para executar o jogo
CMD ["./gradlew", "run", "--console=plain"]

