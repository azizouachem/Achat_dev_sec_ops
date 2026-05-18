package tn.esprit.rh.achat.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * OWASP A05:2021 – Security Misconfiguration / A09 – Logging
 * 
 * Centralized web security configuration:
 * - Registers the AuditLoggingInterceptor for all requests (A09)
 * - Configures centralized CORS policy (A05) to avoid permissive @CrossOrigin("*")
 */
@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    private AuditLoggingInterceptor auditLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLoggingInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
