package br.unitins.foodflow.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.unitins.foodflow.dto.ClienteDTO;
import br.unitins.foodflow.dto.ClienteResponseDTO;
import br.unitins.foodflow.dto.ClienteUpdateDTO;
import br.unitins.foodflow.dto.EnderecoResponseDTO;
import br.unitins.foodflow.dto.TelefoneDTO;
import br.unitins.foodflow.dto.UsuarioResponseDTO;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.model.Endereco;
import br.unitins.foodflow.model.Municipio;
import br.unitins.foodflow.model.Perfil;
import br.unitins.foodflow.model.Telefone;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.repository.MunicipioRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClienteServiceImpl implements ClienteService {
    @Inject
    ClienteRepository clienteRepository;

    @Inject
    MunicipioRepository municipioRepository;

    @Inject
    HashService hashService;

    @Override
    @Transactional
    public ClienteResponseDTO create(ClienteDTO cliente) {
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(cliente.nome());
        novoCliente.setEmail(cliente.email());
        novoCliente.setSenha(cliente.senha());
        novoCliente.setPerfil(Perfil.CLIENTE);

        if (cliente.listaTelefone() != null && !cliente.listaTelefone().isEmpty()) {
            List<Telefone> telefones = cliente.listaTelefone().stream()
                    .map(tel -> {
                        Telefone telefone = new Telefone();
                        telefone.setCodArea(tel.codArea());
                        telefone.setNumero(tel.numero());
                        telefone.setCliente(novoCliente);
                        return telefone;
                    })
                    .collect(Collectors.toList());
            novoCliente.setTelefones(telefones);
        }

        if (cliente.listaEndereco() != null && !cliente.listaEndereco().isEmpty()) {
            List<Endereco> enderecos = cliente.listaEndereco().stream()
                    .map(end -> {
                        Endereco endereco = new Endereco();
                        endereco.setLogradouro(end.logradouro());
                        endereco.setNumero(end.numero());
                        endereco.setComplemento(end.complemento());
                        endereco.setBairro(end.bairro());
                        endereco.setCep(end.cep());

                        Municipio municipio = municipioRepository.findById(end.idMunicipio());
                        if (municipio == null) {
                            throw new EntityNotFoundException(
                                    "Município com ID " + end.idMunicipio() + " não encontrado.");
                        }
                        endereco.setMunicipio(municipio);

                        endereco.setCliente(novoCliente);
                        return endereco;
                    }).collect(Collectors.toList());
            novoCliente.setEnderecos(enderecos);
        }

        clienteRepository.persist(novoCliente);

        return ClienteResponseDTO.valueOf(novoCliente);
    }

    @Override
    @Transactional
    public ClienteResponseDTO update(ClienteDTO clienteDTO, Long id) {
        Cliente clienteEditado = clienteRepository.findById(id);

        clienteEditado.setNome(clienteDTO.nome());
        clienteEditado.setEmail(clienteDTO.email());
        clienteEditado.setSenha(clienteDTO.senha());
        clienteEditado.setPerfil(Perfil.CLIENTE);

        if (clienteDTO.listaTelefone() != null && !clienteDTO.listaTelefone().isEmpty()) {
            clienteEditado.getTelefones().clear();
            List<Telefone> telefones = clienteDTO.listaTelefone().stream()
                    .map(tel -> {
                        Telefone telefone = new Telefone();
                        telefone.setCodArea(tel.codArea());
                        telefone.setNumero(tel.numero());
                        return telefone;
                    })
                    .collect(Collectors.toList());
            clienteEditado.getTelefones().addAll(telefones);
        } else {
            clienteEditado.getTelefones().clear();
        }

        // Atualize a lista de endereços
        if (clienteDTO.listaEndereco() != null && !clienteDTO.listaEndereco().isEmpty()) {
            clienteEditado.getEnderecos().clear();
            List<Endereco> enderecos = clienteDTO.listaEndereco().stream()
                    .map(end -> {
                        Endereco endereco = new Endereco();
                        endereco.setCep(end.cep());
                        endereco.setBairro(end.bairro());
                        endereco.setNumero(end.numero());
                        endereco.setLogradouro(end.logradouro());
                        endereco.setComplemento(end.complemento());
                        Municipio idMunicipio = municipioRepository.findById(end.idMunicipio());
                        endereco.setMunicipio(idMunicipio);

                        return endereco;
                    })
                    .collect(Collectors.toList());
            clienteEditado.getEnderecos().addAll(enderecos);
        } else {
            clienteEditado.getEnderecos().clear();
        }

        return ClienteResponseDTO.valueOf(clienteEditado);
    }

    @Override
    @Transactional
    public void updatePartial(ClienteUpdateDTO dto, Long id) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }

        System.out.println("Telefones recebidos no DTO: " + dto.listaTelefone());
        System.out.println("Telefones atuais no cliente: " + cliente.getTelefones());

        if (dto.nome() != null) {
            cliente.setNome(dto.nome());
        }

        if (dto.listaTelefone() != null) {
            Map<Long, Telefone> telefonesExistentes = cliente.getTelefones().stream()
                    .collect(Collectors.toMap(Telefone::getId, Function.identity()));

            cliente.getTelefones().clear();

            for (TelefoneDTO telDTO : dto.listaTelefone()) {
                Telefone telefone;

                if (telDTO.id() != null && telefonesExistentes.containsKey(telDTO.id())) {
                    // Atualiza existente
                    telefone = telefonesExistentes.get(telDTO.id());
                    telefone.setCodArea(telDTO.codArea());
                    telefone.setNumero(telDTO.numero());
                } else {
                    // Cria novo
                    telefone = new Telefone();
                    telefone.setCodArea(telDTO.codArea());
                    telefone.setNumero(telDTO.numero());
                    telefone.setCliente(cliente);
                }
                cliente.getTelefones().add(telefone);
            }
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public ClienteResponseDTO findById(long id) {
        Cliente cliente = clienteRepository.findById(id);
        return ClienteResponseDTO.valueOf(cliente);
    }

    @Override
    public List<ClienteResponseDTO> findAll(int page, int size) {
        List<Cliente> list = clienteRepository.findAll().page(page, size).list();
        return list.stream().map(e -> ClienteResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponseDTO> findByNome(String nome, int page, int size, String sort) {
        List<String> allowedSortFields = List.of("id", "nome");

        String orderByClause = "order by id"; // padrão

        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.trim().split(" ");
            String field = sortParts[0];
            String direction = (sortParts.length > 1) ? sortParts[1].toLowerCase() : "asc";

            if (allowedSortFields.contains(field)) {
                if (direction.equals("desc") || direction.equals("asc")) {
                    orderByClause = String.format("order by %s %s", field, direction);
                } else {
                    orderByClause = String.format("order by %s", field);
                }
            }
        }

        String query = "lower(nome) like lower(:nome) " + orderByClause;

        PanacheQuery<Cliente> panacheQuery = clienteRepository
                .find(query, Parameters.with("nome", "%" + nome + "%"));

        if (size > 0) {
            panacheQuery = panacheQuery.page(Page.of(page, size));
        }

        return panacheQuery.list().stream()
                .map(ClienteResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return clienteRepository.findAll().count();
    }

    @Override
    public long count(String nome) {
        return clienteRepository.countByNome(nome);
    }

    public UsuarioResponseDTO login(String email, String senha) {

        Cliente cliente = clienteRepository.findByEmailAndSenha(email, senha);

        if (cliente == null) {
            throw new RuntimeException("Cliente não encontrado");
        }

        return UsuarioResponseDTO.valueOf(cliente);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrar(ClienteDTO clienteDTO) {
        if (clienteRepository.existePorEmail(clienteDTO.email())) {
            throw new RuntimeException("Email já cadastrado!");
        }

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(clienteDTO.nome());
        novoCliente.setEmail(clienteDTO.email());
        novoCliente.setSenha(hashService.getHashSenha(clienteDTO.senha()));
        novoCliente.setPerfil(Perfil.CLIENTE);

        if (clienteDTO.listaTelefone() != null && !clienteDTO.listaTelefone().isEmpty()) {
            List<Telefone> telefones = clienteDTO.listaTelefone().stream()
                    .map(telDTO -> {
                        Telefone telefone = new Telefone();
                        telefone.setCodArea(telDTO.codArea());
                        telefone.setNumero(telDTO.numero());
                        telefone.setCliente(novoCliente); // Associa o telefone ao novo cliente
                        return telefone;
                    })
                    .collect(Collectors.toList());
            novoCliente.setTelefones(telefones);
        }

        if (clienteDTO.listaEndereco() != null && !clienteDTO.listaEndereco().isEmpty()) {
            List<Endereco> enderecos = clienteDTO.listaEndereco().stream()
                    .map(endDTO -> {
                        Endereco endereco = new Endereco();
                        endereco.setLogradouro(endDTO.logradouro());
                        endereco.setNumero(endDTO.numero());
                        endereco.setComplemento(endDTO.complemento());
                        endereco.setBairro(endDTO.bairro());
                        endereco.setCep(endDTO.cep());

                        Municipio municipio = municipioRepository.findById(endDTO.idMunicipio());
                        if (municipio == null) {
                            throw new EntityNotFoundException(
                                    "Município com ID " + endDTO.idMunicipio() + " não encontrado.");
                        }
                        endereco.setMunicipio(municipio);

                        endereco.setCliente(novoCliente);
                        return endereco;
                    })
                    .collect(Collectors.toList());
            novoCliente.setEnderecos(enderecos);
        }

        // --- FIM DA LÓGICA CORRIGIDA ---

        clienteRepository.persist(novoCliente);

        return new UsuarioResponseDTO(
                novoCliente.getId(),
                novoCliente.getNome(),
                novoCliente.getEmail(),
                novoCliente.getPerfil());
    }

    @Override
    public ClienteResponseDTO findByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente não encontrado");
        }
        return ClienteResponseDTO.valueOf(cliente);
    }

    @Override
    public List<EnderecoResponseDTO> findEnderecosByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente não encontrado para o email: " + email);
        }

        if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
            return Collections.emptyList();
        }

        return cliente.getEnderecos().stream()
                .map(EnderecoResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

}
