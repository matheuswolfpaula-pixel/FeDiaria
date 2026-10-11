"""Gera o ícone do app a partir da arte do vitral (docs/icone/vitral-original.jpg).

Saídas (em <pasta_res> = androidApp/src/main/res):
  mipmap-<dpi>/ic_launcher_fundo.webp   camada de fundo do ícone adaptativo (azul desfocado)
  mipmap-<dpi>/ic_launcher_frente.webp  camada da frente (painel do vitral, bordas suavizadas)
e <pasta_docs>/icone-play-512.png (ícone da ficha da Play Store).

Uso: python3 -I docs/icone/gerar_icone.py docs/icone/vitral-original.jpg androidApp/src/main/res docs/icone
"""
import sys

import os

from PIL import Image, ImageDraw, ImageFilter

ORIGINAL, RES, DOCS = sys.argv[1:4]

# Painel azul por dentro da moldura dourada (coordenadas no original 2752x1536).
PAINEL = (826, 215, 1926, 1315)
# Fração do ícone (108dp) ocupada pelo painel: mantém o vitral dentro da área visível
# mesmo em launchers de ícone redondo.
ESCALA_ADAPTATIVO = 0.52
ESCALA_PLAY = 0.94
DENSIDADES = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}

painel = Image.open(ORIGINAL).convert("RGB").crop(PAINEL)


# Azul da borda do painel (centro) até um azul mais fundo (cantos).
AZUL_CENTRO = (6, 52, 76)
AZUL_BORDA = (2, 26, 40)


def fundo(lado):
    """Degradê radial no azul do próprio painel, para a borda suavizada não deixar emenda."""
    centro = Image.new("RGB", (lado, lado), AZUL_CENTRO)
    borda = Image.new("RGB", (lado, lado), AZUL_BORDA)
    mascara = Image.radial_gradient("L").resize((lado, lado), Image.LANCZOS)  # 0 no centro, 255 na borda
    return Image.composite(borda, centro, mascara)


def frente(lado, escala):
    """Painel nítido, centralizado, com a borda esmaecendo para transparente."""
    p = int(lado * escala)
    nitido = painel.resize((p, p), Image.LANCZOS)
    mascara = Image.new("L", (p, p), 0)
    margem = int(p * 0.10)
    ImageDraw.Draw(mascara).rounded_rectangle((margem, margem, p - margem, p - margem), radius=margem * 2, fill=255)
    mascara = mascara.filter(ImageFilter.GaussianBlur(margem * 0.6))
    camada = Image.new("RGBA", (lado, lado), (0, 0, 0, 0))
    nitido.putalpha(mascara)
    camada.paste(nitido, ((lado - p) // 2, (lado - p) // 2), nitido)
    return camada


for dpi, lado in DENSIDADES.items():
    pasta = f"{RES}/mipmap-{dpi}"
    os.makedirs(pasta, exist_ok=True)
    fundo(lado).save(f"{pasta}/ic_launcher_fundo.webp", "WEBP", quality=92, method=6)
    frente(lado, ESCALA_ADAPTATIVO).save(f"{pasta}/ic_launcher_frente.webp", "WEBP", quality=92, method=6)

# Play Store: 512x512, 32 bits, sem transparência (a loja arredonda os cantos sozinha).
lado = 512
play = fundo(lado).convert("RGBA")
play.alpha_composite(frente(lado, ESCALA_PLAY))
play.convert("RGB").save(f"{DOCS}/icone-play-512.png", "PNG")

# Prévias com as máscaras mais comuns (só para conferência).
if len(sys.argv) > 4:
    prev = sys.argv[4]
    l = 432
    comp = fundo(l).convert("RGBA")
    comp.alpha_composite(frente(l, ESCALA_ADAPTATIVO))
    vis = comp.crop((l // 6, l // 6, l - l // 6, l - l // 6))  # 72dp visíveis de 108
    s = vis.size[0]
    for nome, desenho in [
        ("circulo", lambda d: d.ellipse((0, 0, s - 1, s - 1), fill=255)),
        ("quadrado", lambda d: d.rounded_rectangle((0, 0, s - 1, s - 1), radius=s // 4, fill=255)),
    ]:
        m = Image.new("L", (s, s), 0)
        desenho(ImageDraw.Draw(m))
        out = Image.new("RGBA", (s, s), (236, 236, 236, 255))
        out.paste(vis, (0, 0), m)
        out.save(f"{prev}/prev_{nome}.png")
