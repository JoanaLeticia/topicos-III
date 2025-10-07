package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ParceiroAppDTO;
import br.unitins.foodflow.dto.ParceiroAppResponseDTO;
import br.unitins.foodflow.model.ParceiroApp;
import br.unitins.foodflow.repository.ParceiroAppRepository;
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
public class ParceiroAppServiceImpl implements ParceiroAppService {

    @Inject
    ParceiroAppRepository parceiroRepository;

    @Override
    @Transactional
    public ParceiroAppResponseDTO create(ParceiroAppDTO dto) {
        ParceiroApp parceiro = new ParceiroApp();
        parceiro.setNome(dto.nome());
        parceiro.setPercentualComissao(dto.percentualComissao());
        parceiro.setTaxaFixa(dto.taxaFixa());
        parceiroRepository.persist(parceiro);
        return ParceiroAppResponseDTO.valueOf(parceiro);
    }

    @Override
    @Transactional
    public ParceiroAppResponseDTO update(ParceiroAppDTO dto, Long id) {
        ParceiroApp parceiro = parceiroRepository.findById(id);
        if (parceiro == null) {
            throw new NotFoundException("Parceiro com ID " + id + " não encontrado.");
        }
        parceiro.setNome(dto.nome());
        parceiro.setPercentualComissao(dto.percentualComissao());
        parceiro.setTaxaFixa(dto.taxaFixa());
        return ParceiroAppResponseDTO.valueOf(parceiro);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParceiroApp parceiro = parceiroRepository.findById(id);
        if (parceiro == null) {
            throw new NotFoundException("Parceiro não encontrado.");
        }
        parceiroRepository.delete(parceiro);
    }

    @Override
    public ParceiroAppResponseDTO findById(Long id) {
        ParceiroApp parceiro = parceiroRepository.findById(id);
        if (parceiro == null) {
            throw new NotFoundException("Parceiro não encontrado.");
        }
        return ParceiroAppResponseDTO.valueOf(parceiro);
    }

    @Override
    public List<ParceiroAppResponseDTO> findAll(int page, int pageSize, String sort) {
        String query = sort != null && !sort.isEmpty() ? "order by " + sort : "order by id";
        Map<String, Object> params = new HashMap<>();

        PanacheQuery<ParceiroApp> panacheQuery = parceiroRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(ParceiroAppResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParceiroAppResponseDTO> findByNome(String nome, int page, int pageSize, String sort) {
        PanacheQuery<ParceiroApp> panacheQuery = parceiroRepository.findByNome(nome);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(ParceiroAppResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return parceiroRepository.count();
    }

    @Override
    public long count(String nome) {
        return parceiroRepository.countByNome(nome);
    }
}