package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.email.EmailService
import com.kunotice.kunotice.notice.enum.NoticeKind
import com.kunotice.kunotice.notice.repository.NoticeRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val kuNoticeService: KuNoticeService,
    private val careerEmploymentNoticeService: CareerEmploymentNoticeService,
    private val emailService: EmailService
) {
    fun crawlAllKuNotice() {
        // 건국대학교 공지사항
        val kuNotices = noticeRepository.findAllByKind(NoticeKind.KU_NOTICE)
        val crawledKuNotices = kuNoticeService.crawlAllKuNotice(kuNotices)

        // 건국대학교 진로취업센터 공지사항
        val careerEmploymentNotices =
            noticeRepository.findAllByKind(NoticeKind.CAREER_EMPLOYMENT_NOTICE)
        val crawledCareerEmploymentNotices =
            careerEmploymentNoticeService.crawlCareerEmploymentNotice(careerEmploymentNotices)

        emailService.sendAll(crawledKuNotices, crawledCareerEmploymentNotices)

        noticeRepository.saveAll(crawledKuNotices)
        noticeRepository.saveAll(crawledCareerEmploymentNotices)
    }
}