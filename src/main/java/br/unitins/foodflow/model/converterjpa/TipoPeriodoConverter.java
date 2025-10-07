package br.unitins.foodflow.model.converterjpa;

import br.unitins.foodflow.model.TipoPeriodo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoPeriodoConverter implements AttributeConverter<TipoPeriodo, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TipoPeriodo tipoPeriodo) {
        return tipoPeriodo == null ? null : tipoPeriodo.getId();

    }

    @Override
    public TipoPeriodo convertToEntityAttribute(Integer id) {
        return TipoPeriodo.valueOf(id);
    }
}
