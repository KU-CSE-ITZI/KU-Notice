package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.email.EmailService
import com.kunotice.kunotice.notice.enum.NoticeKind
import com.kunotice.kunotice.notice.repository.NoticeRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val kuNoticeService: KuNoticeService,
    private val engineeringEducationNoticeService: EngineeringEducationNoticeService,
    private val kuVolunteerNoticeService: KuVolunteerNoticeService,
    private val emailService: EmailService
) {
    fun crawlAllKuNotice() {
        val kuNotices = noticeRepository.findAllByKind(NoticeKind.KU_NOTICE)
        val crawledKuNotices = kuNoticeService.crawlAllKuNotice(kuNotices)

        val engineeringEducationNotices =
            noticeRepository.findAllByKind(NoticeKind.ENGINEERING_EDUCATION_NOTICE)
        val crawledEngineeringEducationNotices =
            engineeringEducationNoticeService.crawlAllEngineeringEducationNotice(
                engineeringEducationNotices
            )

        val kuVolunteerNotices =
            noticeRepository.findAllByKind(NoticeKind.KU_VOLUNTEER_NOTICE)
        val crawledKuVolunteerNotices =
            kuVolunteerNoticeService.crawlAllKuVolunteerNotice(kuVolunteerNotices)

        emailService.sendAll(
            crawledKuNotices,
            crawledEngineeringEducationNotices,
            crawledKuVolunteerNotices
        )

        noticeRepository.saveAll(crawledKuNotices)
        noticeRepository.saveAll(crawledEngineeringEducationNotices)
    }
}