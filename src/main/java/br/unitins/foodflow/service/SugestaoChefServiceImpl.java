package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.SugestaoChefDTO;
import br.unitins.foodflow.dto.SugestaoChefResponseDTO;
import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.SugestaoChefe;
import br.unitins.foodflow.model.TipoPeriodo;
import br.unitins.foodflow.repository.ItemCardapioRepository;
import br.unitins.foodflow.repository.SugestaoChefRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;

@ApplicationScoped
public class SugestaoChefServiceImpl implements SugestaoChefService {

    @Inject
    SugestaoChefRepository sugestaoRepository;

    @Inject
    ItemCardapioRepository itemRepository;

    @Override
    @Transactional
    public SugestaoChefResponseDTO create(SugestaoChefDTO dto) {
        if (sugestaoRepository.existsByData(dto.data())) {
            throw new BadRequestException("Já existe uma sugestão para a data " + dto.data());
        }

        SugestaoChefe sugestao = new SugestaoChefe();
        sugestao.setData(dto.data());

        if (dto.idItemAlmoco() != null) {
            ItemCardapio itemAlmoco = itemRepository.findById(dto.idItemAlmoco());
            if (itemAlmoco == null) {
                throw new NotFoundException("Item de almoço não encontrado.");
            }
            if (itemAlmoco.getPeriodo() != TipoPeriodo.ALMOCO) {
                throw new BadRequestException("O item selecionado não é do período de almoço.");
            }
            sugestao.setItemAlmoco(itemAlmoco);
        }

        if (dto.idItemJantar() != null) {
            ItemCardapio itemJantar = itemRepository.findById(dto.idItemJantar());
            if (itemJantar == null) {
                throw new NotFoundException("Item de jantar não encontrado.");
            }
            if (itemJantar.getPeriodo() != TipoPeriodo.JANTAR) {
                throw new BadRequestException("O item selecionado não é do período de jantar.");
            }
            sugestao.setItemJantar(itemJantar);
        }

        if (sugestao.getItemAlmoco() == null && sugestao.getItemJantar() == null) {
            throw new BadRequestException("Pelo menos um item (almoço ou jantar) deve ser informado.");
        }

        sugestaoRepository.persist(sugestao);
        return SugestaoChefResponseDTO.valueOf(sugestao);
    }

    @Override
    @Transactional
    public SugestaoChefResponseDTO update(SugestaoChefDTO dto, Long id) {
        SugestaoChefe sugestao = sugestaoRepository.findById(id);
        if (sugestao == null) {
            throw new NotFoundException("Sugestão com ID " + id + " não encontrada.");
        }

        if (!sugestao.getData().equals(dto.data())) {
            if (sugestaoRepository.existsByData(dto.data())) {
                throw new BadRequestException("Já existe uma sugestão para a data " + dto.data());
            }
            sugestao.setData(dto.data());
        }

        if (dto.idItemAlmoco() != null) {
            ItemCardapio itemAlmoco = itemRepository.findById(dto.idItemAlmoco());
            if (itemAlmoco == null) {
                throw new NotFoundException("Item de almoço não encontrado.");
            }
            if (itemAlmoco.getPeriodo() != TipoPeriodo.ALMOCO) {
                throw new BadRequestException("O item selecionado não é do período de almoço.");
            }
            sugestao.setItemAlmoco(itemAlmoco);
        } else {
            sugestao.setItemAlmoco(null);
        }

        if (dto.idItemJantar() != null) {
            ItemCardapio itemJantar = itemRepository.findById(dto.idItemJantar());
            if (itemJantar == null) {
                throw new NotFoundException("Item de jantar não encontrado.");
            }
            if (itemJantar.getPeriodo() != TipoPeriodo.JANTAR) {
                throw new BadRequestException("O item selecionado não é do período de jantar.");
            }
            sugestao.setItemJantar(itemJantar);
        } else {
            sugestao.setItemJantar(null);
        }

        return SugestaoChefResponseDTO.valueOf(sugestao);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SugestaoChefe sugestao = sugestaoRepository.findById(id);
        if (sugestao == null) {
            throw new NotFoundException("Sugestão não encontrada.");
        }
        sugestaoRepository.delete(sugestao);
    }

    @Override
    public SugestaoChefResponseDTO findById(Long id) {
        SugestaoChefe sugestao = sugestaoRepository.findById(id);
        if (sugestao == null) {
            throw new NotFoundException("Sugestão não encontrada.");
        }
        return SugestaoChefResponseDTO.valueOf(sugestao);
    }

    @Override
    public SugestaoChefResponseDTO findByData(LocalDate data) {
        SugestaoChefe sugestao = sugestaoRepository.findByData(data);
        return SugestaoChefResponseDTO.valueOf(sugestao);
    }

    @Override
    public SugestaoChefResponseDTO findSugestaoAtiva() {
        SugestaoChefe sugestao = sugestaoRepository.findSugestaoAtiva();
        return SugestaoChefResponseDTO.valueOf(sugestao);
    }

    @Override
    @Transactional
    public void deletarSugestoesAntigas(int diasAtras) {
        LocalDate dataLimite = LocalDate.now().minusDays(diasAtras);
        sugestaoRepository.deletarSugestoesAntigas(dataLimite);
    }
}