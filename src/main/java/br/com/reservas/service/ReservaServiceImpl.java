package br.com.reservas.service;

import br.com.reservas.model.Cliente;
import br.com.reservas.model.Item;
import br.com.reservas.model.Reserva;
import br.com.reservas.model.Servico;
import br.com.reservas.repository.ReservaRepository;
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
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    ReservaRepository reservaRepository;

    @Autowired
    QuartoService quartoService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    EmailService emailService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${url.ms.clientes}")
    private String urlClientes;

    @Override
    public List<Reserva> getAll() {
        return reservaRepository.findAll();
    }

    @Override
    public Reserva getById(String id) {
        return reservaRepository.findById(Integer.parseInt(id)).orElse(null);
    }

    @Override
    public List<Reserva> getByClienteId(String id) {
        return reservaRepository.findAll().stream().filter(reserva -> reserva.getClienteId().equals(UUID.fromString(id))).collect(Collectors.toList());
    }

    @Override
    public Reserva createReserva(Reserva reserva) {
        reserva.setValorDiaria(getDiariaQuarto(reserva.getQuartoId()).setScale(2, RoundingMode.CEILING));
        reserva.setValorTotal(calcularValorFinal(reserva).setScale(2, RoundingMode.CEILING));

        Boolean disponivel = verificaDisponibilidade(reserva);
        if(disponivel) {
            Cliente cliente = getCliente(reserva.getClienteId());
            Reserva efetuarReserva = reservaRepository.save(reserva);
            emailService.sendMail(cliente.getEmail(), "Sua reserva foi efetuada com sucesso.", gerarMensagemReserva(cliente.getNome(), efetuarReserva));
            return efetuarReserva;
        }
        return null;
    }

    private String gerarMensagemReserva(String nome, Reserva reserva) {


        return MessageFormat.format("Caro sr(a) {0}, sua reserva foi efetuada. Confira abaixo os detalhes: \n\n Número da reserva: {1}\n Pessoas: {2}" +
                "\n Data do Check-In: {3}" +
                "\n Data do Check-Out: {4}" +
                "\n Valor da Diária: R$ {5}" +
                "\n Valor Total (incluindo serviços e itens adicionais): R$ {6}" +
                        "\n\n A equipe do Hotel agradece a preferência e deseja uma ótima estadia." +
                        "\n Em caso de dúvidas, sugestões ou reclamações, não hesite em nos acionar: (11) 9000-9000", nome, reserva.getId(), reserva.getQuantidadePessoas(), reserva.getDataInicio().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
                reserva.getDataFim().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)), reserva.getValorDiaria(), reserva.getValorTotal());
    }

    private Cliente getCliente(String clienteId) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                urlClientes + clienteId,
                String.class);

        if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
        }
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            String id = json.get("id").asText();
            String nome = json.get("nome").asText();
            String email = json.get("email").asText();
            Cliente cliente = new Cliente(id, nome, email);
            return cliente;
        }
        catch (IOException ex) {
            return null;
        }
    }

    private Boolean verificaDisponibilidade(Reserva reserva) {
        LocalDate inicio = reserva.getDataInicio();
        LocalDate fim = reserva.getDataFim();
        List<Reserva> reservasExistentes = reservaRepository.findAll().stream().filter(res ->
                ((res.getDataInicio().isBefore(fim) || res.getDataInicio().isEqual(fim)) && (res.getDataFim().isAfter(inicio) || res.getDataFim().isEqual(inicio)))
                && res.getQuartoId() == reserva.getQuartoId()
        ).collect(Collectors.toList());
        return reservasExistentes.size() > 0 ? false : true;
    }

    private BigDecimal calcularValorFinal(Reserva reserva) {
        Long diarias = ChronoUnit.DAYS.between(reserva.getDataInicio(), reserva.getDataFim());
        Double valorDiarias = reserva.getValorDiaria().doubleValue() * diarias;
        Double valorAdicionais = 0D;
        for (Item item : reserva.getItens()) {
            valorAdicionais += item.getValorFinal().doubleValue();
        }
        for (Servico servico : reserva.getServicos()) {
            valorAdicionais += servico.getValorFinal().doubleValue();
        }

        return BigDecimal.valueOf(valorDiarias + valorAdicionais);
    }

    @Override
    public Reserva editReserva(Reserva reserva, String id) {
        Reserva edited = reservaRepository.findById(Integer.parseInt(id)).orElse(null);
        if(edited != null) {
            reserva.setId(edited.getId());
            reservaRepository.save(reserva);
        }
        return null;
    }

    @Override
    public Boolean removeReserva(String id) {
        Reserva reserva = reservaRepository.findById(Integer.parseInt(id)).orElse(null);
        if(reserva != null) {
            reservaRepository.deleteById(Integer.parseInt(id));
            return true;
        }
        return false;
    }

    private BigDecimal getDiariaQuarto(Integer id) {
        return quartoService.getDiariaQuarto(id);
    }
}
