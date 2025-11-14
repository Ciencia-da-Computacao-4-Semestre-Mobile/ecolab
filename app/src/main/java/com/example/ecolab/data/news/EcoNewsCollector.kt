package com.example.ecolab.data.news

import com.example.ecolab.R
import com.example.ecolab.ui.screens.LibraryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.random.Random
import java.net.URI

/**
 * Sistema simplificado de coleta de notícias ambientais para o EcoLab
 * Apenas coleta notícias de sustentabilidade e meio ambiente automaticamente
 */
object EcoNewsCollector {
    
    // Palavras-chave para identificar notícias ambientais
    private val environmentalKeywords = listOf(
        "sustentabilidade", "sustentável", "meio ambiente", "ecologia", "ecológico",
        "reciclagem", "reciclável", "reciclaveis", "descarte", "economia circular",
        "clima", "mudanças climáticas", "carbono", "emissão", "emissões",
        "energia", "energia solar", "energia eólica", "energia renovável",
        "poluição", "reutilização", "reuso", "compostagem",
        "floresta", "amazônia", "amazonia", "desmatamento", "reflorestamento",
        "água", "oceano", "mar", "recursos naturais", "biodiversidade",
        "inpe", "cop", "cop30", "verde", "ambiental", "natureza", "conservação"
    )
    
    // Categorias ambientais
    private val environmentalCategories = listOf(
        "Sustentabilidade", "Meio Ambiente", "Reciclagem", "Energia", "Clima",
        "Preservação", "Água", "Natureza", "Biodiversidade", "Tecnologia Verde"
    )
    
    /**
     * Coleta notícias ambientais automaticamente das principais fontes
     * Filtra apenas conteúdo relevante para o EcoLab
     */
    suspend fun collectEnvironmentalNews(): List<EnvironmentalNews> = withContext(Dispatchers.IO) {
        val allNews = mutableListOf<EnvironmentalNews>()
        
        try {
            // Coleta de fontes diferentes (simulação com dados reais)
            allNews.addAll(collectFromG1Ambiental())
            allNews.addAll(collectFromFolhaAmbiental())
            allNews.addAll(collectFromEstadaoAmbiental())
            allNews.addAll(collectFromUOLAmbiental())
            
            // Remove duplicados e ordena por relevância/data
            allNews.distinctBy { it.title }
                .sortedByDescending { it.relevanceScore }
                .take(10) // Apenas as 10 mais relevantes
                
        } catch (e: Exception) {
            // Em caso de erro, retorna notícias mockadas para não quebrar a UI
            getMockEnvironmentalNews()
        }
    }
    
