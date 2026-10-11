# FéDiária — Plano de arquitetura

> Fase 0 · 2026-10-10 · Decisões do dono do produto: Kotlin Multiplatform, `br.app.fediaria`,
> público 13+, minSdk 26, paleta azul mariano + dourado, repositório público, ~150 perguntas no lançamento.

## 1. Stack (e por quê, em 5 linhas)

1. **Kotlin Multiplatform + Compose Multiplatform (Material 3)**: Android agora, e ~80% do código (regras, dados, telas) reaproveitável no iOS depois, sem reescrever em outra linguagem.
2. **Room KMP + DataStore KMP**: banco SQLite local (progresso, histórico) e preferências (configurações), ambos oficiais do Google e multiplataforma → offline-first de verdade.
3. **Koin** (no lugar de Hilt, que é só Android) + **ViewModel/Navigation multiplataforma** (androidx/JetBrains) → MVVM simples, sem geração de código pesada.
4. **Lembretes: AlarmManager inexato + WorkManager como "vigia"** (só no Android, por `expect/actual`): dispensa a permissão de alarme exato, sobrevive a reinício e a mudança de fuso.
5. **kotlinx-datetime, kotlinx-serialization (conteúdo em JSON), kotlin.test + Turbine, Compose UI tests, detekt + ktlint, GitHub Actions**. Sem internet, sem backend, sem login, sem anúncios.

**Versões (Fase 1):** Kotlin 2.4.21, AGP 9.4.1, Gradle 9.8.1, Compose Multiplatform 1.12.1, Room 2.8.5, Koin 4.2.2, compileSdk 37 / targetSdk 36 / minSdk 26. iOS: só `iosArm64` + `iosSimulatorArm64` (o Compose 1.12 não suporta mais Mac Intel/`iosX64`).

**Limite importante:** o iOS só **compila num Mac**. No Linux, os alvos iOS ficam declarados mas são pulados. Como o repositório é público, o GitHub Actions oferece runners macOS gratuitos, e vamos usá-los a partir da Fase 7 só para garantir que o código comum continua compilando para iOS. O app iOS em si fica para depois do MVP.

## 2. Módulos

```
FeDiaria/
├── core/          KMP puro (sem Android, sem UI): regras de negócio testáveis
│   └── ofensiva, dia de graça, mistérios do dia, cálculo de lembretes,
│       repetição espaçada (SRS), datas litúrgicas (Páscoa/Pentecostes), progresso de novena
├── shared/        KMP: dados + UI
│   ├── data/      Room (progresso), DataStore (config), leitor de conteúdo JSON, backup
│   ├── ui/        Telas Compose, ViewModels, tema, navegação
│   ├── composeResources/values/strings.xml      ← todas as strings (pt-BR)
│   └── composeResources/files/conteudo/*.json   ← TODO o conteúdo religioso
├── androidApp/    App Android: MainActivity, Application, notificações, alarmes,
│                  receivers (boot/fuso/hora), WorkManager, export via SAF
└── (iosApp/)      Depois do MVP, num Mac
```

Regra de dependência: `androidApp → shared → core`. O `core` não conhece nada de Android nem de banco, por isso seus testes rodam em milissegundos e cobrem a lógica crítica.

## 3. Dados

**Conteúdo (somente leitura)** fica em JSON dentro do app, versionado no git e separado do código. É carregado na memória sob demanda. Cada item tem um `id` estável (ex.: `quiz.cic.n2.014`), além de `fonte` e `revisado`.

```json
{
  "id": "quiz.escrituras.n1.001",
  "trilha": "escrituras", "nivel": 1, "tipo": "multipla_escolha",
  "pergunta": "…", "alternativas": ["…", "…", "…", "…"], "correta": 2,
  "explicacao": "…",
  "referencia": { "tipo": "biblia", "livro": "Jo", "capitulo": 3, "versiculos": "16" },
  "fonte": "Pergunta original; texto bíblico parafraseado",
  "revisado": false
}
```
Referência do Catecismo: `{ "tipo": "cic", "paragrafo": 1213 }`. Do Catecismo de S. Pio X: `{ "tipo": "pio_x", "pergunta": 412 }`.

