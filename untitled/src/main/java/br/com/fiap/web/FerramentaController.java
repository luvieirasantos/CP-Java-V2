package br.com.fiap.web;

import br.com.fiap.api.FerramentaModelAssembler;
import br.com.fiap.domain.dto.*;
import br.com.fiap.domain.entity.Ferramenta;
import br.com.fiap.service.FerramentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/ferramentas")
@RequiredArgsConstructor
public class FerramentaController {
    private final FerramentaService service;
    private final FerramentaModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<FerramentaOutputDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<Ferramenta> p = service.listar(pageable);
        var models = p.map(assembler::toModel).getContent();
        return CollectionModel.of(models, linkTo(methodOn(FerramentaController.class).list(page, size)).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<FerramentaOutputDTO> getById(@PathVariable Long id){
        return assembler.toModel(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<FerramentaOutputDTO>> create(@RequestBody @Valid FerramentaCreateDTO dto){
        Ferramenta salvo = service.criar(dto);
        EntityModel<FerramentaOutputDTO> model = assembler.toModel(salvo);
        return ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<FerramentaOutputDTO> update(@PathVariable Long id, @RequestBody @Valid FerramentaUpdateDTO dto){
        return assembler.toModel(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}")
    public EntityModel<FerramentaOutputDTO> patch(@PathVariable Long id, @RequestBody FerramentaUpdateDTO dto){
        // patch simples: permite enviar apenas alguns campos
        Ferramenta existente = service.buscar(id);
        if(dto.getNome()!=null) existente.setNome(dto.getNome());
        if(dto.getTipo()!=null) existente.setTipo(dto.getTipo());
        if(dto.getClassificacao()!=null) existente.setClassificacao(dto.getClassificacao());
        if(dto.getTamanho()!=null) existente.setTamanho(dto.getTamanho());
        if(dto.getPreco()!=null) existente.setPreco(dto.getPreco());
        return assembler.toModel(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}