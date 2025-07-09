package com.example.chatplatform.service;

import com.example.chatplatform.entity.User;
import com.example.chatplatform.util.XmlUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBException;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String XML_FILE_PATH = "src/main/resources/Users.xml";

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        try {
            List<User> userList = XmlUtils.fromXmlList(XML_FILE_PATH);
            User foundUser = userList.stream()
                    .filter(u -> u.getUsername().equals(usernameOrEmail) || u.getEmail().equals(usernameOrEmail))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

            return new org.springframework.security.core.userdetails.User(
                    foundUser.getUsername(),
                    foundUser.getPassword(),
                    java.util.Collections.emptyList() // Ajustez les autorités/roles si nécessaire
            );
        } catch (JAXBException e) {
            throw new UsernameNotFoundException("Error loading users from XML: " + e.getMessage());
        }
    }
}