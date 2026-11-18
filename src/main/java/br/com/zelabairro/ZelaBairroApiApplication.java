package br.com.zelabairro;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZelaBairroApiApplication {

    public static void main(String[] args) {
        //Datas "de hoje" (dias em aberto, RN11) sempre no horario de Sao Paulo
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(ZelaBairroApiApplication.class, args);
    }
}
