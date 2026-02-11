package com.luisgosampaio.adventura.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public Group getGroup (Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group with id: " + id + " was not found in our records"));
    }

    @Transactional(readOnly = true)
    public List<Group> getGroups () {
        return groupRepository.findAll();
    }

    @Transactional
    public Group saveGroup (Group group) {
        return groupRepository.save(group);
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
}
