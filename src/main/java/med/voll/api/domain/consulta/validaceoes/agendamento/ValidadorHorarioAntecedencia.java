package med.voll.api.domain.consulta.validaceoes.agendamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorHorarioAntecedencia implements ValidadorAgendamentoConsulta {

    public void validar(DadosAgendamentoConsulta dados) {
        var dataConsulta = dados.data();
        var agora = LocalDateTime.now();
        long intervalo = Duration.between(agora, dataConsulta).toMinutes();

        if (agendouAntesDeTrintaMinutos(intervalo)) {
            throw new ValidacaoException("Consulta deve ser agendada com antecedência mínima de trinta minutos");
        }
    }

    private boolean agendouAntesDeTrintaMinutos(long diferencaEmMinutos) {
        return diferencaEmMinutos < 30;
    }
}
