package br.com.reservas.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private final Cliente cliente = new Cliente("1", "Teste", "teste@email.com");

    @Test
    void getId() {
        assertEquals(cliente.getId(), "1");
    }

    @Test
    void getNome() {
        assertEquals(cliente.getNome(), "Teste");
    }

    @Test
    void getEmail() {
        assertEquals(cliente.getEmail(), "teste@email.com");
    }
}