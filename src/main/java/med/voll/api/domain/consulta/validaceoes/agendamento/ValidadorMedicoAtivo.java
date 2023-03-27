package med.voll.api.domain.consulta.validaceoes.agendamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.medico.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorMedicoAtivo implements ValidadorAgendamentoConsulta {

    @Autowired
    private MedicoRepository medicoRepository;

    public void validar(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() == null) {
            return;
        }

       var medicoAtivo = medicoRepository.findAtivoByAtivo(dados.idMedico());
        if (!medicoAtivo) {
            throw new ValidacaoException("Médico inativo.Consulta permitada apenas para Médicos ativos");
        }
    }
}
