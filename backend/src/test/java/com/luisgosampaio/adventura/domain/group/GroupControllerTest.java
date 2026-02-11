package com.luisgosampaio.adventura.domain.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.UserAlreadyMemberException;
import com.luisgosampaio.adventura.domain.exceptions.UserNotFoundException;
import com.luisgosampaio.adventura.domain.user.User;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private User user;
    private Group group;
    private GroupMember member;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        group = new Group();
        group.setId(1L);
        group.setName("Travel Group");
        group.setDescription("A travel group");
        group.setCreatedBy(user);

        member = new GroupMember();
        member.setId(1L);
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupRole.ADMIN);
    }

    @Test
    void getGroup_ReturnsOk() throws Exception {
        when(groupService.getGroup(1L)).thenReturn(group);

        mockMvc.perform(get("/group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Travel Group"));
    }

    @Test
    void getGroup_ThrowsGroupNotFoundException() {
        when(groupService.getGroup(99L)).thenThrow(new GroupNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(get("/group/99")))
                .cause()
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void getGroups_ReturnsOk() throws Exception {
        when(groupService.getAllGroups()).thenReturn(List.of(group));

        mockMvc.perform(get("/group"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Travel Group"));
    }

    @Test
    void saveGroup_ReturnsCreated() throws Exception {
        when(groupService.saveGroup(any(Group.class), eq(1L))).thenReturn(group);

        mockMvc.perform(post("/group/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Travel Group"));
    }

    @Test
    void saveGroup_ThrowsUserNotFoundException() {
        when(groupService.saveGroup(any(Group.class), eq(99L))).thenThrow(new UserNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(post("/group/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group))))
                .cause()
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateGroup_ReturnsOk() throws Exception {
        when(groupService.updateGroup(eq(1L), any(Group.class))).thenReturn(group);

        mockMvc.perform(put("/group/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteGroup_ReturnsNoContent() throws Exception {
        doNothing().when(groupService).deleteGroup(1L);

        mockMvc.perform(delete("/group/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMyGroups_ReturnsOk() throws Exception {
        when(groupService.getMyGroups(1L)).thenReturn(List.of(group));

        mockMvc.perform(get("/group/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Travel Group"));
    }

    @Test
    void getGroupMembers_ReturnsOk() throws Exception {
        when(groupService.getMembers(1L)).thenReturn(List.of(member));

        mockMvc.perform(get("/group/1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void getGroupMembers_ThrowsGroupNotFoundException() {
        when(groupService.getMembers(99L)).thenThrow(new GroupNotFoundException(99L));

        assertThatThrownBy(() -> mockMvc.perform(get("/group/99/members")))
                .cause()
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void addMember_ReturnsCreated() throws Exception {
        when(groupService.addMember(1L, 2L, GroupRole.MEMBER)).thenReturn(member);

        mockMvc.perform(post("/group/1/user/2").param("role", "MEMBER"))
                .andExpect(status().isCreated());
    }

    @Test
    void addMember_ThrowsUserAlreadyMemberException() {
        when(groupService.addMember(1L, 1L, GroupRole.MEMBER)).thenThrow(new UserAlreadyMemberException(1L, 1L));

        assertThatThrownBy(() -> mockMvc.perform(post("/group/1/user/1").param("role", "MEMBER")))
                .cause()
                .isInstanceOf(UserAlreadyMemberException.class);
    }

    @Test
    void removeMember_ReturnsNoContent() throws Exception {
        doNothing().when(groupService).removeMember(1L, 2L);

        mockMvc.perform(delete("/group/1/member/2"))
                .andExpect(status().isNoContent());
    }
}
