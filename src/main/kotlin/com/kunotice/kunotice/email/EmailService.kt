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
                text.append("<H2>${NoticeKind.getTitle(kind)}</H2>" + notices.joinToString(separator = "<br><br>") {
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