package com.luisgosampaio.adventura.domain.group;

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
        return new ResponseEntity<>(groupService.getGroups(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Group> saveGroup (@RequestBody Group group) {
        return new ResponseEntity<>(groupService.saveGroup(group), HttpStatus.CREATED);
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
}
