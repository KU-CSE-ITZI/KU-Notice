package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

@Service
class KuVolunteerNoticeService(
    private val apiService: ApiService
) {
    fun crawlAllKuVolunteerNotice(kuVolunteerNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(kuVolunteerNotices)
        val crawledNotices = HashSet<Notice>()

        val baseUrl =
            "https://kuvolunteer.konkuk.ac.kr/noticeList.do?siteId=VOLUNTEER&boardSeq=773&menuSeq=5528"
        run outForEach@{
            generateSequence(1) { it + 1 }.forEach { page ->
                val response = apiService.get(baseUrl, pageQuery(page))
                val html = response.body?.string() ?: return@outForEach
                val parsedNotices = parseNoticeList(html)

                if (parsedNotices.isEmpty()) return@outForEach
                for (parsedNotice in parsedNotices) {
                    if (existNotices.contains(parsedNotice)) return@outForEach
                    if (!crawledNotices.add(parsedNotice)) return@outForEach
                }
            }
        }

        return crawledNotices.toList()
    }

    private fun parseNoticeList(html: String): List<Notice> {
        val doc = Jsoup.parse(html)
        val noticeTables = doc.select("tbody#noticeList > tr")
        noticeTables.addAll(doc.select("tbody#dispList > tr"))

        return noticeTables.mapNotNull { parseNotice(it) }
    }

    private fun parseNotice(element: Element?): Notice? {
        if (element == null) return null
        val noticeTag = element.selectFirst("td.subject > a") ?: return null

        val title = noticeTag.text()
        val id = noticeTag.attr("data-itsp-view-link")
        val url =
            "https://kuvolunteer.konkuk.ac.kr/noticeView.do?siteId=VOLUNTEER&boardSeq=773&menuSeq=5528&seq=$id"
        val isImportant = element.selectFirst("td")?.text() == "[공지]"
        return Notice(
            noticeId = id,
            title = title,
            url = url,
            isImportant = isImportant,
            kind = NoticeKind.KU_VOLUNTEER_NOTICE
        )
    }

    private fun pageQuery(page: Int): String {
        return "&pageNum=$page"
    }
}