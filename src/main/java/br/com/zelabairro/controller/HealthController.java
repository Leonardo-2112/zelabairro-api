package br.com.zelabairro.controller;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//GET / (informacoes) e GET /health (testa o banco). Usado pelo Render e antes das demos
@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public Map<String, String> info() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("api", "ZelaBairro API");
        info.put("versao", "1.0.0");
        info.put("saude", "/health");
        return info;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new LinkedHashMap<>();
        status.put("status", "UP");
        try (Connection conexao = dataSource.getConnection()) {
            status.put("banco", conexao.isValid(5) ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("banco", "DOWN");
        }
        HttpStatus http = "UP".equals(status.get("banco")) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(http).body(status);
    }
}
