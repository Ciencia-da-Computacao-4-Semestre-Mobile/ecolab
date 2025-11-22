package com.example.ecolab.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.example.ecolab.BuildConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable
data class AiQuizData(val questions: List<AiQuizQuestion>)
@Serializable
data class AiQuizQuestion(val question: String, val options: List<String>, val correctIndex: Int, val explanation: String)

sealed interface QuizUiState {
    object Initial : QuizUiState
    data class Loading(val progress: Int) : QuizUiState
    data class Success(val quiz: AiQuizData) : QuizUiState
    data class Error(val error: String) : QuizUiState
}

class QuizViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Initial)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()
    private val httpClient by lazy { OkHttpClient() }
    private val MAX_AI_CHARS = 8_000
    private val json by lazy { Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true; allowTrailingComma = true } }
    private var cache: QuizCache? = null
    private val basePrompt = """
        Gere um quiz em JSON no formato:
        {"questions":[{"question":"texto","options":["A","B","C","D"],"correctIndex":0,"explanation":"texto"}]}
        Regras:
        - Somente JSON válido, sem texto fora do JSON
        - Não repita perguntas, nem variações semânticas iguais
        - Perguntas claras e objetivas, uma correta por item
    """.trimIndent()

    fun generateQuiz(topic: String, numQuestions: Int) {
        viewModelScope.launch {
            val user = auth.currentUser ?: run {
                _uiState.value = QuizUiState.Error("Você precisa estar logado para gerar o quiz.")
                return@launch
            }
            if (BuildConfig.GOOGLE_IA_STUDIO_API_KEY.isBlank()) {
                _uiState.value = QuizUiState.Error("Chave da IA não configurada")
                return@launch
            }
            _uiState.value = QuizUiState.Loading(0)
            try {
                val safeCount = numQuestions.coerceIn(1, 30)
                val prompt = buildPrompt(topic, safeCount)
                Log.d("QuizViewModel", "Prompt enviado: ${prompt.take(120)}...")
                _uiState.value = QuizUiState.Loading(10)
                val rawText = tryGenerateContent(prompt)
                _uiState.value = QuizUiState.Loading(30)
                if (rawText.isNotBlank()) {
                    Log.d("QuizViewModel", "Resposta bruta: ${rawText.take(200)}...")
                    _uiState.value = QuizUiState.Loading(50)
                    val quiz = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                        runCatching { safeParsePipeline(rawText, topic, safeCount) }
                            .getOrElse { normalizeQuizData(generateLocalQuiz(topic, safeCount)) }
                    }
                    _uiState.value = QuizUiState.Loading(95)
                    _uiState.value = QuizUiState.Success(quiz)
                } else {
                    _uiState.value = QuizUiState.Loading(40)
                    val cached = cache?.get(cacheKey(topic, safeCount))
                    val fallback = if (!cached.isNullOrBlank()) {
                        try {
                            val data = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true; allowTrailingComma = true }
                                .decodeFromString<AiQuizData>(cached)
                            normalizeQuizData(data)
                        } catch (_: Throwable) {
                            normalizeQuizData(generateLocalQuiz(topic, safeCount))
                        }
                    } else {
                        normalizeQuizData(generateLocalQuiz(topic, safeCount))
                    }
                    _uiState.value = QuizUiState.Success(fallback)
                }
            } catch (e: Throwable) {
                Log.e("QuizViewModel", "Falha ao gerar quiz via IA: ${e.javaClass.simpleName}: ${e.message}")
                Log.e("QuizViewModel", e.stackTraceToString())
                _uiState.value = QuizUiState.Loading(90)
                val safeCount = numQuestions.coerceIn(1, 30)
                val cached = cache?.get(cacheKey(topic, safeCount))
                val fallback = if (!cached.isNullOrBlank()) {
                    try {
                        val data = Json { ignoreUnknownKeys = true; isLenient = true; coerceInputValues = true; allowTrailingComma = true }
                            .decodeFromString<AiQuizData>(cached)
                        normalizeQuizData(data)
                    } catch (_: Throwable) {
                        normalizeQuizData(generateLocalQuiz(topic, 10))
                    }
                } else {
                    normalizeQuizData(generateLocalQuiz(topic, 10))
                }
                _uiState.value = QuizUiState.Success(fallback)
            }
        }
    }

    private fun parseQuiz(jsonText: String, limit: Int): AiQuizData {
        val decoded = json.decodeFromString<AiQuizData>(jsonText)
        val map = LinkedHashMap<String, AiQuizQuestion>()
        for (q in decoded.questions) {
            val question = q.question.trim()
            val opts = q.options.map { it.trim() }.filter { it.isNotBlank() }
            val idx = q.correctIndex
            if (question.isNotBlank() && opts.size >= 2 && idx in 0 until opts.size) {
                val key = question.lowercase()
                if (!map.containsKey(key)) {
                    map[key] = AiQuizQuestion(question, opts, idx, q.explanation)
                }
            }
        }
        val finalList = map.values.take(limit)
        return AiQuizData(finalList)
    }

    private fun buildPrompt(topic: String, num: Int): String {
        return """
            $basePrompt
            - $num perguntas sobre: $topic
        """.trimIndent()
    }

    private fun extractJson(text: String): String? {
        val cleaned = text
            .replace("```json", "")
            .replace("```", "")
            .replace('\uFEFF'.toString(), "")
            .trim()
        val start = cleaned.indexOf('{')
        val end = cleaned.lastIndexOf('}')
        return if (start >= 0 && end > start && end - start < MAX_AI_CHARS) cleaned.substring(start, end + 1) else null
    }

    private fun sanitizeJson(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            if (c == ']' || c == '}') {
                var j = sb.length - 1
                while (j >= 0 && sb[j].isWhitespace()) j--
                if (j >= 0 && sb[j] == ',') {
                    sb.deleteCharAt(j)
                }
                sb.append(c)
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun parseOrFallback(rawText: String, theme: String, limit: Int): AiQuizData {
        return try {
            val json = extractJson(rawText)
            if (json.isNullOrBlank()) generateLocalQuiz(theme, limit) else {
                val fixed = sanitizeJson(json)
                val quiz = parseQuiz(fixed, limit)
                if (quiz.questions.isEmpty()) generateLocalQuiz(theme, limit) else quiz
            }
        } catch (e: Throwable) {
            Log.w("QuizViewModel", "Parse falhou: ${e.javaClass.simpleName}: ${e.message}")
            Log.w("QuizViewModel", e.stackTraceToString())
            generateLocalQuiz(theme, limit)
        }
    }

    private fun normalizeQuizData(data: AiQuizData): AiQuizData {
        fun truncate(s: String, max: Int) = if (s.length > max) s.take(max) else s
        val questions = data.questions.mapNotNull { q ->
            val opts = q.options.take(6).map { o -> truncate(o.trim(), 100) }.filter { it.isNotBlank() }
            val question = truncate(q.question.trim(), 200)
            val idx = q.correctIndex.coerceIn(0, if (opts.isNotEmpty()) opts.size - 1 else 0)
            if (question.isNotBlank() && opts.size >= 2) {
                AiQuizQuestion(question = question, options = opts, correctIndex = idx, explanation = truncate(q.explanation, 200))
            } else null
        }
        return AiQuizData(questions)
    }

    private fun safeParsePipeline(rawText: String, theme: String, limit: Int): AiQuizData {
        val base = extractJson(rawText) ?: return normalizeQuizData(generateLocalQuiz(theme, limit))
        val sanitized = sanitizeJson(base)
        val minimized = buildMinJson(sanitized)
        cache?.put(cacheKey(theme, limit), minimized)
        cache?.put("latest_ai_quiz", minimized)
        val parsed = parseQuiz(minimized, limit)
        return normalizeQuizData(parsed)
    }

    private fun buildMinJson(text: String): String {
        val s = if (text.length > MAX_AI_CHARS) text.take(MAX_AI_CHARS) else text
        val key = "\"questions\""
        val i = s.indexOf(key)
        if (i < 0) return s
        var j = s.indexOf('[', i)
        if (j < 0) return s
        var depth = 0
        var k = j
        while (k < s.length) {
            val ch = s[k]
            if (ch == '[') depth++
            else if (ch == ']') {
                depth--
                if (depth == 0) { break }
            }
            k++
        }
        if (k >= s.length) return s
        val arr = s.substring(j, k + 1)
        return "{\"questions\":$arr}"
    }

    private suspend fun tryGenerateContent(prompt: String): String {
        val discovered = discoverModels()
        val candidates = if (discovered.isNotEmpty()) discovered else listOf(
            "gemini-2.5-flash",
            "gemini-2.5-flash-lite",
            "gemini-2.5-mini",
            "gemini-2.5-pro",
            "gemini-1.5-flash-001",
            "gemini-1.5-pro-001",
            "gemini-1.0-pro"
        )
        return try {
            kotlinx.coroutines.withTimeout(18_000) {
                var out = ""
                for (m in candidates) {
                    try {
                        val txt = callGemini(m, prompt)
                        if (txt.isNotBlank()) {
                            out = if (txt.length > MAX_AI_CHARS) txt.take(MAX_AI_CHARS) else txt
                            break
                        }
                    } catch (e: Exception) {
                        Log.w("QuizViewModel", "Modelo falhou: $m -> ${e.message}")
                    }
                }
                out
            }
        } catch (_: Exception) { "" }
    }

    private suspend fun callGemini(model: String, prompt: String): String {
        val key = BuildConfig.GOOGLE_IA_STUDIO_API_KEY.trim().trimEnd('.')
        val base = if (useV1()) "https://generativelanguage.googleapis.com/v1" else "https://generativelanguage.googleapis.com/v1beta"
        val url = "$base/models/$model:generateContent?key=$key"
        val media = "application/json; charset=utf-8".toMediaType()

        // Escapar o prompt corretamente para JSON
        val escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")

        val bodyJson = """
        {
          "contents": [{
            "role": "user",
            "parts": [{
              "text": "$escapedPrompt"
            }]
          }],
          "generationConfig": {
            "maxOutputTokens": 1536,
            "temperature": 0.2
          }
        }
        """.trimIndent()

        val req = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody(media))
            .build()
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            httpClient.newCall(req).execute().use { resp ->
                val body = resp.body?.string() ?: ""
                if (!resp.isSuccessful) {
                    Log.w("QuizViewModel", "HTTP ${resp.code}: ${body.take(300)}")
                    ""
                } else {
                    extractTextFromResponse(body)
                }
            }
        }
    }

    private fun extractTextFromResponse(body: String): String {
        val root = org.json.JSONObject(body)
        val candidates = root.optJSONArray("candidates") ?: return ""
        if (candidates.length() == 0) return ""
        val content = candidates.getJSONObject(0).optJSONObject("content") ?: return ""
        val parts = content.optJSONArray("parts") ?: return ""
        val sb = StringBuilder()
        for (i in 0 until parts.length()) {
            val p = parts.getJSONObject(i)
            val t = p.optString("text", "")
            if (t.isNotBlank()) {
                if (sb.length + t.length > MAX_AI_CHARS) {
                    val remaining = MAX_AI_CHARS - sb.length
                    if (remaining > 0) sb.append(t.take(remaining))
                    break
                } else {
                    sb.append(t)
                }
            }
        }
        return sb.toString()
    }

    private fun useV1(): Boolean = true

    private suspend fun discoverModels(): List<String> {
        val key = BuildConfig.GOOGLE_IA_STUDIO_API_KEY.trim().trimEnd('.')
        val v1Url = "https://generativelanguage.googleapis.com/v1/models?key=$key"
        val v1betaUrl = "https://generativelanguage.googleapis.com/v1beta/models?key=$key"
        val names = mutableSetOf<String>()
        suspend fun fetch(url: String) {
            val req = Request.Builder().url(url).get().build()
            val body = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                httpClient.newCall(req).execute().use { resp -> resp.body?.string() ?: "" }
            }
            if (body.isNotBlank()) {
                try {
                    val arr = org.json.JSONObject(body).optJSONArray("models") ?: return
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val fullName = obj.optString("name")
                        if (fullName.startsWith("models/")) {
                            val slug = fullName.removePrefix("models/")
                            names.add(slug)
                        }
                    }
                } catch (_: Exception) {}
            }
        }
        try { fetch(v1Url) } catch (_: Exception) {}
        try { fetch(v1betaUrl) } catch (_: Exception) {}
        // Preferir 2.5 (flash/mini/flash-lite), depois pro, depois 1.5/1.0
        return names.filter { it.contains("gemini", true) }
            .sortedBy { n ->
                when {
                    n.contains("2.5", true) && n.contains("flash-lite", true) -> 0
                    n.contains("2.5", true) && n.contains("mini", true) -> 1
                    n.contains("2.5", true) && n.contains("flash", true) -> 2
                    n.contains("2.5", true) && n.contains("pro", true) -> 3
                    n.contains("1.5", true) && n.contains("flash", true) -> 5
                    n.contains("1.5", true) && n.contains("pro", true) -> 6
                    else -> 9
                }
            }
    }

    private fun generateLocalQuiz(theme: String, limit: Int): AiQuizData {
        val source = when (theme) {
            "Aleatório" -> quizQuestions.shuffled()
            "Default" -> quizQuestions.shuffled()
            else -> quizQuestions.filter { it.theme == theme }.shuffled()
        }
        val items = source.take(limit).map { q ->
            val idx = q.options.indexOf(q.correctAnswer).let { if (it < 0) 0 else it }
            AiQuizQuestion(
                question = q.question,
                options = q.options,
                correctIndex = idx,
                explanation = "Resposta correta: ${q.correctAnswer}"
            )
        }
        return AiQuizData(items)
    }

    fun attachCache(context: Context) {
        cache = QuizCache(context)
    }

    fun reset() {
        _uiState.value = QuizUiState.Initial
    }

    private fun cacheKey(topic: String, count: Int): String = topic.trim().lowercase() + ":" + count
}

private class QuizCache(context: Context) {
    private val prefs = context.getSharedPreferences("quiz_cache", Context.MODE_PRIVATE)
    fun get(key: String): String? = prefs.getString(key, null)
    fun put(key: String, value: String) { prefs.edit().putString(key, value).commit() }
}