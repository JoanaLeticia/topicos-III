package br.unitins.foodflow.repository;

import java.util.List;

import br.unitins.foodflow.model.Endereco;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepository<Endereco> {
    public List<Endereco> findByMunicipio(Long idMunicipio) {
        return list("municipio.id", idMunicipio);
    }

    public List<Endereco> findByLogradouro(String logradouro) {
        return list("LOWER(logradouro) LIKE LOWER(?1)", "%" + logradouro + "%");
    }

    public long countByLogradouro(String logradouro) {
        return count("UPPER(logradouro) LIKE UPPER(?1)", "%" + logradouro + "%");
    }

    public List<Endereco> findByBairro(String bairro) {
        return list("LOWER(bairro) = LOWER(?1)", bairro);
    }

    public long countByBairro(String bairro) {
        return count("UPPER(bairro) LIKE UPPER(?1)", "%" + bairro + "%");
    }

    public List<Endereco> findByCep(String cep) {
        return find("UPPER(cep) LIKE UPPER(?1) ", "%" + cep + "%").list();
    }
}
