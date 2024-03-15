package br.com.reservas.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private final Item item = new Item(1, 1, 10);

    @Test
    void getId() {
        assertEquals(item.getId(), 1);
    }

    @Test
    void getItemId() {
        assertEquals(item.getItemId(), 1);
    }

    @Test
    void getQuantidade() {
        assertEquals(item.getQuantidade(), 10);
    }
}