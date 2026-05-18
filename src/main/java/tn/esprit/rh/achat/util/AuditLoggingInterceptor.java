package tn.esprit.rh.achat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OWASP A09:2021 – Security Logging and Monitoring Failures
 * 
 * Intercepts every HTTP request and logs critical audit information:
 * - HTTP method and URI
 * - Client IP address
 * - Response status code
 * - Request duration
 * 
 * This ensures that all API access is traceable, enabling
 * detection of brute-force attacks, unauthorized access attempts,
 * and forensic analysis after incidents.
 */
@Component
public class AuditLoggingInterceptor implements HandlerInterceptor {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());

        auditLogger.info("[REQUEST] {} {} | IP: {} | User-Agent: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        auditLogger.info("[RESPONSE] {} {} | Status: {} | Duration: {}ms | IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration,
                request.getRemoteAddr());

        if (ex != null) {
            auditLogger.warn("[ERROR] {} {} | Exception: {} | IP: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage(),
                    request.getRemoteAddr());
        }
    }
}
