package br.gov.caixa.adapters.out.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável pelo mapeamento entre entidades de persistência e modelos de domínio
 */
@ApplicationScoped
public class EntityModelMapper {

    @Inject
    protected ObjectMapper objectMapper;

    /**
     * Faz o parse de um objeto de acordo com o tipo especificado
     * @param <T> tipo de objeto resultante
     * @param source objeto de origem
     * @param destinationClass classe do objeto de destino
     * @return objeto mapeado
     */
    public <T> T map(Object source, Class<T> destinationClass) {
        return objectMapper.convertValue(source, destinationClass);
    }

    /**
     * Faz o parse de uma lista de objetos de acordo com o tipo especificado
     * @param lista a ser mapeada
     * @param classeDestino classe do objeto de destino
     * @return lista mapeada
     * @param <D> tipo de objeto resultante
     * @param <T> tipo de objeto de origem
     */
    public <D,T> List<D> mapAll(Collection<T> lista, Class<D> classeDestino){
        return lista.stream().map(entity -> map(entity, classeDestino)).collect(Collectors.toList());
    }
}
