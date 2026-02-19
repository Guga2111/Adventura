package com.luisgosampaio.adventura.domain.group;

import com.luisgosampaio.adventura.domain.exceptions.GroupNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.LastGroupAdminException;
import com.luisgosampaio.adventura.domain.exceptions.MemberNotFoundException;
import com.luisgosampaio.adventura.domain.exceptions.LastGroupMemberException;
import com.luisgosampaio.adventura.domain.exceptions.UnauthorizedGroupActionException;
import com.luisgosampaio.adventura.domain.exceptions.UserAlreadyMemberException;
import com.luisgosampaio.adventura.domain.exceptions.UserNotFoundException;
import com.luisgosampaio.adventura.domain.user.User;
import com.luisgosampaio.adventura.domain.user.UserRepository;
import com.luisgosampaio.adventura.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Group getGroup (Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Group> getAllGroups () {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Group> getMyGroups (Long userId) {
        SecurityUtils.verifyAuthenticatedUser(userId);

        List<GroupMember> memberships = memberRepository.findByUserId(userId);

        List<Long> groupIds = memberships.stream()
                .map(gm -> gm.getGroup().getId())
                .toList();

        return groupRepository.findAllById(groupIds);

    }

    @Transactional
    public Group saveGroup (Group group, Long userId) {

        SecurityUtils.verifyAuthenticatedUser(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        group.setCreatedBy(user);
        Group savedGroup = groupRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroup(savedGroup);
        member.setUser(user);
        member.setRole(GroupRole.ADMIN);
        memberRepository.save(member);

        return savedGroup;
    }

    @Transactional
    public Group updateGroup (Long id, Group groupInfo, Long userId) {
        SecurityUtils.verifyAuthenticatedUser(userId);

        GroupMember member = memberRepository.findByUserIdAndGroupId(userId, id)
                .orElseThrow(() -> new MemberNotFoundException(id, userId));

        if (member.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(id, userId);
        }

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        group.setName(groupInfo.getName());
        group.setDescription(groupInfo.getDescription());
        group.setUpdatedAt(LocalDateTime.now());

        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup (Long id, Long userId) {
        SecurityUtils.verifyAuthenticatedUser(userId);
        Group group = groupRepository.findById(id)
                        .orElseThrow(() -> new GroupNotFoundException(id));

        GroupMember member = memberRepository.findByUserIdAndGroupId(userId, id)
                .orElseThrow(() -> new MemberNotFoundException(id, userId));

        if (member.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(id, userId);
        }

        groupRepository.delete(group);
    }

    @Transactional
    public GroupMember addMember(Long groupId, Long userId, GroupRole role, Long requestedByUserId) {
        SecurityUtils.verifyAuthenticatedUser(requestedByUserId);
        GroupMember requester = memberRepository.findByUserIdAndGroupId(requestedByUserId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, requestedByUserId));

        if (requester.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(groupId, requestedByUserId);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new UserAlreadyMemberException(groupId, userId);
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);

        return memberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long groupId, Long targetUserId, Long requestedByUserId) {
        SecurityUtils.verifyAuthenticatedUser(requestedByUserId);
        GroupMember requester = memberRepository.findByUserIdAndGroupId(requestedByUserId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, requestedByUserId));

        if (requester.getRole() != GroupRole.ADMIN) {
            throw new UnauthorizedGroupActionException(groupId, requestedByUserId);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));

        if (group.getMembers().size() <= 1) {
            throw new LastGroupMemberException(groupId);
        }

        GroupMember target = memberRepository.findByUserIdAndGroupId(targetUserId, groupId)
                .orElseThrow(() -> new MemberNotFoundException(groupId, targetUserId));

        if (target.getRole() == GroupRole.ADMIN
                && memberRepository.countByGroupIdAndRole(groupId, GroupRole.ADMIN) <= 1) {
            throw new LastGroupAdminException(groupId);
        }

        group.getMembers().remove(target);
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getMembers(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException(groupId);
        }

        return memberRepository.findByGroupId(groupId);
    }
}
