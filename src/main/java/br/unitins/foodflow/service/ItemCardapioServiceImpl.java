package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ItemCardapioDTO;
import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.dto.SugestaoChefResponseDTO;
import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.TipoPeriodo;
import br.unitins.foodflow.repository.ItemCardapioRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

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
    public List<ItemCardapioResponseDTO> findByPeriodo(TipoPeriodo periodo) {
        return itemRepository.findByPeriodo(periodo)
                .stream()
                .map(ItemCardapioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemCardapioResponseDTO> findByPeriodo(Integer idPeriodo) {
        TipoPeriodo periodo = TipoPeriodo.valueOf(idPeriodo);
        List<ItemCardapio> items = itemRepository.findByPeriodo(periodo);
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
}