package com.urly.urlyservices.vo.response;

import com.urly.urlyservices.enums.JwtType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTResponse {

    private String token;

    private JwtType jwtType;

    private String username;

    private String email;

    private List<String> roles;
}
