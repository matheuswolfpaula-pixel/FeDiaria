"""Gera o ícone do vitral (108x108, ícone adaptativo) como SVG (prévia) e VectorDrawable.

Uso: python3 -I docs/icone/gerar_vitral.py <pasta_saida>
Copiar ic_launcher_foreground.xml e ic_launcher_monochrome.xml para androidApp/src/main/res/drawable/.
"""
import math
import sys

OUT = sys.argv[1]

AZUL_MARIANO = "#1F3F7A"
FUNDO_ESCURO = "#152C57"
CHUMBO = "#0E1B36"
DOURADO = "#E9C349"

# Vidros
AZUL = "#2C5FA8"
CEU = "#9CC7EC"
VERMELHO = "#A8323A"
OURO = "#F2D272"
VERDE = "#3C8D5A"
ROXO = "#6A4A9C"
BRANCO = "#F6F0DC"

# Janela em arco ogival: vão de X0 a X1, nascença do arco em Y_ARCO, base em Y_BASE.
X0, X1 = 37.0, 71.0
CX = (X0 + X1) / 2
Y_ARCO, Y_BASE = 49.0, 78.0
R = 22.0
MOLDURA = 2.8


def f(v):
    return f"{v:.2f}".rstrip("0").rstrip(".")


def ogiva(x0, x1, y_arco, y_base, r):
    """Contorno de um arco ogival (dois arcos de raio r) fechado na base."""
    cx = (x0 + x1) / 2
    meio = (x1 - x0) / 2
    topo = y_arco - math.sqrt(r * r - (r - meio) ** 2)
    return (
        f"M{f(x0)},{f(y_base)} L{f(x0)},{f(y_arco)} "
        f"A{f(r)},{f(r)} 0 0,1 {f(cx)},{f(topo)} "
        f"A{f(r)},{f(r)} 0 0,1 {f(x1)},{f(y_arco)} "
        f"L{f(x1)},{f(y_base)} Z"
    ), topo


def ret(x0, y0, x1, y1):
    return f"M{f(x0)},{f(y0)} L{f(x1)},{f(y0)} L{f(x1)},{f(y1)} L{f(x0)},{f(y1)} Z"


def poli(*pts):
    return "M" + " L".join(f"{f(x)},{f(y)}" for x, y in pts) + " Z"


def circulo(cx, cy, r):
    return (
        f"M{f(cx - r)},{f(cy)} A{f(r)},{f(r)} 0 1,1 {f(cx + r)},{f(cy)} "
        f"A{f(r)},{f(r)} 0 1,1 {f(cx - r)},{f(cy)} Z"
    )


contorno_ext, topo_ext = ogiva(X0 - MOLDURA, X1 + MOLDURA, Y_ARCO, Y_BASE + MOLDURA, R + MOLDURA)
contorno_vidro, topo = ogiva(X0, X1, Y_ARCO, Y_BASE, R)

# (cor, path) — pintados em ordem; os vidros ficam por cima da cor de chumbo
# e os filetes de chumbo são o espaço entre eles.
camadas = [
    (DOURADO, contorno_ext),
    (CHUMBO, contorno_vidro),
]

G = 1.3  # metade da espessura do filete de chumbo

# Cabeça do arco: vidro azul com uma rosácea dourada.
y_rosa = Y_ARCO - 8.5
cabeca, _ = ogiva(X0 + G, X1 - G, Y_ARCO - 2 - G, Y_ARCO - 2 - G, R - G)
camadas.append((AZUL, cabeca))
vidros = [cabeca]  # recortes da silhueta monocromática
losangos = []
camadas.append((CHUMBO, circulo(CX, y_rosa, 6.2)))
for k in range(8):
    a = math.radians(k * 45)
    px, py = CX + 3.5 * math.cos(a), y_rosa + 3.5 * math.sin(a)
    camadas.append((OURO if k % 2 == 0 else VERMELHO, circulo(px, py, 1.8)))
camadas.append((BRANCO, circulo(CX, y_rosa, 1.7)))

# Corpo: duas lancetas lado a lado, com vidros em losango e faixas.
linhas = [Y_ARCO - 2, 57.0, 67.5, Y_BASE]
cores = [
    (AZUL, VERMELHO),
    (OURO, AZUL),
    (VERMELHO, AZUL),
]
for i in range(3):
    ya, yb = linhas[i] + G, linhas[i + 1] - G
    for j, (xa, xb) in enumerate([(X0 + G, CX - G), (CX + G, X1 - G)]):
        cor = cores[i][j]
        camadas.append((cor, ret(xa, ya, xb, yb)))
        vidros.append(camadas[-1][1])
        # losango central em cada vidro
        mx, my = (xa + xb) / 2, (ya + yb) / 2
        dx, dy = (xb - xa) / 2 - 1.6, (yb - ya) / 2 - 1.4
        camadas.append((CHUMBO, poli((mx, my - dy - G), (mx + dx + G, my), (mx, my + dy + G), (mx - dx - G, my))))
        claro = {VERMELHO: OURO, OURO: BRANCO, AZUL: CEU}[cor]
        camadas.append((claro, poli((mx, my - dy + G), (mx + dx - G, my), (mx, my + dy - G), (mx - dx + G, my))))
        losangos.append(camadas[-1][1])


# --- SVG de prévia (fundo + primeiro plano) ---
svg = [
    '<svg xmlns="http://www.w3.org/2000/svg" width="1024" height="1024" viewBox="0 0 108 108">',
    f'<rect width="108" height="108" fill="{AZUL_MARIANO}"/>',
    f'<circle cx="54" cy="54" r="54" fill="{FUNDO_ESCURO}" opacity="0"/>',
]
svg += [f'<path fill="{c}" d="{d}"/>' for c, d in camadas]
svg.append("</svg>")
open(f"{OUT}/vitral.svg", "w").write("\n".join(svg))

# --- VectorDrawable do primeiro plano ---
cor_res = {DOURADO: "@color/dourado"}
xml = [
    '<?xml version="1.0" encoding="utf-8"?>',
    "<!-- Vitral gótico (gerado por docs/icone/gerar_vitral.py; não editar à mão). Fundo: @color/azul_mariano. -->",
    '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
    '    android:width="108dp"',
    '    android:height="108dp"',
    '    android:viewportWidth="108"',
    '    android:viewportHeight="108">',
]
for c, d in camadas:
    xml += [
        "    <path",
        f'        android:fillColor="{cor_res.get(c, c)}"',
        f'        android:pathData="{d}" />',
    ]
xml.append("</vector>")
open(f"{OUT}/ic_launcher_foreground.xml", "w").write("\n".join(xml) + "\n")

# --- Monocromático (ícones temáticos do Android 13+): moldura + filetes, sem cores ---
mono_paths = [contorno_ext]
mono = [
    '<?xml version="1.0" encoding="utf-8"?>',
    "<!-- Silhueta do vitral para ícones temáticos (Android 13+). -->",
    '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
    '    android:width="108dp"',
    '    android:height="108dp"',
    '    android:viewportWidth="108"',
    '    android:viewportHeight="108">',
    "    <path",
    '        android:fillColor="#FFFFFFFF"',
    '        android:fillType="evenOdd"',
    # moldura externa com os vidros recortados (evenOdd), deixando filetes
    f'        android:pathData="{contorno_ext} '
    + " ".join(vidros + losangos + [circulo(CX, y_rosa, 3.2)])
    + '" />',
    "</vector>",
]
open(f"{OUT}/ic_launcher_monochrome.xml", "w").write("\n".join(mono) + "\n")
print("topo do arco:", round(topo_ext, 1), "| camadas:", len(camadas))
