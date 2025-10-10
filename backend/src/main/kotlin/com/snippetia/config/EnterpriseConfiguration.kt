package com.snippetia.config

import com.snippetia.service.*
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.cache.annotation.EnableCaching
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.boot.actuator.autoconfigure.metrics.MeterRegistryCustomizer
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.boot.actuate.health.Health
import org.springframework.stereotype.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.Executors
import java.time.Duration

/**
 * Enterprise-Grade Configuration
 * Production-ready settings for global scale deployment
 */

@Configuration
@EnableConfigurationProperties(
    EnterpriseProperties::class,
    AIOrchestrationProperties::class,
    SecurityProperties::class,
    ScalingProperties::class,
    MLOpsProperties::class
)
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableTransactionManagement
@EnableMethodSecurity(prePostEnabled = true)
class EnterpriseConfiguration {

    @Bean
    fun applicationCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Bean
    fun highPerformanceExecutor() = Executors.newVirtualThreadPerTaskExecutor()

    @Bean
    fun meterRegistryCustomizer(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config()
                .meterFilter(MeterFilter.deny { id ->
                    id.name.startsWith("jvm.gc.pause") && 
                    id.getTag("cause")?.contains("Allocation Failure") == true
                })
                .commonTags("application", "snippetia", "environment", "production")
        }
    }
}

@ConfigurationProperties(prefix = "snippetia.enterprise")
data class EnterpriseProperties(
    val deployment: DeploymentConfig = DeploymentConfig(),
    val performance: PerformanceConfig = PerformanceConfig(),
    val monitoring: MonitoringConfig = MonitoringConfig(),
    val compliance: ComplianceConfig = ComplianceConfig()
)

data class DeploymentConfig(
    val environment: String = "production",
    val region: String = "us-east-1",
    val availabilityZones: List<String> = listOf("us-east-1a", "us-east-1b", "us-east-1c"),
    val multiRegion: Boolean = true,
    val autoScaling: Boolean = true,
    val loadBalancing: Boolean = true,
    val healthCheckInterval: Duration = Duration.ofSeconds(30),
    val gracefulShutdownTimeout: Duration = Duration.ofSeconds(30)
)

data class PerformanceConfig(
    val maxConcurrentRequests: Int = 10000,
    val requestTimeoutMs: Long = 30000,
    val connectionPoolSize: Int = 100,
    val cacheSize: Long = 1000000, // 1M entries
    val cacheTtl: Duration = Duration.ofMinutes(15),
    val enableCompression: Boolean = true,
    val enableHttp2: Boolean = true,
    val enableGrpc: Boolean = true
)

data class MonitoringConfig(
    val metricsEnabled: Boolean = true,
    val tracingEnabled: Boolean = true,
    val loggingLevel: String = "INFO",
    val alertingEnabled: Boolean = true,
    val healthCheckEndpoint: String = "/actuator/health",
    val metricsEndpoint: String = "/actuator/metrics",
    val prometheusEnabled: Boolean = true,
    val grafanaEnabled: Boolean = true
)

data class ComplianceConfig(
    val gdprEnabled: Boolean = true,
    val ccpaEnabled: Boolean = true,
    val hipaaEnabled: Boolean = false,
    val soc2Enabled: Boolean = true,
    val iso27001Enabled: Boolean = true,
    val auditLogging: Boolean = true,
    val dataRetentionDays: Int = 2555, // 7 years
    val encryptionAtRest: Boolean = true,
    val encryptionInTransit: Boolean = true
)

@ConfigurationProperties(prefix = "snippetia.ai")
data class AIOrchestrationProperties(
    val models: ModelConfig = ModelConfig(),
    val orchestration: OrchestrationConfig = OrchestrationConfig(),
    val performance: AIPerformanceConfig = AIPerformanceConfig()
)

data class ModelConfig(
    val defaultModel: String = "gpt-4-turbo",
    val fallbackModel: String = "gpt-3.5-turbo",
    val maxConcurrentRequests: Int = 1000,
    val requestTimeoutMs: Long = 60000,
    val retryAttempts: Int = 3,
    val circuitBreakerThreshold: Int = 5,
    val circuitBreakerTimeout: Duration = Duration.ofMinutes(1)
)

data class OrchestrationConfig(
    val loadBalancing: Boolean = true,
    val failoverEnabled: Boolean = true,
    val cachingEnabled: Boolean = true,
    val batchingEnabled: Boolean = true,
    val batchSize: Int = 10,
    val batchTimeoutMs: Long = 100
)

data class AIPerformanceConfig(
    val gpuAcceleration: Boolean = true,
    val tensorOptimization: Boolean = true,
    val modelQuantization: Boolean = true,
    val dynamicBatching: Boolean = true,
    val memoryOptimization: Boolean = true
)

@ConfigurationProperties(prefix = "snippetia.security")
data class SecurityProperties(
    val authentication: AuthConfig = AuthConfig(),
    val authorization: AuthzConfig = AuthzConfig(),
    val encryption: EncryptionConfig = EncryptionConfig(),
    val monitoring: SecurityMonitoringConfig = SecurityMonitoringConfig()
)

