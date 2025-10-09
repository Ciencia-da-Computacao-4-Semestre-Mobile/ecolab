# EcoLab: Um Guia Completo para Iniciantes (Nível: Cabaço)

## Introdução

Bem-vindo ao manual de sobrevivência do EcoLab!

Este documento foi escrito pensando em você, que talvez nunca tenha visto uma linha de código na vida. O objetivo é que, ao final da leitura, você consiga entender a estrutura do nosso projeto como se fosse a planta da sua própria casa. Vamos explicar para que serve cada pasta e cada arquivo importante, sem "economês" de programador.

## Arquitetura Geral: Como o App Pensa?

Imagine que nosso aplicativo é um restaurante. A forma como organizamos a cozinha, o salão e o estoque é a nossa "arquitetura". Nós usamos uma abordagem chamada **Clean Architecture** (Arquitetura Limpa), que divide o trabalho em camadas bem definidas. Pense nisso como construir com blocos de LEGO: cada peça tem seu lugar e sua função, tornando a construção (e futuras reformas) muito mais fácil.

As camadas do nosso "restaurante" são:

*   **Apresentação (`app`) - O Salão:** É a parte que o cliente (usuário) vê e com a qual interage. O garçom anota os pedidos, serve as mesas e mostra o cardápio (as telas). É a "cara" do nosso aplicativo.

*   **Domínio (`core/domain`) - O Chef de Cozinha:** É o cérebro da operação. O Chef sabe todas as receitas (as regras de negócio) e como o prato deve ser. Ele não fala com o cliente nem vai ao estoque; ele apenas segue as regras para criar o prato perfeito. Esta camada é o coração da lógica do EcoLab.

*   **Dados (`core/data`) - O Estoque e os Entregadores:** É onde os ingredientes (dados) são armazenados e buscados. Temos um estoque interno (banco de dados do celular) e entregadores que buscam ingredientes frescos fora (na internet). O Chef não precisa saber de onde vêm os ingredientes, apenas que eles estão disponíveis.

## Estrutura de Pastas e Arquivos: O Mapa do Tesouro

A seguir, vamos explorar cada canto do nosso projeto.

---

### Diretório Raiz (A Planta Baixa do Terreno)

Aqui ficam as pastas e arquivos mais importantes que definem todo o projeto.

*   `.idea/`: Pasta de configuração do Android Studio (a ferramenta que usamos para construir o app). Pense nela como a "caixa de bagunça" do Android Studio. **NUNCA MEXA AQUI.**

*   `app/`: É o módulo principal, o "prédio" do nosso aplicativo. A maior parte do que o usuário vê e toca está aqui.

*   `core/`: Uma pasta que guarda nossas "caixas de ferramentas" (outros módulos) que o `app` utiliza. Manter as ferramentas organizadas aqui facilita o trabalho.

*   `build/`: Pasta gerada automaticamente quando o código é transformado em um aplicativo funcional. É a "área de construção" do projeto. **NÃO TOQUE.**

*   `gradle/` e `.gradle/`: O Gradle é o nosso "mestre de obras". Ele pega todo o nosso código e recursos e constrói o aplicativo. Essas pastas guardam as ferramentas e os arquivos temporários dele.

*   `.gitignore`: Uma "lista de ignorados" para o Git (nosso sistema de backup e versionamento). Diz a ele para não salvar arquivos desnecessários, como a pasta `build`.

*   `build.gradle.kts`: A "planta mestra" do projeto. Diz ao "mestre de obras" (Gradle) quais são as peças do projeto e como montá-las.

*   `local.properties`: Um arquivo para "segredos locais" do seu computador, como onde você guardou o martelo (o SDK do Android). Este arquivo não é compartilhado.

*   `gradle.properties`: Configurações e ajustes para o "mestre de obras" (Gradle).

*   `settings.gradle.kts`: Define quais "prédios" (módulos, como `app` e os do `core`) fazem parte do nosso "condomínio" (projeto).

---

### Módulo `app` (O Prédio Principal)

*   `src/`: Abreviatura de "source" (fonte). É aqui que fica todo o código e os recursos deste módulo.

*   `build.gradle.kts`: A "planta" específica deste "prédio". Lista todos os "materiais de construção" (bibliotecas e outras dependências) que só o módulo `app` precisa.

*   `google-services.json`: A "chave de acesso" para os serviços do Google (Firebase) que usamos, como o sistema de login.

#### `app/src/main` (O Andar Principal do Prédio)

