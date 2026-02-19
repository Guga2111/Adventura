package com.luisgosampaio.adventura.domain.group;

import com.luisgosampaio.adventura.domain.exceptions.*;
import com.luisgosampaio.adventura.domain.user.User;
import com.luisgosampaio.adventura.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setUpSecurityContext(Long userId) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@test.com", null, Collections.emptyList());
        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
        setUpSecurityContext(1L);
        when(memberRepository.findByUserId(1L)).thenReturn(List.of(member));
        when(groupRepository.findAllById(List.of(1L))).thenReturn(List.of(group));

        List<Group> result = groupService.getMyGroups(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(group);
    }

    @Test
    void saveGroup_Success() {
        setUpSecurityContext(1L);
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
        setUpSecurityContext(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.saveGroup(group, 99L))
                .isInstanceOf(UserNotFoundException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void updateGroup_Success() {
        setUpSecurityContext(1L);
        Group updatedInfo = new Group();
        updatedInfo.setName("Updated Name");
        updatedInfo.setDescription("Updated description");

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        Group result = groupService.updateGroup(1L, updatedInfo, 1L);

        assertThat(group.getName()).isEqualTo("Updated Name");
        assertThat(group.getDescription()).isEqualTo("Updated description");
        verify(groupRepository).save(group);
    }

    @Test
    void updateGroup_ThrowsGroupNotFoundException() {
        setUpSecurityContext(1L);
        when(memberRepository.findByUserIdAndGroupId(1L, 99L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.updateGroup(99L, group, 1L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void updateGroup_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        assertThatThrownBy(() -> groupService.updateGroup(1L, group, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void updateGroup_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        assertThatThrownBy(() -> groupService.updateGroup(1L, group, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void updateGroup_ThrowsMemberNotFoundException() {
        setUpSecurityContext(1L);
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.updateGroup(1L, group, 1L))
                .isInstanceOf(MemberNotFoundException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    // ---- deleteGroup ----

    @Test
    void deleteGroup_Success() {
        setUpSecurityContext(1L);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));

        groupService.deleteGroup(1L, 1L);

        verify(groupRepository).delete(group);
    }

    @Test
    void deleteGroup_ThrowsGroupNotFoundException() {
        setUpSecurityContext(1L);
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.deleteGroup(99L, 1L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void deleteGroup_ThrowsMemberNotFoundException() {
        setUpSecurityContext(1L);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.deleteGroup(1L, 1L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void deleteGroup_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        assertThatThrownBy(() -> groupService.deleteGroup(1L, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(groupRepository, never()).delete(any(Group.class));
    }

    @Test
    void deleteGroup_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        assertThatThrownBy(() -> groupService.deleteGroup(1L, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(groupRepository, never()).delete(any(Group.class));
    }

    // ---- addMember ----

    @Test
    void addMember_Success() {
        setUpSecurityContext(1L);
        User newUser = new User();
        newUser.setId(2L);
        newUser.setName("Jane Doe");

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(memberRepository.existsByGroupIdAndUserId(1L, 2L)).thenReturn(false);
        when(memberRepository.save(any(GroupMember.class))).thenReturn(member);

        GroupMember result = groupService.addMember(1L, 2L, GroupRole.MEMBER, 1L);

        assertThat(result).isNotNull();
        verify(memberRepository).save(any(GroupMember.class));
    }

    @Test
    void addMember_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        assertThatThrownBy(() -> groupService.addMember(1L, 3L, GroupRole.MEMBER, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(memberRepository, never()).save(any(GroupMember.class));
    }

    @Test
    void addMember_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        assertThatThrownBy(() -> groupService.addMember(1L, 2L, GroupRole.MEMBER, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(memberRepository, never()).save(any(GroupMember.class));
    }

    @Test
    void addMember_ThrowsGroupNotFoundException() {
        setUpSecurityContext(1L);
        when(memberRepository.findByUserIdAndGroupId(1L, 99L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.addMember(99L, 2L, GroupRole.MEMBER, 1L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void addMember_ThrowsUserNotFoundException() {
        setUpSecurityContext(1L);
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.addMember(1L, 99L, GroupRole.MEMBER, 1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addMember_ThrowsUserAlreadyMemberException() {
        setUpSecurityContext(1L);
        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(memberRepository.existsByGroupIdAndUserId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> groupService.addMember(1L, 1L, GroupRole.MEMBER, 1L))
                .isInstanceOf(UserAlreadyMemberException.class);

        verify(memberRepository, never()).save(any(GroupMember.class));
    }

    // ---- removeMember ----

    @Test
    void removeMember_Success() {
        setUpSecurityContext(1L);
        User targetUser = new User();
        targetUser.setId(2L);
        GroupMember targetMember = new GroupMember();
        targetMember.setId(2L);
        targetMember.setGroup(group);
        targetMember.setUser(targetUser);
        targetMember.setRole(GroupRole.MEMBER);

        group.setMembers(new ArrayList<>(List.of(member, targetMember)));

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(targetMember));

        groupService.removeMember(1L, 2L, 1L);

        assertThat(group.getMembers()).doesNotContain(targetMember);
    }

    @Test
    void removeMember_ThrowsUnauthorizedWhenNotAdmin() {
        setUpSecurityContext(2L);
        GroupMember regularMember = new GroupMember();
        regularMember.setRole(GroupRole.MEMBER);

        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(regularMember));

        assertThatThrownBy(() -> groupService.removeMember(1L, 3L, 2L))
                .isInstanceOf(UnauthorizedGroupActionException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void removeMember_ThrowsAccessDeniedWhenUserIdMismatch() {
        setUpSecurityContext(10L);

        assertThatThrownBy(() -> groupService.removeMember(1L, 2L, 1L))
                .isInstanceOf(AccessDeniedException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void removeMember_ThrowsLastGroupMemberException() {
        setUpSecurityContext(1L);
        group.setMembers(new ArrayList<>(List.of(member)));

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> groupService.removeMember(1L, 1L, 1L))
                .isInstanceOf(LastGroupMemberException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void removeMember_ThrowsLastGroupAdminException() {
        setUpSecurityContext(1L);
        User targetUser = new User();
        targetUser.setId(2L);
        GroupMember targetAdmin = new GroupMember();
        targetAdmin.setId(2L);
        targetAdmin.setGroup(group);
        targetAdmin.setUser(targetUser);
        targetAdmin.setRole(GroupRole.ADMIN);

        group.setMembers(new ArrayList<>(List.of(member, targetAdmin)));

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.findByUserIdAndGroupId(2L, 1L)).thenReturn(Optional.of(targetAdmin));
        when(memberRepository.countByGroupIdAndRole(1L, GroupRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> groupService.removeMember(1L, 2L, 1L))
                .isInstanceOf(LastGroupAdminException.class);

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void removeMember_ThrowsMemberNotFoundException() {
        setUpSecurityContext(1L);
        group.setMembers(new ArrayList<>(List.of(member, new GroupMember())));

        when(memberRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(member));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.findByUserIdAndGroupId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.removeMember(1L, 99L, 1L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    // ---- getMembers ----

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
