package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.PedidoDTO;
import br.unitins.foodflow.dto.PedidoResponseDTO;
import br.unitins.foodflow.model.*;
import br.unitins.foodflow.repository.*;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class PedidoServiceImpl implements PedidoService {

    @Inject
    PedidoRepository pedidoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ItemCardapioRepository itemRepository;

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    ParceiroAppRepository parceiroRepository;

    @Inject
    SugestaoChefRepository sugestaoRepository;

    @Override
    @Transactional
    public PedidoResponseDTO create(PedidoDTO dto, String email) {
        // Validar usuário
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado para o email: " + email);
        }

        // Criar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setPeriodo(TipoPeriodo.valueOf(dto.idPeriodo()));
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);

        // Validar e adicionar itens
        if (dto.idsItens() == null || dto.idsItens().isEmpty()) {
            throw new BadRequestException("O pedido deve conter ao menos um item.");
        }

        List<ItemCardapio> itens = new ArrayList<>();
        TipoPeriodo periodoEsperado = pedido.getPeriodo();

        for (Long idItem : dto.idsItens()) {
            ItemCardapio item = itemRepository.findById(idItem);
            if (item == null) {
                throw new NotFoundException("Item com ID " + idItem + " não encontrado.");
            }

            // REGRA: Validar se o item pertence ao período correto
            if (item.getPeriodo() != periodoEsperado) {
                throw new BadRequestException(
                        "O item '" + item.getNome() + "' não pertence ao período de " +
                                periodoEsperado.name().toLowerCase() + ".");
            }

            itens.add(item);
        }
        pedido.setItens(itens);

        // Criar atendimento baseado no tipo
        Atendimento atendimento = criarAtendimento(dto);
        pedido.setAtendimento(atendimento);

        // Calcular valor total (com desconto da Sugestão do Chefe)
        SugestaoChefe sugestaoAtiva = sugestaoRepository.findSugestaoAtiva();
        BigDecimal valorTotal = pedido.calcularValorTotal(sugestaoAtiva);
        pedido.setValorTotal(valorTotal);

        // Persistir
        pedidoRepository.persist(pedido);

        return recalcularEConverterParaDTO(pedido);
    }

    private Atendimento criarAtendimento(PedidoDTO dto) {
        TipoAtendimento tipoAtendimento = TipoAtendimento.valueOf(dto.idTipoAtendimento());

        switch (tipoAtendimento) {
            case PRESENCIAL:
                AtendimentoPresencial presencial = new AtendimentoPresencial();
                if (dto.numeroMesa() == null) {
                    throw new BadRequestException("Número da mesa é obrigatório para atendimento presencial.");
                }
                presencial.setNumeroMesa(dto.numeroMesa());
                return presencial;

            case DELIVERY_PROPRIO:
                AtendimentoDeliveryProprio deliveryProprio = new AtendimentoDeliveryProprio();
                if (dto.idEndereco() == null) {
                    throw new BadRequestException("Endereço é obrigatório para delivery próprio.");
                }
                Endereco enderecoProprio = enderecoRepository.findById(dto.idEndereco());
                if (enderecoProprio == null) {
                    throw new NotFoundException("Endereço não encontrado.");
                }
                deliveryProprio.setEnderecoEntrega(enderecoProprio);
                deliveryProprio.setTaxaEntrega(new BigDecimal("5.00")); // Taxa fixa padrão
                return deliveryProprio;

            case DELIVERY_APLICATIVO:
                AtendimentoDeliveryAplicativo deliveryApp = new AtendimentoDeliveryAplicativo();
                if (dto.idEndereco() == null) {
                    throw new BadRequestException("Endereço é obrigatório para delivery.");
                }
                if (dto.idParceiro() == null) {
                    throw new BadRequestException("Parceiro é obrigatório para delivery por aplicativo.");
                }
                Endereco enderecoApp = enderecoRepository.findById(dto.idEndereco());
                if (enderecoApp == null) {
                    throw new NotFoundException("Endereço não encontrado.");
                }
                ParceiroApp parceiro = parceiroRepository.findById(dto.idParceiro());
                if (parceiro == null) {
                    throw new NotFoundException("Parceiro não encontrado.");
                }
                deliveryApp.setEnderecoEntrega(enderecoApp);
                deliveryApp.setParceiro(parceiro);
                return deliveryApp;

            default:
                throw new BadRequestException("Tipo de atendimento inválido.");
        }
    }

    @Override
    @Transactional
    public PedidoResponseDTO update(PedidoDTO dto, Long id) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new NotFoundException("Pedido com ID " + id + " não encontrado.");
        }

        // Só permite atualizar pedidos pendentes
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new BadRequestException("Apenas pedidos pendentes podem ser editados.");
        }

        // Atualizar período
        pedido.setPeriodo(TipoPeriodo.valueOf(dto.idPeriodo()));

        // Atualizar itens
        if (dto.idsItens() != null && !dto.idsItens().isEmpty()) {
            List<ItemCardapio> itens = new ArrayList<>();
            TipoPeriodo periodoEsperado = pedido.getPeriodo();

            for (Long idItem : dto.idsItens()) {
                ItemCardapio item = itemRepository.findById(idItem);
                if (item == null) {
                    throw new NotFoundException("Item com ID " + idItem + " não encontrado.");
                }
                if (item.getPeriodo() != periodoEsperado) {
                    throw new BadRequestException(
                            "O item '" + item.getNome() + "' não pertence ao período de " +
                                    periodoEsperado.name().toLowerCase() + ".");
                }
                itens.add(item);
            }
            pedido.setItens(itens);
        }

        // Recalcular valor total
        SugestaoChefe sugestaoAtiva = sugestaoRepository.findSugestaoAtiva();
        BigDecimal valorTotal = pedido.calcularValorTotal(sugestaoAtiva);
        pedido.setValorTotal(valorTotal);

        return recalcularEConverterParaDTO(pedido);
    }

    @Override
    @Transactional
    public PedidoResponseDTO updateStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new NotFoundException("Pedido com ID " + id + " não encontrado.");
        }

        // Validar transição de status
        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        pedido.setStatus(novoStatus);
        return recalcularEConverterParaDTO(pedido);
    }

    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus) {
        // Regras de transição de status
        if (statusAtual == StatusPedido.CANCELADO || statusAtual == StatusPedido.CONCLUIDO) {
            throw new BadRequestException("Não é possível alterar o status de um pedido " +
                    statusAtual.name().toLowerCase() + ".");
        }

        if (statusAtual == StatusPedido.PENDENTE && novoStatus == StatusPedido.EM_PREPARO) {
            return; // Válido
        }
        if (statusAtual == StatusPedido.PENDENTE && novoStatus == StatusPedido.CONFIRMADO) {
            return; // Válido
        }
        if (statusAtual == StatusPedido.CONFIRMADO && novoStatus == StatusPedido.EM_PREPARO) {
            return; // Válido
        }
        if (statusAtual == StatusPedido.EM_PREPARO && novoStatus == StatusPedido.CONCLUIDO) {
            return; // Válido
        }
        if (novoStatus == StatusPedido.CANCELADO) {
            return; // Sempre pode cancelar (exceto se já concluído/cancelado)
        }

        throw new BadRequestException("Transição de status inválida: " +
                statusAtual + " -> " + novoStatus);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new NotFoundException("Pedido não encontrado.");
        }

        // Só permite deletar pedidos pendentes ou cancelados
        if (pedido.getStatus() != StatusPedido.PENDENTE &&
                pedido.getStatus() != StatusPedido.CANCELADO) {
            throw new BadRequestException("Apenas pedidos pendentes ou cancelados podem ser deletados.");
        }

        pedidoRepository.delete(pedido);
    }

    private PedidoResponseDTO recalcularEConverterParaDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        SugestaoChefe sugestaoDoDia = sugestaoRepository.findByData(pedido.getDataPedido().toLocalDate());

        return PedidoResponseDTO.valueOf(pedido, sugestaoDoDia);
    }

    @Override
    public PedidoResponseDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new NotFoundException("Pedido não encontrado.");
        }
        // Usa o método auxiliar para recalcular e converter
        return recalcularEConverterParaDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> findByClienteId(Long clienteId, int page, int pageSize) {
        PanacheQuery<Pedido> query = pedidoRepository.findByClienteIdWithPagination(clienteId);

        if (pageSize > 0) {
            query = query.page(page, pageSize);
        }

        return query.list()
                .stream()
                .map(this::recalcularEConverterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDTO> findByStatus(StatusPedido status, int page, int size, String sort) {
        String query = "status = :status";
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);

        if (sort != null && !sort.isEmpty()) {
            String[] parts = sort.split(" ");
            String field = parts[0];
            String direction = parts.length > 1 ? parts[1] : "asc";

            if (List.of("dataHora", "valorTotal").contains(field)) {
                query += " order by " + field + " " + direction;
            }
        }

        PanacheQuery<Pedido> panacheQuery = pedidoRepository.find(query, params);

        if (size > 0) {
            panacheQuery = panacheQuery.page(page, size);
        }

        return panacheQuery.list()
                .stream()
                .map(this::recalcularEConverterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDTO> findAll(int page, int pageSize, String sort) {
        String query = "order by dataPedido desc";
        Map<String, Object> params = new HashMap<>();

        if (sort != null && !sort.isEmpty()) {
            query = "order by " + sort;
        }

        PanacheQuery<Pedido> panacheQuery = pedidoRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(this::recalcularEConverterParaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return pedidoRepository.count();
    }

    @Override
    public long countByClienteId(Long clienteId) {
        return pedidoRepository.countByClienteId(clienteId);
    }

    @Override
    public List<PedidoResponseDTO> pedidosUsuarioLogado(Cliente cliente) {
        Cliente usuario = clienteRepository.findByEmail(cliente.getEmail());
        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);
        return pedidos.stream().map(this::recalcularEConverterParaDTO).collect(Collectors.toList());
    }

    @Override
    public long countByStatus(StatusPedido status) {
        return pedidoRepository.count("status", status);
    }

    @Override
    public PedidoResponseDTO findLastByUser(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }

        // Correção: usar a sintaxe correta do Panache para ordenação
        Pedido ultimoPedido = pedidoRepository.find("cliente", Sort.descending("dataHora"), cliente)
                .firstResult();

        if (ultimoPedido == null) {
            throw new EntityNotFoundException("Nenhum pedido encontrado para este usuário");
        }

        return recalcularEConverterParaDTO(ultimoPedido);
    }
}