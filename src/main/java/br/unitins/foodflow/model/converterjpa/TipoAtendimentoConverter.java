package br.unitins.foodflow.model.converterjpa;

import br.unitins.foodflow.model.TipoAtendimento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoAtendimentoConverter implements AttributeConverter<TipoAtendimento, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TipoAtendimento tipoAtendimento) {
        return tipoAtendimento == null ? null : tipoAtendimento.getId();

    }

    @Override
    public TipoAtendimento convertToEntityAttribute(Integer id) {
        return TipoAtendimento.valueOf(id);
    }
}
