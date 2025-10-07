package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ReservaDTO;
import br.unitins.foodflow.dto.ReservaResponseDTO;
import br.unitins.foodflow.model.Mesa;
import br.unitins.foodflow.model.Reserva;
import br.unitins.foodflow.model.Usuario;
import br.unitins.foodflow.repository.MesaRepository;
import br.unitins.foodflow.repository.ReservaRepository;
import br.unitins.foodflow.repository.UsuarioRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReservaServiceImpl implements ReservaService {

    @Inject
    ReservaRepository reservaRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    MesaRepository mesaRepository;

    private static final LocalTime HORARIO_INICIO_JANTAR = LocalTime.of(19, 0);
    private static final LocalTime HORARIO_FIM_JANTAR = LocalTime.of(22, 0);

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaDTO dto, Long usuarioId) {
        // Validar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        // Validar horário (apenas jantar)
        LocalTime horario = dto.dataHora().toLocalTime();
        if (horario.isBefore(HORARIO_INICIO_JANTAR) || horario.isAfter(HORARIO_FIM_JANTAR)) {
            throw new BadRequestException(
                "Reservas só podem ser feitas entre " + HORARIO_INICIO_JANTAR + " e " + HORARIO_FIM_JANTAR
            );
        }

        // Validar mesa
        Mesa mesa = mesaRepository.findById(dto.idMesa());
        if (mesa == null) {
            throw new NotFoundException("Mesa não encontrada.");
        }

        // Validar capacidade
        if (dto.numeroPessoas() > mesa.getCapacidade()) {
            throw new BadRequestException(
                "A mesa comporta no máximo " + mesa.getCapacidade() + " pessoas."
            );
        }

        // Verificar conflito de horário
        if (reservaRepository.existsReservaConflito(mesa.id, dto.dataHora())) {
            throw new BadRequestException("A mesa já está reservada neste horário.");
        }

        // Criar reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setMesa(mesa);
        reserva.setDataHora(dto.dataHora());
        reserva.setNumeroPessoas(dto.numeroPessoas());
        reserva.setCodigoConfirmacao(gerarCodigoConfirmacao());

        reservaRepository.persist(reserva);

        return ReservaResponseDTO.valueOf(reserva);
    }

    private String gerarCodigoConfirmacao() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    @Transactional
    public ReservaResponseDTO update(ReservaDTO dto, Long id) {
        Reserva reserva = reservaRepository.findById(id);
        if (reserva == null) {
            throw new NotFoundException("Reserva com ID " + id + " não encontrada.");
        }

        // Validar horário
        LocalTime horario = dto.dataHora().toLocalTime();
        if (horario.isBefore(HORARIO_INICIO_JANTAR) || horario.isAfter(HORARIO_FIM_JANTAR)) {
            throw new BadRequestException(
                "Reservas só podem ser feitas entre " + HORARIO_INICIO_JANTAR + " e " + HORARIO_FIM_JANTAR
            );
        }

        // Validar mesa
        Mesa mesa = mesaRepository.findById(dto.idMesa());
        if (mesa == null) {
            throw new NotFoundException("Mesa não encontrada.");
        }

        // Validar capacidade
        if (dto.numeroPessoas() > mesa.getCapacidade()) {
            throw new BadRequestException(
                "A mesa comporta no máximo " + mesa.getCapacidade() + " pessoas."
            );
        }

        // Verificar conflito (exceto com a própria reserva)
        if (!reserva.getMesa().id.equals(mesa.id) || 
            !reserva.getDataHora().equals(dto.dataHora())) {
            if (reservaRepository.existsReservaConflito(mesa.id, dto.dataHora())) {
                throw new BadRequestException("A mesa já está reservada neste horário.");
            }
        }

        reserva.setMesa(mesa);
        reserva.setDataHora(dto.dataHora());
        reserva.setNumeroPessoas(dto.numeroPessoas());

        return ReservaResponseDTO.valueOf(reserva);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reserva reserva = reservaRepository.findById(id);
        if (reserva == null) {
            throw new NotFoundException("Reserva não encontrada.");
        }
        reservaRepository.delete(reserva);
    }

    @Override
    public ReservaResponseDTO findById(Long id) {
        Reserva reserva = reservaRepository.findById(id);
        if (reserva == null) {
            throw new NotFoundException("Reserva não encontrada.");
        }
        return ReservaResponseDTO.valueOf(reserva);
    }

    @Override
    public ReservaResponseDTO findByCodigoConfirmacao(String codigo) {
        Reserva reserva = reservaRepository.findByCodigoConfirmacao(codigo);
        if (reserva == null) {
            throw new NotFoundException("Reserva não encontrada.");
        }
        return ReservaResponseDTO.valueOf(reserva);
    }

    @Override
    public List<ReservaResponseDTO> findByUsuarioId(Long usuarioId, int page, int pageSize) {
        PanacheQuery<Reserva> query = reservaRepository.findByUsuarioIdWithPagination(usuarioId);

        if (pageSize > 0) {
            query = query.page(page, pageSize);
        }

        return query.list()
                .stream()
                .map(ReservaResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponseDTO> findReservasFuturas(Long usuarioId) {
        return reservaRepository.findReservasFuturas(usuarioId)
                .stream()
                .map(ReservaResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUsuarioId(Long usuarioId) {
        return reservaRepository.count("usuario.id", usuarioId);
    }
}