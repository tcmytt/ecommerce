package io.github.tcmytt.ecommerce.domain.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReqForgetPasswordDTO {

    // Email validation
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    // OTP validation
    @NotBlank(message = "Mã OTP không được để trống")
    @Size(min = 8, max = 8, message = "Mã OTP phải có đúng 8 ký tự")
    private String otpString;

    // New password validation
    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String newPassword;
}