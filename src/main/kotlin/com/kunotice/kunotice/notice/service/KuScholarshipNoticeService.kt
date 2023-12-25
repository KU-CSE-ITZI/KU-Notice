package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class KuScholarshipNoticeService(
    private val apiService: ApiService
) {

    fun crawlAllKuScholarshipNotice(kuScholarshipNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(kuScholarshipNotices)
        val crawledNotices = HashSet<Notice>()

        val baseUrl = "https://www.konkuk.ac.kr/do/MessageBoard/ArticleList.do?forum=11688412"
        run outForEach@{
            generateSequence(0) { it + 1 }.forEach { page ->
                try {
                    val response = apiService.get(baseUrl, pageQuery(page))
                    val html = response.body?.string() ?: return@outForEach
                    for (parsedNotice in parseNoticeList(html)) {
                        if (existNotices.contains(parsedNotice)) return@outForEach
                        if (!crawledNotices.add(parsedNotice)) return@outForEach
                    }
                } catch (e: Exception) {
                    println(e)
                    return@outForEach
                }
            }
        }

        return crawledNotices.toList()
    }

    private fun parseNoticeList(html: String): List<Notice> {
        val doc = Jsoup.parse(html)
        val noticeTables = doc.select("tbody > tr")
        return noticeTables.mapNotNull { parseNotice(it) }
    }

    private fun parseNotice(element: Element?): Notice? {
        if (element == null) return null
        val noticeTag = element.selectFirst("td.subject > a") ?: return null

        val title = noticeTag.text()
        val url = "https://www.konkuk.ac.kr/do/MessageBoard/${noticeTag.attr("href")}"
        val id = url.split("id=").last()
        val isImportant = element.selectFirst("td > img") != null
        val date = element.select("td").eq(2).text()
        return Notice(
            noticeId = id,
            title = title,
            url = url,
            isImportant = isImportant,
            kind = NoticeKind.KU_SCHOLARSHIP_NOTICE,
            date = date
        )
    }

    private fun pageQuery(page: Int): String {
        return "&p=$page"
    }
}