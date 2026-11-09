
package br.com.pi.otimizador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/processos")
public class ProcessoController {

    private final ProcessoRepository processoRepository;
    private final EquipamentoRepository equipamentoRepository;

    public ProcessoController(
            ProcessoRepository processoRepository,
            EquipamentoRepository equipamentoRepository) {

        this.processoRepository = processoRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    // Consultar processos cadastrados
    @GetMapping
    public List<Processo> listar() {
        return processoRepository.findAll();
    }

    // Cadastrar um novo processo
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Processo processo) {

        if (processo.getEquipamento() == null ||
            processo.getEquipamento().getId() == null) {

            return ResponseEntity.badRequest()
                    .body("Selecione um equipamento.");
        }

        Long equipamentoId = processo.getEquipamento().getId();

        return equipamentoRepository.findById(equipamentoId)
                .map(equipamento -> {

                    processo.setId(null);
                    processo.setEquipamento(equipamento);

                    // Processo recém-cadastrado ainda não foi iniciado
                    processo.setStatus("Pendente");
                    processo.setInicioExecucao(null);
                    processo.setFimPrevisto(null);

                    Processo salvo = processoRepository.save(processo);

                    return ResponseEntity.ok(salvo);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Iniciar um processo cadastrado
    @PostMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciar(@PathVariable Long id) {

        return processoRepository.findById(id)
                .map(processo -> {

                    if ("Em execução".equals(processo.getStatus())) {
                        return ResponseEntity.badRequest()
                                .body("Este processo já está em execução.");
                    }

                    if (processo.getHorasExecucao() <= 0) {
                        return ResponseEntity.badRequest()
                                .body("Informe uma duração maior que zero.");
                    }

                    LocalDateTime inicio = LocalDateTime.now();

                    // Converte horas em segundos.
                    long duracaoSegundos =
                            Math.round(processo.getHorasExecucao() * 3600);

                    if (duracaoSegundos < 1) {
                        return ResponseEntity.badRequest()
                                .body("A duração deve ser de pelo menos 1 segundo.");
                    }

                    processo.setInicioExecucao(inicio);
                    processo.setFimPrevisto(
                            inicio.plusSeconds(duracaoSegundos)
                    );
                    processo.setStatus("Em execução");

                    Equipamento equipamento = processo.getEquipamento();
                    equipamento.setStatus("Ativo");

                    equipamentoRepository.save(equipamento);
                    processoRepository.save(processo);

                    return ResponseEntity.ok(processo);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());

               }
    // Excluir um processo cadastrado
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {

        return processoRepository.findById(id)
                .map(processo -> {

                    // Impede excluir processos em execução
                    if ("Em execução".equals(processo.getStatus())) {
                        return ResponseEntity.status(409)
                                .body("Não é possível excluir um processo em execução.");
                    }

                    processoRepository.delete(processo);

                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    

    }
}
