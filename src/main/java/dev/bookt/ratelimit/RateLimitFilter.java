package dev.bookt.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bookt.common.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;

    public RateLimitFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isRateLimitedPath(String path) {
        return pathMatcher.match("/bookings/hold", path)
                || pathMatcher.match("/bookings/*/confirm", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isRateLimitedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        ObjectMapper objectMapper = new ObjectMapper();

        if(!rateLimitService.isAllowed(ip)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            String json = objectMapper.writeValueAsString(new ErrorResponse("Too many requests sent within a short period of time. Wait until you make another request."));
            response.setContentType("application/json");
            response.getWriter().write(json);
        }
        else {
            filterChain.doFilter(request,response);
        }

    }
}
