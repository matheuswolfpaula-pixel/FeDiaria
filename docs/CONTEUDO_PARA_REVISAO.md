# Conteúdo para revisão humana

Todo o conteúdo religioso do FéDiária precisa ser conferido por um **sacerdote ou catequista** antes da
publicação. Cada item está em JSON com o campo `revisado: false`. Depois de revisado, mude para `true`
(e corrija o texto, se preciso). O app só deve ir para a loja com **todos** os itens revisados.

**Como revisar:** leia os textos abaixo (ou direto no app, no terço guiado) e anote correções por id.
Arquivo-fonte: `shared/src/commonMain/composeResources/files/conteudo/`.

---

## 1. Terço (`terco.json`): Fase 2

### 1.1 Orações (10 itens)

| id | Oração | Fonte declarada | Pontos a conferir |
|---|---|---|---|
| `pelo_sinal` | Sinal da Cruz ("Pelo sinal...") | Piedade popular (domínio público) | Uso do "Pelo sinal" na abertura do terço; pontuação |
| `oferecimento` | Oferecimento do terço | Devocionários brasileiros | **Redação varia entre devocionários.** A menção às indulgências está correta/adequada? |
| `credo` | Creio (Símbolo dos Apóstolos) | Texto tradicional | Conferir com a forma usada no Brasil ("desceu à mansão dos mortos") |
| `pai_nosso` | Pai-Nosso | Mt 6,9-13, forma tradicional | Forma litúrgica atual no Brasil |
| `ave_maria` | Ave-Maria | Tradicional | "o Senhor é convosco" / "Mãe de Deus" |
| `gloria` | Glória | Gloria Patri | Variante com "e pelos séculos dos séculos" |
| `o_meu_jesus` | Ó meu Jesus | Oração de Fátima (1917) | Forma usual no Brasil |
| `agradecimento` | Agradecimento | Devocionários brasileiros | **Redação varia.** Confirmar a forma e o uso antes da Salve-Rainha |
| `salve_rainha` | Salve-Rainha | Salve Regina (séc. XI) | Tradução tradicional; versículo/resposta final |
| `sinal_da_cruz` | Sinal da Cruz (final) | Tradicional | (sem pontos específicos) |

### 1.2 Mistérios (20 itens)

| id | Mistério | Referência | Pontos a conferir |
|---|---|---|---|
| `gozosos.1` | Anunciação do Anjo a Nossa Senhora | Lc 1,26-38 | |
| `gozosos.2` | Visitação a Isabel | Lc 1,39-56 | |
| `gozosos.3` | Nascimento de Jesus em Belém | Lc 2,1-20 | |
| `gozosos.4` | Apresentação no Templo | Lc 2,22-38 | |
| `gozosos.5` | Perda e Encontro no Templo | Lc 2,41-52 | |
| `luminosos.1` | Batismo no Jordão | Mt 3,13-17 | |
| `luminosos.2` | Bodas de Caná | Jo 2,1-12 | |
| `luminosos.3` | Anúncio do Reino e convite à conversão | Mc 1,14-15 | |
| `luminosos.4` | Transfiguração | Lc 9,28-36 | |
| `luminosos.5` | Instituição da Eucaristia | Lc 22,14-20 | |
| `dolorosos.1` | Agonia no Horto | Lc 22,39-46 | |
| `dolorosos.2` | Flagelação | Jo 19,1 | Referência curta (um versículo); suficiente? |
| `dolorosos.3` | Coroação de espinhos | Mt 27,27-31 | |
| `dolorosos.4` | Jesus carrega a cruz | Jo 19,16-17 | |
| `dolorosos.5` | Crucifixão e morte | Lc 23,33-46 | |
| `gloriosos.1` | Ressurreição | Lc 24,1-12 | |
| `gloriosos.2` | Ascensão | At 1,6-11 | |
| `gloriosos.3` | Vinda do Espírito Santo | At 2,1-13 | |
| `gloriosos.4` | Assunção de Nossa Senhora | Dogma: *Munificentissimus Deus* (Pio XII, 1950) | **Sem passagem bíblica direta**; citamos o dogma. Adequado? |
| `gloriosos.5` | Coroação de Nossa Senhora | Ap 12,1 | Referência tradicionalmente associada; adequada? |

### 1.3 Regras litúrgicas usadas no código (não estão no JSON)

- **Esquema padrão** (*Rosarium Virginis Mariae*, 2002): seg/sáb Gozosos · ter/sex Dolorosos · qua/dom Gloriosos · qui Luminosos.
- **Esquema tradicional** (antes de 2002), opcional: seg/qui Gozosos · ter/sex Dolorosos · qua/sáb/dom Gloriosos.
  *Conferir:* existem variações tradicionais para os domingos do Advento e da Quaresma; o app **não** as aplica.
- **Sequência do terço:** Pelo sinal → (Oferecimento) → Credo → Pai-Nosso → 3 Ave-Marias (fé, esperança, caridade) → Glória → 5 dezenas [mistério, Pai-Nosso, 10 Ave-Marias, Glória, Ó meu Jesus] → (Agradecimento) → Salve-Rainha → Sinal da Cruz.

---

## 2. Novenas (`novenas.json`): Fase 3

Seis novenas, 9 dias cada (54 meditações + 18 orações). **Meditações, orações iniciais/finais marcadas como "Texto original" foram escritas para o app**; precisam de leitura doutrinal. As referências bíblicas são só citações (sem transcrição de tradução). Frases curtas entre aspas nas meditações são **paráfrases** dos versículos, não transcrições de uma tradução: conferir o sentido.

