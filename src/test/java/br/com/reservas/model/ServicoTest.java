package br.com.reservas.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ServicoTest {

    private final Servico servico = new Servico(1, 1, 10);

    @Test
    void getId() {
        assertEquals(servico.getId(), 1);
    }

    @Test
    void getServicoId() {
        assertEquals(servico.getServicoId(), 1);
    }

    @Test
    void getQuantidade() {
        assertEquals(servico.getQuantidade(), 10);
    }
}