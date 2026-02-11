package com.luisgosampaio.adventura.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luisgosampaio.adventura.domain.exceptions.EmailAlreadyExistsException;
import com.luisgosampaio.adventura.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
    }

    @Test
    void getUserById_ReturnsOk() throws Exception {
        when(userService.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserById_ThrowsUserNotFoundException() {
        when(userService.getUser(99L)).thenThrow(new UserNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(get("/user/99")))
                .cause()
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByEmail_ReturnsOk() throws Exception {
        when(userService.getUser("john@example.com")).thenReturn(user);

        mockMvc.perform(get("/user").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getUserByEmail_ThrowsUserNotFoundException() {
        when(userService.getUser("unknown@example.com")).thenThrow(new UserNotFoundException("unknown@example.com"));

        assertThatThrownBy(() -> mockMvc.perform(get("/user").param("email", "unknown@example.com")))
                .cause()
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void saveUser_ReturnsCreated() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    void saveUser_ThrowsEmailAlreadyExistsException() {
        when(userService.saveUser(any(User.class))).thenThrow(new EmailAlreadyExistsException("john@example.com"));

        assertThatThrownBy(() -> mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))))
                .cause()
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
}
