package com.epam.esm.jwt;

import com.epam.esm.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final JwtProvider jwtProvider;
    private final HandlerExceptionResolver resolver;

    @Autowired
    public JwtFilter(JwtProvider jwtProvider,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtProvider = jwtProvider;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        try {
            JwtAuthentication jwtAuthentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException expiredJwtException) {
            resolver.resolveException(request, response, null,
                    new JwtAuthenticationException("Token expired"));
        } catch (UnsupportedJwtException unsupportedJwtException) {
            resolver.resolveException(request, response, null,
                    new JwtAuthenticationException("Unsupported jwt"));
        } catch (MalformedJwtException malformedJwtException) {
            resolver.resolveException(request, response, null,
                    new JwtAuthenticationException("Malformed jwt"));
        } catch (SignatureException signatureException) {
            resolver.resolveException(request, response, null,
                    new JwtAuthenticationException("Invalid signature"));
        } catch (Exception e) {
            resolver.resolveException(request, response, null,
                    new JwtAuthenticationException("Invalid token"));
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER)) {
            return bearer.substring(BEARER.length());
        }
        return null;
    }
}
