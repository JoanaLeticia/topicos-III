package br.unitins.foodflow.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @Column(nullable = false, name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "numero_pessoas")
    private Integer numeroPessoas;

    @Column(name = "codigo_confirmacao")
    private String codigoConfirmacao;

    @Column(name = "nome_convidado")
    private String nomeConvidado;

    @Column(name = "email_convidado")
    private String emailConvidado;

    @Column(name = "telefone_convidado")
    private String telefoneConvidado;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getNumeroPessoas() {
        return numeroPessoas;
    }

    public void setNumeroPessoas(Integer numeroPessoas) {
        this.numeroPessoas = numeroPessoas;
    }

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }

    public void setCodigoConfirmacao(String codigoConfirmacao) {
        this.codigoConfirmacao = codigoConfirmacao;
    }

    public String getNomeConvidado() {
        return nomeConvidado;
    }

    public void setNomeConvidado(String nomeConvidado) {
        this.nomeConvidado = nomeConvidado;
    }

    public String getEmailConvidado() {
        return emailConvidado;
    }

    public void setEmailConvidado(String emailConvidado) {
        this.emailConvidado = emailConvidado;
    }

    public String getTelefoneConvidado() {
        return telefoneConvidado;
    }

    public void setTelefoneConvidado(String telefoneConvidado) {
        this.telefoneConvidado = telefoneConvidado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}