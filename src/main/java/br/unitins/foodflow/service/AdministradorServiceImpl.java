package br.unitins.foodflow.service;

import java.util.List;
import java.util.stream.Collectors;

import br.unitins.foodflow.dto.AdministradorDTO;
import br.unitins.foodflow.dto.AdministradorResponseDTO;
import br.unitins.foodflow.dto.UsuarioResponseDTO;
import br.unitins.foodflow.model.Administrador;
import br.unitins.foodflow.model.Perfil;
import br.unitins.foodflow.repository.AdministradorRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class AdministradorServiceImpl implements AdministradorService {
    @Inject
    AdministradorRepository administradorRepository;

    @Override
    @Transactional
    public AdministradorResponseDTO create(AdministradorDTO administrador) {
        Administrador novoAdmin = new Administrador();
        novoAdmin.setNome(administrador.nome());
        novoAdmin.setEmail(administrador.email());
        novoAdmin.setSenha(administrador.senha());
        novoAdmin.setPerfil(Perfil.ADMIN);
        novoAdmin.setCpf(administrador.cpf());
        novoAdmin.setDataNascimento(administrador.dataNascimento());

        administradorRepository.persist(novoAdmin);

        return AdministradorResponseDTO.valueOf(novoAdmin);
    }

    @Override
    @Transactional
    public AdministradorResponseDTO update(AdministradorDTO administradorDTO, Long id) {
        Administrador adminEditado = administradorRepository.findById(id);

        adminEditado.setNome(administradorDTO.nome());
        adminEditado.setEmail(administradorDTO.email());
        adminEditado.setSenha(administradorDTO.senha());
        adminEditado.setCpf(administradorDTO.cpf());
        adminEditado.setPerfil(Perfil.ADMIN);
        adminEditado.setDataNascimento(administradorDTO.dataNascimento());

        return AdministradorResponseDTO.valueOf(adminEditado);
    }

    @Override
    @Transactional
    public void delete(long id) {
        administradorRepository.deleteById(id);
    }

    @Override
    public AdministradorResponseDTO findById(long id) {
        Administrador administrador = administradorRepository.findById(id);

        return AdministradorResponseDTO.valueOf(administrador);
    }

    @Override
    public List<AdministradorResponseDTO> findAll(int page, int size) {
        List<Administrador> list = administradorRepository.findAll().page(page, size).list();
        return list.stream().map(e -> AdministradorResponseDTO.valueOf(e)).collect(Collectors.toList());
    }

    @Override
    public List<AdministradorResponseDTO> findByNome(String nome, int page, int size, String sort) {
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

        PanacheQuery<Administrador> panacheQuery = administradorRepository
            .find(query, Parameters.with("nome", "%" + nome + "%"));

        if (size > 0) {
            panacheQuery = panacheQuery.page(Page.of(page, size));
        }

        return panacheQuery.list().stream()
            .map(AdministradorResponseDTO::valueOf)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return administradorRepository.findAll().count();
    }

    @Override
    public long count(String nome) {
        return administradorRepository.countByNome(nome);
    }

    public UsuarioResponseDTO login(String email, String senha) {
        
        Administrador adm = administradorRepository.findByEmailAndSenha(email, senha);
    
        if (adm == null) {
            throw new RuntimeException("Administrador não encontrado");
        }
    
        return UsuarioResponseDTO.valueOf(adm);
    }

}
