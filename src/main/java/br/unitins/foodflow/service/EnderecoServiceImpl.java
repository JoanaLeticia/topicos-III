package br.unitins.foodflow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.unitins.foodflow.dto.EnderecoDTO;
import br.unitins.foodflow.dto.EnderecoResponseDTO;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.model.Endereco;
import br.unitins.foodflow.model.Municipio;
import br.unitins.foodflow.model.StatusPedido;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.repository.EnderecoRepository;
import br.unitins.foodflow.repository.MunicipioRepository;
import br.unitins.foodflow.repository.PedidoRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class EnderecoServiceImpl implements EnderecoService {
    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    MunicipioRepository municipioRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public EnderecoResponseDTO create(EnderecoDTO endereco) {
        Endereco novoEndereco = new Endereco();
        novoEndereco.setLogradouro(endereco.logradouro());
        novoEndereco.setNumero(endereco.numero());
        novoEndereco.setComplemento(endereco.complemento());
        novoEndereco.setBairro(endereco.bairro());
        novoEndereco.setCep(endereco.cep());

        Cliente cliente = clienteRepository.findById(endereco.idCliente());
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }
        novoEndereco.setCliente(cliente);

        novoEndereco.setMunicipio(municipioRepository.findById(endereco.idMunicipio()));

        enderecoRepository.persist(novoEndereco);

        cliente.getEnderecos().add(novoEndereco);
        clienteRepository.persist(cliente);

        return EnderecoResponseDTO.valueOf(novoEndereco);
    }

    @Override
    @Transactional
    public EnderecoResponseDTO update(EnderecoDTO dto, Long id) {
        Endereco enderecoEditado = enderecoRepository.findById(id);
        if (enderecoEditado == null) {
            throw new IllegalArgumentException("Endereço com ID " + id + " não encontrado.");
        }

        // Atualiza os dados do endereço
        if (!enderecoEditado.getCliente().getId().equals(dto.idCliente())) {
            Cliente novoCliente = clienteRepository.findById(dto.idCliente());
            if (novoCliente == null) {
                throw new NotFoundException("Novo cliente não encontrado");
            }

            enderecoEditado.getCliente().getEnderecos().remove(enderecoEditado);
            enderecoEditado.setCliente(novoCliente);
            novoCliente.getEnderecos().add(enderecoEditado);
        }

        enderecoEditado.setLogradouro(dto.logradouro());
        enderecoEditado.setNumero(dto.numero());
        enderecoEditado.setComplemento(dto.complemento());
        enderecoEditado.setBairro(dto.bairro());
        enderecoEditado.setCep(dto.cep());

        Municipio municipio = municipioRepository.findById(dto.idMunicipio());
        enderecoEditado.setMunicipio(municipio);

        enderecoRepository.persist(enderecoEditado);

        return EnderecoResponseDTO.valueOf(enderecoEditado);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Endereco endereco = enderecoRepository.findById(id);
        if (endereco == null) {
            throw new NotFoundException("Endereço não encontrado");
        }

        if (endereco.getCliente() != null) {
            endereco.getCliente().getEnderecos().removeIf(e -> e.getId().equals(id));
        }

        if (pedidoRepository.existsByEnderecoIdAndStatusIn(id,
                List.of(StatusPedido.PENDENTE, StatusPedido.EM_PREPARO))) {
            throw new WebApplicationException("Endereço não pode ser excluído pois está vinculado a pedidos ativos",
                    400);
        }

        pedidoRepository.getEntityManager().createQuery(
                "UPDATE Pedido p SET p.endereco = null WHERE p.endereco.id = :id")
                .setParameter("id", id)
                .executeUpdate();

        enderecoRepository.getEntityManager().flush();

        enderecoRepository.deleteById(id);
    }

    @Override
    public EnderecoResponseDTO findById(long id) {
        Endereco end = enderecoRepository.findById(id);
        return EnderecoResponseDTO.valueOf(end);
    }

    @Override
    public List<EnderecoResponseDTO> findByMunicipio(Long idMunicipio, int page, int pageSize, String sort) {
        String query = "municipio.id = :idMunicipio";
        Map<String, Object> params = new HashMap<>();
        params.put("idMunicipio", idMunicipio);

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "logradouro":
                    query += " order by logradouro";
                    break;
                case "logradouro desc":
                    query += " order by logradouro desc";
                    break;
                default:
                    query += " order by id";
            }
        } else {
            query += " order by id";
        }

        PanacheQuery<Endereco> panacheQuery = enderecoRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(endereco -> EnderecoResponseDTO.valueOf(endereco))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnderecoResponseDTO> findAll(int page, int pageSize, String sort) {
        String query = "";
        Map<String, Object> params = new HashMap<>();

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "logradouro":
                    query = "order by logradouro";
                    break;
                case "logradouro desc":
                    query = "order by logradouro desc";
                    break;
                default:
                    query = "order by id";
            }
        } else {
            query = "order by id";
        }

        PanacheQuery<Endereco> panacheQuery = enderecoRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(endereco -> EnderecoResponseDTO.valueOf(endereco))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnderecoResponseDTO> findByLogradouro(String logradouro, int page, int pageSize, String sort) {
        String query = "UPPER(logradouro) LIKE UPPER(:logradouro)";
        Map<String, Object> params = new HashMap<>();
        params.put("logradouro", "%" + logradouro + "%");

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "logradouro":
                    query += " order by logradouro";
                    break;
                case "logradouro desc":
                    query += " order by logradouro desc";
                    break;
                default:
                    query += " order by id";
            }
        } else {
            query += " order by id";
        }

        PanacheQuery<Endereco> panacheQuery = enderecoRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(endereco -> EnderecoResponseDTO.valueOf(endereco))
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return enderecoRepository.findAll().count();
    }

    @Override
    public long count(String logradouro) {
        return enderecoRepository.countByLogradouro(logradouro);
    }

    @Override
    public List<EnderecoResponseDTO> findByBairro(String bairro, int page, int pageSize, String sort) {
        String query = "UPPER(bairro) LIKE UPPER(:bairro)";
        Map<String, Object> params = new HashMap<>();
        params.put("bairro", "%" + bairro + "%");

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "bairro":
                    query += " order by bairro";
                    break;
                case "bairro desc":
                    query += " order by bairro desc";
                    break;
                default:
                    query += " order by id";
            }
        } else {
            query += " order by id";
        }

        PanacheQuery<Endereco> panacheQuery = enderecoRepository.find(query, params);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(page, pageSize);
        }

        return panacheQuery.list()
                .stream()
                .map(endereco -> EnderecoResponseDTO.valueOf(endereco))
                .collect(Collectors.toList());
    }

    @Override
    public long countByBairro(String bairro) {
        return enderecoRepository.countByBairro(bairro);
    }

    @Override
    public List<EnderecoResponseDTO> findByClienteId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .getEnderecos()
                .stream()
                .map(EnderecoResponseDTO::valueOf)
                .collect(Collectors.toList());
    }
}

