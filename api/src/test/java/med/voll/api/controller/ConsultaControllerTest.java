package med.voll.api.controller;

import med.voll.api.domain.consulta.AgendaConsultaService;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.DadosDetalhamentoConsulta;
import med.voll.api.domain.endereco.Endereco;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ConsultaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<DadosAgendamentoConsulta> dadosAgendamentoConsultaJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoConsulta> dadosDetalhamentoConsultaJson;

    @Autowired
    private AgendaConsultaService agendaConsultaService;

    @MockBean
    private ConsultaRepository consultaRepository;

    @MockBean
    private PacienteRepository pacienteRepository;

    @MockBean
    private MedicoRepository medicoRepository;

    private DadosAgendamentoConsulta dados;

    private Paciente paciente;

    LocalDateTime data = LocalDateTime.of(2023, Month.MARCH, 23, 10,00);

    @BeforeEach
    void config() {
        var especialidade = Especialidade.CARDIOLOGIA;
        dados = new DadosAgendamentoConsulta(2l, 5l, data, especialidade);
        var endereco = new Endereco("656560", "vdvdvd", "dvdfdfd", "fgdgfgfg", "gfgfgfg", "dvfgfgfgf", "gf");

        paciente = new Paciente(1l, "and", "and@fkkf", "111111111111", "111111111112", endereco, true);
    }


    @Test
    @DisplayName("Deveria devolver codigo http 400 quando informações estão invalidas")
    @WithMockUser
    void agendar_cebario1() throws Exception {
        var response = mvc.perform(post("/consultas"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando informações estão validas")
    @WithMockUser
    void agendar_cebario2() throws Exception {
        var dadosDetalhamento = new DadosDetalhamentoConsulta(1l, 2l, 5l, data);

        when(pacienteRepository.existsById(dados.idPaciente())).thenReturn(true);
        when(medicoRepository.existsById(dados.idMedico())).thenReturn(true);
        when(medicoRepository.findAtivoById(dados.idMedico())).thenReturn(true);
        when(pacienteRepository.findAtivoById(dados.idPaciente())).thenReturn(true);
        when(pacienteRepository.findById(dados.idPaciente())).thenReturn(Optional.ofNullable(paciente));

        agendaConsultaService.agendar(dados);

        var response = mvc
                .perform(
                        post("/consultas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dadosAgendamentoConsultaJson.write(
                                        dados
                                ).getJson())
                )
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }
}