package com.kunotice.kunotice.email

import com.kunotice.kunotice.notice.entity.Notice
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailRepository: EmailRepository,
    private val javaMailSender: JavaMailSender
) {
    fun sendAll(
        kuNotices: List<Notice>,
        engineeringEducationNotices: List<Notice>,
        kuVolunteerNotices: List<Notice>,
        innovationSupportNotices: List<Notice>,
        cossNotices: List<Notice>
    ) {
        val noticeCount =
            kuNotices.count() + engineeringEducationNotices.count() + kuVolunteerNotices.count() + innovationSupportNotices.count() + cossNotices.count()
        if (noticeCount == 0) return

        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        val kuText =
            "<H2>건국대학교 공지사항</H2>" + kuNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}${if (it.isImportant) "*" else ""}</a>"
            }

        val engineeringEducationText =
            "<H2>건국대학교 공학교육혁신사업단 공지사항</H2>" + engineeringEducationNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}${if (it.isImportant) "*" else ""}</a>"
            }

        val kuVolunteerText =
            "<H2>건국대학교 사회봉사센터 공지사항</H2>" + kuVolunteerNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}${if (it.isImportant) "*" else ""}</a>"
            }


        val innovationSupportText =
            "<H2>건국대학교 대학혁신지원사업 공지사항</H2>" + innovationSupportNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}${if (it.isImportant) "*" else ""}</a>"
            }

        val cossText =
            "<H2>실감미디어 혁신공유대학사업단 공지사항</H2>" + cossNotices.joinToString(separator = "<br><br>") {
                "<a href=\"${it.url}\">${it.title}${if (it.isImportant) "*" else ""}</a>"
            }

        val text =
            "$kuText<br><br>$engineeringEducationText<br><br>$kuVolunteerText<br><br>$innovationSupportText<br><br>$cossText"

        for (email in emailRepository.findAll()) {
            helper.setTo(email.address)
            helper.setSubject("새로운 공지사항이 ${noticeCount}개 등록되었습니다.")
            helper.setText(text, true)
            javaMailSender.send(message)
        }
    }
}