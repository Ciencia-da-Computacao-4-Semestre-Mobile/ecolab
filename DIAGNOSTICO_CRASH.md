# 🚨 Diagnóstico de Crash no Cadastro

## 📋 Problema Relatado
O app está fechando (crashando) quando o usuário clica em "cadastrar" após preencher todas as informações.

## 🔍 Possíveis Causas Investigadas

### 1. Erros de Compilação ✅ RESOLVIDOS
- **Erro de sintaxe no RegisterViewModel**: Havia um erro de sintaxe na criação do objeto `User` que foi corrigido
- **Cores faltantes no Palette**: Adicionadas as cores `backgroundLight`, `textDark` e `outline`
- **Erro de smart cast no ForgotPasswordScreen**: Corrigido com safe call

### 2. Tratamento de Erros Melhorado ✅ IMPLEMENTADO
- **Logs detalhados adicionados** no `RegisterViewModel.onRegisterClick()`
- **Logs no UserRepository.createUser()** para monitorar a criação no Firestore
- **Try-catch aprimorado** para capturar exceções específicas

### 3. Fluxo de Execução do Cadastro
```
1. Usuário clica em "Cadastrar"
2. RegisterViewModel.onRegisterClick() é chamado
3. FirebaseAuth.createUserWithEmailAndPassword() é executado
4. Se sucesso → UserRepository.createUser() cria documento no Firestore
5. Se falha → Mensagem de erro é exibida via Toast
```

## 🎯 Próximos Passos para Diagnóstico

### A. Verificar Logs do Aplicativo
Após instalar a nova versão, execute o seguinte comando para ver os logs:
```bash
# Se estiver usando Android Studio, abra o LogCat
# Ou se tiver adb instalado:
adb logcat | grep RegisterViewModel
```

### B. Verificar Pontos de Falha Comuns
1. **Firebase Authentication**: Verificar se o serviço está habilitado no console
2. **Cloud Firestore**: Verificar se o banco de dados está configurado corretamente
3. **Regras de Segurança**: Verificar se as regras permitem criação de usuários
4. **Conexão com Internet**: Verificar se o dispositivo tem conexão ativa

### C. Testar Cenários Específicos
1. **Email já cadastrado**: Testar com email existente
2. **Senha fraca**: Testar com senha que não atende requisitos
3. **Campos vazios**: Testar validação de formulário
4. **Sem conexão**: Testar comportamento offline

## 🔧 Configurações Necessárias

### Firebase Console
1. **Authentication** → **Sign-in method** → Email/Password: ✅ Habilitado
2. **Cloud Firestore** → **Rules**: Verificar permissões de escrita
3. **Projeto**: Verificar se o `google-services.json` está atualizado

### Regras Sugeridas para Firestore
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 📱 Como Testar a Nova Versão
1. Instale o APK atualizado
2. Preencha o formulário de cadastro completamente
3. Clique em "Cadastrar"
4. Observe se há mensagem de erro ou sucesso
5. Verifique os logs para identificar onde ocorre o crash

## 🆘 Se o Problema Persistir
Se o app continuar crashando:
1. Verifique os logs do LogCat imediatamente após o crash
2. Teste com um email e senha específicos: `teste@teste.com / Teste123!`
3. Verifique se o Firebase está funcionando corretamente
4. Teste o cadastro com Google para isolar o problema

## 📊 Logs Esperados
Ao clicar em cadastrar, você deve ver:
```
D/RegisterViewModel: Iniciando processo de cadastro
D/RegisterViewModel: Criando usuário com email: [email_digitado]
D/RegisterViewModel: Tarefa de criação completada: true
D/RegisterViewModel: UID do usuário: [uid_gerado]
D/RegisterViewModel: Criando documento no Firestore
D/UserRepository: Criando usuário no Firestore: [uid]
D/UserRepository: Usuário criado com sucesso: [uid]
D/RegisterViewModel: Documento criado com sucesso
```

Se algum desses logs não aparecer, o crash ocorreu naquele ponto específico.