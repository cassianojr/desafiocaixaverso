package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.TelemetriaEntity;
import br.gov.caixa.adapters.out.persistence.mapper.EntityModelMapper;
import br.gov.caixa.domain.model.Telemetria;
import br.gov.caixa.domain.port.out.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class TelemetriaRepositoryAdapter implements TelemetriaRepository {

    @Inject
    EntityManager em;

    @Inject
    EntityModelMapper mapper;

    @Override
    public void registrarChamada(String servico, double tempoRespostaMs, LocalDate data) {
        //verifica se já existe telemetria nesse dia para o serviço
        List<TelemetriaEntity> result = em.createQuery("""
                    FROM TelemetriaEntity
                     WHERE servico = :servico AND data = :data
                """, TelemetriaEntity.class)
                .setParameter("servico", servico)
                .setParameter("data", data)
                .getResultList();

        TelemetriaEntity telemetria;
        if(result.isEmpty()) {
            telemetria = new TelemetriaEntity();
            telemetria.setServico(servico);
            telemetria.setData(data);
            telemetria.setQuantidadeChamadas(1L);
            telemetria.setMediaRespostaMs(tempoRespostaMs);
            telemetria.setData(data);
            em.persist(telemetria);
        } else {
            telemetria = result.getFirst();

            double novaMedia = ((telemetria.getMediaRespostaMs() * telemetria.getQuantidadeChamadas()) + tempoRespostaMs)
                    / (telemetria.getQuantidadeChamadas() + 1);
            telemetria.setMediaRespostaMs(novaMedia);
            telemetria.setQuantidadeChamadas(telemetria.getQuantidadeChamadas() + 1);

            em.merge(telemetria);
        }
    }

    @Override
    public List<Telemetria> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return em.createQuery("""
            FROM TelemetriaEntity
             WHERE data BETWEEN :dataInicio AND :dataFim
             ORDER BY data desc
            """, TelemetriaEntity.class)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList()
                .stream()
                .map(e->mapper.map(e, Telemetria.class))
                .toList();
    }
}
