package com.luisgosampaio.adventura.domain.group;

import com.luisgosampaio.adventura.domain.user.User;
import com.luisgosampaio.adventura.domain.user.UserRepository;
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
                .orElseThrow(() -> new RuntimeException("Group with id: " + id + " was not found in our records"));
    }

    @Transactional(readOnly = true)
    public List<Group> getGroups () {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Group> getMyGroups (Long userId) {
        List<GroupMember> memberships = memberRepository.findByUserId(userId);

        List<Long> groupIds = memberships.stream()
                .map(gm -> gm.getGroup().getId())
                .toList();

        return groupRepository.findAllById(groupIds);

    }

    @Transactional
    public Group saveGroup (Group group, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
    public Group updateGroup (Long id, Group groupInfo) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group with id: " + id + " was not found in our records"));

        group.setName(groupInfo.getName());
        group.setDescription(groupInfo.getDescription());
        group.setUpdatedAt(LocalDateTime.now());

        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup (Long id) {

        Group group = groupRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Group with id: " + id + " was not found in our records"));
        groupRepository.delete(group);
    }

    @Transactional
    public GroupMember addMember(Long groupId, Long userId, GroupRole role) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("User already member");
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);

        return memberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long groupId, Long userId) {
        GroupMember member = memberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<GroupMember> getMembers(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new RuntimeException("Group not found");
        }

        return memberRepository.findByGroupId(groupId);
    }
}