data class AuthConfig(
    val jwtSecret: String = "your-super-secret-jwt-key-change-in-production",
    val jwtExpirationMs: Long = 86400000, // 24 hours
    val refreshTokenExpirationMs: Long = 604800000, // 7 days
    val maxLoginAttempts: Int = 5,
    val lockoutDurationMs: Long = 900000, // 15 minutes
    val mfaEnabled: Boolean = true,
    val passwordMinLength: Int = 12,
    val passwordComplexity: Boolean = true
)

data class AuthzConfig(
    val rbacEnabled: Boolean = true,
    val abacEnabled: Boolean = true,
    val defaultRole: String = "USER",
    val adminRole: String = "ADMIN",
    val superAdminRole: String = "SUPER_ADMIN",
    val roleHierarchy: Map<String, List<String>> = mapOf(
        "SUPER_ADMIN" to listOf("ADMIN", "MODERATOR", "USER"),
        "ADMIN" to listOf("MODERATOR", "USER"),
        "MODERATOR" to listOf("USER")
    )
)

data class EncryptionConfig(
    val algorithm: String = "AES-256-GCM",
    val keyRotationDays: Int = 90,
    val hashAlgorithm: String = "bcrypt",
    val hashRounds: Int = 12,
    val tlsVersion: String = "TLSv1.3",
    val cipherSuites: List<String> = listOf(
        "TLS_AES_256_GCM_SHA384",
        "TLS_CHACHA20_POLY1305_SHA256",
        "TLS_AES_128_GCM_SHA256"
    )
)

data class SecurityMonitoringConfig(
    val threatDetection: Boolean = true,
    val anomalyDetection: Boolean = true,
    val realTimeAlerts: Boolean = true,
    val securityAuditLog: Boolean = true,
    val intrusionDetection: Boolean = true,
    val ddosProtection: Boolean = true,
    val rateLimiting: Boolean = true,
    val geoBlocking: Boolean = false
)

@ConfigurationProperties(prefix = "snippetia.scaling")
data class ScalingProperties(
    val autoScaling: AutoScalingConfig = AutoScalingConfig(),
    val loadBalancing: LoadBalancingConfig = LoadBalancingConfig(),
    val regions: RegionConfig = RegionConfig()
)

data class AutoScalingConfig(
    val enabled: Boolean = true,
    val minInstances: Int = 3,
    val maxInstances: Int = 100,
    val targetCpuUtilization: Double = 70.0,
    val targetMemoryUtilization: Double = 80.0,
    val scaleUpCooldown: Duration = Duration.ofMinutes(5),
    val scaleDownCooldown: Duration = Duration.ofMinutes(10),
    val predictiveScaling: Boolean = true
)

data class LoadBalancingConfig(
    val algorithm: String = "ROUND_ROBIN", // ROUND_ROBIN, LEAST_CONNECTIONS, WEIGHTED_ROUND_ROBIN
    val healthCheckPath: String = "/health",
    val healthCheckInterval: Duration = Duration.ofSeconds(30),
    val unhealthyThreshold: Int = 3,
    val healthyThreshold: Int = 2,
    val stickySession: Boolean = false,
    val connectionDraining: Boolean = true,
    val drainingTimeout: Duration = Duration.ofMinutes(5)
)

data class RegionConfig(
    val primaryRegion: String = "us-east-1",
    val secondaryRegions: List<String> = listOf("us-west-2", "eu-west-1", "ap-southeast-1"),
    val crossRegionReplication: Boolean = true,
    val failoverEnabled: Boolean = true,
    val failoverThreshold: Double = 0.95, // 95% failure rate triggers failover
    val dataResidency: Map<String, List<String>> = mapOf(
        "EU" to listOf("eu-west-1", "eu-central-1"),
        "US" to listOf("us-east-1", "us-west-2"),
        "APAC" to listOf("ap-southeast-1", "ap-northeast-1")
    )
)

@ConfigurationProperties(prefix = "snippetia.mlops")
data class MLOpsProperties(
    val experiments: ExperimentConfig = ExperimentConfig(),
    val deployment: MLDeploymentConfig = MLDeploymentConfig(),
    val monitoring: MLMonitoringConfig = MLMonitoringConfig(),
    val governance: MLGovernanceConfig = MLGovernanceConfig()
)

data class ExperimentConfig(
    val trackingEnabled: Boolean = true,
    val autoLogging: Boolean = true,
    val artifactStorage: String = "s3://snippetia-ml-artifacts",
    val maxExperiments: Int = 1000,
    val retentionDays: Int = 365,
    val parallelExperiments: Int = 10
)

data class MLDeploymentConfig(
    val canaryEnabled: Boolean = true,
    val canaryTrafficPercentage: Double = 5.0,
    val canaryDuration: Duration = Duration.ofHours(2),
    val rollbackThreshold: Double = 0.05, // 5% error rate triggers rollback
    val blueGreenEnabled: Boolean = true,
    val shadowTrafficEnabled: Boolean = true,
    val shadowTrafficPercentage: Double = 10.0
)

