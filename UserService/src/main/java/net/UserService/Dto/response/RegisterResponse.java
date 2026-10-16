package net.UserService.Dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private Long userId;

    private String username;

    private String email;

    private String role;

    private String message;

    private boolean success;
}
