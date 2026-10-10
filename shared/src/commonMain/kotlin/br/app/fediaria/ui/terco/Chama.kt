// Geometria e ritmo da animação são valores de desenho; nomeá-los um a um não ajudaria a leitura.
@file:Suppress("MagicNumber")

package br.app.fediaria.ui.terco

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform

private val ChamaExternaTopo = Color(0xFFF2A93B)
private val ChamaExternaBase = Color(0xFFD9600F)
private val ChamaInternaTopo = Color(0xFFFFF3C4)
private val ChamaInternaBase = Color(0xFFF6C344)

/**
 * A chama da ofensiva. Acesa: tremula suavemente. Apagada: brasa cinza, imóvel.
 * Puramente decorativa; o número e o texto ao lado carregam a informação para o TalkBack.
 */
@Composable
fun Chama(
    acesa: Boolean,
    modifier: Modifier = Modifier,
) {
    val transicao = rememberInfiniteTransition(label = "chama")
    val escala by transicao.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1_400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "escala",
    )
    val balanco by transicao.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(tween(2_300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "balanco",
    )
    val apagadaExterna = MaterialTheme.colorScheme.outlineVariant
    val apagadaInterna = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier) {
        val base = Offset(size.width / 2, size.height)
        withTransform({
            if (acesa) {
                rotate(balanco, pivot = base)
                scale(scaleX = 1f, scaleY = escala, pivot = base)
            }
        }) {
            desenharChama(
                tamanho = size,
                pincel =
                    if (acesa) {
                        Brush.verticalGradient(listOf(ChamaExternaTopo, ChamaExternaBase))
                    } else {
                        Brush.verticalGradient(listOf(apagadaExterna, apagadaExterna))
                    },
            )
            val interno = Size(size.width * 0.55f, size.height * 0.58f)
            withTransform({ translate(left = (size.width - interno.width) / 2, top = size.height - interno.height) }) {
                desenharChama(
                    tamanho = interno,
                    pincel =
                        if (acesa) {
                            Brush.verticalGradient(listOf(ChamaInternaTopo, ChamaInternaBase))
                        } else {
                            Brush.verticalGradient(listOf(apagadaInterna, apagadaInterna))
                        },
                )
            }
        }
    }
}

private fun DrawScope.desenharChama(
    tamanho: Size,
    pincel: Brush,
) {
    val w = tamanho.width
    val h = tamanho.height
    val caminho =
        Path().apply {
            moveTo(w * 0.52f, 0f)
            cubicTo(w * 0.62f, h * 0.24f, w * 0.96f, h * 0.40f, w * 0.93f, h * 0.68f)
            cubicTo(w * 0.90f, h * 0.90f, w * 0.72f, h, w * 0.5f, h)
            cubicTo(w * 0.28f, h, w * 0.08f, h * 0.90f, w * 0.07f, h * 0.66f)
            cubicTo(w * 0.06f, h * 0.46f, w * 0.22f, h * 0.34f, w * 0.30f, h * 0.20f)
            cubicTo(w * 0.34f, h * 0.32f, w * 0.40f, h * 0.36f, w * 0.44f, h * 0.36f)
            cubicTo(w * 0.42f, h * 0.22f, w * 0.46f, h * 0.10f, w * 0.52f, 0f)
            close()
        }
    drawPath(caminho, pincel)
}