**Ponto geral de atenção:** a numeração bíblica segue as Bíblias católicas atuais (ex.: Is 9,1; Mq 5,1); os Salmos aparecem como `Sl 19(18)` (hebraica/grega-Vulgata).

### Novena ao Espírito Santo (`espirito_santo`)

Janela: sexta após a Ascensão (Páscoa + 40) até a véspera de Pentecostes. Conferir a lista dos 7 dons e a redação do "Vinde, Espírito Santo".

| Item | Fonte declarada |
|---|---|
| Oração inicial: Vinde, Espírito Santo | Oração tradicional da Igreja (domínio público); redação usual em português a conferir |
| Oração final: Oração final | Texto original do FéDiária |
| Jaculatória: Jaculatória | Invocação tradicional (domínio público) |

| id | Tema | Referência |
|---|---|---|
| `espirito_santo.1` | A promessa do Consolador | Jo 14,16-17 |
| `espirito_santo.2` | O dom da sabedoria | Tg 1,5 |
| `espirito_santo.3` | O dom do entendimento | Lc 24,45 |
| `espirito_santo.4` | O dom do conselho | Jo 14,26 |
| `espirito_santo.5` | O dom da fortaleza | At 1,8 |
| `espirito_santo.6` | O dom da ciência | Sl 19(18),2 |
| `espirito_santo.7` | O dom da piedade | Rm 8,14-16 |
| `espirito_santo.8` | O dom do temor de Deus | Pr 9,10 |
| `espirito_santo.9` | Os frutos do Espírito | Gl 5,22-23 |

### Novena à Imaculada Conceição (`imaculada_conceicao`)

Conferir a tradução do "Lembrai-vos" (Memorare) e a menção ao dogma de 1854 (Pio IX).

| Item | Fonte declarada |
|---|---|
| Oração inicial: Lembrai-vos | Memorare, oração tradicional atribuída a São Bernardo (domínio público); tradução usual a conferir |
| Oração final: Oração final | Texto original do FéDiária |
| Jaculatória: Jaculatória | Jaculatória da Medalha Milagrosa (1830), domínio público |

| id | Tema | Referência |
|---|---|---|
| `imaculada_conceicao.1` | Escolhidos antes da criação | Ef 1,3-4 |
| `imaculada_conceicao.2` | A primeira promessa | Gn 3,15 |
| `imaculada_conceicao.3` | Cheia de graça | Lc 1,28 |
| `imaculada_conceicao.4` | Faça-se em mim | Lc 1,38 |
| `imaculada_conceicao.5` | Bendita entre as mulheres | Lc 1,42 |
| `imaculada_conceicao.6` | A alma que engrandece o Senhor | Lc 1,46-49 |
| `imaculada_conceicao.7` | Guardava tudo no coração | Lc 2,19 |
| `imaculada_conceicao.8` | Mãe ao pé da cruz | Jo 19,25-27 |
| `imaculada_conceicao.9` | A mulher vestida de sol | Ap 12,1 |

### Novena de Natal de Santo Afonso (`natal`)

Substitui a novena de Natal original do app (a pedido do dono). **Tradução do FéDiária**, feita a partir do texto italiano de Santo Afonso Maria de Ligório (1696-1787, domínio público). Fonte usada: edição italiana em Documenta Catholica Omnia (`1696-1787,_De_Ligorio_Alphonsus,_Novena_di_Natale,_IT.doc`), que reproduz uma edição moderna (floscarmeli.org) com citações bíblicas acrescentadas pelo editor. Conferir com uma edição crítica das *Opere ascetiche*, se possível.

Pontos para o revisor:
- **Fidelidade da tradução:** traduzido com "vós" (como as demais orações do app); o original trata Jesus por "tu".
- **Coroinha inicial:** o original traz "Pai-Nosso, Ave-Maria, Glória" só depois da 1ª invocação, e depois "Abbi pietà, ecc." Não está claro se as três orações se repetem a cada invocação; o app segue o texto literalmente.
- **Sem Pai-Nosso/Ave-Maria/Glória após a meditação:** o original não manda rezá-los nesse ponto, então essa seção não aparece nesta novena.
- **Oração final e jaculatória** não fazem parte das nove meditações: vêm de outras devoções do mesmo livro ("Graus da infância de Jesus Cristo" e "Coroinha em honra do Menino Jesus"). A jaculatória é tradução livre, em prosa, de versos rimados.
- **Numeração:** Is 9,5 segue as Bíblias modernas (em outras é 9,6).
- **Erro corrigido na fonte:** a meditação IV cita "Lc 2, 72", que não existe; o app usa Lc 2,12 (o sinal dado aos pastores).
- **Referências da tabela:** as de `natal.1`, `natal.2`, `natal.4`, `natal.5` e `natal.8` vêm do próprio texto. As de `natal.3` (Lc 2,1-7), `natal.6` (Tt 3,4), `natal.7` (Mt 2,13-14) e `natal.9` (Lc 2,4-7) foram escolhidas pelo app.
- **Afirmações devocionais de Santo Afonso, mantidas como estão no original:** viagem de 4 dias, 7 anos no Egito, o diálogo entre Maria e José, e as histórias da bofetada do demônio, da Irmã Maria Madalena Orsini e de Santa Margarida de Cortona. São tradição piedosa, não dados históricos.
- **Tom:** o texto é do século XVIII e fala de inferno, ingratidão e arrependimento com mais força que o resto do app. Foi mantido fiel ao original.

| Item | Fonte declarada |
|---|---|
| Oração inicial: Coroinha ao Menino Jesus | Santo Afonso, coroinha para rezar antes de cada meditação |
| Oração final: Oferecimento ao Menino Jesus | Santo Afonso, oração final dos "Graus da infância de Jesus Cristo" |
| Jaculatória: Cantiga ao Menino Jesus | Santo Afonso, versos da "Coroinha em honra do Menino Jesus" |

