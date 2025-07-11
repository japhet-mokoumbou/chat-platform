package com.example.chatplatform.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlUtils {
    @Value("${xml.storage.path}")
    private String xmlStoragePath;

    /**
     * Sérialise une liste d'objets en XML
     */
    public <T> void saveToXml(List<T> objects, String filename, Class<T> clazz) {
        try {
            System.out.println("=== XMLUTILS SAUVEGARDE ===");
            System.out.println("Chemin de stockage XML: " + xmlStoragePath);
            System.out.println("Nom du fichier: " + filename);
            System.out.println("Nombre d'objets: " + objects.size());

            // Créer le répertoire s'il n'existe pas
            Path directory = Paths.get(xmlStoragePath);
            System.out.println("Répertoire cible: " + directory.toAbsolutePath());
            if (!Files.exists(directory)) {
                System.out.println("Création du répertoire: " + directory);
                Files.createDirectories(directory);
            } else {
                System.out.println("Le répertoire existe déjà");
            }

            // --- CORRECTION ICI ---
            // JAXBContext doit connaître à la fois le wrapper et la classe des objets contenus
            JAXBContext context = JAXBContext.newInstance(XmlListWrapper.class, clazz);
            // --- FIN CORRECTION ---

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // Bonne pratique

            // Créer un wrapper pour la liste
            XmlListWrapper<T> wrapper = new XmlListWrapper<>(objects);

            // Sauvegarder dans le fichier
            File file = new File(xmlStoragePath + filename);
            try (FileWriter writer = new FileWriter(file)) {
                marshaller.marshal(wrapper, writer);
            }
            System.out.println("Sauvegarde XML réussie pour " + filename);

        } catch (JAXBException | IOException e) {
            System.err.println("Erreur lors de la sauvegarde XML: " + e.getMessage());
            e.printStackTrace(); // Affiche la pile d'appels pour un débogage plus facile
            throw new RuntimeException("Erreur lors de la sauvegarde XML: " + e.getMessage(), e);
        }
    }

    /**
     * Désérialise un fichier XML en liste d'objets
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> loadFromXml(String filename, Class<T> clazz) {
        try {
            File file = new File(xmlStoragePath + filename);
            if (!file.exists() || file.length() == 0) { // Vérifie aussi si le fichier est vide
                System.out.println("Fichier XML non trouvé ou vide: " + filename);
                return new ArrayList<>();
            }

            // --- CORRECTION ICI ---
            // JAXBContext doit connaître à la fois le wrapper et la classe des objets contenus
            JAXBContext context = JAXBContext.newInstance(XmlListWrapper.class, clazz);
            // --- FIN CORRECTION ---

            Unmarshaller unmarshaller = context.createUnmarshaller();

            XmlListWrapper<T> wrapper = (XmlListWrapper<T>) unmarshaller.unmarshal(file);
            System.out.println("Chargement XML réussi pour " + filename);
            return wrapper.getItems() != null ? wrapper.getItems() : new ArrayList<>();

        } catch (JAXBException e) {
            System.err.println("Erreur lors du chargement XML: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement XML: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie si un fichier XML existe
     */
    public boolean xmlFileExists(String filename) {
        File file = new File(xmlStoragePath + filename);
        return file.exists();
    }
}