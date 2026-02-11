package com.luisgosampaio.adventura.domain.group;

import com.luisgosampaio.adventura.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupMemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupMemberRepository memberRepository;

    private User user;
    private User otherUser;
    private Group group;
    private Group otherGroup;
    private GroupMember member;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        entityManager.persistAndFlush(user);

        otherUser = new User();
        otherUser.setName("Jane Doe");
        otherUser.setEmail("jane@example.com");
        otherUser.setPassword("password456");
        entityManager.persistAndFlush(otherUser);

        group = new Group();
        group.setName("Travel Group");
        group.setDescription("A travel group");
        group.setCreatedBy(user);
        entityManager.persistAndFlush(group);

        otherGroup = new Group();
        otherGroup.setName("Other Group");
        otherGroup.setDescription("Another group");
        otherGroup.setCreatedBy(otherUser);
        entityManager.persistAndFlush(otherGroup);

        member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupRole.ADMIN);
        entityManager.persistAndFlush(member);
    }

    @Test
    void findByUserIdAndGroupId_ReturnsMember() {
        Optional<GroupMember> found = memberRepository.findByUserIdAndGroupId(user.getId(), group.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getRole()).isEqualTo(GroupRole.ADMIN);
    }

    @Test
    void findByUserIdAndGroupId_ReturnsEmpty_WhenNotFound() {
        Optional<GroupMember> found = memberRepository.findByUserIdAndGroupId(otherUser.getId(), group.getId());

        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_ReturnsList() {
        List<GroupMember> members = memberRepository.findByUserId(user.getId());

        assertThat(members).hasSize(1);
        assertThat(members.get(0).getGroup().getName()).isEqualTo("Travel Group");
    }

    @Test
    void findByUserId_ReturnsEmpty_WhenNoMemberships() {
        List<GroupMember> members = memberRepository.findByUserId(otherUser.getId());

        assertThat(members).isEmpty();
    }

    @Test
    void findByGroupId_ReturnsList() {
        List<GroupMember> members = memberRepository.findByGroupId(group.getId());

        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUser().getName()).isEqualTo("John Doe");
    }

    @Test
    void findByGroupId_ReturnsEmpty_WhenNoMembers() {
        List<GroupMember> members = memberRepository.findByGroupId(otherGroup.getId());

        assertThat(members).isEmpty();
    }

    @Test
    void existsByGroupIdAndUserId_ReturnsTrue() {
        boolean exists = memberRepository.existsByGroupIdAndUserId(group.getId(), user.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByGroupIdAndUserId_ReturnsFalse() {
        boolean exists = memberRepository.existsByGroupIdAndUserId(group.getId(), otherUser.getId());

        assertThat(exists).isFalse();
    }
}