| id | Tema (meditação de Santo Afonso) | Referência |
|---|---|---|
| `natal.1` | O amor de Deus em fazer-se homem | Jo 1,14; Ef 5,2 |
| `natal.2` | O amor de Deus em nascer menino | Is 9,5 |
| `natal.3` | A vida pobre que Jesus começou a levar desde o nascimento | Lc 2,1-7 |
| `natal.4` | A vida humilde que Jesus começou a levar desde menino | Lc 2,12 |
| `natal.5` | A vida de sofrimentos que Jesus começou a levar desde o nascimento | Is 53,3 |
| `natal.6` | A misericórdia de Deus em vir do céu para nos salvar com a sua morte | Tt 3,4 |
| `natal.7` | A viagem do Menino Jesus para o Egito | Mt 2,13-14 |
| `natal.8` | A morada do Menino Jesus no Egito e em Nazaré | Lc 2,51 |
| `natal.9` | O nascimento do Menino Jesus na gruta de Belém | Lc 2,4-7 |

### Novena a Nossa Senhora Aparecida (`aparecida`)

**Fatos históricos a conferir:** imagem encontrada em 1717 por três pescadores no rio Paraíba do Sul; proclamada Padroeira do Brasil em 1930. Oração inicial e final são originais (a tradicional "Consagração" não foi usada por dúvida de autoria).

| Item | Fonte declarada |
|---|---|
| Oração inicial: Oração inicial | Texto original do FéDiária |
| Oração final: Oração final | Texto original do FéDiária |
| Jaculatória: Jaculatória | Invocação tradicional (domínio público) |

| id | Tema | Referência |
|---|---|---|
| `aparecida.1` | A rede vazia | Lc 5,4-6 |
| `aparecida.2` | Fazei tudo o que Ele vos disser | Jo 2,1-5 |
| `aparecida.3` | Mãe dos humildes | Lc 1,52-53 |
| `aparecida.4` | Em oração com a Igreja | At 1,14 |
| `aparecida.5` | Eis a tua mãe | Jo 19,26-27 |
| `aparecida.6` | Mãe peregrina | Lc 1,39-40 |
| `aparecida.7` | Feliz aquela que acreditou | Lc 1,45 |
| `aparecida.8` | Gratidão pelos benefícios | Sl 103(102),2 |
| `aparecida.9` | Ouvir e guardar a Palavra | Lc 11,27-28 |

### Novena a São José (`sao_jose`)

Conferir a tradução da oração "A vós, São José" (Leão XIII, 1889) e a menção a Pio IX (Padroeiro da Igreja Universal, 1870). Dia 8 cita a tradição da "boa morte" (não é relato bíblico).

| Item | Fonte declarada |
|---|---|
| Oração inicial: A vós, São José | Oração do Papa Leão XIII (encíclica Quamquam pluries, 1889), domínio público; tradução usual em português a conferir |
| Oração final: Oração final | Texto original do FéDiária |
| Jaculatória: Jaculatória | Invocação tradicional (domínio público) |

| id | Tema | Referência |
|---|---|---|
| `sao_jose.1` | O homem justo | Mt 1,19 |
| `sao_jose.2` | Obediente à voz de Deus | Mt 1,24 |
| `sao_jose.3` | Guardião em Belém | Lc 2,4-7 |
| `sao_jose.4` | A fuga para o Egito | Mt 2,13-15 |
| `sao_jose.5` | O carpinteiro de Nazaré | Mt 13,55 |
| `sao_jose.6` | Pai que procura | Lc 2,48 |
| `sao_jose.7` | A vida escondida em Nazaré | Lc 2,51-52 |
| `sao_jose.8` | Padroeiro da boa morte | Sl 116(115),15 |
| `sao_jose.9` | Ide a José | Gn 41,55 |

### Novena ao Sagrado Coração de Jesus (`sagrado_coracao`)

Janela: Páscoa + 59 (termina na véspera da solenidade, Páscoa + 68). Conferir menção a Santa Margarida Maria Alacoque.

| Item | Fonte declarada |
|---|---|
| Oração inicial: Oração inicial | Texto original do FéDiária |
| Oração final: Oração final | Texto original do FéDiária |
| Jaculatória: Jaculatória | Jaculatória tradicional (domínio público) |

| id | Tema | Referência |
|---|---|---|
| `sagrado_coracao.1` | Vinde a mim | Mt 11,28-30 |
| `sagrado_coracao.2` | Amou-os até o fim | Jo 13,1 |
| `sagrado_coracao.3` | O Bom Pastor | Jo 10,11 |
| `sagrado_coracao.4` | A ovelha perdida | Lc 15,4-7 |
| `sagrado_coracao.5` | Compaixão pelas multidões | Mt 9,36 |
| `sagrado_coracao.6` | O coração traspassado | Jo 19,33-34 |
| `sagrado_coracao.7` | Rios de água viva | Jo 7,37-38 |
| `sagrado_coracao.8` | Permanecei no meu amor | Jo 15,9 |
| `sagrado_coracao.9` | Deus é amor | 1Jo 4,16 |

**Fora do catálogo:** Novena da Divina Misericórdia: os textos de Santa Faustina têm direitos autorais; só entraria com texto próprio, após decisão.

### Regras usadas no código (não estão no JSON)

- Progresso por dia rezado (não por calendário); no máximo um dia da novena por data; dias perdidos só adiam o término.
- Datas móveis calculadas a partir da Páscoa (algoritmo de Meeus, conferido contra o de Oudin de 1583 a 4099).
---

## 3. Quiz (`quiz.json`): Fase 4

