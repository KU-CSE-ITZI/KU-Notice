package com.kunotice.kunotice.email

import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailRepository: EmailRepository,
    private val javaMailSender: JavaMailSender
) {
    val titleMap = mapOf(
        NoticeKind.KU_NOTICE to "건국대학교 공지사항",
        NoticeKind.ENGINEERING_EDUCATION_NOTICE to "건국대학교 공학교육혁신사업단 공지사항",
        NoticeKind.KU_VOLUNTEER_NOTICE to "건국대학교 사회봉사센터 공지사항",
        NoticeKind.INNOVATION_SUPPORT_NOTICE to "건국대학교 대학혁신지원사업 공지사항",
        NoticeKind.COSS_NOTICE to "실감미디어 혁신공유대학사업단 공지사항",
    )

    fun sendAll(
        notices: MutableList<Notice>,
    ) {
        if (notices.isEmpty()) return

        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        val text = StringBuilder()
        notices
            .sortedByDescending { it.date }
            .groupBy { it.kind }
            .forEach { (kind, notices) ->
                text.append("<H2>${titleMap[kind]}</H2>" + notices.joinToString(separator = "<br><br>") {
                    "[${it.date}] ${if (it.isImportant) "[*중요*] " else ""}<a href=\"${it.url}\">${it.title}</a>"
                })
            }

        for (email in emailRepository.findAll()) {
            helper.setTo(email.address)
            helper.setSubject("새로운 공지사항이 ${notices.size}개 등록되었습니다.")
            helper.setText(text.toString(), true)
            javaMailSender.send(message)
        }
    }
}