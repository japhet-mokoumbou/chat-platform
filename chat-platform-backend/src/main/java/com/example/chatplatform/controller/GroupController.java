package com.example.chatplatform.controller;

import com.example.chatplatform.dto.AddMemberRequest;
import com.example.chatplatform.dto.CreateGroupRequest;
import com.example.chatplatform.entity.Group;
import com.example.chatplatform.service.GroupService;
import com.example.chatplatform.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody CreateGroupRequest request,
                                         @RequestHeader("Authorization") String token) {
        try {
            Long creatorId = jwtUtil.extractUserId(token.substring(7));
            Group group = groupService.createGroup(request, creatorId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Groupe créé avec succès");
            response.put("group", group);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> listGroups(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractUserId(token.substring(7));
            List<Group> groups = groupService.listGroupsForUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("groups", groups);
            response.put("count", groups.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable Long groupId,
                                      @RequestHeader("Authorization") String token) {
        try {
            Group group = groupService.getGroup(groupId);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMember(@PathVariable Long groupId,
                                       @Valid @RequestBody AddMemberRequest request,
                                       @RequestHeader("Authorization") String token) {
        try {
            Long requesterId = jwtUtil.extractUserId(token.substring(7));
            Group group = groupService.addMember(groupId, request, requesterId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Membre ajouté avec succès");
            response.put("group", group);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long groupId,
                                          @PathVariable Long userId,
                                          @RequestHeader("Authorization") String token) {
        try {
            Long requesterId = jwtUtil.extractUserId(token.substring(7));
            Group group = groupService.removeMember(groupId, userId, requesterId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Membre retiré avec succès");
            response.put("group", group);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId,
                                         @RequestHeader("Authorization") String token) {
        try {
            Long requesterId = jwtUtil.extractUserId(token.substring(7));
            groupService.deleteGroup(groupId, requesterId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Groupe supprimé avec succès");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
} 