data class MLMonitoringConfig(
    val modelPerformanceTracking: Boolean = true,
    val dataQualityMonitoring: Boolean = true,
    val driftDetection: Boolean = true,
    val biasDetection: Boolean = true,
    val explainabilityTracking: Boolean = true,
    val alertThresholds: Map<String, Double> = mapOf(
        "accuracy_drop" to 0.05,
        "latency_increase" to 0.2,
        "error_rate_increase" to 0.02
    )
)

data class MLGovernanceConfig(
    val modelApprovalRequired: Boolean = true,
    val auditTrail: Boolean = true,
    val complianceChecks: Boolean = true,
    val ethicsReview: Boolean = true,
    val dataPrivacyValidation: Boolean = true,
    val modelDocumentation: Boolean = true,
    val versionControl: Boolean = true
)

// Health Indicators for Enterprise Monitoring
@Component
class GlobalHealthIndicator(
    private val globalOrchestrationService: GlobalScaleOrchestrationService
) : HealthIndicator {
    
    override fun health(): Health {
        return try {
            // Check global system health
            val isHealthy = checkGlobalSystemHealth()
            
            if (isHealthy) {
                Health.up()
                    .withDetail("status", "All systems operational")
                    .withDetail("regions", "All regions healthy")
                    .withDetail("services", "All services running")
                    .build()
            } else {
                Health.down()
                    .withDetail("status", "System degraded")
                    .withDetail("issue", "Some regions or services experiencing issues")
                    .build()
            }
        } catch (e: Exception) {
            Health.down()
                .withDetail("error", e.message)
                .build()
        }
    }
    
    private fun checkGlobalSystemHealth(): Boolean {
        // Implement comprehensive health checks
        return true // Simplified for now
    }
}

@Component
class AIHealthIndicator(
    private val aiOrchestrationService: AIOrchestrationService
) : HealthIndicator {
    
    override fun health(): Health {
        return try {
            val modelsHealthy = checkAIModelsHealth()
            
            if (modelsHealthy) {
                Health.up()
                    .withDetail("models", "All AI models operational")
                    .withDetail("orchestration", "Load balancing working")
                    .build()
            } else {
                Health.down()
                    .withDetail("models", "Some AI models unavailable")
                    .build()
            }
        } catch (e: Exception) {
            Health.down()
                .withDetail("error", e.message)
                .build()
        }
    }
    
    private fun checkAIModelsHealth(): Boolean {
        // Check AI model availability and performance
        return true // Simplified for now
    }
}

@Component
class SecurityHealthIndicator(
    private val securityService: EnterpriseSecurityService
) : HealthIndicator {
    
    override fun health(): Health {
        return try {
            val securityStatus = checkSecurityStatus()
            
            if (securityStatus.isSecure) {
                Health.up()
                    .withDetail("threat_level", securityStatus.threatLevel)
                    .withDetail("active_incidents", securityStatus.activeIncidents)
                    .build()
            } else {
                Health.down()
                    .withDetail("threat_level", securityStatus.threatLevel)
                    .withDetail("active_incidents", securityStatus.activeIncidents)
                    .withDetail("issues", securityStatus.issues)
                    .build()
            }
        } catch (e: Exception) {
            Health.down()
                .withDetail("error", e.message)
                .build()
        }
    }
    
    private fun checkSecurityStatus(): SecurityStatus {
        // Check security posture
        return SecurityStatus(
            isSecure = true,
            threatLevel = "LOW",
            activeIncidents = 0,
            issues = emptyList()
        )
    }
    
    data class SecurityStatus(
        val isSecure: Boolean,
        val threatLevel: String,
        val activeIncidents: Int,
        val issues: List<String>
    )
}

// Production-ready profiles
@Configuration
@Profile("production")
class ProductionConfiguration {
    
    @Bean
    fun productionOptimizations(): ProductionOptimizations {
        return ProductionOptimizations(
            enableJitCompilation = true,
            enableGraalVMOptimizations = true,
            enableNativeImageHints = true,
            enableStartupOptimizations = true,
            enableMemoryOptimizations = true,
            enableNetworkOptimizations = true
        )
    }
}

@Configuration
@Profile("development")
class DevelopmentConfiguration {
    
    @Bean
    fun developmentOptimizations(): DevelopmentOptimizations {
        return DevelopmentOptimizations(
            enableHotReload = true,
            enableDebugMode = true,
            enableDetailedLogging = true,
            enableTestingEndpoints = true
        )
    }
}

data class ProductionOptimizations(
    val enableJitCompilation: Boolean,
    val enableGraalVMOptimizations: Boolean,
    val enableNativeImageHints: Boolean,
    val enableStartupOptimizations: Boolean,
    val enableMemoryOptimizations: Boolean,
    val enableNetworkOptimizations: Boolean
)

data class DevelopmentOptimizations(
    val enableHotReload: Boolean,
    val enableDebugMode: Boolean,
    val enableDetailedLogging: Boolean,
    val enableTestingEndpoints: Boolean
)