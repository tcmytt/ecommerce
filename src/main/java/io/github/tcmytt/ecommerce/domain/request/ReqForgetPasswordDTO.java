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
    @Pattern(regexp = "[a-zA-Z0-9]{8}", message = "Chỉ được chứa chữ cái và số, không bao gồm ký tự đặc biệt")
    private String OTP;

    // New password validation
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 10, message = "Mật khẩu mới phải có đúng 8 ký tự")
    @Pattern(regexp = "[a-zA-Z0-9]{8}", message = "Mật khẩu mới chỉ được chứa chữ cái và số, không bao gồm ký tự đặc biệt")
    private String newPassword;
}