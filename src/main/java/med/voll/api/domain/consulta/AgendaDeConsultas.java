package med.voll.api.domain.consulta;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.validaceoes.agendamento.ValidadorAgendamentoConsulta;
import med.voll.api.domain.consulta.validaceoes.cancelamento.ValidadorCancelamentoConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultas {


    private ConsultaRepository consultaRepository;
    private MedicoRepository medicoRepository;
    private PacienteRepository pacienteRepository;
    private List<ValidadorAgendamentoConsulta> validadores;
    private List<ValidadorCancelamentoConsulta> validadorCancelamento;

    public AgendaDeConsultas(ConsultaRepository consultaRepository,
                             MedicoRepository medicoRepository,
                             PacienteRepository pacienteRepository,
                             List<ValidadorAgendamentoConsulta> validadores,
                             List<ValidadorCancelamentoConsulta> validadorCancelamento) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.validadores = validadores;
        this.validadorCancelamento = validadorCancelamento;
    }

    public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {

        if (dados.idMedico()!=null && !medicoRepository.existsById(dados.idMedico())) {
            throw new ValidacaoException("Id do medico informado não existe");
        }

        if (dados.idPaciente()!=null && !pacienteRepository.existsById(dados.idPaciente())) {
            throw new ValidacaoException("Id do paciente informado não existe");
        }

        validadores.forEach(v -> v.validar(dados));

        var medico = selecionarMedico(dados);

        if (medico == null) {
            throw new ValidacaoException("Não existe médico disponível nesta data");
        }

        var paciente = pacienteRepository.getReferenceById(dados.idPaciente());

        var consulta = new Consulta(null, medico, paciente, dados.data(), null);
        consultaRepository.save(consulta);
        return new DadosDetalhamentoConsulta(consulta);
    }

    private Medico selecionarMedico(DadosAgendamentoConsulta dados) {
        if (dados.idMedico() != null) {
            return medicoRepository.getReferenceById(dados.idMedico());
        }

        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade deve ser informada, caso o médico não seja selecionado.");
        }

        return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
    }

    public void cancelar(DadosCancelamentoConsulta dados) {
        boolean existeConsulta = consultaRepository.existsById(dados.idConsulta());
        if (!existeConsulta) {
            throw new ValidacaoException("Id da consulta informada não existe");
        }

        validadorCancelamento.forEach(v-> v.validar(dados));

        var consulta = consultaRepository.findById(dados.idConsulta())
                .orElseThrow(()-> new ValidacaoException("Essa consulta não foi encontrada"));
        consulta.cancelar(dados.motivo());
    }
}
