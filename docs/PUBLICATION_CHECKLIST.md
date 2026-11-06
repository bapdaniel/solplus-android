# Checklist antes de publicar no GitHub

## Conta e identidade

- [x] conectar a conta GitHub que receberá o projeto (`bapdaniel`);
- [x] confirmar o nome do repositório (`solplus-android`);
- [x] usar nome do perfil e e-mail protegido `noreply` no commit;
- [ ] confirmar explicitamente a visibilidade pública.

## Conteúdo

- [x] código-fonte e Gradle Wrapper incluídos;
- [x] caches, builds, APKs e configurações locais excluídos;
- [x] chaves e arquivos de assinatura excluídos;
- [x] logs, bancos, backups e exportações de saúde excluídos;
- [x] capturas revisadas e limitadas a dados de demonstração;
- [x] autoria e assistência de IA descritas sem atribuição manual não comprovada;
- [x] duração apresentada apenas como estimativa;
- [ ] usuário revisar textos e capturas uma última vez.

## Verificação técnica

- [x] 28 testes JVM aprovados;
- [x] lint sem erros;
- [x] build debug aprovado;
- [x] build release não assinado aprovado;
- [ ] 10 testes instrumentados reexecutados nesta versão;
- [ ] checklist físico no Xiaomi/HyperOS concluído;
- [ ] 60 avisos restantes do lint triados;
- [ ] chave release criada e armazenada fora do Git, somente se houver distribuição de APK.

## Publicação

- [x] revisar `git status` e a lista completa do primeiro commit;
- [x] criar o primeiro commit com a identidade protegida;
- [x] criar o repositório sem adicionar README/licença automáticos;
- [x] enviar primeiro como privado e verificar o conteúdo no GitHub;
- [x] confirmar que o GitHub Actions concluiu sem falhas;
- [ ] tornar público somente após confirmação final explícita;
- [ ] habilitar proteção contra envio de segredos, se disponível na conta.