153 perguntas **originais** do FéDiária, em 3 trilhas × 3 níveis. Cada uma tem explicação e referência. Escritura só parafraseada; Catecismo citado só pelo número do parágrafo, nunca pelo texto.

Pontos para o revisor:
- **Conferir cada número de parágrafo e de cânon.** Usei só números que conheço com segurança, mas este é o ponto mais sensível do quiz.
- **CIC** = Catecismo da Igreja Católica (edição típica de 1997). **CDC** = Código de Direito Canônico (1983). A Instrução Geral do Missal Romano segue a numeração da 3ª edição típica (2002).
- **Regras que variam por país:** jejum e abstinência e festas de preceito podem ser adaptados pela CNBB. As explicações avisam isso, mas vale confirmar se o texto está adequado ao Brasil.
- **Alternativas:** a ordem é embaralhada a cada dia no app; a coluna "Resposta" mostra só a certa.
- **Tom:** quem erra vê "Quase! A resposta é: …", com a explicação e a referência. Sem vidas e sem punição.

### Escrituras · nível 1

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.escrituras.n1.001` | Quantos livros tem a Bíblia católica? | 73 | CIC § 120 |
| `quiz.escrituras.n1.002` | Qual é o primeiro livro da Bíblia? | Gênesis | Gn 1,1 |
| `quiz.escrituras.n1.003` | Quem construiu a arca por ordem de Deus, antes do dilúvio? | Noé | Gn 6,13-14 |
| `quiz.escrituras.n1.004` | Em que cidade Jesus nasceu? | Belém | Lc 2,4-7 |
| `quiz.escrituras.n1.005` | Quantos apóstolos Jesus escolheu? | 12 | Mc 3,13-19 |
| `quiz.escrituras.n1.006` | Quem foi lançado na cova dos leões e saiu ileso? | Daniel | Dn 6 |
| `quiz.escrituras.n1.007` | Quem venceu o gigante Golias? | Davi | 1Sm 17 |
| `quiz.escrituras.n1.008` | Qual era o ofício de São José, segundo o Evangelho? | Carpinteiro | Mt 13,55 |
| `quiz.escrituras.n1.009` | Qual foi o primeiro sinal (milagre) de Jesus narrado no Evangelho de João? | A transformação da água em vinho em Caná | Jo 2,1-11 |
| `quiz.escrituras.n1.010` | Qual apóstolo negou conhecer Jesus três vezes? | Pedro | Mt 26,69-75 |
| `quiz.escrituras.n1.011` | Em que rio Jesus foi batizado por João Batista? | Jordão | Mc 1,9 |
| `quiz.escrituras.n1.012` | Quem recebeu de Deus as tábuas da Lei no monte Sinai? | Moisés | Ex 31,18 |
| `quiz.escrituras.n1.013` | O Novo Testamento começa com os quatro Evangelhos. | Verdadeiro | CIC § 125 |
| `quiz.escrituras.n1.014` | Qual apóstolo traiu Jesus? | Judas Iscariotes | Mt 26,14-16 |
| `quiz.escrituras.n1.015` | Qual anjo anunciou a Maria que ela seria a mãe do Salvador? | Gabriel | Lc 1,26-27 |
| `quiz.escrituras.n1.016` | Quantos dias Jesus jejuou no deserto? | 40 | Mt 4,1-2 |
| `quiz.escrituras.n1.017` | Qual oração Jesus ensinou aos discípulos? | Pai-Nosso | Mt 6,9-13 |

### Escrituras · nível 2

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.escrituras.n2.001` | Quantos livros tem o Antigo Testamento na Bíblia católica? | 46 | CIC § 120 |
| `quiz.escrituras.n2.002` | Qual destes livros está na Bíblia católica, mas não na maioria das Bíblias protestantes? | Tobias | CIC § 120 |
| `quiz.escrituras.n2.003` | A quem são atribuídas as cartas aos Romanos, aos Coríntios e aos Gálatas? | São Paulo | Rm 1,1 |
| `quiz.escrituras.n2.004` | Qual Evangelho começa dizendo que "no princípio era o Verbo"? | João | Jo 1,1 |
| `quiz.escrituras.n2.005` | Em que festa o Espírito Santo desceu sobre os apóstolos reunidos? | Pentecostes | At 2,1-4 |
| `quiz.escrituras.n2.006` | Quem subiu numa árvore para conseguir ver Jesus passar? | Zaqueu | Lc 19,1-10 |
| `quiz.escrituras.n2.007` | Como é conhecida a parábola do filho que gasta a herança e volta para casa do pai? | Filho pródigo | Lc 15,11-32 |
| `quiz.escrituras.n2.008` | Quem foi vendido pelos irmãos e depois se tornou governador no Egito? | José | Gn 37,28 |
| `quiz.escrituras.n2.009` | Qual profeta passou três dias no ventre de um grande peixe? | Jonas | Jn 2 |
| `quiz.escrituras.n2.010` | Qual destes livros NÃO faz parte do Pentateuco (os cinco primeiros livros)? | Josué | Js 1,1-2 |
| `quiz.escrituras.n2.011` | A quem Jesus disse: "tu és Pedro, e sobre esta pedra edificarei a minha Igreja"? | Simão | Mt 16,16-18 |
| `quiz.escrituras.n2.012` | Quem é o primeiro mártir cristão narrado nos Atos dos Apóstolos? | Estêvão | At 7,54-60 |
| `quiz.escrituras.n2.013` | A caminho de qual cidade Saulo se converteu ao encontrar Jesus ressuscitado? | Damasco | At 9,1-9 |
| `quiz.escrituras.n2.014` | Qual rei construiu o primeiro Templo de Jerusalém? | Salomão | 1Rs 6 |
| `quiz.escrituras.n2.015` | Que mulher moabita disse à sogra: "o teu povo será o meu povo, e o teu Deus será o meu Deus"? | Rute | Rt 1,16 |
| `quiz.escrituras.n2.016` | Quais são os dois maiores mandamentos, segundo Jesus? | Amar a Deus de todo o coração e amar o próximo como a si mesmo | Mt 22,36-40 |
| `quiz.escrituras.n2.017` | Qual destes NÃO é um dos quatro evangelistas? | Paulo | CIC § 120 |

