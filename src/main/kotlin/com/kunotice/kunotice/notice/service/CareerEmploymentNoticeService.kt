package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.common.service.ApiService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.springframework.stereotype.Service

@Service
class CareerEmploymentNoticeService(
    private val apiService: ApiService
) : BaseNoticeService() {
    fun crawlCareerEmploymentNotice(careerEmploymentNotices: List<Notice>): List<Notice> {
        val existNotices = HashSet<Notice>(careerEmploymentNotices)
        val crawledNotices = HashSet<Notice>()

        val baseUrl = "https://www.konkuk.ac.kr/do/MessageBoard/ArticleList.do?forum=11731332"
        run outForEach@{
            generateSequence(0) { it + 1 }.forEach { page ->
                val response = apiService.get(baseUrl, pageQuery(page))
                val html = response.body?.string() ?: return@outForEach
                for (parsedNotice in parseNoticeList(html, NoticeKind.CAREER_EMPLOYMENT_NOTICE)) {
                    if (existNotices.contains(parsedNotice)) return@outForEach
                    if (!crawledNotices.add(parsedNotice)) return@outForEach
                }
            }
        }

        return crawledNotices.toList()
    }
}