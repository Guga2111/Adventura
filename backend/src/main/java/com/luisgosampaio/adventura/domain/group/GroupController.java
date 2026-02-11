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

    @PostMapping("/{userId}")
    public ResponseEntity<Group> saveGroup (@RequestBody Group group, @PathVariable Long userId) {
        return new ResponseEntity<>(groupService.saveGroup(group, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup (@RequestBody Group group, @PathVariable Long id) {
        return new ResponseEntity<>(groupService.updateGroup(id, group), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMapping (@PathVariable Long id) {
        groupService.deleteGroup(id);
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

    @PostMapping("/{groupId}/user/{userId}")
    public ResponseEntity<GroupMember> addMember (@PathVariable Long groupId, @PathVariable Long userId, @RequestParam GroupRole role) {
        return new ResponseEntity<>(groupService.addMember(groupId, userId, role), HttpStatus.CREATED);
    }

    @DeleteMapping("/{groupId}/member/{memberId}")
    public ResponseEntity<Void> removeMember (@PathVariable Long groupId, @PathVariable Long memberId) {
        groupService.removeMember(groupId, memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
