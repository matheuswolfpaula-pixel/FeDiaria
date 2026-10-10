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

## 2. Textos da interface com conteúdo religioso

Estão em `shared/src/commonMain/composeResources/values/strings.xml` (não têm campo `revisado`, mas pedem leitura):

- Mensagem ao concluir: *"Obrigado por este tempo de oração. Que Nossa Senhora interceda por você."*
- Status: *"Terço de hoje rezado. Deus seja louvado!"*
- Notificações (tom acolhedor, sem culpa): "Hora do terço", "Um momento com Nossa Senhora", "Pausa para a oração", "Sua chama está pedindo cuidado", "Ainda dá tempo".
- Conquista *"Um ano com Maria"*.

---

## Resumo

| Arquivo | Itens | Revisados |
|---|---|---|
| `terco.json` | 30 | 0 |
