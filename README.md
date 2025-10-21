# EcoLab

## Introdução

Bem-vindo ao repositório oficial do EcoLab! Este documento fornece uma visão geral completa da estrutura do projeto, arquitetura e convenções de código. O objetivo é servir como um guia central para desenvolvedores que estão começando a contribuir com o projeto.

## Arquitetura do Projeto

O projeto adota os princípios da **Clean Architecture**, que promove a separação de responsabilidades e a criação de um software mais testável, manutenível e escalável. A arquitetura é dividida em três camadas principais:

*   **Camada de Apresentação (`app`)**: Responsável pela interface do usuário (UI) e pela interação com o usuário. Esta camada inclui as telas (construídas com Jetpack Compose), ViewModels e toda a lógica de navegação. Ela depende da camada de Domínio para executar as ações do usuário.

*   **Camada de Domínio (`core/domain`)**: O coração do aplicativo. Contém a lógica de negócio principal (casos de uso) e as entidades do domínio. Esta camada é totalmente independente de frameworks de UI ou de detalhes de implementação de persistência de dados.

*   **Camada de Dados (`core/data`)**: Responsável por fornecer os dados para a aplicação. Inclui as implementações dos repositórios, fontes de dados (locais e remotas) e modelos de dados. Ela define como os dados são obtidos, seja de um banco de dados local (Room) ou de uma API de rede.

## Estrutura de Módulos

O projeto é modularizado para reforçar a separação de responsabilidades e melhorar os tempos de compilação.

*   `:app`: Módulo principal da aplicação Android. Contém a camada de Apresentação e coordena a inicialização da aplicação.
*   `:core:ui`: Componentes de UI reutilizáveis e temas do Jetpack Compose.
*   `:core:domain`: Contém a lógica de negócio e os modelos de domínio. Não possui dependências do Android Framework.
*   `:core:data`: Implementação da camada de dados, incluindo repositórios e fontes de dados.
*   `:core:common`: Utilitários e código auxiliar que podem ser compartilhados entre todos os outros módulos.

## Estrutura de Diretórios

A seguir, uma descrição detalhada dos arquivos e diretórios mais importantes.

---

### Diretório Raiz

*   `.idea/`: Arquivos de configuração específicos do Android Studio. Não deve ser versionado em projetos colaborativos.
*   `app/`: O módulo principal da aplicação.
*   `core/`: Módulos que contêm a lógica de negócio, acesso a dados e componentes de UI compartilhados.
*   `build/`: Diretório com os artefatos de compilação gerados. Não deve ser modificado manualmente.
*   `gradle/` e `.gradle/`: Ferramentas e arquivos de cache do Gradle.
*   `.gitignore`: Especifica os arquivos e diretórios que o Git deve ignorar.
*   `build.gradle.kts`: Script de build do Gradle para o projeto raiz.
*   `local.properties`: Arquivo para propriedades locais do desenvolvedor, como o caminho do SDK do Android. Não é versionado.
*   `gradle.properties`: Configurações globais para o sistema de build Gradle.
*   `settings.gradle.kts`: Declara os módulos que compõem o projeto.

---

### Módulo `app`

*   `src/`: Contém o código-fonte e os recursos do módulo.
*   `build.gradle.kts`: Script de build do módulo `app`. Declara suas dependências e configurações específicas.
*   `google-services.json`: Arquivo de configuração para os serviços do Google, como o Firebase.

#### `app/src/main`

*   `res/`: Recursos do aplicativo que não são código:
    *   `drawable`: Imagens e ícones.
    *   `values`: Arquivos XML para strings, cores e temas.
    *   `mipmap`: Ícones do launcher em diferentes densidades.
    *   `font`: Arquivos de fontes personalizadas.
*   `java/com/example/ecolab/`: Código-fonte em Kotlin.
*   `assets/`: Arquivos brutos, como os arquivos GeoJSON com os dados dos pontos de coleta.
*   `AndroidManifest.xml`: O "manifesto" do aplicativo. Descreve os componentes da aplicação para o sistema Android.

#### Pacotes Principais em `app`

*   `di/` (Injeção de Dependência): Módulos do Hilt que ensinam o framework a fornecer as dependências necessárias, como repositórios e outras classes.
*   `ui/`: Contém toda a lógica de interface do usuário, construída com Jetpack Compose.
    *   `theme/`: Definição do tema do aplicativo (cores, tipografia, formas).
    *   `screens/`: Composables que representam as telas completas do aplicativo.
    *   `components/`: Pequenos componentes de UI reutilizáveis.
    *   `navigation/`: Lógica de navegação entre as telas usando o Navigation Compose.
*   `feature/`: Contém os ViewModels para cada tela ou funcionalidade, que preparam e gerenciam os dados para a UI.
*   `MainActivity.kt`: A única Activity do aplicativo, que serve como ponto de entrada e hospeda o conteúdo do Jetpack Compose.
*   `EcoLabApplication.kt`: Classe `Application` customizada, usada para inicializar o Hilt e outras bibliotecas.

---

## Tecnologias e Bibliotecas Utilizadas

*   **Linguagem**: [Kotlin](https://kotlinlang.org/)
*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) para uma UI declarativa e moderna.
*   **Arquitetura**: Clean Architecture, MVVM (Model-View-ViewModel).
*   **Injeção de Dependência**: [Hilt](https://dagger.dev/hilt/) para gerenciar dependências.
*   **Assincronia**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) para um código assíncrono mais limpo.
*   **Persistência Local**: [Room](https://developer.android.com/jetpack/androidx/releases/room) para o banco de dados local.
*   **Navegação**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation).
*   **Rede**: (Se aplicável, adicione Retrofit/Ktor aqui).
*   **Mapas**: [Google Maps Compose Library](https://developers.google.com/maps/documentation/android-sdk/maps-compose).
*   **Autenticação**: Firebase Authentication.

## Como Compilar e Executar

1.  Clone este repositório.
2.  Abra o projeto no Android Studio.
3.  Crie um arquivo `local.properties` na raiz do projeto.
4.  Adicione suas chaves de API ao arquivo da seguinte forma:
    ```properties
    maps.api_key=SUA_CHAVE_DE_API_DO_MAPS_AQUI
    google_ia_studio.api_key=SUA_CHAVE_DE_API_DO_AI_STUDIO_AQUI
    ```
5.  Sincronize o projeto com os arquivos Gradle.
6.  Compile e execute no emulador ou em um dispositivo físico.