**Progresso do usuário (Room)**. As tabelas guardam só ids de conteúdo e datas:
- `dia_rezado(data LocalDate PK, origem GUIADO|MANUAL, registrado_em Instant)`
- `novena_ativa(id, novena_id, iniciada_em, lembrete_hora, concluida_em?)` + `novena_dia(novena_ativa_id, dia 1..9, rezado_em)`
- `srs_item(item_id PK, caixa 1..5, proxima_revisao LocalDate, acertos, erros)`: usado tanto pelo quiz quanto pelos flashcards de latim
- `licao_concluida(id PK, licao_id, concluida_em, acertos, total, xp)` (a revisão pode se repetir), `conquista(id, obtida_em)`

**Configurações (DataStore):** horários dos lembretes, mapeamento de mistérios, orações opcionais do terço, tema, tamanho de fonte, onboarding concluído.

**Backup:** exporta/importa um arquivo `.json` com todo o progresso, usando o seletor de arquivos do sistema (sem permissão de armazenamento). O Auto Backup do Android também fica ligado, para quem troca de celular.

## 4. Regras centrais

### Ofensiva (streak)
- O histórico é a **lista de datas locais** em que o terço foi rezado (`2026-10-10`). A ofensiva **não é um contador salvo**: ela é recalculada a partir do histórico. Assim, nunca fica inconsistente.
- A data gravada é a data **do relógio do aparelho no momento** da conclusão. "Hoje" é sempre calculado no fuso atual. Mudança de fuso ou horário de verão não altera registros passados. Uma viagem que "pule" um dia no calendário é coberta pelo dia de graça.
- **Hoje ainda não rezado não quebra nada**: o dia fica "pendente" até a meia-noite.
- Marcação manual vale para **hoje**, e também para **ontem** ("esqueci de marcar"). É um sistema de confiança e evita frustração.
- **Dia de graça:** a cada **7 dias rezados seguidos** a pessoa ganha 1 graça (guarda no máximo 2). Quando um dia passa sem terço e há graça disponível, ela é usada **automaticamente** e a ofensiva continua (o dia aparece no calendário com um ícone próprio, sem contar como rezado). Sem graça disponível, a ofensiva recomeça, e o **recorde** fica guardado para sempre.
- A oração **não vale XP**. A ofensiva é a única métrica do terço. XP existe só no quiz e no latim.

### Terço guiado
Sequência: Sinal da Cruz → (Oferecimento, opcional) → Credo → Pai-Nosso → 3 Ave-Marias → Glória → 5 × [anúncio do mistério → Pai-Nosso → 10 Ave-Marias → Glória → Ó meu Jesus] → (Agradecimento, opcional) → Salve-Rainha → Sinal da Cruz. (O Agradecimento termina com "vos saudamos com uma Salve-Rainha", por isso vem antes dela.)
Observação: o briefing não citava as 3 Ave-Marias iniciais. Incluí porque são tradicionais. A UI mostra um contador de contas, avança com toque grande ou deslize, permite voltar, retoma de onde parou e tem **vibração suave opcional**, sem sons. Mistérios por dia da semana: padrão de *Rosarium Virginis Mariae* (Gozosos seg/sáb, Dolorosos ter/sex, Gloriosos qua/dom, Luminosos qui), editável. Também há o atalho "esquema tradicional sem Luminosos".

### Lembretes (Android)
- **Sem `SCHEDULE_EXACT_ALARM`.** Usamos `AlarmManager.setWindow` com janela de 10 min (o mínimo do Android 12+): o aviso chega entre o horário e 10 min depois. Escolhido em vez de `setAndAllowWhileIdle` porque este último é "inexato" com atraso proporcional à antecedência (um alarme agendado 20h antes poderia atrasar horas). Com o aparelho em repouso profundo (Doze), o aviso sai quando o aparelho desperta, por exemplo ao ser pego, o que é aceitável para um lembrete de oração. `USE_EXACT_ALARM` é restrito pela Play a apps de despertador/agenda.
- O disparo carrega a **data-alvo**: se chegar atrasado depois da meia-noite, é descartado.
- Quando o alarme dispara, o app **consulta o banco**: só notifica se o terço de hoje ainda não foi rezado. O 2º lembrete ("sua chama está pedindo cuidado") usa a mesma checagem, num horário configurável (padrão: 21h).
- Reagendamento em: `BOOT_COMPLETED`, `TIME_SET`, `TIMEZONE_CHANGED`, `MY_PACKAGE_REPLACED`, ao abrir o app e a cada ~12h por um **WorkManager vigia** (cinto e suspensório contra fabricantes agressivos).
- Android 13+: pedimos `POST_NOTIFICATIONS` **no contexto certo** (quando a pessoa escolhe o horário), nunca na primeira tela.
- Tela "Lembretes não chegam?", com instruções por fabricante (Xiaomi, Samsung, Motorola…) e botão para as configurações de bateria. Não usamos `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` (restrita pela Play).
- Novenas: um lembrete diário por novena ativa, que se cancela sozinho quando a novena termina.
- **Permissões totais do app:** `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED` e as que o WorkManager declara. **Sem `INTERNET`**, o que deixa o formulário de Segurança dos dados trivial: "nenhum dado coletado".

