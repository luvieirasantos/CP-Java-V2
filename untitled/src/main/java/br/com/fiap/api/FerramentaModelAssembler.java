package br.com.fiap.api;

import br.com.fiap.domain.dto.FerramentaOutputDTO;
import br.com.fiap.domain.entity.Ferramenta;
import br.com.fiap.web.FerramentaController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class FerramentaModelAssembler implements RepresentationModelAssembler<Ferramenta, EntityModel<FerramentaOutputDTO>> {
    @Override
    public EntityModel<FerramentaOutputDTO> toModel(Ferramenta entidade) {
        FerramentaOutputDTO dto = FerramentaOutputDTO.from(entidade);
        return EntityModel.of(dto,
                linkTo(methodOn(FerramentaController.class).getById(entidade.getId())).withSelfRel(),
                linkTo(methodOn(FerramentaController.class).list(0, 10)).withRel(IanaLinkRelations.COLLECTION),
                linkTo(methodOn(FerramentaController.class).update(entidade.getId(), null)).withRel("update"),
                linkTo(methodOn(FerramentaController.class).delete(entidade.getId())).withRel("delete")
        );
    }
}