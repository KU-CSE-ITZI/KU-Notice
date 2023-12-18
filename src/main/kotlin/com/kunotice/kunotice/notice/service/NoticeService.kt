package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.email.EmailService
import com.kunotice.kunotice.notice.repository.NoticeRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val kuNoticeService: KuNoticeService,
    private val emailService: EmailService
) {
    fun crawlAllKuNotice() {
        val latestKuNotice = noticeRepository.findTopByOrderByNoticeIdDesc()
        val crawledKuNotices = kuNoticeService.crawlAllKuNotice(latestKuNotice)
        if (crawledKuNotices.isNotEmpty()) emailService.sendAll(crawledKuNotices)
        noticeRepository.saveAll(crawledKuNotices)
    }
}