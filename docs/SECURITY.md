# Segurança e privacidade

## Dados tratados

O aplicativo pode armazenar localmente horários, confirmações, correções e textos de medicamentos. Isso pode constituir informação pessoal de saúde. Este repositório contém apenas código e capturas com dados de demonstração.

## Proteções existentes

- sem API remota e sem permissão de internet;
- banco e preferências no armazenamento privado do aplicativo;
- backup automático do Android desativado;
- receivers internos marcados como não exportados;
- `PendingIntent` explícito e imutável nos fluxos revisados;
- notificação pública genérica e conteúdo privado na tela bloqueada;
- materiais de assinatura, bancos, backups, logs e exportações bloqueados pelo `.gitignore`.

## Regras para contribuições e releases

1. Nunca envie keystores, senhas, tokens, `local.properties`, backups ou logs de aparelho.
2. Use somente dados sintéticos em testes, issues e capturas.
3. Revise o histórico Git, não apenas a versão atual, antes de tornar um repositório público.
4. Armazene a chave de release fora do projeto, com cópia cifrada e procedimento de recuperação.
5. Não publique APK release até confirmar assinatura, permissões, política de privacidade e testes físicos.

## Limites da revisão

A preparação para portfólio inclui busca estática por segredos e artefatos sensíveis. Ela não equivale a teste de penetração, análise completa de dependências/CVEs ou certificação de segurança. Falhas devem ser comunicadas de forma privada ao responsável pelo repositório, sem anexar dados reais.
