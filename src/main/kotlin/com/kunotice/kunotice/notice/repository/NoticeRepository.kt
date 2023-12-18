package com.kunotice.kunotice.notice.repository

import com.kunotice.kunotice.notice.entity.Notice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<Notice, Long> {
    fun findTopByOrderByNoticeIdDesc(): Notice?
}