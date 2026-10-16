package net.UserService.Controller;

import lombok.RequiredArgsConstructor;
import net.UserService.Dto.request.LoginRequest;
import net.UserService.Dto.response.LoginResponse;
import net.UserService.Dto.request.RegisterRequest;
import net.UserService.Dto.response.RegisterResponse;
import net.UserService.Entity.UserEntity;
import net.UserService.Repo.UserRepository;
import net.UserService.Service.AuthService;
import net.company.common.JwtUtility.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {

        RegisterResponse response =
                authService.createProfile(request);

        if (!response.isSuccess()) {

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {
        try {


            authenticate(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );


            UserEntity userDetails = userRepository.findByEmail(loginRequest.getEmail()).get();



            String jwtToken =
                    jwtUtil.generateToken(userDetails.getId() , userDetails.getEmail(),userDetails.getRole() );

//            ResponseCookie cookie = ResponseCookie
//                    .from("jwt", jwtToken)
//                    .httpOnly(true)
//                    .secure(false) // true in production HTTPS
//                    .path("/")
//                    .maxAge(Duration.ofDays(1))
//                    .sameSite("Strict")
//                    .build();

            return ResponseEntity.ok()
//                    .header(
//                            HttpHeaders.SET_COOKIE,
//                            cookie.toString()
//                    )
                    .body(
                            new LoginResponse(
                                    "Login successful",
                                    loginRequest.getEmail(),
                                    jwtToken
                            )
                    );

        } catch (BadCredentialsException e) {

            Map<String, Object> error =
                    new HashMap<>();

            error.put("error", true);
            error.put(
                    "message",
                    "Email or Password is incorrect"
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);

        }
        catch (DisabledException e) {

            Map<String, Object> error =
                    new HashMap<>();

            error.put("error", true);
            error.put(
                    "message",
                    "Account is disabled"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);

        }
        catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> error =
                    new HashMap<>();

            error.put("error", true);
            error.put(
                    "message",
                    "Authentication failed"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        }
    }

    private void authenticate(
            String email,
            String password
    ) {



        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }


}
