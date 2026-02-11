package com.luisgosampaio.adventura.domain.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByUserIdAndGroupId(Long userId, Long groupId);

    List<GroupMember> findByUserId(Long userId);

    List<GroupMember> findByGroupId(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
