package br.com.zelabairro.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro CORS: permite que o front (React, localhost do Vite ou Vercel) chame a API
 * de outro dominio. As origens liberadas vem da variavel CORS_ORIGINS, separadas por virgula.
 * Libera tambem os cabecalhos X-Gestor-Id (RN16) e X-Cidadao-Id (RN18).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private final List<String> origensPermitidas;

    public CorsFilter(@Value("${app.cors.origins}") String origens) {
        this.origensPermitidas = Arrays.stream(origens.split(","))
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toList();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origem = request.getHeader("Origin");
        if (origensPermitidas.contains("*")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        } else if (origem != null && origensPermitidas.contains(origem)) {
            response.setHeader("Access-Control-Allow-Origin", origem);
            response.setHeader("Vary", "Origin");
        }
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Gestor-Id, X-Cidadao-Id");
        response.setHeader("Access-Control-Expose-Headers", "Location");
        response.setHeader("Access-Control-Max-Age", "3600");

        //Requisicao de "pre-voo" (OPTIONS) do navegador: responde 200 sem chamar o endpoint
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(req, res);
    }
}
