package br.com.reservas.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class QuartoServiceImpl implements QuartoService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${url.ms.quartos}")
    private String urlQuartos;

    public BigDecimal getDiariaQuarto(Integer id) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                urlQuartos + id,
                String.class);
        if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return BigDecimal.ZERO;
        }
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            Double valorDiaria = json.get("valorDiaria").asDouble();
            return BigDecimal.valueOf(valorDiaria);
        }
        catch (IOException ex) {
            return BigDecimal.ZERO;
        }
    }
}