### Escrituras · nível 3

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.escrituras.n3.001` | Qual sumo sacerdote interrogou Jesus, pedindo que dissesse se era o Cristo? | Caifás | Mt 26,57-63 |
| `quiz.escrituras.n3.002` | Qual livro narra a resistência de Matatias e seus filhos contra a imposição de cultos pagãos? | 1 Macabeus | 1Mc 2 |
| `quiz.escrituras.n3.003` | Qual profeta anunciou que a virgem conceberia e daria à luz um filho chamado Emanuel? | Isaías | Is 7,14 |
| `quiz.escrituras.n3.004` | Segundo a tradição, quem escreveu os Atos dos Apóstolos? | Lucas | At 1,1 |
| `quiz.escrituras.n3.005` | Em qual Evangelho está o cântico de Maria conhecido como Magnificat? | Lucas | Lc 1,46-55 |
| `quiz.escrituras.n3.006` | Qual patriarca foi chamado por Deus a deixar sua terra e recebeu a promessa de uma grande descendência? | Abraão | Gn 12,1-3 |
| `quiz.escrituras.n3.007` | Quem eram os pais de João Batista? | Zacarias e Isabel | Lc 1,5-13 |
| `quiz.escrituras.n3.008` | Quais apóstolos testemunharam a Transfiguração de Jesus? | Pedro, Tiago e João | Mt 17,1-2 |
| `quiz.escrituras.n3.009` | Qual livro do Antigo Testamento é uma coleção de orações cantadas, muito usada na liturgia? | Salmos | CIC § 2585 |
| `quiz.escrituras.n3.010` | Qual profetisa idosa louvou a Deus e falou do Menino Jesus no Templo? | Ana | Lc 2,36-38 |
| `quiz.escrituras.n3.011` | A Carta aos Hebreus apresenta Jesus sobretudo como: | Sumo sacerdote | Hb 4,14 |
| `quiz.escrituras.n3.012` | Quem foi escolhido para ocupar o lugar de Judas entre os Doze? | Matias | At 1,21-26 |
| `quiz.escrituras.n3.013` | Segundo o próprio livro, onde estava o João que recebeu as visões do Apocalipse? | Na ilha de Patmos | Ap 1,9 |
| `quiz.escrituras.n3.014` | Qual rei da Pérsia permitiu que os judeus voltassem do exílio para reconstruir o Templo? | Ciro | Esd 1,1-3 |
| `quiz.escrituras.n3.015` | Que personagem do Antigo Testamento, sacerdote do Deus Altíssimo, ofereceu pão e vinho a Abraão? | Melquisedec | Gn 14,18-20 |
| `quiz.escrituras.n3.016` | Em Emaús, como os discípulos reconheceram Jesus ressuscitado? | Ao partir o pão | Lc 24,30-31 |
| `quiz.escrituras.n3.017` | Qual evangelista narra a visita dos Magos ao Menino Jesus? | Mateus | Mt 2,1-12 |

### Catecismo · nível 1

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.catecismo.n1.001` | Em quantas partes está dividido o Catecismo da Igreja Católica? | 4 | CIC § 13 |
| `quiz.catecismo.n1.002` | Quantos são os sacramentos? | 7 | CIC § 1113 |
| `quiz.catecismo.n1.003` | Qual sacramento é a porta de entrada para a vida cristã e para os outros sacramentos? | Batismo | CIC § 1213 |
| `quiz.catecismo.n1.004` | Qual é o mistério central da fé e da vida cristã? | A Santíssima Trindade | CIC § 234 |
| `quiz.catecismo.n1.005` | A Santíssima Trindade é: | Um só Deus em três Pessoas | CIC § 253 |
| `quiz.catecismo.n1.006` | Como o Concílio Vaticano II, citado no Catecismo, chama a Eucaristia? | Fonte e ápice da vida cristã | CIC § 1324 |
| `quiz.catecismo.n1.007` | Segundo a definição clássica citada pelo Catecismo, o que é a oração? | A elevação da alma a Deus | CIC § 2559 |
| `quiz.catecismo.n1.008` | Quantos são os mandamentos da Lei de Deus? | 10 | CIC § 2056 |
| `quiz.catecismo.n1.009` | O que pede o primeiro mandamento? | Amar a Deus sobre todas as coisas | CIC § 2083 |
| `quiz.catecismo.n1.010` | Por que a Igreja chama Maria de "Mãe de Deus"? | Porque o Filho que ela concebeu é verdadeiramente Deus | CIC § 495 |
| `quiz.catecismo.n1.011` | O Credo também é chamado de: | Símbolo da fé | CIC § 187 |
| `quiz.catecismo.n1.012` | Quem é Jesus Cristo, segundo a fé católica? | Verdadeiro Deus e verdadeiro homem | CIC § 464 |
| `quiz.catecismo.n1.013` | Quais são as virtudes teologais? | Fé, esperança e caridade | CIC § 1813 |
| `quiz.catecismo.n1.014` | Qual sacramento perdoa os pecados cometidos depois do Batismo? | Penitência (Confissão) | CIC § 1422 |
| `quiz.catecismo.n1.015` | Quais são as quatro notas (características) da Igreja que professamos no Credo? | Una, santa, católica e apostólica | CIC § 811 |
| `quiz.catecismo.n1.016` | O domingo é o dia principal para celebrar a Eucaristia por ser o dia da Ressurreição do Senhor. | Verdadeiro | CIC § 2177 |
| `quiz.catecismo.n1.017` | Quais são os sacramentos da iniciação cristã? | Batismo, Confirmação e Eucaristia | CIC § 1212 |

