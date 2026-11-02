 package br.com.pi.otimizador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository repository;

    public EquipamentoController(EquipamentoRepository repository) {
        this.repository = repository;
    }

    // Lista todos os equipamentos
    @GetMapping
    public List<Equipamento> listar() {
        return repository.findAll();
    }

    // Cadastra um equipamento
    @PostMapping
    public Equipamento cadastrar(@RequestBody Equipamento equipamento) {
        return repository.save(equipamento);
    }

    // Exclui um equipamento pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Atualiza um equipamento existente
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