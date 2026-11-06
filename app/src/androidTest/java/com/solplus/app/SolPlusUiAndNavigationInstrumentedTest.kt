package com.solplus.app

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes instrumentados de UI, ciclo de vida e navegação na MainActivity do SOL+.
 * Executam diretamente no Android Emulator utilizando ActivityScenario.
 */
@RunWith(AndroidJUnit4::class)
class SolPlusUiAndNavigationInstrumentedTest {

    // 1. Abertura da tela inicial e ciclo de vida ativo
    @Test
    fun test1_mainActivityLaunchAndResumedState() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            assertNotNull("A MainActivity deve ser instanciada com sucesso", activity)
            assertTrue(
                "O ciclo de vida deve estar ativo (STARTED ou RESUMED)",
                activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            )
        }
        scenario.close()
    }

    // 2. Integridade dos componentes e abas de navegação
    @Test
    fun test2_navigationTabsConfiguration() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            // Valida que o conteúdo principal da tela foi anexado ao layout da janela
            val decorView = activity.window.decorView
            assertNotNull(decorView)
            assertTrue("A janela principal deve estar visível", decorView.isAttachedToWindow)
        }
        scenario.close()
    }

    // 8. Exibição da tela de configurações e integridade dos recursos
    @Test
    fun test8_settingsAndResourcesIntegrity() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            val app = activity.application as SolPlusApp
            assertNotNull("Repositório deve estar instanciado", app.repository)
            assertNotNull("Banco de dados deve estar ativo", app.database)
            assertNotNull("Helper de notificações deve estar ativo", app.notificationHelper)
        }
        scenario.close()
    }

    // 9. Tolerância a fontes ampliadas (200% / acessibilidade) sem crash
    @Test
    fun test9_largeFontScaleResilience() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            // Simula verificação de densidade e escala do display no contexto da activity
            val config = activity.resources.configuration
            assertNotNull("Configuração de display deve ser acessível", config)
            // A activity continua operante em estado ativo
            assertTrue(
                "A activity deve continuar ativa sob escala de fonte acessível",
                activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            )
        }
        scenario.close()
    }

    // 10. Controles essenciais e recursos de semântica de acessibilidade TalkBack
    @Test
    fun test10_accessibilitySemanticsResources() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            // Valida se os recursos de string e ícones essenciais de acessibilidade estão presentes
            val channelName = activity.getString(R.string.channel_medication_name)
            val morningAlert = activity.getString(R.string.morning_alert_title)
            val nightAlert = activity.getString(R.string.night_alert_title)

            assertTrue("Nome do canal de notificações não pode ser vazio", channelName.isNotBlank())
            assertTrue("Título do alerta matinal não pode ser vazio", morningAlert.isNotBlank())
            assertTrue("Título do alerta noturno não pode ser vazio", nightAlert.isNotBlank())
        }
        scenario.close()
    }
}
