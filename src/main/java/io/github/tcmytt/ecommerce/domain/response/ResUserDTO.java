package io.github.tcmytt.ecommerce.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResUserDTO {
    private Long id;

    private String name;

    private String email;

    private Boolean gender;

    private String avatar;

    private String address;

    private String phoneNumber;

    private String dateOfBirth;

    private String createdAt;

}
