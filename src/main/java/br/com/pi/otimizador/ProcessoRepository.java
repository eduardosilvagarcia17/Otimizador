package br.com.pi.otimizador;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessoRepository extends JpaRepository<Processo, Long> {

boolean existsByEquipamentoId(Long equipamentoId);

}