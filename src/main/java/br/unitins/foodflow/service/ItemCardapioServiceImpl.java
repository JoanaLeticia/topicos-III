package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ItemCardapioDTO;
import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.dto.SugestaoChefResponseDTO;
import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.SugestaoChefe;
import br.unitins.foodflow.model.TipoPeriodo;
import br.unitins.foodflow.repository.ItemCardapioRepository;
import br.unitins.foodflow.repository.SugestaoChefRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ItemCardapioServiceImpl implements ItemCardapioService {

    @Inject
    ItemCardapioRepository itemRepository;

    @Inject
    SugestaoChefService sugestaoService;

    @Inject
    SugestaoChefRepository sugestaoRepository;

    @Override
    @Transactional
    public ItemCardapioResponseDTO create(ItemCardapioDTO dto) {
        ItemCardapio item = new ItemCardapio();
        item.setNome(dto.nome());
        item.setDescricao(dto.descricao());
        item.setPrecoBase(dto.precoBase());
        item.setPeriodo(TipoPeriodo.valueOf(dto.idPeriodo()));

        itemRepository.persist(item);
        return ItemCardapioResponseDTO.valueOf(item);
    }

    @Override
    @Transactional
    public ItemCardapioResponseDTO update(ItemCardapioDTO dto, Long id) {
        ItemCardapio item = itemRepository.findById(id);
        if (item == null) {
            throw new NotFoundException("Item com ID " + id + " não encontrado.");
        }

        item.setNome(dto.nome());
        item.setDescricao(dto.descricao());
        item.setPrecoBase(dto.precoBase());
        item.setPeriodo(TipoPeriodo.valueOf(dto.idPeriodo()));

        return ItemCardapioResponseDTO.valueOf(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ItemCardapio item = itemRepository.findById(id);
        if (item == null) {
            throw new NotFoundException("Item não encontrado.");
        }
        itemRepository.delete(item);
    }

    @Override
    public ItemCardapioResponseDTO findById(Long id) {
        ItemCardapio item = itemRepository.findById(id);
        if (item == null) {
            throw new NotFoundException("Item não encontrado.");
        }
        return enrichWithSugestao(List.of(item)).get(0);
    }

    @Override
    public List<ItemCardapioResponseDTO> findAll(int page, int pageSize, String sort) {
        String query = "";
        Map<String, Object> params = new HashMap<>();

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "nome":
                    query = "order by nome";
                    break;
                case "nome desc":
                    query = "order by nome desc";
                    break;
                case "preco":
                    query = "order by precoBase";
                    break;
                case "preco desc":
                    query = "order by precoBase desc";
                    break;
                default:
                    query = "order by id";
            }
        } else {
            query = "order by id";
        }

        PanacheQuery<ItemCardapio> panacheQuery = itemRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        List<ItemCardapio> items = panacheQuery.list();
        return enrichWithSugestao(items);
    }

    @Override
    public List<ItemCardapioResponseDTO> findAll(int page, int pageSize) {
        List<ItemCardapio> list = itemRepository.findAll().page(page, pageSize).list();
        return list.stream().map(e -> ItemCardapioResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public List<ItemCardapioResponseDTO> findByNome(String nome, int page, int pageSize, String sort) {
        String query = "UPPER(nome) LIKE UPPER(:nome)";
        Map<String, Object> params = new HashMap<>();
        params.put("nome", "%" + nome + "%");

        if (sort != null && !sort.isEmpty()) {
            query += " order by " + sort;
        } else {
            query += " order by id";
        }

        PanacheQuery<ItemCardapio> panacheQuery = itemRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        List<ItemCardapio> items = panacheQuery.list();
        return enrichWithSugestao(items);
    }

    @Override
    public long count() {
        return itemRepository.count();
    }

    @Override
    public long count(String nome) {
        return itemRepository.countByNome(nome);
    }

    private List<ItemCardapioResponseDTO> enrichWithSugestao(List<ItemCardapio> itemList) {
        SugestaoChefResponseDTO sugestaoAtiva = null;
        try {
            sugestaoAtiva = sugestaoService.findSugestaoAtiva();
        } catch (NotFoundException e) {
        }

        final Long idSugestaoAlmoco = (sugestaoAtiva != null && sugestaoAtiva.itemAlmoco() != null)
                ? sugestaoAtiva.itemAlmoco().id()
                : null;
        final Long idSugestaoJantar = (sugestaoAtiva != null && sugestaoAtiva.itemJantar() != null)
                ? sugestaoAtiva.itemJantar().id()
                : null;

        return itemList.stream()
                .map(item -> {
                    boolean isSugestao = item.id.equals(idSugestaoAlmoco) || item.id.equals(idSugestaoJantar);
                    return ItemCardapioResponseDTO.valueOf(item, isSugestao);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemCardapioResponseDTO salvarImagem(Long id, String nomeImagem) {
        ItemCardapio entity = itemRepository.findById(id);
        entity.setNomeImagem(nomeImagem);

        return ItemCardapioResponseDTO.valueOf(entity);
    }

    @Override
    public List<ItemCardapioResponseDTO> buscarPorPeriodo(
            String nomePeriodo,
            int page,
            int size,
            String sort,
            Double precoMax) {

        // Obter período usando método auxiliar (retorna final)
        final TipoPeriodo periodo = obterPeriodo(nomePeriodo);

        // Determinar ordenação
        String orderBy = (sort != null && !sort.isEmpty()) ? sort : "nome ASC";

        // Construir query com filtros
        PanacheQuery<ItemCardapio> query;

        if (precoMax != null) {
            query = itemRepository.find(
                    "periodo = ?1 AND precoBase <= ?2 ORDER BY " + orderBy,
                    periodo,
                    new BigDecimal(precoMax.toString()));
        } else {
            query = itemRepository.find(
                    "periodo = ?1 ORDER BY " + orderBy,
                    periodo);
        }

        // Aplicar paginação se necessário
        if (size > 0) {
            query = query.page(page, size);
        }

        // Obter sugestão ativa para marcar itens com desconto
        SugestaoChefe sugestaoAtiva = sugestaoRepository.findSugestaoAtiva();

        // Mapear itens para DTO
        return query.list().stream()
                .map(item -> {
                    boolean isSugestao = (sugestaoAtiva != null)
                            && sugestaoAtiva.isItemSugestao(item, periodo);
                    return ItemCardapioResponseDTO.valueOf(item, isSugestao);
                })
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para converter string/número em TipoPeriodo
     * 
     * @param nomePeriodo pode ser "1", "2", "ALMOCO", "JANTAR", "Almoço", "Jantar"
     * @return TipoPeriodo correspondente
     * @throws BadRequestException se o período for inválido
     */
    private TipoPeriodo obterPeriodo(String nomePeriodo) {
        if (nomePeriodo == null || nomePeriodo.trim().isEmpty()) {
            throw new BadRequestException("Período não pode ser vazio");
        }

        try {
            // Tentar converter para número (1 ou 2)
            Integer id = Integer.parseInt(nomePeriodo.trim());
            return TipoPeriodo.valueOf(id);
        } catch (NumberFormatException e) {
            // Se não for número, buscar pelo nome
            try {
                return TipoPeriodo.fromString(nomePeriodo);
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException(
                        "Período inválido: '" + nomePeriodo + "'. " +
                                "Use: 1 (Almoço), 2 (Jantar), ALMOCO ou JANTAR");
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Período inválido: '" + nomePeriodo + "'. " +
                            "Use: 1 (Almoço), 2 (Jantar), ALMOCO ou JANTAR");
        }
    }

    @Override
    public long countPorPeriodo(String nomePeriodo, Double precoMax) {
        try {
            TipoPeriodo periodo = TipoPeriodo.valueOf(nomePeriodo.toUpperCase());

            StringBuilder queryBuilder = new StringBuilder("periodo = :periodo");
            Parameters parameters = Parameters.with("periodo", periodo);

            if (precoMax != null) {
                queryBuilder.append(" and precoBase <= :precoMax");
                parameters.and("precoMax", precoMax);
            }

            return itemRepository.count(queryBuilder.toString(), parameters);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Periodo inválido: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getFiltrosPorPeriodo(String nomePeriodo) {
        Map<String, Object> filtros = new HashMap<>();

        try {
            TipoPeriodo periodo = TipoPeriodo.valueOf(nomePeriodo.toUpperCase());

            // Obter faixas de preço (usando a implementação dinâmica que criamos)
            List<Double> faixasPreco = itemRepository.findItemPrecoByPeriodo(periodo);

            // Adicionar ao mapa de filtros
            filtros.put("faixasPreco", faixasPreco);

            // Opcional: adicionar estatísticas úteis
            filtros.put("quantidadeProdutos", itemRepository.countByPeriodo(periodo));

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Período inválida: " + nomePeriodo);
        }

        return filtros;
    }

    @Override
    public List<ItemCardapioResponseDTO> findByPeriodo(Integer idPeriodo) {
        TipoPeriodo periodo = TipoPeriodo.valueOf(idPeriodo);
        return findByPeriodo(periodo);
    }

    @Override
    public List<ItemCardapioResponseDTO> findByPeriodo(TipoPeriodo periodo) {
        return itemRepository.findByPeriodo(periodo)
                .stream()
                .map(ItemCardapioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

}