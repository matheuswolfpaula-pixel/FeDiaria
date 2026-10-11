# FéDiária

"Duolingo da fé católica" em pt-BR: terço com ofensiva, novenas, quiz e latim. Android primeiro, offline-first, sem backend/login/anúncios. applicationId `br.app.fediaria` (debug: `br.app.fediaria.debug`), minSdk 26, targetSdk 36, compileSdk 37 (exigido pelo Compose 1.12).
Detalhes: `docs/ARQUITETURA.md` · Fases e critérios de aceite: `docs/ROADMAP.md`.

## Stack
Kotlin Multiplatform + Compose Multiplatform (Material 3) · MVVM · Koin · Room KMP · DataStore · Navigation/ViewModel multiplataforma · kotlinx-datetime/serialization · AlarmManager inexato + WorkManager (Android) · kotlin.test, Turbine, Compose UI tests · detekt, ktlint, Android lint · GitHub Actions.

## Módulos
- `core/`: regras puras (ofensiva, dia de graça, mistérios, SRS/lições do quiz, agendamento, Páscoa). Sem Android, sem I/O.
- `shared/`: dados (Room, DataStore, leitor de conteúdo) + UI Compose + ViewModels. Strings em `composeResources/values/strings.xml`, conteúdo em `composeResources/files/conteudo/`.
- `androidApp/`: Activity, notificações, alarmes, receivers, WorkManager.

## Comandos
```bash
./gradlew check                       # testes + lint + detekt + ktlint (precisa estar verde antes de commit)
./gradlew :core:jvmTest               # lógica de negócio (rápido)
./gradlew :shared:testAndroidHostTest # testes do shared (dados) na JVM
./gradlew :androidApp:assembleDebug   # APK debug
./gradlew :androidApp:installDebug    # instala no emulador/aparelho conectado
./gradlew :androidApp:connectedDebugAndroidTest   # testes de UI (emulador ligado)
./gradlew ktlintFormat                # formata
./testar.sh [--limpo]                 # emulador do zero (sem Quick Boot) + instala e abre o app
```
SDK em `~/Android/Sdk` (`local.properties`, não versionado) · emulador: `emulator -avd fediaria` (Android 16, Pixel 7).
Versões só em `gradle/libs.versions.toml`. iOS: alvos `iosArm64`/`iosSimulatorArm64` declarados, só compilam no macOS (código em `iosMain` ainda não verificado).
detekt 1.23.x roda isolado com Kotlin 2.0.21 (ver `build.gradle.kts` raiz); ao migrar para detekt 2 estável, remover esse ajuste.

## Convenções
- Identificadores em português sem acento (`Terco`, `diaRezado`); sufixos técnicos do framework ficam em inglês (`ViewModel`, `Dao`, `Repository`). **Texto visível ao usuário em pt-BR, sempre em recursos** (nunca string literal na UI).
- Datas: `LocalDate` local do aparelho para dias rezados; nunca `System.currentTimeMillis()` direto, sempre injete um `Clock` + `TimeZone` (testabilidade).
- A ofensiva é **derivada** do histórico de dias, nunca um contador salvo.
- Mudou o schema do Room? Suba a versão, declare a migração e acrescente um caso em `MigracoesTest`.
- Lógica nova vai para `core` com teste. ViewModels expõem `StateFlow<UiState>`.
- Permissões permitidas: `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED` (+ as do WorkManager). **Nunca** `INTERNET`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` sem decisão explícita do dono.
- Commits: mensagem em pt-BR, imperativo, uma fase/assunto por commit. Nunca commitar keystore, `keystore.properties` ou senhas.

## Regras de conteúdo (críticas)
- **Direitos autorais:** só textos de domínio público, tradicionais ou originais. PROIBIDO copiar Bíblia CNBB/Ave-Maria/Jerusalém etc., texto do Catecismo da Igreja Católica (LEV) ou compilações modernas de novenas/orações. Escrituras: parafrasear, ou citar literalmente só a tradução do Pe. Antônio Pereira de Figueiredo **com o trecho verificado em fonte de domínio público** (atenção à numeração da Vulgata nos Salmos). Catecismo: perguntas ORIGINAIS que só citam o nº do parágrafo. Catecismo de S. Pio X (original) é domínio público; traduções modernas podem não ser.
- **Fidelidade doutrinal:** nunca inventar doutrina, versículo, nº de parágrafo, data litúrgica ou fato histórico. Na dúvida, não incluir e anotar em `docs/CONTEUDO_PARA_REVISAO.md`.
- Todo conteúdo fica em JSON (separado do código), com `id` estável, `fonte` e `revisado: false` por padrão. Todo conteúdo novo é registrado em `docs/CONTEUDO_PARA_REVISAO.md`.
- **Tom:** reverente e acolhedor. Sem vidas, sem punição, sem sons de jogo durante a oração, sem culpa nos lembretes. A oração não gera XP.

## Fluxo de trabalho
Uma fase por vez: implementar → `./gradlew check` verde → explicar como testar no emulador/aparelho → commit → **esperar o "ok" do dono** antes da próxima fase. O dono não é especialista em Android: explicar decisões em linguagem simples e sinalizar tudo que depende de ação dele. Relatar falhas com honestidade; nunca avançar com testes quebrando.
