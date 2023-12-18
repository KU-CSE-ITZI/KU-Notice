package com.kunotice.kunotice.email

import org.springframework.data.jpa.repository.JpaRepository

interface EmailRepository : JpaRepository<Email, Long>