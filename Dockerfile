# Dockerfile para Old Dragon RPG Mobile com QR Code
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

# Instalar dependências para suporte web
RUN npm install react-dom@19.1.0 react-native-web@^0.21.0

# Expor portas do Expo
EXPOSE 8081
EXPOSE 19000
EXPOSE 19001
EXPOSE 19002

# Configurar variáveis de ambiente para interface web
ENV EXPO_DEVTOOLS_LISTEN_ADDRESS=0.0.0.0
ENV REACT_NATIVE_PACKAGER_HOSTNAME=0.0.0.0

# Comando para iniciar o Expo
CMD ["sh", "-c", "npx expo start --tunnel"]
