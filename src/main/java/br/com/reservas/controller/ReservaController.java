package br.com.reservas.controller;

import br.com.reservas.model.Reserva;
import br.com.reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    ReservaService reservaService;

    @GetMapping
    public ResponseEntity getAll() {
        return ResponseEntity.ok(reservaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable String id) {
        Reserva reserva = reservaService.getById(id);
        return reserva != null ? ResponseEntity.ok(reserva) : ResponseEntity.notFound().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity getByClienteId(@PathVariable String clienteId) {
        List<Reserva> reservas = reservaService.getByClienteId(clienteId);
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity createReserva(@RequestBody Reserva reserva) {
        try {
            Reserva created = reservaService.createReserva(reserva);
            return created != null ? ResponseEntity.created(URI.create(created.getId().toString())).body(created) : ResponseEntity.internalServerError().build();
        }
        catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity editReserva(@RequestBody Reserva reserva, @PathVariable String id) {
        Reserva edited = reservaService.editReserva(reserva, id);
        return edited != null ? ResponseEntity.ok(edited) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteReserva(@PathVariable String id) {
        Boolean removed = reservaService.removeReserva(id);
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
