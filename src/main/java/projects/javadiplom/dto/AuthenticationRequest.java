package projects.javadiplom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationRequest {
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    @NotBlank
    private String password;
}
