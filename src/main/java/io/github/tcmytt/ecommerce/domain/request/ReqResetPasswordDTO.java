package io.github.tcmytt.ecommerce.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReqResetPasswordDTO {
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 10, message = "Mật khẩu mới phải có đúng 8 ký tự")
    @Pattern(regexp = "[a-zA-Z0-9]{8}", message = "Mật khẩu mới chỉ được chứa chữ cái và số, không bao gồm ký tự đặc biệt")
    String oldPassWord;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 10, message = "Mật khẩu mới phải có đúng 8 ký tự")
    @Pattern(regexp = "[a-zA-Z0-9]{8}", message = "Mật khẩu mới chỉ được chứa chữ cái và số, không bao gồm ký tự đặc biệt")
    String newPassword;
}
