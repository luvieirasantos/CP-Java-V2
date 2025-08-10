package br.com.fiap.service;

import br.com.fiap.domain.dto.*;
import br.com.fiap.domain.entity.Ferramenta;
import br.com.fiap.repository.FerramentaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FerramentaService {
    private final FerramentaRepository repo;

    public Page<Ferramenta> listar(Pageable pageable){
        return repo.findAll(pageable);
    }

    public Ferramenta buscar(Long id){
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Ferramenta não encontrada: "+id));
    }

    @Transactional
    public Ferramenta criar(FerramentaCreateDTO dto){
        return repo.save(dto.toEntity());
    }

    @Transactional
    public Ferramenta atualizar(Long id, FerramentaUpdateDTO dto){
        Ferramenta f = buscar(id);
        Ferramenta novo = dto.toEntity();
        f.setNome(novo.getNome());
        f.setTipo(novo.getTipo());
        f.setClassificacao(novo.getClassificacao());
        f.setTamanho(novo.getTamanho());
        f.setPreco(novo.getPreco());
        return f; // dirty checking
    }

    @Transactional
    public void excluir(Long id){
        repo.deleteById(id);
    }
}