    /**
     * Coleta notícias da seção ambiental do G1
     */
    private suspend fun collectFromG1Ambiental(): List<EnvironmentalNews> {
        return try {
            val url = "https://g1.globo.com/natureza/"
            val doc = Jsoup.connect(url)
                .userAgent("EcoLab-NewsCollector/1.0")
                .timeout(10000)
                .get()
            
            parseEnvironmentalNews(doc, "G1", "https://g1.globo.com")
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Coleta notícias da seção ambiental da Folha
     */
    private suspend fun collectFromFolhaAmbiental(): List<EnvironmentalNews> {
        return try {
            val url = "https://www1.folha.uol.com.br/ambiente/"
            val doc = Jsoup.connect(url)
                .userAgent("EcoLab-NewsCollector/1.0")
                .timeout(10000)
                .get()
            
            parseEnvironmentalNews(doc, "FOLHA", "https://www1.folha.uol.com.br")
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Coleta notícias da seção ambiental do Estadão
     */
    private suspend fun collectFromEstadaoAmbiental(): List<EnvironmentalNews> {
        return try {
            val url = "https://www.estadao.com.br/ambiente/"
            val doc = Jsoup.connect(url)
                .userAgent("EcoLab-NewsCollector/1.0")
                .timeout(10000)
                .get()
            
            parseEnvironmentalNews(doc, "ESTADAO", "https://www.estadao.com.br")
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Coleta notícias da seção ambiental do UOL
     */
    private suspend fun collectFromUOLAmbiental(): List<EnvironmentalNews> {
        return try {
            val url = "https://www.uol.com.br/meio-ambiente/"
            val doc = Jsoup.connect(url)
                .userAgent("EcoLab-NewsCollector/1.0")
                .timeout(10000)
                .get()
            
            parseEnvironmentalNews(doc, "UOL", "https://www.uol.com.br")
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Parse das notícias ambientais do HTML
     */
    private fun parseEnvironmentalNews(doc: org.jsoup.nodes.Document, source: String, baseUrl: String): List<EnvironmentalNews> {
        val newsList = mutableListOf<EnvironmentalNews>()
        
        // Seletores comuns para notícias ambientais
        val selectors = listOf(
            ".feed-post-body-title a",
            ".feed-post a",
            ".headline__title a",
            ".c-headline__title a",
            ".hyperlink-title a",
            ".title a",
            "article h2 a",
            ".news-title a",
            "a[href*='ambiente']",
            "a[href*='meio-ambiente']",
            "a[href*='natureza']"
        )
        
        for (selector in selectors) {
            val elements = doc.select(selector)
            
            for (element in elements.take(5)) { // Máximo 5 por fonte
                try {
                    val title = element.text().trim()
                    val url = element.attr("href")
                    
                    if (title.isNotEmpty() && url.isNotEmpty()) {
                        val fullUrl = normalizeUrl(url, baseUrl)
                        
                        // Encontra resumo e imagem nos elementos próximos
                        val container = element.closest("article") ?: element.parent() ?: element
                        val summary = container.select(".summary, .subtitle, .description, .feed-post-body-resumo")
                            .first()?.text() ?: title
                        val imageUrl = container.select("img").first()?.let { img ->
                            val abs = img.absUrl("src")
                            if (abs.isNotEmpty()) abs else img.attr("src")
                        }?.takeIf { it.isNotEmpty() }
                        
                        val relevanceScore = calculateRelevanceScore(title, summary)
                        val publishedDate = parsePublishedDate(container)
                        val isRelevant = isEnvironmentalNews(title) || isEnvironmentalNews(summary)
                        if (isRelevant) {
                            newsList.add(
                                EnvironmentalNews(
                                    id = "${source}_${title.hashCode().absoluteValue}",
                                    title = title,
                                    summary = summary,
                                    url = fullUrl,
                                    imageUrl = imageUrl,
                                    source = source,
                                    date = publishedDate,
                                    category = categorizeNews(title),
                                    relevanceScore = relevanceScore
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    continue // Pula para próxima notícia se houver erro
                }
            }
            
            if (newsList.size >= 5) break
        }
        
        return newsList
    }
    
    /**
     * Verifica se a notícia é ambiental baseada em palavras-chave
     */
    private fun isEnvironmentalNews(title: String): Boolean {
        val lowerTitle = title.lowercase()
        return environmentalKeywords.any { keyword ->
            lowerTitle.contains(keyword)
        }
    }
    
    /**
     * Calcula score de relevância baseado em palavras-chave ambientais
     */
    private fun calculateRelevanceScore(title: String, summary: String): Int {
        val text = (title + " " + summary).lowercase()
        val keywordCount = environmentalKeywords.count { text.contains(it) }
        return keywordCount * 10 + Random.nextInt(10) // Adiciona randomização para não ficar repetitivo
    }
    
    /**
     * Categoriza a notícia automaticamente
     */
    private fun categorizeNews(title: String): String {
        val lowerTitle = title.lowercase()
        
        return when {
            lowerTitle.contains("reciclagem") || lowerTitle.contains("lixo") || lowerTitle.contains("resíduo") -> "Reciclagem"
            lowerTitle.contains("energia") && (lowerTitle.contains("solar") || lowerTitle.contains("renovável")) -> "Energia"
            lowerTitle.contains("água") || lowerTitle.contains("mar") || lowerTitle.contains("oceano") -> "Água"
            lowerTitle.contains("floresta") || lowerTitle.contains("desmatamento") || lowerTitle.contains("árvore") -> "Floresta"
            lowerTitle.contains("clima") || lowerTitle.contains("carbono") || lowerTitle.contains("emissão") -> "Clima"
            lowerTitle.contains("animal") || lowerTitle.contains("espécie") || lowerTitle.contains("biodiversidade") || lowerTitle.contains("ecologia") -> "Biodiversidade"
            else -> "Sustentabilidade"
        }
    }
    
    private fun normalizeUrl(url: String, baseUrl: String): String {
        return try {
            val trimmed = url.trim()
            val absolute = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                trimmed
            } else {
                val base = if (baseUrl.endsWith("/") || trimmed.startsWith("/")) baseUrl else "$baseUrl/"
                base + trimmed.removePrefix("/")
            }
            val uri = URI(absolute)
            val clean = URI(
                uri.scheme ?: "https",
                uri.userInfo,
                uri.host,
                uri.port,
                uri.path,
                uri.query,
                null // remove fragment
            )
            clean.toString()
        } catch (_: Exception) {
            if (url.startsWith("http")) url else baseUrl + url
        }
    }

    private fun parsePublishedDate(container: Element): Date {
        return try {
            val timeEl = container.selectFirst("time[datetime]")
            val metaEl = container.selectFirst("meta[itemprop=datePublished], meta[property=article:published_time], meta[name=article:published_time]")
            val raw = timeEl?.attr("datetime")?.trim()
                ?: metaEl?.attr("content")?.trim()
            if (!raw.isNullOrEmpty()) {
                val patterns = listOf(
                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd"
                )
                for (p in patterns) {
                    try {
                        val sdf = SimpleDateFormat(p, Locale.getDefault())
                        return sdf.parse(raw) ?: Date()
                    } catch (_: Exception) { }
                }
                Date()
            } else {
                Date()
            }
        } catch (_: Exception) {
            Date()
        }
    }
    
    /**
     * Notícias mockadas como fallback quando não conseguir coletar
     */
    private fun getMockEnvironmentalNews(): List<EnvironmentalNews> {
        return listOf(
            EnvironmentalNews(
                id = "mock_1",
                title = "Brasil atinge recorde de reciclagem de latinhas em 2024",
                summary = "Taxa de reciclagem de alumínio atinge 98%, maior índice da história",
                url = "https://g1.globo.com/natureza/noticia/2024/11/14/reciclagem-aluminio.ghtml",
                imageUrl = null,
                source = "G1",
                date = Date(),
                category = "Reciclagem",
                relevanceScore = 95
            ),
            EnvironmentalNews(
                id = "mock_2", 
                title = "Energia solar cresce 45% nas residências brasileiras",
                summary = "Instalações de painéis solares batem novo recorde este ano",
                url = "https://www1.folha.uol.com.br/ambiente/2024/11/energia-solar.shtml",
                imageUrl = null,
                source = "FOLHA",
                date = Date(),
                category = "Energia",
                relevanceScore = 90
            ),
            EnvironmentalNews(
                id = "mock_3",
                title = "Desmatamento na Amazônia tem queda de 30% em novembro",
                summary = "Dados mostram redução significativa no desmatamento",
                url = "https://www.estadao.com.br/ambiente/2024/11/amazonia.shtml",
                imageUrl = null,
                source = "ESTADAO",
                date = Date(),
                category = "Floresta",
                relevanceScore = 88
            )
        )
    }
}

/**
 * Modelo simplificado de notícia ambiental para o EcoLab
 */
data class EnvironmentalNews(
    val id: String,
    val title: String,
    val summary: String,
    val url: String,
    val imageUrl: String?,
    val source: String,
    val date: Date,
    val category: String,
    val relevanceScore: Int
) {
    // Converte para o formato esperado pela UI
    fun toLibraryItem(): LibraryItem {
        return LibraryItem(
            id = id.hashCode(),
            title = title,
            description = summary,
            category = category,
            readTime = "5 min", // Tempo estimado de leitura
            imageRes = getCategoryIcon(), // Ícone baseado na categoria
            date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date),
            url = url
        )
    }
    
    private fun getCategoryIcon(): Int {
        return R.drawable.ic_launcher_foreground
    }
}