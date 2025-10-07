package br.unitins.foodflow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.unitins.foodflow.dto.MunicipioDTO;
import br.unitins.foodflow.dto.MunicipioResponseDTO;
import br.unitins.foodflow.model.Estado;
import br.unitins.foodflow.model.Municipio;
import br.unitins.foodflow.repository.EstadoRepository;
import br.unitins.foodflow.repository.MunicipioRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MunicipioServiceImpl implements MunicipioService {
    @Inject
    MunicipioRepository municipioRepository;

    @Inject
    EstadoRepository estadoRepository;

    @Override
    @Transactional
    public MunicipioResponseDTO create(MunicipioDTO municipio) {
        System.out.println("ID do Estado recebido: " + municipio.idEstado());

        Estado estado = estadoRepository.findById(municipio.idEstado());

        System.out.println("Estado encontrado: " + estado);

        if (estado == null) {
            throw new IllegalArgumentException("Estado não encontrado" + municipio.idEstado());
        }

        Municipio novoMunicipio = new Municipio();
        novoMunicipio.setNome(municipio.nome());
        novoMunicipio.setEstado(estado);

        municipioRepository.persist(novoMunicipio);
        System.out.println("Município persistido com ID: " + novoMunicipio.getId());

        return MunicipioResponseDTO.valueOf(novoMunicipio);
    }

    @Override
    @Transactional
    public MunicipioResponseDTO update(MunicipioDTO dto, Long id) {
        Municipio municipioEditado = municipioRepository.findById(id);
        if (municipioEditado == null) {
            throw new IllegalArgumentException("Município com ID " + id + " não encontrado.");
        }

        municipioEditado.setNome(dto.nome());
        municipioEditado.getEstado().setId(dto.idEstado());
        municipioRepository.persist(municipioEditado);

        return MunicipioResponseDTO.valueOf(municipioEditado);
    }

    @Override
    @Transactional
    public void delete(long id) {
        municipioRepository.deleteById(id);
    }

    @Override
    public MunicipioResponseDTO findById(long id) {
        Municipio municipio = municipioRepository.findById(id);
        return MunicipioResponseDTO.valueOf(municipio);
    }

    @Override
    public List<MunicipioResponseDTO> findAll(int page, int pageSize) {
        List<Municipio> list = municipioRepository.findAll().page(page, pageSize).list();
        return list.stream().map(e -> MunicipioResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public List<MunicipioResponseDTO> findByNome(String nome, int page, int pageSize, String sort) {
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

        PanacheQuery<Municipio> panacheQuery = municipioRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(municipio -> MunicipioResponseDTO.valueOf(municipio))
                .collect(Collectors.toList());
    }

    public List<MunicipioResponseDTO> findByNome(String nome) {
        return municipioRepository.find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%")
                .stream()
                .map(MunicipioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    public List<MunicipioResponseDTO> findByEstado(Long idEstado, int page, int pageSize, String sort) {
        String query = "estado.id = :idEstado";
        Map<String, Object> params = new HashMap<>();
        params.put("idEstado", idEstado);

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

        PanacheQuery<Municipio> panacheQuery = municipioRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(municipio -> MunicipioResponseDTO.valueOf(municipio))
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return municipioRepository.findAll().count();
    }

    @Override
    public long count(String nome) {
        return municipioRepository.countByNome(nome);
    }

    @Override
    public List<Estado> getEstados() {
        return estadoRepository.listAll();
    }

    @Override
    public List<MunicipioResponseDTO> findByEstado(Long idEstado) {
        return municipioRepository.find("estado.id", idEstado).stream()
                .map(e -> MunicipioResponseDTO.valueOf(e)).toList();
    }

}

