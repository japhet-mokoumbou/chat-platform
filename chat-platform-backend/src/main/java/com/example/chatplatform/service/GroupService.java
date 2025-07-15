package com.example.chatplatform.service;

import com.example.chatplatform.dto.AddMemberRequest;
import com.example.chatplatform.dto.CreateGroupRequest;
import com.example.chatplatform.entity.Group;
import com.example.chatplatform.entity.User;
import com.example.chatplatform.repository.GroupRepository;
import com.example.chatplatform.repository.UserRepository;
import com.example.chatplatform.util.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private XmlUtils xmlUtils;

    private static final String GROUPS_XML_FILE = "groups.xml";

    @Transactional
    public Group createGroup(CreateGroupRequest request, Long creatorId) {
        // Vérifier que le créateur existe
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Créateur non trouvé"));
        // Vérifier que tous les membres existent
        Set<Long> validMembers = new HashSet<>();
        for (Long memberId : request.getMemberIds()) {
            if (!userRepository.existsById(memberId)) {
                throw new RuntimeException("Membre non trouvé: " + memberId);
            }
            validMembers.add(memberId);
        }
        // Ajouter le créateur au groupe s'il n'y est pas déjà
        validMembers.add(creatorId);
        Group group = new Group(request.getName(), creatorId, validMembers);
        Group saved = groupRepository.save(group);
        saveGroupsToXml();
        return saved;
    }

    public List<Group> listGroupsForUser(Long userId) {
        return groupRepository.findByMembersContaining(userId);
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
    }

    @Transactional
    public Group addMember(Long groupId, AddMemberRequest request, Long requesterId) {
        Group group = getGroup(groupId);
        if (!group.getCreatorId().equals(requesterId)) {
            throw new RuntimeException("Seul le créateur peut ajouter des membres");
        }
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("Utilisateur à ajouter non trouvé");
        }
        group.getMembers().add(request.getUserId());
        Group saved = groupRepository.save(group);
        saveGroupsToXml();
        return saved;
    }

    @Transactional
    public Group removeMember(Long groupId, Long userId, Long requesterId) {
        Group group = getGroup(groupId);
        if (!group.getCreatorId().equals(requesterId)) {
            throw new RuntimeException("Seul le créateur peut retirer des membres");
        }
        if (!group.getMembers().contains(userId)) {
            throw new RuntimeException("Ce membre n'est pas dans le groupe");
        }
        group.getMembers().remove(userId);
        Group saved = groupRepository.save(group);
        saveGroupsToXml();
        return saved;
    }

    @Transactional
    public void deleteGroup(Long groupId, Long requesterId) {
        Group group = getGroup(groupId);
        if (!group.getCreatorId().equals(requesterId)) {
            throw new RuntimeException("Seul le créateur peut supprimer le groupe");
        }
        groupRepository.delete(group);
        saveGroupsToXml();
    }

    private void saveGroupsToXml() {
        List<Group> allGroups = groupRepository.findAll();
        xmlUtils.saveToXml(allGroups, GROUPS_XML_FILE, Group.class);
    }
} 