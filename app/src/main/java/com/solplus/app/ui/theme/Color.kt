package com.solplus.app.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Oficial SOL+ (Inspirada na Costela-de-Adão Variegata)
val SolDarkGreen = Color(0xFF0F4A32)          // Verde floresta escuro do cabeçalho e botões principais
val SolHeaderGreen = Color(0xFF0D432D)        // Verde profundo para topo da tela
val SolHeaderSubtext = Color(0xFFC7E3D3)      // Texto creme-esverdeado suave para subtítulo
val SolCreamBg = Color(0xFFF8F6F0)            // Fundo creme/marfim aconchegante de toda a aplicação
val SolCardBg = Color(0xFFFFFFFF)             // Branco puro para os cartões destacados
val SolCardBorder = Color(0xFFEBE6DC)         // Borda sutil nos cartões
val SolSunGold = Color(0xFFF59E0B)            // Dourado solar vibrante do SOL+
val SolSunGoldLight = Color(0xFFFFF3DC)       // Fundo amarelo claro para badge e cápsula
val SolVariegataCream = Color(0xFFFDFBF7)     // Creme claro da variegação da folha

// Textos
val SolTextDark = Color(0xFF0F3826)           // Verde quase preto para títulos (máximo contraste WCAG AAA)
val SolTextSecondary = Color(0xFF5A7367)      // Cinza-esverdeado para subtítulos e instruções

// Badges Semânticos (Idênticos ao Mockup)
val SolBadgePendingBg = Color(0xFFFFF3DC)     // Fundo pêssego/amarelo de Pendente
val SolBadgePendingText = Color(0xFFB45309)   // Texto âmbar escuro de Pendente
val SolBadgeConfirmedBg = Color(0xFFE6F4EA)   // Fundo verde claro de Confirmado
val SolBadgeConfirmedText = Color(0xFF137333) // Texto verde escuro de Confirmado
val SolBadgePostponedBg = Color(0xFFE0F2FE)   // Fundo azul suave de Adiado
val SolBadgePostponedText = Color(0xFF0369A1) // Texto azul de Adiado
val SolBadgeUncertainBg = Color(0xFFF3E8FF)   // Fundo lilás de Incerto
val SolBadgeUncertainText = Color(0xFF7E22CE) // Texto roxo de Incerto

// Botões da Tela de Alerta
val SolBtnPrimaryGreen = Color(0xFF0F4A32)    // Botão "SIM, JÁ TOMEI / APLIQUEI"
val SolBtnSecondaryBg = Color(0xFFEFECE6)     // Botão "NÃO, AINDA NÃO"
val SolBtnSecondaryText = Color(0xFF1C3B2E)   // Texto do botão secundário

// Caixa Informativa
val SolInfoBoxBg = Color(0xFFE6F4EA)          // Fundo verde menta suave da dica de rodapé
val SolInfoBoxBorder = Color(0xFFCEEAD6)      // Borda da caixa de dica

// Aliases de compatibilidade para componentes e diálogos
val SolGreen = SolDarkGreen
val SolCardSurface = SolCardBg
val SolGold = SolSunGold
val SolBackground = SolCreamBg
val StatusConfirmedColor = SolBadgeConfirmedText
val StatusPendingColor = SolBadgePendingText
val StatusPostponedColor = SolBadgePostponedText
val StatusUncertainColor = SolBadgeUncertainText
