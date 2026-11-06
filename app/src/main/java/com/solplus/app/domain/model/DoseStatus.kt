package com.solplus.app.domain.model

enum class DoseStatus(
    val label: String,
    val description: String
) {
    SCHEDULED("Programado", "Horário futuro programado"),
    PENDING("Pendente", "Hora do remédio chegou, aguardando resposta"),
    CONFIRMED("Confirmado", "Dose confirmada pelo usuário com data e hora"),
    POSTPONED("Adiado", "Lembrete adiado para tomar em instantes"),
    UNCERTAIN("Incerto", "Usuário não tem certeza se já tomou ou aplicou"),
    NOT_CONFIRMED("Não confirmado", "Horário expirou sem confirmação explícita"),
    NOT_TAKEN("Não realizado", "Medicamento não foi tomado por orientação ou impedimento");

    val isResolved: Boolean
        get() = this == CONFIRMED || this == NOT_TAKEN
}