*   `res/`: A "caixa de decoração". Guarda tudo que não é código:
    *   `drawable`: Imagens e ícones.
    *   `values`: Textos (`strings.xml`), cores (`colors.xml`), temas (`themes.xml`).
    *   `mipmap`: Ícones do aplicativo em diferentes tamanhos.
    *   `font`: Fontes personalizadas.

*   `java/`: Apesar do nome, é aqui que fica nosso código-fonte em Kotlin. É o "cérebro" do módulo.

*   `assets/`: Uma "dispensa" para arquivos brutos. Usamos para guardar os arquivos `GeoJSON`, que são como mapas do tesouro com a localização dos pontos de coleta.

*   `AndroidManifest.xml`: A "carteira de identidade" do app para o sistema Android. Descreve o que o app é, o que ele precisa (permissões como acesso à internet e localização) e quais telas (Activities) ele tem.

#### `app/src/main/java/com/example/ecolab/` (Os Cômodos do Andar)

*   `di/` (Injeção de Dependência): A "caixa de ferramentas mágica" do Hilt. Em vez de construirmos nossas próprias ferramentas a todo momento, nós pedimos ao Hilt e ele nos entrega uma pronta. Isso deixa o código mais limpo e organizado.
    *   `AppModule.kt`, `RepositoryModule.kt`: "Manuais de instruções" que ensinam o Hilt a criar as ferramentas que precisamos, como o `Geocoder` (que transforma um endereço em coordenadas de mapa) ou os Repositórios.

*   `ui/` (Interface do Usuário): Tudo relacionado ao que o usuário vê.
    *   `theme/`: O "guarda-roupa" do app. Define o esquema de cores (`Palette.kt`), as fontes (`Type.kt`) e os formatos (`Shape.kt`).
    *   `screens/`: Cada arquivo aqui é uma tela do aplicativo (`HomeScreen.kt`, `MapScreen.kt`, etc.).
    *   `components/`: "Peças de LEGO" reutilizáveis para montar as telas, como um botão personalizado (`PointCard.kt`).
    *   `navigation/`: O "GPS" do app. Define todas as rotas possíveis e como navegar de uma tela para outra (`NavGraph.kt`).
    *   `profile/`: A tela de perfil do usuário.

*   `auth/`: O "segurança" do aplicativo. Contém a lógica para saber se o usuário está logado ou não.
    *   `FakeAuthRepository.kt`: Uma "versão de mentira" do sistema de login, usada para testes.

*   `data/`: A parte do `app` que lida diretamente com os dados.
    *   `model/`: Os "moldes" que definem a estrutura dos nossos dados. Por exemplo, `RankedUser.kt` diz quais informações um usuário no ranking deve ter.
    *   `geojson/`: Moldes específicos para ler os dados dos mapas.
    *   `repository/`: O "gerente do estoque". Implementa as regras de como buscar e salvar dados. Por exemplo, `RankingRepository.kt` busca a lista de usuários para o ranking.

*   `feature/`: A "sala de controle" de cada tela. Cada tela tem um "cérebro" aqui, chamado **ViewModel**. O ViewModel prepara os dados para a tela exibir e processa as ações do usuário (como cliques em botões).
    *   `map/`, `home/`, etc.: A sala de controle de cada tela específica.

*   `MainActivity.kt`: A "porta de entrada" principal do app. É a primeira tela que o Android carrega, e ela é responsável por exibir todas as outras telas que construímos.

*   `EcoLabApplication.kt`: Um "ritual de inicialização". Código que roda uma única vez quando o app abre, usado para preparar bibliotecas ou outras configurações importantes.

---

### Módulo `core` (Nossas Caixas de Ferramentas)

Esta pasta contém módulos independentes que podem ser reutilizados em qualquer parte do projeto.

*   `ui/`: Ferramentas de interface, como componentes visuais genéricos.
*   `data/`: A parte mais "profunda" do nosso sistema de dados. Define as "interfaces" (contratos) que os repositórios devem seguir e como o app conversa com a internet ou o banco de dados.
*   `common/`: Ferramentas e utilitários genéricos que podem ser úteis em qualquer lugar do código.
*   `domain/`: O "livro de regras" do EcoLab. Contém a lógica de negócio mais pura, que não depende de nenhuma tela ou tecnologia específica. Define o que é um `CollectionPoint` ou um `AuthUser` para o *negócio*, independentemente de como eles são mostrados na tela.

---

Espero que este guia super detalhado ajude a "criança" (ou qualquer pessoa) a navegar pelo projeto EcoLab sem medo! Se algo ainda não estiver claro, a culpa é minha, não sua. :)
