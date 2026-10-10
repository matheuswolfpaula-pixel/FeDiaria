package br.app.fediaria.ui.navegacao

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_configuracoes
import br.app.fediaria.shared.resources.acao_voltar
import br.app.fediaria.shared.resources.app_nome
import br.app.fediaria.shared.resources.configuracoes_titulo
import br.app.fediaria.shared.resources.ic_configuracoes
import br.app.fediaria.shared.resources.ic_voltar
import br.app.fediaria.ui.telas.TelaConfiguracoes
import br.app.fediaria.ui.telas.TelaLatim
import br.app.fediaria.ui.telas.TelaNovenas
import br.app.fediaria.ui.telas.TelaQuiz
import br.app.fediaria.ui.telas.TelaTerco
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavegacaoPrincipal(navController: NavHostController = rememberNavController()) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destino = entradaAtual?.destination
    val abaAtual = Aba.entries.firstOrNull { destino.eh(it.rota) }

    Scaffold(
        topBar = {
            BarraSuperior(
                abaAtual = abaAtual,
                aoAbrirConfiguracoes = { navController.navigate(Rota.Configuracoes) { launchSingleTop = true } },
                aoVoltar = { navController.popBackStack() },
            )
        },
        bottomBar = {
            if (abaAtual != null) {
                BarraInferior(abaAtual = abaAtual, aoSelecionar = { navController.irParaAba(it) })
            }
        },
    ) { espacamento ->
        NavHost(
            navController = navController,
            startDestination = Rota.Terco,
            modifier = Modifier.padding(espacamento),
        ) {
            composable<Rota.Terco> { TelaTerco() }
            composable<Rota.Novenas> { TelaNovenas() }
            composable<Rota.Quiz> { TelaQuiz() }
            composable<Rota.Latim> { TelaLatim() }
            composable<Rota.Configuracoes> { TelaConfiguracoes() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraSuperior(
    abaAtual: Aba?,
    aoAbrirConfiguracoes: () -> Unit,
    aoVoltar: () -> Unit,
) {
    TopAppBar(
        title = {
            val titulo = if (abaAtual != null) Res.string.app_nome else Res.string.configuracoes_titulo
            Text(stringResource(titulo))
        },
        navigationIcon = {
            if (abaAtual == null) {
                IconButton(onClick = aoVoltar) {
                    Icon(
                        painterResource(Res.drawable.ic_voltar),
                        contentDescription = stringResource(Res.string.acao_voltar),
                    )
                }
            }
        },
        actions = {
            if (abaAtual != null) {
                IconButton(onClick = aoAbrirConfiguracoes) {
                    Icon(
                        painterResource(Res.drawable.ic_configuracoes),
                        contentDescription = stringResource(Res.string.acao_configuracoes),
                    )
                }
            }
        },
    )
}

@Composable
private fun BarraInferior(
    abaAtual: Aba,
    aoSelecionar: (Aba) -> Unit,
) {
    NavigationBar {
        Aba.entries.forEach { aba ->
            NavigationBarItem(
                selected = aba == abaAtual,
                onClick = { aoSelecionar(aba) },
                icon = { Icon(painterResource(aba.icone), contentDescription = null) },
                label = { Text(stringResource(aba.titulo)) },
                modifier = Modifier.testTag(aba.tag),
            )
        }
    }
}

private fun NavDestination?.eh(rota: Rota): Boolean = this?.hasRoute(rota::class) == true

/** Troca de aba preservando o estado de cada uma e sem empilhar telas repetidas. */
private fun NavHostController.irParaAba(aba: Aba) {
    navigate(aba.rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
