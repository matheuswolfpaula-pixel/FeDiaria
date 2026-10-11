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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.toRoute
import br.app.fediaria.plataforma.AcoesDoSistema
import br.app.fediaria.shared.resources.Res
import br.app.fediaria.shared.resources.acao_configuracoes
import br.app.fediaria.shared.resources.acao_voltar
import br.app.fediaria.shared.resources.app_nome
import br.app.fediaria.shared.resources.configuracoes_titulo
import br.app.fediaria.shared.resources.ic_configuracoes
import br.app.fediaria.shared.resources.ic_voltar
import br.app.fediaria.shared.resources.titulo_ajuda_lembretes
import br.app.fediaria.shared.resources.titulo_exercicios_latim
import br.app.fediaria.shared.resources.titulo_flashcards
import br.app.fediaria.shared.resources.titulo_historico
import br.app.fediaria.shared.resources.titulo_latim
import br.app.fediaria.shared.resources.titulo_leitura_latim
import br.app.fediaria.shared.resources.titulo_licao
import br.app.fediaria.shared.resources.titulo_novena
import br.app.fediaria.shared.resources.titulo_pronuncia
import br.app.fediaria.shared.resources.titulo_rezar_latim
import br.app.fediaria.shared.resources.titulo_terco_guiado
import br.app.fediaria.ui.latim.TelaExerciciosLatim
import br.app.fediaria.ui.latim.TelaFlashcards
import br.app.fediaria.ui.latim.TelaGuiaPronuncia
import br.app.fediaria.ui.latim.TelaLatim
import br.app.fediaria.ui.latim.TelaLeituraLatim
import br.app.fediaria.ui.latim.TelaOracaoLatim
import br.app.fediaria.ui.latim.TelaRezarLatim
import br.app.fediaria.ui.novena.TelaDetalheNovena
import br.app.fediaria.ui.novena.TelaNovenaAtiva
import br.app.fediaria.ui.novena.TelaNovenas
import br.app.fediaria.ui.quiz.TelaLicao
import br.app.fediaria.ui.quiz.TelaQuiz
import br.app.fediaria.ui.telas.TelaAjudaLembretes
import br.app.fediaria.ui.telas.TelaConfiguracoes
import br.app.fediaria.ui.terco.TelaHistorico
import br.app.fediaria.ui.terco.TelaTerco
import br.app.fediaria.ui.terco.TelaTercoGuiado
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun NavegacaoPrincipal(
    novenaAAbrir: Long? = null,
    aoAbrirNovena: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    acoes: AcoesDoSistema = koinInject(),
) {
    LaunchedEffect(novenaAAbrir) {
        if (novenaAAbrir != null) {
            navController.navigate(Rota.NovenaAtiva(novenaAAbrir)) { launchSingleTop = true }
            aoAbrirNovena()
        }
    }
    val entradaAtual by navController.currentBackStackEntryAsState()
    val destino = entradaAtual?.destination
    val abaAtual = Aba.entries.firstOrNull { destino.eh(it.rota::class) }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = if (abaAtual != null) Res.string.app_nome else tituloDaTela(destino),
                ehAba = abaAtual != null,
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
            composable<Rota.Terco> {
                TelaTerco(
                    aoRezar = { navController.navigate(Rota.TercoGuiado(it.name)) },
                    aoVerHistorico = { navController.navigate(Rota.Historico) },
                    aoAjustarLembretes = { navController.navigate(Rota.Configuracoes) },
                    aoLiberarNotificacoes = acoes::abrirConfiguracoesDeNotificacao,
                )
            }
            composable<Rota.Novenas> {
                TelaNovenas(
                    aoAbrirNovenaAtiva = { navController.navigate(Rota.NovenaAtiva(it)) },
                    aoAbrirDetalhe = { navController.navigate(Rota.DetalheNovena(it)) },
                )
            }
            composable<Rota.Quiz> { TelaQuiz(aoAbrirLicao = { navController.navigate(Rota.Licao(it)) }) }
            composable<Rota.Latim> {
                TelaLatim(
                    aoAbrirOracao = { navController.navigate(Rota.OracaoLatim(it)) },
                    aoAbrirFlashcards = { navController.navigate(Rota.Flashcards(it)) },
                    aoAbrirGuia = { navController.navigate(Rota.GuiaPronuncia) },
                )
            }
            composable<Rota.OracaoLatim> { entrada ->
                val id = entrada.toRoute<Rota.OracaoLatim>().id
                TelaOracaoLatim(
                    id = id,
                    aoLer = { navController.navigate(Rota.LeituraLatim(id)) },
                    aoExercitar = { navController.navigate(Rota.ExerciciosLatim(id)) },
                    aoFlashcards = { navController.navigate(Rota.Flashcards(id)) },
                    aoRezar = { navController.navigate(Rota.RezarLatim(id)) },
                )
            }
            composable<Rota.LeituraLatim> { entrada ->
                TelaLeituraLatim(entrada.toRoute<Rota.LeituraLatim>().id, aoConcluir = { navController.popBackStack() })
            }
            composable<Rota.RezarLatim> { entrada -> TelaRezarLatim(entrada.toRoute<Rota.RezarLatim>().id) }
            composable<Rota.ExerciciosLatim> { entrada ->
                TelaExerciciosLatim(
                    entrada.toRoute<Rota.ExerciciosLatim>().id,
                    aoSair = { navController.popBackStack() },
                )
            }
            composable<Rota.Flashcards> { entrada ->
                TelaFlashcards(entrada.toRoute<Rota.Flashcards>().alvo, aoSair = { navController.popBackStack() })
            }
            composable<Rota.GuiaPronuncia> { TelaGuiaPronuncia() }
            composable<Rota.Configuracoes> {
                TelaConfiguracoes(
                    aoAbrirAjudaLembretes = { navController.navigate(Rota.AjudaLembretes) },
                    aoLiberarNotificacoes = acoes::abrirConfiguracoesDeNotificacao,
                )
            }
            composable<Rota.TercoGuiado> { entrada ->
                TelaTercoGuiado(
                    conjunto = entrada.toRoute<Rota.TercoGuiado>().conjunto,
                    aoVoltarAoInicio = { navController.popBackStack(Rota.Terco, inclusive = false) },
                )
            }
            composable<Rota.Historico> { TelaHistorico() }
            composable<Rota.AjudaLembretes> { TelaAjudaLembretes() }
            composable<Rota.DetalheNovena> { entrada ->
                TelaDetalheNovena(
                    novenaId = entrada.toRoute<Rota.DetalheNovena>().novenaId,
                    aoAbrirNovenaAtiva = { id ->
                        navController.navigate(Rota.NovenaAtiva(id)) {
                            popUpTo<Rota.DetalheNovena> { inclusive = true }
                        }
                    },
                )
            }
            composable<Rota.Licao> { entrada ->
                TelaLicao(
                    licaoId = entrada.toRoute<Rota.Licao>().licaoId,
                    aoSair = { navController.popBackStack() },
                )
            }
            composable<Rota.NovenaAtiva> { entrada ->
                TelaNovenaAtiva(
                    id = entrada.toRoute<Rota.NovenaAtiva>().id,
                    aoSair = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun tituloDaTela(destino: NavDestination?): StringResource =
    when {
        destino.eh(Rota.TercoGuiado::class) -> Res.string.titulo_terco_guiado
        destino.eh(Rota.Historico::class) -> Res.string.titulo_historico
        destino.eh(Rota.AjudaLembretes::class) -> Res.string.titulo_ajuda_lembretes
        destino.eh(Rota.DetalheNovena::class) || destino.eh(Rota.NovenaAtiva::class) -> Res.string.titulo_novena
        destino.eh(Rota.Licao::class) -> Res.string.titulo_licao
        destino.eh(Rota.OracaoLatim::class) -> Res.string.titulo_latim
        destino.eh(Rota.LeituraLatim::class) -> Res.string.titulo_leitura_latim
        destino.eh(Rota.RezarLatim::class) -> Res.string.titulo_rezar_latim
        destino.eh(Rota.ExerciciosLatim::class) -> Res.string.titulo_exercicios_latim
        destino.eh(Rota.Flashcards::class) -> Res.string.titulo_flashcards
        destino.eh(Rota.GuiaPronuncia::class) -> Res.string.titulo_pronuncia
        else -> Res.string.configuracoes_titulo
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraSuperior(
    titulo: StringResource,
    ehAba: Boolean,
    aoAbrirConfiguracoes: () -> Unit,
    aoVoltar: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(titulo)) },
        navigationIcon = {
            if (!ehAba) {
                IconButton(onClick = aoVoltar) {
                    Icon(
                        painterResource(Res.drawable.ic_voltar),
                        contentDescription = stringResource(Res.string.acao_voltar),
                    )
                }
            }
        },
        actions = {
            if (ehAba) {
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

private fun NavDestination?.eh(rota: kotlin.reflect.KClass<out Rota>): Boolean = this?.hasRoute(rota) == true

/** Troca de aba preservando o estado de cada uma e sem empilhar telas repetidas. */
private fun NavHostController.irParaAba(aba: Aba) {
    navigate(aba.rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
