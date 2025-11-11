# 🧪 Teste de Diagnóstico do Crash no Cadastro

## ✅ **Melhorias Implementadas**

### 1. **Crash Handler Global**
- Adicionado capturador de crashes global na `MyEcolabApplication`
- Logs detalhados serão exibidos no LogCat quando o app crashar
- Identificação específica de crashes no processo de cadastro

### 2. **Validação Aprimorada no RegisterViewModel**
- Validação de campos vazios antes de tentar criar usuário
- Logs detalhados de cada etapa do processo
- Verificação de senhas coincidentes
- Validação de requisitos de senha

### 3. **Logs Detalhados no UserRepository**
- Validação de dados antes de salvar no Firestore
- Verificação de formato de email
- Logs de sucesso e falha com detalhes específicos
- Verificação após criação para confirmar sucesso

## 🎯 **Como Testar Agora**

### Passo 1: Instalar a Nova Versão
1. Aguarde a compilação terminar
2. Instale o APK atualizado no dispositivo

### Passo 2: Testar o Cadastro
1. **Abra o app**
2. **Vá para a tela de cadastro**
3. **Preencha os campos com dados válidos:**
   - Nome: "Teste Usuário"
   - Email: "teste@example.com"
   - Senha: "Teste123!"
   - Confirmar Senha: "Teste123!"

### Passo 3: Observar os Resultados

#### ✅ **Se Funcionar:**
- Você verá mensagem de sucesso
- O app navegará para a tela inicial
- Os logs mostrarão:
```
D/RegisterViewModel: === INICIANDO CRIAÇÃO DE USUÁRIO ===
D/UserRepository: ✅ USUÁRIO CRIADO COM SUCESSO: [userId]
```

#### ❌ **Se Crashar:**
- O app fechará inesperadamente
- **Os logs mostrarão exatamente onde ocorreu o erro**
- Procure por mensagens como:
```
E/EcoLabCrash: 🚨 CRASH DETECTADO! 🚨
E/EcoLabCrash: 💥 CRASH NO PROCESSO DE CADASTRO DETECTADO! 💥
```

## 🔍 **Como Ver os Logs**

### Opção 1: Android Studio (Recomendado)
1. Abra o Android Studio
2. Conecte o dispositivo
3. Vá para **LogCat** (janela inferior)
4. Filtre por "EcoLabCrash" ou "RegisterViewModel"

### Opção 2: Linha de Comando (se tiver ADB)
```bash
adb logcat | grep -E "(EcoLabCrash|RegisterViewModel|UserRepository)"
```

## 📋 **Possíveis Causas e Soluções**

### **1. Firebase não configurado**
**Sintoma:** Erro sobre permissões ou serviço não disponível
**Solução:** Verifique no Firebase Console:
- Authentication → Sign-in method → Email/Password: Habilitado
- Cloud Firestore → Criado e com regras permissivas

### **2. Regras do Firestore muito restritivas**
**Sintoma:** "Permission denied" ou similar
**Solução:** Temporariamente use regras abertas para teste:
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

### **3. Dados inválidos**
**Sintoma:** "IllegalArgumentException" ou validação falha
**Solução:** Use os dados de teste sugeridos acima

### **4. Problema de conexão**
**Sintoma:** Timeouts ou erros de rede
**Solução:** Verifique conexão com internet

## 🚨 **Se Ainda Crashar**

1. **Copie os logs completos** do momento do crash
2. **Envie os logs** - eles mostrarão exatamente onde está o problema
3. **Teste com Google Sign-In** para isolar se é problema específico do email/senha

## 📞 **Próximos Passos**

Assim que você testar e me enviar os logs do crash (se ainda ocorrer), poderei identificar exatamente qual é o problema e aplicar a correção específica!

Os logs agora são muito mais detalhados e vão mostrar:
- Em qual arquivo ocorreu o erro
- Qual linha causou o problema
- Qual tipo de exceção foi lançada
- Mensagem detalhada do erro