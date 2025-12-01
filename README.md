# EcoLab — Educação, Mapa de Coleta e Engajamento Ambiental

EcoLab é um aplicativo Android focado em educação ambiental, mapa de pontos de coleta seletiva, quizzes assistidos por IA e biblioteca de conteúdos. Explore pontos de coleta, acompanhe conquistas, faça quizzes e salve seus favoritos — tudo em um só lugar.

## Tecnologias Utilizadas

[![Kotlin](https://img.shields.io/badge/Kotlin-6F51F5?logo=kotlin&logoColor=white)](https://kotlinlang.org/) 
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-3DDC84?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose) 
[![Material 3](https://img.shields.io/badge/Material%203-757575?logo=materialdesign&logoColor=white)](https://m3.material.io/) 
[![Hilt](https://img.shields.io/badge/Hilt-35495E?logo=dagger&logoColor=white)](https://dagger.dev/hilt/) 
[![Firebase Auth](https://img.shields.io/badge/Firebase%20Auth-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com/products/auth) 
[![Firestore](https://img.shields.io/badge/Firestore-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com/docs/firestore) 
[![Google Maps SDK](https://img.shields.io/badge/Google%20Maps%20SDK-4285F4?logo=googlemaps&logoColor=white)](https://developers.google.com/maps/documentation/android-sdk) 
[![OkHttp](https://img.shields.io/badge/OkHttp-2D2D2D?logo=square&logoColor=white)](https://square.github.io/okhttp/) 
[![DataStore](https://img.shields.io/badge/DataStore-3DDC84?logo=android&logoColor=white)](https://developer.android.com/topic/libraries/architecture/datastore) 
[![Coil](https://img.shields.io/badge/Coil-6F51F5?logo=kotlin&logoColor=white)](https://coil-kt.github.io/coil/) 
[![Jsoup](https://img.shields.io/badge/Jsoup-007396?logo=java&logoColor=white)](https://jsoup.org/) 
[![Gemini 1.5 Flash](https://img.shields.io/badge/Gemini%201.5%20Flash-4285F4?logo=google&logoColor=white)](https://ai.google.dev/) 

## Principais Funcionalidades

**Autenticação**
- Login com Google
- Login e cadastro com e-mail/senha
- Recuperação de senha

**Mapa de Coleta**
- Pontos de coleta em tempo real (Firestore)
- Busca por endereço (Geocoder)
- Favoritar e filtrar por categoria

**Biblioteca de Conteúdos**
- Artigos e notícias ambientais
- Coleta automática de conteúdo (Jsoup)
- Carregamento de imagens otimizado (Coil)

**Quiz com IA**
- Geração e execução de quizzes
- Modelos Gemini 1.5 Flash

**Perfil, Conquistas e Loja**
- Perfil com estatísticas básicas
- Conquistas desbloqueáveis
- Loja de avatares e temas visuais

**Aparência**
- Material 3 e Jetpack Compose
- Interface moderna com animações

Arquitetura: Clean Architecture, MVVM, StateFlow, Coroutines

## Instalação (Desenvolvimento Local)
- Clone o repositório: `git clone <REPO_URL>`
- Entre na pasta: `cd ecolab`
- Abra no Android Studio: File → Open → pasta do projeto

## Configuração
- Firebase: inclua seu `google-services.json` em `app/`
- Chaves no `local.properties` (não versionar):
  - `MAPS_API_KEY=SUA_CHAVE_DE_API_DO_MAPS_AQUI`
  - `google_ia_studio.api_key=SUA_CHAVE_DE_API_DO_AI_STUDIO_AQUI` (Gemini 1.5 Flash)

## Build & Execução
- Via Gradle (Windows):
  - `./gradlew.bat assembleDebug`
  - `./gradlew.bat installDebug`
- Ou execute pelo Android Studio (Run ▶)

## Estrutura do Projeto
- `app/`: aplicação Android (UI, telas, navegação e ViewModels)
- `core/domain`: modelos e contratos de domínio (independente de plataforma)
- `core/data`: repositórios e integrações (Firestore, Auth)
- `core/ui`: componentes e tema compartilhados
- `core/common`: utilitários comuns
- `gradle/libs.versions.toml`: gerência de dependências

## Arquitetura (Resumo)
- Apresentação (`app`): Compose + ViewModels
- Domínio (`core/domain`): entidades e interfaces (`AuthRepository`, `PointsRepository`)
- Dados (`core/data`): `AuthRepositoryImpl`, `FirebasePointsRepository` (Firestore, favoritos por usuário)

## Arquitetura Detalhada

**Camadas e Dependências**
- `app` (Apresentação): telas em Compose, navegação e `ViewModel`s. Depende apenas de contratos do domínio e implementações providas via DI.
- `core/domain` (Domínio): modelos e interfaces puras. Sem dependência de Android ou bibliotecas externas.
- `core/data` (Dados): implementações de repositórios e integrações com Firestore/Auth. Mapeia documentos para modelos de domínio. Sem uso de Room no momento.

**Módulos**
- `:app`: UI, navegação e orquestração.
- `:core:domain`: `AuthUser`, `CollectionPoint` e contratos (`AuthRepository`, `PointsRepository`).
- `:core:data`: `AuthRepositoryImpl` e `FirebasePointsRepository` com Firestore/Auth.
- `:core:ui`: componentes e tema compartilhados.
- `:core:common`: utilitários e tipos comuns.

**Fluxo de Dados**
- Firestore fornece `collection_points` e favoritos do usuário em `users/{uid}.favoritePoints`.
- `FirebasePointsRepository` observa mudanças em tempo real e expõe `Flow<List<CollectionPoint>>` com o estado de favoritos mesclado.
- `ViewModel`s consomem os repositórios e expõem `StateFlow` para a UI. Filtros, busca e merges de estado local (ex.: favoritos) são aplicados antes da emissão.
- A UI Compose coleta `uiState` e renderiza listas, detalhes e ações (favoritar, busca, filtro).

**Injeção de Dependências (Hilt)**
- `AppModule`: `Geocoder`, `GeoJsonParser`, `FirebaseFirestore` com escopo `Singleton`.
- `DataModule`: `FirebaseAuth`, `AuthRepositoryImpl`, `FirebasePointsRepository`, `AchievementsRepository`, `QuizRepository`, `UserRepository` com escopo `Singleton`.

**Estado e Navegação**
- Estado reativo com `StateFlow`/`Flow` e operadores como `combine`.
- Navegação com `Navigation Compose`, itens do bottom bar definidos por sealed class e grafo central com destinos por feature.

**Erros e Resultados**
- Operações críticas (auth, reset de senha) retornam `Result<T>` para comunicar sucesso/erro/`loading`.
- Erros de rede/credenciais são logados e tratados no fluxo.

**Configuração e Segredos**
- Chaves via `local.properties`: `MAPS_API_KEY` e `google_ia_studio.api_key`.
- Exposição em `BuildConfig` para uso em runtime.

**IA (Gemini 1.5 Flash)**
- Quizzes usam modelos Gemini 1.5 Flash via API com a chave `google_ia_studio.api_key`.

## Dados do Mapa (Firestore)
- Coleção `collection_points` com campos compatíveis ao `CollectionPoint` de domínio:
  - `id: Long`, `name: String`, `description: String`, `latitude: Double`, `longitude: Double`, `category: String`, `openingHours: String?`, `materials: String?`
- Favoritos em `users/{uid}` no campo `favoritePoints: List<String>`
- Seeds locais opcionais em `app/src/main/assets/collection_points/*.geojson` para testes

## Segurança
- Não comite chaves privadas
- Use `local.properties`, variáveis de ambiente ou secrets no CI/CD
