# Roadmap e pendências

## Prioridade alta

- executar o checklist completo em Xiaomi/HyperOS e registrar versão do sistema;
- configurar assinatura release fora do repositório;
- implementar restauração de backup transacional, validada somente com dados sintéticos;
- criar migrações Room antes de alterar o schema;
- ampliar testes de concorrência e virada do dia.

## Qualidade e acessibilidade

- validar TalkBack e ordem de foco com uma pessoa usuária;
- revisar telas em 320 dp e escalas de fonte elevadas;
- eliminar textos e configurações específicas de um único aparelho quando não forem necessárias;
- automatizar testes, lint e build no GitHub Actions;
- acompanhar avisos do Android Lint e atualizar dependências de forma controlada.

Na primeira execução da preparação, havia também 1 alerta de política de bateria; ele foi corrigido. Permanecem 60 avisos: 36 recursos não utilizados, 16 dependências com versões mais recentes e 8 verificações de SDK obsoletas. Eles devem ser tratados em mudanças pequenas e testáveis.

## Arquitetura

- separar modelos de domínio das entidades Room;
- introduzir injeção de dependências;
- isolar laboratório/diagnóstico em variante de desenvolvimento;
- definir estratégia de logs técnicos sem dados pessoais;
- avaliar modularização apenas quando o tamanho do projeto justificar.
