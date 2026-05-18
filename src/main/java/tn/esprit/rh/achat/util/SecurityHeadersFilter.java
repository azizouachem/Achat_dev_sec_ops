package tn.esprit.rh.achat.util;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        
        // Anti-Clickjacking
        httpServletResponse.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable Cross-Site Scripting (XSS) filter
        httpServletResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // HTTP Strict Transport Security (HSTS)
        httpServletResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization
    }

    @Override
    public void destroy() {
        // Cleanup
    }
}