### Novenas
- O progresso é **por dia rezado, não por calendário**: o "dia atual" é o próximo dia ainda não rezado. Se a pessoa perdeu dias, retoma de onde parou, sem punição e sem mensagem de culpa. A tela mostra uma previsão de término.
- O app sugere a data de início das novenas com data fixa (Natal: 16/12; Imaculada: 29/11; Aparecida: 03/10; Espírito Santo: sexta após a Ascensão, calculada a partir da Páscoa por algoritmo testado).
- Várias novenas ao mesmo tempo são permitidas.
- No máximo **um dia da novena por data** (como manda a tradição). Toque por engano pode ser desfeito no mesmo dia.
- Lembrete próprio por novena (canal de notificação separado), que para sozinho ao concluir; tocar na notificação abre a novena.
- Banco v2: tabelas `novena_ativa` e `novena_dia` (migração automática 1→2, coberta por teste instrumentado).
- Banco v3: tabelas `srs_item` e `licao_concluida` (migração automática 2→3, coberta por teste instrumentado).

### Quiz e Latim: repetição espaçada
Sistema de **Leitner** com caixas de intervalos 1, 3, 7, 14 e 30 dias. Errou, a pergunta volta para a caixa 1. Acertou, sobe uma caixa (uma pergunta nova que acerta vai direto para a 2ª). Uma lição de "Revisão" (até 10 perguntas, as mais atrasadas primeiro) aparece quando há itens vencidos. Sem vidas e sem punição: um erro mostra a explicação e a referência com calma.

**Quiz (Fase 4):** 3 trilhas (`escrituras`, `catecismo`, `fe_catolica`) × 3 níveis. As lições são fixas e derivadas da ordem do JSON: cada nível é dividido em lições equilibradas de 5 a 10 perguntas (17 → 6 + 6 + 5), com id estável `<trilha>.n<nível>.l<n>`. O nível 1 já começa aberto; os seguintes abrem quando todas as lições do anterior forem feitas (uma lição conta como feita mesmo com erros). XP = 10 por acerto, só no quiz e no latim. A ordem das alternativas é embaralhada por dia (semente = id + data), para a resposta certa não ficar sempre na mesma posição; Verdadeiro/Falso não é embaralhado. A resposta é gravada no SRS assim que é dada, então sair no meio da lição não perde nada. Referências: `biblia`, `cic` (nº do parágrafo), `cdc` (cânon) e `documento` (título + nº), validadas por `ConteudoQuizTest`.

## 5. UI

Barra inferior: **Terço** (início, com a chama) · **Novenas** · **Quiz** · **Latim**. Configurações ficam no ícone do topo. Tema Material 3 com azul mariano, dourado e fundo creme, modo escuro completo e tipografia serifada nos textos de oração. Acessibilidade: escala de fonte do sistema + ajuste interno, alvos de toque ≥ 48dp, `contentDescription` em tudo, contraste AA verificado.

## 6. Qualidade e CI
- Testes do `core` (ofensiva, graça, fuso, mistérios, SRS, Páscoa) com cobertura alta e casos extremos de data.
- Testes de UI dos fluxos: rezar o terço guiado, marcar manualmente, iniciar uma novena, fazer uma lição.
- `./gradlew check` = testes + Android lint + detekt + ktlint. O GitHub Actions roda isso a cada push. O emulador no CI entra na Fase 7.
