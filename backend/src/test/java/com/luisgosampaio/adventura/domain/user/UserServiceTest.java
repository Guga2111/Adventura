package com.luisgosampaio.adventura.domain.user;

import com.luisgosampaio.adventura.domain.exceptions.EmailAlreadyExistsException;
import com.luisgosampaio.adventura.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
    }

    @Test
    void getUser_ById_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertThat(result).isEqualTo(user);
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUser_ById_ThrowsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(99L);
    }

    @Test
    void getUser_ByEmail_ReturnsUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = userService.getUser("john@example.com");

        assertThat(result).isEqualTo(user);
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void getUser_ByEmail_ThrowsUserNotFoundException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser("unknown@example.com"))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void saveUser_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.saveUser(user);

        assertThat(result).isEqualTo(user);
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        verify(bCryptPasswordEncoder).encode("password123");
        verify(userRepository).save(user);
    }

    @Test
    void saveUser_LowercasesEmail() {
        user.setEmail("JOHN@EXAMPLE.COM");

        when(userRepository.existsByEmail("JOHN@EXAMPLE.COM")).thenReturn(false);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.saveUser(user);

        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void saveUser_ThrowsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.saveUser(user))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
    }
}