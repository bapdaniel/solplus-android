# Changelog

Todas as mudanças notáveis deste projeto serão registradas aqui. O projeto ainda não possui uma release pública assinada.

## Não publicado

### Preparação para portfólio público

- removidos APKs, caches, logs brutos, hierarquias de tela, caminhos locais e relatórios históricos conflitantes;
- ampliado o `.gitignore` para chaves, credenciais, bancos, backups e exportações de saúde;
- removida a assinatura da variante release com a chave debug;
- adicionada documentação de arquitetura, testes, segurança, limitações, autoria e assistência de IA;
- selecionadas capturas de emulador com dados de demonstração;
- adicionada verificação contínua com testes JVM, lint e build debug;
- reproduzidos 28 testes JVM sem falhas, lint sem erros e builds debug/release;
- mantidos como pendentes os testes instrumentados desta preparação e a validação física completa no Xiaomi/HyperOS.

### Funcionalidades presentes

- lembretes matinais agrupados e lembrete noturno independente;
- confirmações separadas, adiamento, incerteza e correção auditável;
- calendário, histórico e preferências locais;
- persistência Room e DataStore;
- notificações privadas na tela bloqueada;
- exportação de backup, com restauração desativada na interface;
- suporte a reagendamento após boot e mudança de horário.
