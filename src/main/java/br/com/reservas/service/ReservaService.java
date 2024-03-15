package br.com.reservas.service;

import br.com.reservas.model.Reserva;

import java.util.List;

public interface ReservaService {
    List<Reserva> getAll();
    Reserva getById(String id);
    List<Reserva> getByClienteId(String id);
    Reserva createReserva(Reserva reserva) throws Exception;
    Reserva editReserva(Reserva reserva, String id);
    Boolean removeReserva(String id);
}
