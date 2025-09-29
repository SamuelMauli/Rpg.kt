# Dockerfile para Old Dragon RPG Mobile
FROM node:18-alpine

# Instalar dependências do sistema
RUN apk add --no-cache \
    git \
    bash \
    curl \
    wget \
    unzip

# Criar diretório de trabalho
WORKDIR /app

# Instalar Expo CLI e ngrok globalmente
RUN npm install -g @expo/cli@latest @expo/ngrok@latest

# Baixar o projeto
RUN wget https://github.com/SamuelMauli/Rpg.kt/archive/refs/heads/main.zip && \
    unzip main.zip && \
    mv Rpg.kt-main/mobile/* . && \
    rm -rf Rpg.kt-main main.zip

# Instalar dependências do projeto
RUN npm install

# Expor porta do Expo
EXPOSE 8081
EXPOSE 19000
EXPOSE 19001
EXPOSE 19002

# Comando para iniciar o Expo
ENV CI=1
CMD ["npx", "expo", "start", "--tunnel"]
