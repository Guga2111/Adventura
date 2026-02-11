package com.luisgosampaio.adventura.domain.group;

import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.UserAlreadyMemberException;
import com.luisgosampaio.adventura.domain.exceptions.UserNotFoundException;
import com.luisgosampaio.adventura.domain.user.User;
import com.luisgosampaio.adventura.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @InjectMocks
    private GroupService groupService;

    private User user;
    private Group group;
    private GroupMember member;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        group = new Group();
        group.setId(1L);
        group.setName("Travel Group");
        group.setDescription("A group for travel lovers");
        group.setCreatedBy(user);

        member = new GroupMember();
        member.setId(1L);
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupRole.ADMIN);
    }

    @Test
    void getGroup_ReturnsGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        Group result = groupService.getGroup(1L);

        assertThat(result).isEqualTo(group);
        assertThat(result.getName()).isEqualTo("Travel Group");
        verify(groupRepository).findById(1L);
    }

    @Test
    void getGroup_ThrowsGroupNotFoundException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroup(99L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void getAllGroups_ReturnsList() {
        when(groupRepository.findAll()).thenReturn(List.of(group));

        List<Group> result = groupService.getAllGroups();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(group);
    }

    @Test
    void getMyGroups_ReturnsList() {
        when(memberRepository.findByUserId(1L)).thenReturn(List.of(member));
        when(groupRepository.findAllById(List.of(1L))).thenReturn(List.of(group));

        List<Group> result = groupService.getMyGroups(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(group);
    }

    @Test
    void saveGroup_Success() {
        Group newGroup = new Group();
        newGroup.setName("New Group");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(memberRepository.save(any(GroupMember.class))).thenReturn(member);

        Group result = groupService.saveGroup(newGroup, 1L);

        assertThat(result).isEqualTo(group);
        assertThat(newGroup.getCreatedBy()).isEqualTo(user);
        verify(groupRepository).save(newGroup);
        verify(memberRepository).save(any(GroupMember.class));
    }

    @Test
    void saveGroup_ThrowsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.saveGroup(group, 99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void updateGroup_Success() {
        Group updatedInfo = new Group();
        updatedInfo.setName("Updated Name");
        updatedInfo.setDescription("Updated description");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group result = groupService.updateGroup(1L, updatedInfo);

        assertThat(group.getName()).isEqualTo("Updated Name");
        assertThat(group.getDescription()).isEqualTo("Updated description");
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroup_ThrowsGroupNotFoundException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.updateGroup(99L, group))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void deleteGroup_Success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        groupService.deleteGroup(1L);

        verify(groupRepository).delete(group);
    }

    @Test
    void deleteGroup_ThrowsGroupNotFoundException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.deleteGroup(99L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void addMember_Success() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setName("Jane Doe");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(memberRepository.existsByGroupIdAndUserId(1L, 2L)).thenReturn(false);
        when(memberRepository.save(any(GroupMember.class))).thenReturn(member);

        GroupMember result = groupService.addMember(1L, 2L, GroupRole.MEMBER);

        assertThat(result).isNotNull();
        verify(memberRepository).save(any(GroupMember.class));
    }

    @Test
    void addMember_ThrowsGroupNotFoundException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.addMember(99L, 1L, GroupRole.MEMBER))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void addMember_ThrowsUserNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.addMember(1L, 99L, GroupRole.MEMBER))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addMember_ThrowsUserAlreadyMemberException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(memberRepository.existsByGroupIdAndUserId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> groupService.addMember(1L, 1L, GroupRole.MEMBER))
                .isInstanceOf(UserAlreadyMemberException.class);

        verify(memberRepository, never()).save(any(GroupMember.class));
    }

    @Test
    void removeMember_Success() {
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));

        groupService.removeMember(1L, 1L);

        verify(memberRepository).delete(member);
    }

    @Test
    void removeMember_ThrowsMemberNotFoundException() {
        when(memberRepository.findByUserIdAndGroupId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.removeMember(1L, 99L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getMembers_ReturnsList() {
        when(groupRepository.existsById(1L)).thenReturn(true);
        when(memberRepository.findByGroupId(1L)).thenReturn(List.of(member));

        List<GroupMember> result = groupService.getMembers(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(member);
    }

    @Test
    void getMembers_ThrowsGroupNotFoundException() {
        when(groupRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> groupService.getMembers(99L))
                .isInstanceOf(GroupNotFoundException.class);
    }
}