### Catecismo · nível 2

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.catecismo.n2.001` | Quais são as virtudes cardeais? | Prudência, justiça, fortaleza e temperança | CIC § 1805 |
| `quiz.catecismo.n2.002` | Quantos são os dons do Espírito Santo? | 7 | CIC § 1831 |
| `quiz.catecismo.n2.003` | Para que um pecado seja mortal, além de matéria grave e pleno conhecimento, é preciso: | Deliberado consentimento | CIC § 1857 |
| `quiz.catecismo.n2.004` | Qual destes NÃO está entre os pecados capitais? | Medo | CIC § 1866 |
| `quiz.catecismo.n2.005` | Quais sacramentos imprimem caráter e por isso não se repetem? | Batismo, Confirmação e Ordem | CIC § 1121 |
| `quiz.catecismo.n2.006` | Como se chama a conversão de toda a substância do pão e do vinho no Corpo e no Sangue de Cristo? | Transubstanciação | CIC § 1376 |
| `quiz.catecismo.n2.007` | Quem tem consciência de pecado grave deve, antes de comungar: | Receber o sacramento da Reconciliação | CIC § 1385 |
| `quiz.catecismo.n2.008` | Quais são os sacramentos de cura? | Penitência e Unção dos Enfermos | CIC § 1421 |
| `quiz.catecismo.n2.009` | Quais são os sacramentos "a serviço da comunhão"? | Ordem e Matrimônio | CIC § 1534 |
| `quiz.catecismo.n2.010` | Por quais dois meios a Revelação divina é transmitida? | Pela Sagrada Escritura e pela Sagrada Tradição | CIC § 80 |
| `quiz.catecismo.n2.011` | A quem foi confiada a interpretação autêntica da Palavra de Deus? | Ao Magistério da Igreja: o Papa e os bispos em comunhão com ele | CIC § 85 |
| `quiz.catecismo.n2.012` | Segundo a Tradição da Igreja, quantos são os frutos do Espírito Santo? | 12 | CIC § 1832 |
| `quiz.catecismo.n2.013` | O que significa o dogma da Imaculada Conceição? | Que Maria foi preservada do pecado original desde a sua concepção | CIC § 491 |
| `quiz.catecismo.n2.014` | O que significa o dogma da Assunção de Maria? | Que Maria foi elevada em corpo e alma à glória do céu | CIC § 966 |
| `quiz.catecismo.n2.015` | O que é o purgatório, segundo o Catecismo? | A purificação dos que morrem na graça de Deus, mas ainda não plenamente purificados | CIC § 1030 |
| `quiz.catecismo.n2.016` | Como o Catecismo, citando Tertuliano, chama o Pai-Nosso? | Resumo de todo o Evangelho | CIC § 2761 |
| `quiz.catecismo.n2.017` | Em caso de necessidade, qualquer pessoa, mesmo não batizada, pode batizar validamente, se tiver a intenção de fazer o que a Igreja faz. | Verdadeiro | CIC § 1256 |

### Catecismo · nível 3

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.catecismo.n3.001` | O Credo rezado na Missa aos domingos (Niceno-Constantinopolitano) vem de quais concílios? | Niceia e Constantinopla | CIC § 195 |
| `quiz.catecismo.n3.002` | Como o Catecismo explica, de modo positivo, a frase "fora da Igreja não há salvação"? | Que toda salvação vem de Cristo-Cabeça por meio da Igreja, seu Corpo | CIC § 846 |
| `quiz.catecismo.n3.003` | Qual é o sinal sensível usado no sacramento da Unção dos Enfermos? | Óleo | CIC § 1513 |
| `quiz.catecismo.n3.004` | No rito latino, quem é o ministro originário da Confirmação? | O bispo | CIC § 1312 |
| `quiz.catecismo.n3.005` | Quais são os três graus do sacramento da Ordem? | Episcopado, presbiterado e diaconado | CIC § 1554 |
| `quiz.catecismo.n3.006` | Na Igreja latina, quem são os ministros do sacramento do Matrimônio? | Os próprios noivos | CIC § 1623 |
| `quiz.catecismo.n3.007` | Quando o Papa goza da infalibilidade? | Quando proclama por ato definitivo uma doutrina de fé ou de moral, como pastor supremo | CIC § 891 |
| `quiz.catecismo.n3.008` | A superstição e a idolatria são pecados contra qual mandamento? | Primeiro | CIC § 2110 |
| `quiz.catecismo.n3.009` | O quarto mandamento pede: | Honrar pai e mãe | CIC § 2197 |
| `quiz.catecismo.n3.010` | O que é a lei moral natural? | A lei inscrita pelo Criador no coração humano, conhecida pela razão | CIC § 1954 |
| `quiz.catecismo.n3.011` | Quais são as finalidades do matrimônio? | O bem dos cônjuges e a geração e educação dos filhos | CIC § 1601 |
| `quiz.catecismo.n3.012` | O que é uma indulgência? | A remissão, diante de Deus, da pena temporal dos pecados já perdoados quanto à culpa | CIC § 1471 |
| `quiz.catecismo.n3.013` | Quais são os três estados da Igreja, segundo a comunhão dos santos? | Peregrinos na terra, os que se purificam e os que estão na glória | CIC § 954 |
| `quiz.catecismo.n3.014` | Qual a diferença entre o culto a Deus e o culto a Maria e aos santos? | Só Deus é adorado; Maria e os santos são venerados | CIC § 971 |
| `quiz.catecismo.n3.015` | Quando acontece o juízo particular? | Logo após a morte de cada pessoa | CIC § 1022 |
| `quiz.catecismo.n3.016` | Qual destas é uma obra de misericórdia espiritual? | Ensinar os ignorantes | CIC § 2447 |
| `quiz.catecismo.n3.017` | Segundo o Catecismo, a fé e a razão podem estar em verdadeiro conflito. | Falso | CIC § 159 |

