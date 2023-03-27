package med.voll.api.domain.consulta.validaceoes.agendamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoPacienteSemOutraConsultaNoDia implements ValidadorAgendamentoConsulta {

    @Autowired
    private ConsultaRepository consultaRepository;

    public void validar(DadosAgendamentoConsulta dados) {
        var horaAberturaClinica = dados.data().withHour(7);
        var horaFechamentoClinica = dados.data().withHour(18);
        var pacientePossuiOutraConsulta = consultaRepository.existsByPacienteIdAndDataBetween(dados.idPaciente(), horaAberturaClinica, horaFechamentoClinica);

        if (pacientePossuiOutraConsulta) {
            throw new ValidacaoException("Paciente já possui uma consulta agendada nesse dia");
        }
    }
}
