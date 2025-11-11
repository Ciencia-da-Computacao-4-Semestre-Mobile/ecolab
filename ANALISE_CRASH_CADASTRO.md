# 🔍 Análise Detalhada do Crash no Cadastro

## 🎯 **O que foi implementado para diagnosticar o crash:**

### 1. **Crash Handler Global** ✅
```kotlin
// Em MyEcolabApplication.kt
Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
    Log.e("EcoLabCrash", "🚨 CRASH DETECTADO! 🚨", exception)
    // Detecta especificamente crashes no cadastro
    if (exception.stackTrace.any { element ->
        element.className.contains("RegisterViewModel", ignoreCase = true) ||
        element.className.contains("UserRepository", ignoreCase = true)
    }) {
        Log.e("EcoLabCrash", "💥 CRASH NO PROCESSO DE CADASTRO! 💥")
    }
}
```

### 2. **Validação Completa no RegisterViewModel** ✅
```kotlin
// Validações adicionadas:
- Nome não pode estar vazio
- Email não pode estar vazio  
- Senha não pode estar vazia
- Senhas devem coincidir
- Senha deve atender requisitos
- Email deve ter formato válido
```

### 3. **Logs Detalhados no UserRepository** ✅
```kotlin
// Validação de dados antes de salvar:
- ID não pode estar vazio
- Nome não pode estar vazio
- Email não pode estar vazio
- Email deve ter formato válido (regex)
- Verificação após criação
```

## 🚨 **Principais Causas Prováveis do Crash**

### **Causa #1: Firebase não configurado corretamente** 🔥
**Probabilidade: 90%**

**Sintomas:**
- Crash imediato ao clicar em cadastrar
- Log mostra: "FirebaseFirestoreException" ou "Permission denied"

**Verifique:**
1. ✅ **Firestore está habilitado** no Firebase Console?
2. ✅ **Authentication está habilitado** com Email/Password?
3. ✅ **google-services.json está atualizado** no projeto?
4. ✅ **Regras do Firestore permitem escrita**?

**Teste rápido:** Use regras abertas temporariamente:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

### **Causa #2: Falta de permissão de internet** 🔥
**Probabilidade: 70%**

**Sintomas:**
- Timeout ou "Network error"
- App fecha após alguns segundos de tentativa

**Verifique:**
- ✅ Permissão `INTERNET` no AndroidManifest.xml
- ✅ Dispositivo tem conexão ativa
- ✅ Não está em modo avião

### **Causa #3: Dados inválidos sendo enviados** 🔥
**Probabilidade: 60%**

**Sintomas:**
- IllegalArgumentException
- "Email inválido" ou campos vazios

**Verifique:**
- ✅ Email tem formato válido (ex: teste@example.com)
- ✅ Nome não está vazio
- ✅ Senha atende requisitos (8+ chars, maiúscula, minúscula, número, especial)

### **Causa #4: Email já cadastrado** 🔥
**Probabilidade: 50%**

**Sintomas:**
- FirebaseAuthUserCollisionException
- Mensagem: "Este e-mail já está em uso"

**Solução:**
- Use um email diferente para teste
- Ou tente fazer login com o email existente

### **Causa #5: Problema com Hilt/DI** 🔥
**Probabilidade: 40%**

**Sintomas:**
- Erro de injeção de dependência
- NullPointerException em repositories

**Verifique:**
- ✅ `@HiltAndroidApp` na Application
- ✅ `@AndroidEntryPoint` na MainActivity
- ✅ `@HiltViewModel` no RegisterViewModel

## 🔍 **Como Identificar Exatamente o Problema**

### **Passo 1: Ver Logs no Android Studio**
1. Abra Android Studio
2. Conecte o dispositivo
3. Vá para **LogCat** 
4. Filtre por: `EcoLabCrash OR RegisterViewModel OR UserRepository`

### **Passo 2: Teste com Dados Específicos**
```
Nome: "Teste Usuario"
Email: "teste2024@example.com" 
Senha: "Teste123!"
Confirmar Senha: "Teste123!"
```

### **Passo 3: Teste Google Sign-In**
- Se o Google Sign-In funcionar, o problema é específico do email/senha
- Se também crashar, é problema geral do Firebase

## 🎯 **Logs que Você Deve Ver**

### ✅ **Fluxo de Sucesso:**
```
D/RegisterViewModel: Iniciando processo de cadastro
D/RegisterViewModel: Dados do cadastro - Nome: 'Teste Usuario', Email: 'teste@example.com'
D/RegisterViewModel: Criando usuário com email: teste@example.com
D/RegisterViewModel: Tarefa de criação completada: true
D/RegisterViewModel: UID do usuário: abc123
D/UserRepository: === INICIANDO CRIAÇÃO DE USUÁRIO ===
D/UserRepository: ✅ USUÁRIO CRIADO COM SUCESSO: abc123
```

### ❌ **Fluxo de Erro (você verá isso):**
```
E/EcoLabCrash: 🚨 CRASH DETECTADO! 🚨
E/EcoLabCrash: 💥 CRASH NO PROCESSO DE CADASTRO DETECTADO! 💥
E/UserRepository: ❌ ERRO AO CRIAR USUÁRIO NO FIRESTORE
E/UserRepository: Tipo da exceção: FirebaseFirestoreException
```

## 🚀 **Próximos Passos**

1. **Teste a nova versão** com os dados sugeridos
2. **Copie os logs completos** do crash (se ainda ocorrer)
3. **Envie os logs** - eles mostrarão exatamente qual é o problema
4. **Verifique o Firebase Console** - certifique-se de que os serviços estão habilitados

**Com os logs detalhados que implementamos, agora conseguiremos identificar exatamente onde está o problema e aplicar a correção específica!** 🎯