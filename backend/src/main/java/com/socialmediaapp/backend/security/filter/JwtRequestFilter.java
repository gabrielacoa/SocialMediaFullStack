package com.socialmediaapp.backend.security.filter;

import com.socialmediaapp.backend.security.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro para validar JWT en cada solicitud con manejo robusto de excepciones.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtService.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expirado para la solicitud: {}", request.getRequestURI());
            request.setAttribute("jwtError", "Token expirado");
        } catch (MalformedJwtException e) {
            logger.warn("JWT token malformado para la solicitud: {}", request.getRequestURI());
            request.setAttribute("jwtError", "Token inválido");
        } catch (SignatureException e) {
            logger.warn("Firma JWT inválida para la solicitud: {}", request.getRequestURI());
            request.setAttribute("jwtError", "Token no autorizado");
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token vacío o inválido para la solicitud: {}", request.getRequestURI());
            request.setAttribute("jwtError", "Token requerido");
        } catch (Exception e) {
            logger.error("Error inesperado al procesar JWT para la solicitud: {}", request.getRequestURI(), e);
            request.setAttribute("jwtError", "Error de autenticación");
        }

        chain.doFilter(request, response);
    }
}