### Fé Católica · nível 1

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.fe_catolica.n1.001` | Qual é a cor litúrgica do Advento e da Quaresma? | Roxo | Instrução Geral do Missal Romano, n. 346 |
| `quiz.fe_catolica.n1.002` | Quanto tempo de jejum eucarístico se pede antes de comungar? | 1 hora | CDC cân. 919 |
| `quiz.fe_catolica.n1.003` | Qual tempo litúrgico prepara a Páscoa, durante quarenta dias? | Quaresma | CIC § 540 |
| `quiz.fe_catolica.n1.004` | Em que dia da semana Jesus ressuscitou, e por isso os cristãos se reúnem para a Missa? | Domingo | CIC § 2174 |
| `quiz.fe_catolica.n1.005` | Qual celebração abre a Quaresma, com jejum e abstinência? | Quarta-feira de Cinzas | CDC cân. 1251 |
| `quiz.fe_catolica.n1.006` | Como se chama a oração que recorda o anúncio do anjo a Maria e a Encarnação, tradicionalmente rezada ao meio-dia? | Ângelus | Lc 1,26-38 |
| `quiz.fe_catolica.n1.007` | A fórmula "em nome do Pai, e do Filho e do Espírito Santo" vem de qual ordem de Jesus? | Do envio dos discípulos para batizar todos os povos | Mt 28,19 |
| `quiz.fe_catolica.n1.008` | O que a Igreja celebra no Natal? | O nascimento de Jesus | Lc 2,1-14 |
| `quiz.fe_catolica.n1.009` | Contando o domingo da Páscoa, em que dia do Tempo Pascal se celebra Pentecostes? | 50º dia | CIC § 731 |
| `quiz.fe_catolica.n1.010` | Quem é o Papa? | O bispo de Roma, sucessor de Pedro | CIC § 882 |
| `quiz.fe_catolica.n1.011` | Como se chamam sinais sagrados como a água benta, as bênçãos e o terço? | Sacramentais | CIC § 1667 |
| `quiz.fe_catolica.n1.012` | Qual oração começa com "Creio em Deus Pai todo-poderoso, criador do céu e da terra"? | Símbolo dos Apóstolos (Credo) | CIC § 194 |
| `quiz.fe_catolica.n1.013` | Qual é o primeiro mandamento da Igreja? | Participar da Missa aos domingos e festas de guarda | CIC § 2042 |
| `quiz.fe_catolica.n1.014` | Segundo os mandamentos da Igreja, com que frequência mínima o fiel deve se confessar? | Ao menos uma vez por ano | CIC § 2042 |
| `quiz.fe_catolica.n1.015` | Em que época o fiel deve comungar, ao menos uma vez por ano? | Na Páscoa | CIC § 2042 |
| `quiz.fe_catolica.n1.016` | Quais são as duas grandes partes da Missa? | Liturgia da Palavra e Liturgia Eucarística | CIC § 1346 |
| `quiz.fe_catolica.n1.017` | O Advento é o tempo em que a Igreja se prepara para o Natal, revivendo a espera do Messias. | Verdadeiro | CIC § 524 |

### Fé Católica · nível 2

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.fe_catolica.n2.001` | Em que dia da semana a Igreja pede uma penitência especial, como a abstinência de carne? | Sexta | CDC cân. 1251 |
| `quiz.fe_catolica.n2.002` | Pelo Código de Direito Canônico, em que idade a lei do jejum obriga? | Dos 18 anos até o início dos 60 | CDC cân. 1252 |
| `quiz.fe_catolica.n2.003` | Qual é a cor litúrgica do Tempo Comum? | Verde | Instrução Geral do Missal Romano, n. 346 |
| `quiz.fe_catolica.n2.004` | Qual cor se usa nas festas dos mártires? | Vermelho | Instrução Geral do Missal Romano, n. 346 |
| `quiz.fe_catolica.n2.005` | Quem é o ministro ordinário da Sagrada Comunhão? | O bispo, o presbítero e o diácono | CDC cân. 910 |
| `quiz.fe_catolica.n2.006` | Quantas vezes no mesmo dia é possível comungar? | Até duas, desde que a segunda seja participando da Missa | CDC cân. 917 |
| `quiz.fe_catolica.n2.007` | Quem é o ministro ordinário do Batismo? | O bispo, o presbítero e o diácono | CDC cân. 861 |
| `quiz.fe_catolica.n2.008` | Pelo Código de Direito Canônico, qual a idade mínima para ser padrinho de Batismo? | 16 anos | CDC cân. 874 |
| `quiz.fe_catolica.n2.009` | Quantos padrinhos o batizando pode ter? | Um padrinho, ou uma madrinha, ou um padrinho e uma madrinha | CDC cân. 873 |
| `quiz.fe_catolica.n2.010` | O padre pode revelar o que ouviu em confissão? | Nunca: o sigilo sacramental é absoluto | CIC § 1467 |
| `quiz.fe_catolica.n2.011` | O que é a Liturgia das Horas? | A oração pública da Igreja que santifica o dia | CIC § 1174 |
| `quiz.fe_catolica.n2.012` | Qual Papa propôs acrescentar os Mistérios Luminosos ao terço? | João Paulo II | Carta apostólica Rosarium Virginis Mariae, n. 19 |
| `quiz.fe_catolica.n2.013` | Em quais tempos litúrgicos não se canta o Glória na Missa de domingo? | Advento e Quaresma | Instrução Geral do Missal Romano, n. 53 |
| `quiz.fe_catolica.n2.014` | Em qual tempo litúrgico não se canta o Aleluia? | Quaresma | Instrução Geral do Missal Romano, n. 62 |
| `quiz.fe_catolica.n2.015` | Quem pode receber a Unção dos Enfermos? | O fiel que começa a correr perigo por doença ou velhice | CIC § 1514 |
| `quiz.fe_catolica.n2.016` | Quem já recebeu a Unção dos Enfermos pode recebê-la de novo se, curado, voltar a adoecer gravemente. | Verdadeiro | CIC § 1515 |
| `quiz.fe_catolica.n2.017` | O que se celebra na solenidade de Corpus Christi? | O Santíssimo Corpo e Sangue de Cristo | CDC cân. 1246 |

