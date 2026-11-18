package br.com.zelabairro.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//ModelMapper: copia os campos de mesmo nome entre DTO e Model.
//STRICT = so copia quando o nome bate exatamente (evita que "idBairro" caia em "id" por engano).
//Ids de FK e campos calculados sao preenchidos a mao no controller.
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper;
    }
}
