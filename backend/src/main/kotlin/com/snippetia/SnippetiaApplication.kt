package com.snippetia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.cache.annotation.EnableCaching
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * Snippetia Application - Enterprise-Grade Code Sharing Platform
 * 
 * Features:
 * - AI-Powered Code Analysis and Suggestions
 * - Global Scale Multi-Region Deployment
 * - Advanced Security with Zero-Trust Architecture
 * - Real-Time Collaboration and Synchronization
 * - Enterprise MLOps and Model Management
 * - Comprehensive Analytics and Monitoring
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true)
@EnableJpaRepositories
@EnableJpaAuditing
class SnippetiaApplication

fun main(args: Array<String>) {
    runApplication<SnippetiaApplication>(*args)
}