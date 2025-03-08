package io.github.tcmytt.ecommerce.domain.response;

import io.github.tcmytt.ecommerce.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateUserDTO {
    private long id;
    private String email;
    private String name;
    private Boolean gender;
    private Role role;
    private String address;
    private String phoneNumber;
    private String dateOfBirth;
}