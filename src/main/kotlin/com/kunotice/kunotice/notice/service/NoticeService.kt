package com.kunotice.kunotice.notice.service

import com.kunotice.kunotice.email.EmailService
import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import com.kunotice.kunotice.notice.repository.NoticeRepository
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val kuNoticeService: KuNoticeService,
    private val kuScholarshipNoticeService: KuScholarshipNoticeService,
    private val engineeringEducationNoticeService: EngineeringEducationNoticeService,
    private val kuVolunteerNoticeService: KuVolunteerNoticeService,
    private val innovationSupportNoticeService: InnovationSupportNoticeService,
    private val cossNoticeService: CossNoticeService,
    private val emailService: EmailService
) {
    fun crawlAllKuNotice() {
        val notices = mutableListOf<Notice>()

        val kuNotices = noticeRepository.findAllByKind(NoticeKind.KU_NOTICE)
        notices.addAll(kuNoticeService.crawlAllKuNotice(kuNotices))

        val kuScholarshipNotices =
            noticeRepository.findAllByKind(NoticeKind.KU_SCHOLARSHIP_NOTICE)
        notices.addAll(
            kuScholarshipNoticeService.crawlAllKuScholarshipNotice(
                kuScholarshipNotices
            )
        )

        val engineeringEducationNotices =
            noticeRepository.findAllByKind(NoticeKind.ENGINEERING_EDUCATION_NOTICE)
        notices.addAll(
            engineeringEducationNoticeService.crawlAllEngineeringEducationNotice(
                engineeringEducationNotices
            )
        )

        val kuVolunteerNotices =
            noticeRepository.findAllByKind(NoticeKind.KU_VOLUNTEER_NOTICE)
        notices.addAll(kuVolunteerNoticeService.crawlAllKuVolunteerNotice(kuVolunteerNotices))

        val innovationSupportNotices =
            noticeRepository.findAllByKind(NoticeKind.INNOVATION_SUPPORT_NOTICE)
        notices.addAll(
            innovationSupportNoticeService.crawlAllInnovationSupportNotice(
                innovationSupportNotices
            )
        )

        val cossNotices = noticeRepository.findAllByKind(NoticeKind.COSS_NOTICE)
        notices.addAll(cossNoticeService.crawlAllCossNotice(cossNotices))

        emailService.sendAll(notices)

        noticeRepository.saveAll(notices)
    }

    fun resetAllNotices() {
        noticeRepository.deleteAll()
    }

    fun sendSavedNotices() {
        val notices = noticeRepository.findAll()
        emailService.sendAll(notices.toMutableList())
    }
}