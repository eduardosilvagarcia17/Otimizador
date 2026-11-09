
package br.com.pi.otimizador;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private double horasExecucao;
    private String status;

    // Horário registrado quando o processo é iniciado
    private LocalDateTime inicioExecucao;

    // Horário previsto para terminar
    private LocalDateTime fimPrevisto;

    @ManyToOne
    @JoinColumn(name = "equipamento_id")
    private Equipamento equipamento;

    public Processo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getHorasExecucao() {
        return horasExecucao;
    }

    public void setHorasExecucao(double horasExecucao) {
        this.horasExecucao = horasExecucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(LocalDateTime inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public LocalDateTime getFimPrevisto() {
        return fimPrevisto;
    }

    public void setFimPrevisto(LocalDateTime fimPrevisto) {
        this.fimPrevisto = fimPrevisto;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }
}
