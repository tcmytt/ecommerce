package io.github.tcmytt.ecommerce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.tcmytt.ecommerce.domain.User;
import io.github.tcmytt.ecommerce.domain.request.ReqLoginDTO;
import io.github.tcmytt.ecommerce.domain.response.ResCreateUserDTO;
import io.github.tcmytt.ecommerce.domain.response.ResLoginDTO;
import io.github.tcmytt.ecommerce.service.UserService;
import io.github.tcmytt.ecommerce.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        private final PasswordEncoder passwordEncoder;

        public AuthController(
                        AuthenticationManagerBuilder authenticationManagerBuilder,
                        SecurityUtil securityUtil,
                        UserService userService,
                        PasswordEncoder passwordEncoder) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
                this.passwordEncoder = passwordEncoder;
        }

        @Value("${jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        @Operation(summary = "Login", description = "Login with username and password")
        @ApiResponse(responseCode = "200", description = "Login successfully")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @PostMapping("/login")
        public ResponseEntity<ResLoginDTO> login(@RequestBody ReqLoginDTO loginDto) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();
                User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName(),
                                        currentUserDB.getGender(),
                                        currentUserDB.getAddress(),
                                        currentUserDB.getPhoneNumber(),
                                        currentUserDB.getDateOfBirth(),
                                        currentUserDB.getAvatar());
                        res.setUser(userLogin);
                }

                // create access token
                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
                res.setAccessToken(access_token);

                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

                // update user
                this.userService.updateUserToken(refresh_token, loginDto.getUsername());

                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @Operation(summary = "Get account", description = "Get account information")
        @ApiResponse(responseCode = "200", description = "Get account successfully")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @GetMapping("/account")
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUserDB = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = null;
                if (currentUserDB != null) {
                        userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName(),
                                        currentUserDB.getGender(),
                                        currentUserDB.getAddress(),
                                        currentUserDB.getPhoneNumber(),
                                        currentUserDB.getDateOfBirth(),
                                        currentUserDB.getAvatar());
                }

                return ResponseEntity.ok().body(userLogin);
        }

        @Operation(summary = "Register", description = "Register new account")
        @ApiResponse(responseCode = "201", description = "Register successfully")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @PostMapping("/register")
        public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User regUser) throws Exception {
                boolean isEmailExist = this.userService.isEmailExist(regUser.getEmail());
                if (isEmailExist) {
                        throw new Exception(
                                        "Email " + regUser.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
                }

                String hashPassword = this.passwordEncoder.encode(regUser.getPassword());
                regUser.setPassword(hashPassword);
                User user = this.userService.handleCreateUser(regUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
        }

        @Operation(summary = "Logout", description = "Logout account")
        @ApiResponse(responseCode = "200", description = "Logout successfully")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @PostMapping("/logout")
        public ResponseEntity<String> logout() throws Exception {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                if (email.equals("")) {
                        throw new Exception("Access Token không hợp lệ");
                }

                // update refresh token = null
                this.userService.updateUserToken(null, email);

                // remove refresh token cookie
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", "")
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body("Đăng xuất thành công");
        }

        @Operation(summary = "Refresh token", description = "get new Refresh token ")
        @ApiResponse(responseCode = "200", description = "Refresh token successfully")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @GetMapping("/refresh")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws Exception {
                if (refresh_token.equals("abc")) {
                        throw new Exception("Bạn không có refresh token ở cookie");
                }
                // check valid
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                // check user by token + email
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new Exception("Refresh Token không hợp lệ");
                }

                // issue new token/set refresh token as cookies
                ResLoginDTO res = new ResLoginDTO();
                User currentUserDB = this.userService.handleGetUserByUsername(email);
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName(),
                                        currentUserDB.getGender(),
                                        currentUserDB.getAddress(),
                                        currentUserDB.getPhoneNumber(),
                                        currentUserDB.getDateOfBirth(),
                                        currentUserDB.getAvatar());
                        res.setUser(userLogin);
                }

                // create access token
                String access_token = this.securityUtil.createAccessToken(email, res);
                res.setAccessToken(access_token);

                // create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

                // update user
                this.userService.updateUserToken(new_refresh_token, email);

                // set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @Operation(summary = "OAuth2 Google Login ", description = "Login Google with OAuth2")
        @ApiResponse(responseCode = "200", description = "Login successfully")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @GetMapping("/oauth2/success")
        public ResponseEntity<ResLoginDTO> handleOAuth2LoginSuccess(@AuthenticationPrincipal OAuth2User principal) {
                // Lấy thông tin người dùng từ OAuth2
                String email = principal.getAttribute("email");
                String name = principal.getAttribute("name");

                // Kiểm tra xem người dùng đã tồn tại chưa
                User user = userService.findOrCreateUserByEmail(email, name);
                System.out.println(">>> User: " + user);

                // Tạo token
                ResLoginDTO res = new ResLoginDTO();
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                user.getId(),
                                user.getEmail(),
                                user.getName(),
                                user.getGender(),
                                user.getAddress(),
                                user.getPhoneNumber(),
                                user.getDateOfBirth(),
                                user.getAvatar());
                res.setUser(userLogin);

                String accessToken = securityUtil.createAccessToken(user.getEmail(), res);
                String refreshToken = securityUtil.createRefreshToken(user.getEmail(), res);

                // Lưu refresh token vào database
                userService.updateUserToken(refreshToken, user.getEmail());

                // Set cookies
                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(604800) // 7 ngày
                                .build();

                res.setAccessToken(accessToken);

                System.out.println("Gửi thành công ");
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @Operation(summary = "OAuth2 Google Login Failure", description = "Login Google with OAuth2")
        @ApiResponse(responseCode = "401", description = "Login failure")
        @GetMapping("/oauth2/failure")
        public ResponseEntity<String> handleOAuth2LoginFailure() {
                System.out.println(">>> OAuth2 Login Failure");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại");
        }

}