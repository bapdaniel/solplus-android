# Arquitetura técnica

## Visão geral

O SOL+ é um aplicativo Android de módulo único (`:app`) e operação local. A interface é escrita em Jetpack Compose; ViewModels expõem estado para as telas; um repositório coordena Room, preferências e regras de registro. Alarmes e receivers integram os lembretes ao sistema Android.

```text
Compose UI
   ↓ ações / ↑ estado
ViewModels
   ↓
MedicationRepository
   ├─ Room (medicamentos, ocorrências e auditoria)
   ├─ DataStore (preferências)
   └─ AlarmManager / notificações
```

## Organização do código

- `ui/`: telas, componentes, navegação e tema;
- `data/local/`: banco Room, entidades e DAOs;
- `data/preferences/`: preferências locais;
- `data/repository/`: coordenação das regras e persistência;
- `core/alarm/`: agendamento, ações e reagendamento;
- `core/notification/`: canais e notificações;
- `core/util/`: relógio e exportação de backup;
- `domain/model/`: estados e tipos de medicamento.

## Decisões relevantes

- **Offline por padrão:** não existe permissão `INTERNET` no manifesto.
- **Dados privados no aparelho:** Room e DataStore usam o armazenamento interno do app; o backup automático Android está desativado.
- **Confirmação explícita:** adiar ou escolher “tomar agora” não equivale a registrar uma dose como concluída.
- **Auditoria:** correções de histórico geram registros próprios.
- **Alarmes locais:** receivers não são exportados, exceto a Activity de launcher conforme exigido pelo Android.

## Dívida técnica

O domínio ainda depende de entidades de persistência em alguns fluxos, as dependências são montadas manualmente e o projeto não possui módulos separados. Evoluções recomendadas: interfaces de domínio, injeção de dependências, migrações Room versionadas e separação entre recursos de produção e laboratório de testes.
