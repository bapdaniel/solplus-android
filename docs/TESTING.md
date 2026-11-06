# Testes e validação

## Suites existentes

| Camada | Quantidade no código | Escopo principal |
|---|---:|---|
| JVM (`src/test`) | 28 | regras de dose, agrupamento, calendário, backup e auditoria |
| Instrumentada (`src/androidTest`) | 10 | Room, Activity, navegação, semântica e configuração de fonte |

Os números acima foram conferidos nos arquivos de teste. Resultados de execução devem ser registrados separadamente e nunca inferidos apenas pela presença dos testes.

## Comandos de verificação

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
.\gradlew.bat connectedDebugAndroidTest
```

`connectedDebugAndroidTest` exige emulador ou aparelho autorizado. Testes físicos de alarmes não devem usar dados reais durante a validação de portfólio.

## Resultado reproduzido nesta preparação

Execução local em **2 de novembro de 2026**, na cópia limpa preparada para o portfólio:

- testes JVM: **28 executados, 28 aprovados, 0 falhas, 0 erros e 0 ignorados**;
- lint debug: **concluído com 0 erros e 60 avisos** após a correção da política de bateria (36 recursos não utilizados, 16 atualizações de dependências sugeridas e 8 verificações de SDK obsoletas);
- build debug: **aprovado**;
- build release: **aprovado como `app-release-unsigned.apk`**, sem chave privada no projeto;
- testes instrumentados: **não reexecutados nesta preparação**; os 10 testes permanecem no código e exigem emulador ou aparelho conectado.

Comando reproduzido:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease --stacktrace
```

Os avisos do lint não bloquearam a compilação, mas devem ser triados antes de tratar o app como candidato a release.

## Checklist físico pendente

- concessão e recusa de notificações e alarmes exatos;
- entrega com app aberto, encerrado e tela bloqueada;
- reagendamento após reinício, atualização, mudança de hora e fuso;
- políticas de autostart e bateria do Xiaomi/HyperOS;
- TalkBack, ordem de foco, contraste, zoom e fontes grandes;
- persistência após atualização por cima;
- privacidade das notificações na tela bloqueada;
- exportação de backup apenas com dados sintéticos.

## Critério de comunicação

Use “aprovado” somente para um cenário executado com ambiente e data registrados. Itens não executados devem permanecer como pendentes. O app não deve ser descrito como clinicamente validado.
