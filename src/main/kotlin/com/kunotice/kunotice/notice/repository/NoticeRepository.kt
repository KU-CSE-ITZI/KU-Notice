package com.kunotice.kunotice.notice.repository

import com.kunotice.kunotice.notice.entity.Notice
import com.kunotice.kunotice.notice.enum.NoticeKind
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<Notice, Long> {
    fun findAllByKind(noticeKind: NoticeKind): List<Notice>
}