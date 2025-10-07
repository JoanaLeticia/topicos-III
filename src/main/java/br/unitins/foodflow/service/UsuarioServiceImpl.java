package br.unitins.foodflow.service;

import java.util.List;
import java.util.stream.Collectors;

import br.unitins.foodflow.dto.UsuarioResponseDTO;
import br.unitins.foodflow.model.Usuario;
import br.unitins.foodflow.repository.AdministradorRepository;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.repository.PedidoRepository;
import br.unitins.foodflow.repository.UsuarioRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UsuarioServiceImpl implements UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PedidoRepository pedidoRepository;

    @Inject
    AdministradorRepository administradorRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    HashService hashService;

    @Override
    public UsuarioResponseDTO updateNome(String email, String nome) {
        Usuario usuarioNomeEditado = usuarioRepository.findByEmail(email);
        usuarioNomeEditado.setNome(nome);
        usuarioRepository.persist(usuarioNomeEditado);

        return UsuarioResponseDTO.valueOf(usuarioNomeEditado);
    }

    @Transactional
    public void updateSenha(String login, String novaSenha, String senhaAtual) {
        Usuario usuario = usuarioRepository.findByEmail(login);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        // Verifica senha atual
        if (!hashService.verificarSenha(senhaAtual, usuario.getSenha())) {
            throw new SecurityException("Senha atual incorreta");
        }

        usuario.setSenha(hashService.getHashSenha(novaSenha));
    }

    @Override
    public UsuarioResponseDTO findById(long id) {
        return UsuarioResponseDTO.valueOf(usuarioRepository.findById(id));
    }

    @Override
    public UsuarioResponseDTO findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        return UsuarioResponseDTO.valueOf(usuario);
    }

    @Override
    public UsuarioResponseDTO findByEmailAndSenha(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmailAndSenha(email, senha);
        if (usuario == null)
            throw new br.unitins.foodflow.validation.ValidationException("email", "Login ou senha inválido");
        return UsuarioResponseDTO.valueOf(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> findAll(int page, int pageSize, String sort) {
        // Campos permitidos para ordenação
        List<String> allowedSortFields = List.of("id", "nome", "email");

        String orderByClause = "order by id"; // padrão

        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.trim().split(" ");
            String field = sortParts[0];
            String direction = (sortParts.length > 1) ? sortParts[1].toLowerCase() : "asc";

            // Validar campo e direção
            if (allowedSortFields.contains(field)) {
                if (direction.equals("desc") || direction.equals("asc")) {
                    orderByClause = String.format("order by %s %s", field, direction);
                } else {
                    orderByClause = String.format("order by %s", field); // padrão asc
                }
            }
        }

        // Criar a consulta paginada com ordenação
        PanacheQuery<Usuario> panacheQuery = usuarioRepository.find(orderByClause);

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(Page.of(page, pageSize));
        }

        List<Usuario> usuarios = panacheQuery.list();

        return usuarios.stream()
                .map(UsuarioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponseDTO> findByNome(String nome, int page, int pageSize, String sort) {
        // Campos permitidos para ordenação
        List<String> allowedSortFields = List.of("id", "nome", "email");

        String orderByClause = "order by id"; // padrão

        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.trim().split(" ");
            String field = sortParts[0];
            String direction = (sortParts.length > 1) ? sortParts[1].toLowerCase() : "asc";

            // Validar campo e direção
            if (allowedSortFields.contains(field)) {
                if (direction.equals("desc") || direction.equals("asc")) {
                    orderByClause = String.format("order by %s %s", field, direction);
                } else {
                    orderByClause = String.format("order by %s", field); // padrão asc
                }
            }
        }

        // Consulta com filtro LIKE para nome (insensível a maiúsculas)
        String jpql = "lower(nome) like lower(:nome) " + orderByClause;

        PanacheQuery<Usuario> panacheQuery = usuarioRepository
                .find(jpql, Parameters.with("nome", "%" + nome + "%"));

        if (pageSize > 0) {
            panacheQuery = panacheQuery.page(Page.of(page, pageSize));
        }

        List<Usuario> usuarios = panacheQuery.list();

        return usuarios.stream()
                .map(UsuarioResponseDTO::valueOf)
                .collect(Collectors.toList());
    }

    public List<Usuario> findByNome(String nome) {
        return usuarioRepository.findByNome(nome).list();
    }

    @Override
    public long count() {
        return usuarioRepository.findAll().count();
    }

    @Override
    public long count(String nome) {
        return usuarioRepository.countByNome(nome);
    }

}
