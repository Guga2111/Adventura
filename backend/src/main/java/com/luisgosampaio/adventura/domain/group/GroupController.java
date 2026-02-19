package com.luisgosampaio.adventura.domain.group;

import com.luisgosampaio.adventura.domain.user.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup (@PathVariable Long id) {
        return new ResponseEntity<>(groupService.getGroup(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Group>> getGroups () {
        return new ResponseEntity<>(groupService.getAllGroups(), HttpStatus.OK);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Group> saveGroup (@RequestBody Group group, @PathVariable Long userId) {
        return new ResponseEntity<>(groupService.saveGroup(group, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<Group> updateGroup (@RequestBody Group group, @PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(groupService.updateGroup(id, group, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteMapping (@PathVariable Long id, @PathVariable Long userId) {
        groupService.deleteGroup(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Group>> getMyGroups (@PathVariable Long userId) {
        return new ResponseEntity<>(groupService.getMyGroups(userId), HttpStatus.OK);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers (@PathVariable Long groupId) {
        return new ResponseEntity<>(groupService.getMembers(groupId), HttpStatus.OK);
    }

    @PostMapping("/{groupId}/user/{userId}/requested-by/{requestedByUserId}")
    public ResponseEntity<GroupMember> addMember (@PathVariable Long groupId, @PathVariable Long userId, @RequestParam GroupRole role, @PathVariable Long requestedByUserId) {
        return new ResponseEntity<>(groupService.addMember(groupId, userId, role, requestedByUserId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{groupId}/member/{targetUserId}/requested-by/{requestedByUserId}")
    public ResponseEntity<Void> removeMember (@PathVariable Long groupId, @PathVariable Long targetUserId, @PathVariable Long requestedByUserId) {
        groupService.removeMember(groupId, targetUserId, requestedByUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
