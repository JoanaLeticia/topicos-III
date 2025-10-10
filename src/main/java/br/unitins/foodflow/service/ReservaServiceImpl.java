package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.DisponibilidadeMesaResponse;
import br.unitins.foodflow.dto.MesaResponseDTO;
import br.unitins.foodflow.dto.ReservaConvidadoDTO;
import br.unitins.foodflow.dto.ReservaDTO;
import br.unitins.foodflow.dto.ReservaResponseDTO;
import br.unitins.foodflow.model.Mesa;
import br.unitins.foodflow.model.Perfil;
import br.unitins.foodflow.model.Reserva;
import br.unitins.foodflow.model.Usuario;
import br.unitins.foodflow.repository.MesaRepository;
import br.unitins.foodflow.repository.ReservaRepository;
import br.unitins.foodflow.repository.UsuarioRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.security.ForbiddenException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        LocalTime horario = dto.dataHora().toLocalTime();
        if (horario.isBefore(HORARIO_INICIO_JANTAR) || horario.isAfter(HORARIO_FIM_JANTAR)) {
            throw new BadRequestException(
                    "Reservas só podem ser feitas entre " + HORARIO_INICIO_JANTAR + " e " + HORARIO_FIM_JANTAR);
        }

        Mesa mesa = mesaRepository.findById(dto.idMesa());
        if (mesa == null) {
            throw new NotFoundException("Mesa não encontrada.");
        }

        if (dto.numeroPessoas() > mesa.getCapacidade()) {
            throw new BadRequestException(
                    "A mesa comporta no máximo " + mesa.getCapacidade() + " pessoas.");
        }

        if (reservaRepository.existsReservaConflito(mesa.getId(), dto.dataHora())) {
            throw new BadRequestException("A mesa já está reservada neste horário.");
        }

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

        LocalTime horario = dto.dataHora().toLocalTime();
        if (horario.isBefore(HORARIO_INICIO_JANTAR) || horario.isAfter(HORARIO_FIM_JANTAR)) {
            throw new BadRequestException(
                    "Reservas só podem ser feitas entre " + HORARIO_INICIO_JANTAR + " e " + HORARIO_FIM_JANTAR);
        }

        Mesa mesa = mesaRepository.findById(dto.idMesa());
        if (mesa == null) {
            throw new NotFoundException("Mesa não encontrada.");
        }

        if (dto.numeroPessoas() > mesa.getCapacidade()) {
            throw new BadRequestException(
                    "A mesa comporta no máximo " + mesa.getCapacidade() + " pessoas.");
        }

        if (!reserva.getMesa().getId().equals(mesa.getId()) ||
                !reserva.getDataHora().equals(dto.dataHora())) {
            if (reservaRepository.existsReservaConflito(mesa.getId(), dto.dataHora())) {
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

    @Override
    public List<DisponibilidadeMesaResponse> verificarDisponibilidade(LocalDate data, Integer numeroPessoas) {
        if (data == null) {
            throw new BadRequestException("Data não pode ser nula.");
        }
        if (numeroPessoas == null || numeroPessoas < 1) {
            throw new BadRequestException("Número de pessoas deve ser positivo.");
        }

        List<Mesa> mesasCapazes = mesaRepository.findByCapacidadeMinima(numeroPessoas);

        return mesasCapazes.stream()
                .map(mesa -> {
                    List<String> horariosDisponiveis = gerarHorariosDisponiveis(data, mesa);
                    return new DisponibilidadeMesaResponse(
                            MesaResponseDTO.valueOf(mesa),
                            horariosDisponiveis);
                })
                .filter(disponibilidade -> !disponibilidade.horariosDisponiveis().isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> gerarHorariosDisponiveis(LocalDate data, Mesa mesa) {
        List<String> horariosDisponiveis = new ArrayList<>();

        LocalTime inicio = LocalTime.of(19, 0);
        LocalTime fim = LocalTime.of(22, 0);
        LocalTime horarioAtual = inicio;

        while (!horarioAtual.isAfter(fim)) {
            LocalDateTime dataHora = LocalDateTime.of(data, horarioAtual);

            if (!reservaRepository.existsReservaConflito(mesa.getId(), dataHora)) {
                horariosDisponiveis.add(horarioAtual.toString());
            }

            horarioAtual = horarioAtual.plusMinutes(15);
        }

        return horariosDisponiveis;
    }

    @Override
    public Mesa encontrarMesaDisponivel(LocalDateTime dataHora, Integer numeroPessoas) {
        List<Mesa> mesasCapazes = mesaRepository.findByCapacidadeMinima(numeroPessoas);

        return mesasCapazes.stream()
                .filter(mesa -> !reservaRepository.existsReservaConflito(mesa.getId(), dataHora))
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public ReservaResponseDTO createConvidado(ReservaConvidadoDTO dto) {
        Reserva reserva = new Reserva();

        Mesa mesa = mesaRepository.findById(dto.idMesa());
        if (mesa == null) {
            throw new NotFoundException("Mesa não encontrada.");
        }

        reserva.setDataHora(dto.dataHora());
        reserva.setMesa(mesa);
        reserva.setNumeroPessoas(dto.numeroPessoas());

        reserva.setNomeConvidado(dto.nomeConvidado());
        reserva.setEmailConvidado(dto.emailConvidado());
        reserva.setTelefoneConvidado(dto.telefoneConvidado());

        reserva.setUsuario(null);
        reserva.setCodigoConfirmacao(gerarCodigoConfirmacao());

        reservaRepository.persist(reserva);

        return ReservaResponseDTO.valueOf(reserva);
    }

    @Override
    public List<ReservaResponseDTO> findByClienteEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        List<Reserva> reservas = reservaRepository.findByUsuario(usuario);
        if (reservas == null || reservas.isEmpty()) {
            return Collections.emptyList();
        }

        return reservas.stream()
                .sorted(Comparator.comparing(Reserva::getDataHora).reversed())
                .map(ReservaResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id, String login) {
        Reserva reserva = reservaRepository.findById(id);
        if (reserva == null) {
            throw new NotFoundException("Reserva não encontrada.");
        }

        Usuario usuarioLogado = usuarioRepository.findByEmail(login);
        if (usuarioLogado == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        if (!reserva.getUsuario().equals(usuarioLogado)) {
            if (!usuarioLogado.getPerfil().equals(Perfil.ADMIN)) {
                throw new ForbiddenException("Você não tem permissão para cancelar esta reserva.");
            }
        }

        reservaRepository.delete(reserva);
    }
}