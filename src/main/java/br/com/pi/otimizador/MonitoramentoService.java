
package br.com.pi.otimizador;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class MonitoramentoService {

    private final ProcessoRepository processoRepository;
    private final EquipamentoRepository equipamentoRepository;

    public MonitoramentoService(
            ProcessoRepository processoRepository,
            EquipamentoRepository equipamentoRepository) {

        this.processoRepository = processoRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void verificarProcessos() {

        List<Processo> processos = processoRepository.findAll();
        LocalDateTime agora = LocalDateTime.now();

        // Equipamentos que tiveram processos concluídos nesta verificação
        Set<Long> equipamentosParaVerificar = new HashSet<>();

        // Primeiro, conclui todos os processos cujo prazo terminou
        for (Processo processo : processos) {

            if (!"Em execução".equals(processo.getStatus())) {
                continue;
            }

            if (processo.getFimPrevisto() == null ||
                    agora.isBefore(processo.getFimPrevisto())) {
                continue;
            }

            processo.setStatus("Concluído");
            processoRepository.save(processo);

            if (processo.getEquipamento() != null) {
                equipamentosParaVerificar.add(
                        processo.getEquipamento().getId()
                );
            }
        }

        // Depois, verifica os equipamentos afetados
        for (Long equipamentoId : equipamentosParaVerificar) {

            boolean possuiProcessosAtivos = processos.stream()
                    .anyMatch(processo ->
                            processo.getEquipamento() != null
                            && equipamentoId.equals(
                                    processo.getEquipamento().getId()
                            )
                            && "Em execução".equals(processo.getStatus())
                    );

            if (!possuiProcessosAtivos) {

                equipamentoRepository.findById(equipamentoId)
                        .ifPresent(equipamento -> {

                            equipamento.setStatus("Ocioso");
                            equipamentoRepository.save(equipamento);

                            System.out.println(
                                    "Equipamento identificado como ocioso: "
                                    + equipamento.getNome()
                            );
                        });
            }
        }
    }
}
