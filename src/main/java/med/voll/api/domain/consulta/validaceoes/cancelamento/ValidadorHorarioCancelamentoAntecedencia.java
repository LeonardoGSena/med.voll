package med.voll.api.domain.consulta.validaceoes.cancelamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.Consulta;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosCancelamentoConsulta;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorHorarioCancelamentoAntecedencia implements ValidadorCancelamentoConsulta{

    private ConsultaRepository consultaRepository;

    public ValidadorHorarioCancelamentoAntecedencia(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Override
    public void validar(DadosCancelamentoConsulta dados) {
        Consulta consulta = consultaRepository.getReferenceById(dados.idConsulta());
        LocalDateTime hoje = LocalDateTime.now();
        var diferencaHoras = Duration.between(hoje, consulta.getData()).toHours();

        if (diferencaHoras < 24) {
            throw new ValidacaoException("Consulta deve ser cancelada com antecedência mínima de 24 horas");
        }
    }
}
