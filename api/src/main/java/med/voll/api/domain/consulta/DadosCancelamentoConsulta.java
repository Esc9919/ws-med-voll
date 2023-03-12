package med.voll.api.domain.consulta;

import jakarta.validation.constraints.NotNull;

public record DadosCancelamentoConsulta(

        @NotNull
        Long idConculta,

        @NotNull
        MotivoCancelamento motivo
) {
}