### Fé Católica · nível 3

| id | Pergunta | Resposta | Referência |
|---|---|---|---|
| `quiz.fe_catolica.n3.001` | Qual é a maior festa do ano litúrgico, chamada de "festa das festas"? | Páscoa | CIC § 1169 |
| `quiz.fe_catolica.n3.002` | Segundo o critério do Concílio de Niceia, quando se celebra a Páscoa? | No domingo após a primeira lua cheia depois do equinócio de primavera (hemisfério norte) | CIC § 1170 |
| `quiz.fe_catolica.n3.003` | Qual concílio, em 325, afirmou que o Filho é consubstancial ao Pai, contra Ário? | Niceia | CIC § 465 |
| `quiz.fe_catolica.n3.004` | Qual concílio, em 431, proclamou Maria Mãe de Deus (Theotokos)? | Éfeso | CIC § 466 |
| `quiz.fe_catolica.n3.005` | Qual concílio, em 451, ensinou que Cristo tem duas naturezas, divina e humana, numa só Pessoa? | Calcedônia | CIC § 467 |
| `quiz.fe_catolica.n3.006` | Qual Papa proclamou o dogma da Imaculada Conceição, em 1854? | Pio IX | CIC § 491 |
| `quiz.fe_catolica.n3.007` | Qual Papa proclamou o dogma da Assunção de Maria, em 1950? | Pio XII | CIC § 966 |
| `quiz.fe_catolica.n3.008` | Qual destes concílios ensinou solenemente que os sacramentos são sete e foram instituídos por Cristo? | Trento | CIC § 1114 |
| `quiz.fe_catolica.n3.009` | Segundo o Código de Direito Canônico, qual destas é uma festa de preceito na Igreja universal? | Imaculada Conceição | CDC cân. 1246 |
| `quiz.fe_catolica.n3.010` | O que é o "depósito da fé"? | O patrimônio sagrado da fé, contido na Escritura e na Tradição, confiado à Igreja | CIC § 84 |
| `quiz.fe_catolica.n3.011` | O que é o "cânon" das Escrituras? | A lista dos livros reconhecidos como inspirados | CIC § 120 |
| `quiz.fe_catolica.n3.012` | Qual ensinamento de Jesus é conhecido como "regra de ouro"? | Tudo o que quereis que os outros vos façam, fazei-o vós a eles | Mt 7,12 |
| `quiz.fe_catolica.n3.013` | O diaconato permanente pode ser conferido a homens casados. | Verdadeiro | CIC § 1571 |
| `quiz.fe_catolica.n3.014` | Quem pode receber validamente a sagrada ordenação? | Somente o homem batizado | CIC § 1577 |
| `quiz.fe_catolica.n3.015` | Na Igreja latina, os sacerdotes são normalmente escolhidos entre homens que: | Vivem o celibato pelo Reino dos Céus | CIC § 1579 |
| `quiz.fe_catolica.n3.016` | Quantos são os mandamentos da Igreja, segundo o Catecismo? | 5 | CIC § 2041 |
| `quiz.fe_catolica.n3.017` | A Missa torna presente o único sacrifício de Cristo na cruz; não é um novo sacrifício. | Verdadeiro | CIC § 1367 |

---

## 4. Textos da interface com conteúdo religioso

Estão em `shared/src/commonMain/composeResources/values/strings.xml` (não têm campo `revisado`, mas pedem leitura):

- Mensagem ao concluir: *"Obrigado por este tempo de oração. Que Nossa Senhora interceda por você."*
- Status: *"Terço de hoje rezado. Deus seja louvado!"*
- Notificações (tom acolhedor, sem culpa): "Hora do terço", "Um momento com Nossa Senhora", "Pausa para a oração", "Sua chama está pedindo cuidado", "Ainda dá tempo".
- Conquista *"Um ano com Maria"*.
- Novenas: *"Novena concluída. Deus seja louvado!"*, *"Você continua de onde parou. Sem pressa: o importante é rezar com o coração."*
- Quiz: *"Pontos ganhos nas lições. A oração não vale pontos: é encontro, não competição."*, *"As perguntas que você errou voltam numa revisão, com calma. Errar também é aprender."*

---

## Resumo

| Arquivo | Itens | Revisados |
|---|---|---|
| `terco.json` | 30 | 0 |
| `novenas.json` | 78 (6 novenas × [1 + 3 orações + 9 dias]) | 0 |
| `quiz.json` | 153 perguntas | 0 |
