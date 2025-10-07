package br.unitins.foodflow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.unitins.foodflow.dto.EstadoDTO;
import br.unitins.foodflow.dto.EstadoResponseDTO;
import br.unitins.foodflow.model.Estado;
import br.unitins.foodflow.repository.EstadoRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EstadoServiceImpl implements EstadoService {

    @Inject
    EstadoRepository estadoRepository;

    @Override
    @Transactional
    public EstadoResponseDTO create(EstadoDTO estado) {
        Estado novoEstado = new Estado();
        novoEstado.setNome(estado.nome());
        novoEstado.setSigla(estado.sigla());

        estadoRepository.persist(novoEstado);
        return EstadoResponseDTO.valueOf(novoEstado);
    }

    @Override
    @Transactional
    public EstadoResponseDTO update(EstadoDTO dto, Long id) {
        Estado edicaoEstado = estadoRepository.findById(id);

        edicaoEstado.setNome(dto.nome());
        edicaoEstado.setSigla(dto.sigla());

        return EstadoResponseDTO.valueOf(edicaoEstado);
    }

    @Override
    @Transactional
    public void delete(long id) {
        estadoRepository.deleteById(id);
    }

    @Override
    public EstadoResponseDTO findById(long id) {
        Estado estado = estadoRepository.findById(id);
        return EstadoResponseDTO.valueOf(estado);
    }

    @Override
    public EstadoResponseDTO findBySigla(String sigla) {
        Estado estado = estadoRepository.findBySigla(sigla);
        return EstadoResponseDTO.valueOf(estado);
    }

    @Override
    public List<EstadoResponseDTO> findAll(int page, int size) {
        List<Estado> list = estadoRepository.findAll().page(page, size).list();
        return list.stream().map(e -> EstadoResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public List<EstadoResponseDTO> findByNome(String nome, int page, int size, String sort) {
        String query = "UPPER(nome) LIKE UPPER(:nome)";
        Map<String, Object> params = new HashMap<>();
        params.put("nome", "%" + nome + "%");

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "nome":
                    query += " order by nome";
                    break;
                case "nome desc":
                    query += " order by nome desc";
                    break;
                default:
                    query += " order by id";
            }
        } else {
            query += " order by id";
        }

        PanacheQuery<Estado> panacheQuery = estadoRepository.find(query, params);

        if (size > 0) {
            panacheQuery = panacheQuery.page(page, size);
        }

        return panacheQuery.list()
                .stream()
                .map(estado -> EstadoResponseDTO.valueOf(estado))
                .collect(Collectors.toList());
    }

    public List<EstadoResponseDTO> findByNome(String nome) {
        List<Estado> list = estadoRepository.findByNome(nome);
        return list.stream().map(u -> EstadoResponseDTO.valueOf(u)).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return estadoRepository.findAll().count();
    }

    @Override
    public long count(String nome) {
        return estadoRepository.countByNome(nome);
    }

}
