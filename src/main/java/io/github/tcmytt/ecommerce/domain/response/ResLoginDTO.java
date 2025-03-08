package io.github.tcmytt.ecommerce.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.tcmytt.ecommerce.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
        private Boolean gender;
        private String address;
        private String phoneNumber;
        private String dateOfBirth;
        private String avatar;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String name;
        private Boolean gender;
        private Role role;
        private String address;
        private String phoneNumber;
        private String dateOfBirth;
        private String avatar;
    }

}
