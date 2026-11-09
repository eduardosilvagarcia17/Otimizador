package br.com.pi.otimizador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository repository;
    private final ProcessoRepository processoRepository;

    public EquipamentoController(
            EquipamentoRepository repository,
            ProcessoRepository processoRepository) {

        this.repository = repository;
        this.processoRepository = processoRepository;
    }

    @GetMapping
    public List<Equipamento> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Equipamento cadastrar(@RequestBody Equipamento equipamento) {
        return repository.save(equipamento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Impede excluir equipamentos que possuem processos
        if (processoRepository.existsByEquipamentoId(id)) {
            return ResponseEntity.status(409)
                    .body("Não é possível excluir este equipamento porque existem processos associados a ele.");
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipamento> editar(
            @PathVariable Long id,
            @RequestBody Equipamento dadosAtualizados) {

        return repository.findById(id)
                .map(equipamento -> {

                    equipamento.setNome(dadosAtualizados.getNome());
                    equipamento.setTipo(dadosAtualizados.getTipo());
                    equipamento.setPotencia(dadosAtualizados.getPotencia());
                    equipamento.setHoras(dadosAtualizados.getHoras());
                    equipamento.setStatus(dadosAtualizados.getStatus());

                    Equipamento atualizado = repository.save(equipamento);

                    return ResponseEntity.ok(atualizado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
