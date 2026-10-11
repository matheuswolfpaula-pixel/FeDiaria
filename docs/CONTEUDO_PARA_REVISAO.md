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

## 3. Textos da interface com conteúdo religioso

Estão em `shared/src/commonMain/composeResources/values/strings.xml` (não têm campo `revisado`, mas pedem leitura):

- Mensagem ao concluir: *"Obrigado por este tempo de oração. Que Nossa Senhora interceda por você."*
- Status: *"Terço de hoje rezado. Deus seja louvado!"*
- Notificações (tom acolhedor, sem culpa): "Hora do terço", "Um momento com Nossa Senhora", "Pausa para a oração", "Sua chama está pedindo cuidado", "Ainda dá tempo".
- Conquista *"Um ano com Maria"*.
- Novenas: *"Novena concluída. Deus seja louvado!"*, *"Você continua de onde parou. Sem pressa: o importante é rezar com o coração."*

---

## Resumo

| Arquivo | Itens | Revisados |
|---|---|---|
| `terco.json` | 30 | 0 |
| `novenas.json` | 78 (6 novenas × [1 + 3 orações + 9 dias]) | 0 |
