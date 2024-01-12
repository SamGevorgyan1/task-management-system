package com.taskmanagement.controller;

import com.taskmanagement.dto.requestdto.UserDTO;
import com.taskmanagement.dto.responsedto.UserResponseDTO;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.exceptions.UserBadRequestException;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static com.taskmanagement.util.converters.UserDTOConverter.convertUserEntityToDTO;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final HttpSession session;

    @GetMapping
    public UserResponseDTO getByEmail(@RequestParam String email) throws UserApiException, UserBadRequestException {
        UserEntity user = userService.findByEmail(email);
        return convertUserEntityToDTO(user);
    }

    @GetMapping("/get-all")
    public @ResponseBody List<UserResponseDTO> getAll(@RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String surname,
                                                      @RequestParam(required = false) String role) throws UserApiException {

        return userService.getAll(name, surname, role);
    }

    @PutMapping("/{id}")
    public @ResponseBody UserResponseDTO updateUser(@PathVariable Integer id, @RequestBody @Valid UserDTO userDTO) throws UserApiException, UserBadRequestException {
        return userService.updateUser(id, userDTO);
    }

    @PatchMapping("/")
    public @ResponseBody UserResponseDTO updatePassword(@RequestParam String oldPassword,
                                                        @RequestParam String newPassword,
                                                        @RequestParam String confirmPassword,
                                                        Principal principal) throws UserApiException, UserBadRequestException {

        return userService.updatePassword(principal.getName(), oldPassword, newPassword, confirmPassword);
    }

    @PostMapping("/verify-user")
    public boolean verifyUser(@RequestParam String verifyCode, @RequestParam String email) throws UserApiException, UserBadRequestException {
        return userService.verifyUser(verifyCode,email);
    }

    @PatchMapping("/forgot-password")
    public void forgotPassword(@RequestParam String email, HttpServletRequest request) throws UserApiException, UserBadRequestException {
        request.getSession().setAttribute("email", email);

        userService.forgotPassword(email);
    }

    @PatchMapping("/set-password")
    public @ResponseBody boolean setPassword(@RequestParam String resetToken,
                                             @RequestParam String newPassword,
                                             @RequestParam String confirmPassword
    ) throws UserApiException, UserBadRequestException {

        String email = (String) session.getAttribute("email");
        boolean isUpdated = userService.setPassword(email, resetToken, newPassword, confirmPassword);
        session.removeAttribute("email");
        return isUpdated;
    }

    @PostMapping("/send-verification-code")
    public void sendVerificationCode(@RequestParam String email) throws UserApiException, UserBadRequestException {
        userService.sendVerificationCode(email);
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public @ResponseBody void delete(@RequestParam Integer id) throws UserApiException, UserBadRequestException {
        userService.delete(id);
    }
}