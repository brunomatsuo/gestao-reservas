package br.com.reservas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String clienteId;
    private Integer quartoId;
    private Integer quantidadePessoas;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Servico> servicos;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Item> itens;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal valorDiaria;
    private BigDecimal valorTotal;
}
