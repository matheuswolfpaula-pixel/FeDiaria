# FéDiária — Roadmap

Ritmo: **uma fase por vez** → implementar → `./gradlew check` verde → instruções de teste no emulador/celular → commit → **esperar o "ok" do dono do produto**.

Legenda: 👤 = depende de ação sua.

---

## Fase 0 — Planejamento ✅
Briefing, decisões, arquitetura (`docs/ARQUITETURA.md`), este roadmap, `CLAUDE.md`.

## Fase 1 — Esqueleto ✅ (aguardando ok)
- 👤 Instalar o JDK 21 (`sudo apt install openjdk-21-jdk`) e configurar a identidade do git.
- Android SDK + cmdline-tools + emulador em `~/Android/Sdk` (eu faço, sem sudo) e AVD Pixel com Android 15/16.
- Projeto KMP (`core`, `shared`, `androidApp`), Gradle wrapper, catálogo de versões, Koin, Room KMP (banco vazio + migrações), DataStore, tema azul mariano/dourado (claro/escuro), navegação com as 4 abas e telas placeholder, `strings.xml` pt-BR.
- detekt + ktlint + Android lint, GitHub Actions (build + testes + lint).
- 👤 Criar o repositório público no GitHub (posso criar via `gh`, com sua autorização).

**Aceite:** `./gradlew check` verde localmente e no CI · o app abre no emulador · as 4 abas navegam · o tema escuro segue o sistema · nenhuma permissão além do previsto no manifesto final.

## Fase 2 — Terço + ofensiva + lembretes
- Terço guiado completo (contas, contador, mistérios do dia, retomar, vibração opcional) e marcação manual (hoje/ontem).
- Ofensiva com chama animada, dia de graça, recorde, calendário/histórico, conquistas simples.
- Lembrete diário + 2º lembrete condicional, reagendamento (boot/fuso/hora/atualização), WorkManager vigia, pedido de notificação no Android 13+, tela "Lembretes não chegam?".
- Conteúdo: textos das orações do terço e dos 20 mistérios (JSON, `revisado: false`). Início do `docs/CONTEUDO_PARA_REVISAO.md`.

**Aceite:** testes unitários cobrindo ofensiva (sequência, quebra, graça ganha/usada/limite 2, pendente hoje, ontem manual, mudança de fuso, horário de verão, virada de ano) e agendamento (próximo disparo, 2º lembrete suprimido se já rezou) · teste de UI "rezar terço inteiro → ofensiva +1" · no emulador: lembrete chega, some se já rezou, sobrevive a reinício.

## Fase 3 — Novenas
- Catálogo com 6 novenas: Espírito Santo, Imaculada Conceição, Natal, N. Sra. Aparecida, São José, Sagrado Coração. Divina Misericórdia é candidata a 7ª, com o texto original de S. Faustina descartado por direitos autorais.
- Iniciar em uma data, texto do dia 1–9, marcar rezado, retomar sem punir, várias simultâneas, lembrete por novena, datas sugeridas (incl. cálculo da Páscoa).

**Aceite:** testes de progresso/retomada e do cálculo da Páscoa (2000–2100 contra tabela conhecida) · teste de UI "iniciar novena → marcar dia 1" · lembrete para quando a novena termina.

## Fase 4 — Quiz
- 3 trilhas (Escrituras, Catecismo, Fé Católica) × 3 níveis, ~150 perguntas originais com explicação e referência, lições de 5–10 perguntas, XP, feedback imediato, revisão espaçada.
- 👤 **Sugestão:** criar a conta de desenvolvedor Google Play nesta fase. A verificação de identidade leva dias, e o teste fechado obrigatório (Fase 8) leva semanas.

**Aceite:** testes do SRS e da montagem de lições · validador automático do JSON (todo item tem `fonte`, `revisado` e `referencia` válidos, sem ids duplicados) rodando no `check` · teste de UI de uma lição completa.

## Fase 5 — Latim
- 10 orações: Signum Crucis, Pater Noster, Ave Maria, Gloria Patri, Credo (Apostólico), Salve Regina, Angelus, Anima Christi, Veni Sancte Spiritus, Sub tuum praesidium. *Ad Maiorem Dei Gloriam* entra como jaculatória/lema, não como oração.
- Leitura frase a frase com tradução e pronúncia eclesiástica, flashcards (SRS), exercícios (lacuna, ordenar, associar), modo "rezar em latim". Sem áudio (v2).

**Aceite:** testes dos geradores de exercício · teste de UI do fluxo de uma oração · todo o conteúdo no JSON com `fonte`/`revisado`.

## Fase 6 — Polimento
Onboarding (3 telas: propósito, horário do lembrete, notificação), Configurações completas, exportar/importar backup, acessibilidade (TalkBack, fonte grande, contraste), ícone adaptativo, splash, revisão do modo escuro, conquistas.

**Aceite:** varredura do TalkBack em todas as telas · fonte 200% sem cortes · Accessibility Scanner sem erros graves · backup exportado e reimportado em instalação limpa.

## Fase 7 — Robustez
Testes de casos extremos de notificação (Doze via `adb`, app parado à força, troca de fuso), testes instrumentados no CI com emulador, job macOS compilando o código comum para iOS, Baseline Profile, medição de tamanho do AAB, verificação de vazamentos/ANR.

**Aceite:** CI completo verde · AAB com menos de ~15 MB · tempo de abertura medido e registrado · checklist de casos de notificação documentado e aprovado em aparelho real (👤 seu celular).

## Fase 8 — Publicação na Google Play
Pesquisa dos requisitos **atuais** da Play (targetSdk, teste fechado, Segurança dos dados, IARC, verificação de desenvolvedor), applicationId `br.app.fediaria`, versionamento, R8/shrink, assinatura de release (👤 keystore gerada por você), AAB validado via bundletool, política de privacidade (LGPD) em GitHub Pages, textos da ficha, lista de capturas, `docs/PUBLICACAO.md`, plano de teste fechado → produção.

**Aceite:** AAB assinado instalado e testado em aparelho real · política no ar com URL pública · checklist 100% preenchido · lista "só você pode fazer" entregue.

---

## Caminho crítico fora do código (👤)
1. **Revisão do conteúdo** por um sacerdote ou catequista. É o maior gargalo; dá para começar a enviar o material a partir da Fase 2.
2. **Conta Google Play + verificação de identidade**: começar na Fase 4.
3. **Testadores do teste fechado**: contas pessoais costumam exigir um mínimo de testadores por um mínimo de dias (os números atuais serão confirmados na Fase 8). Comece a listar pessoas da paróquia/grupo já.
