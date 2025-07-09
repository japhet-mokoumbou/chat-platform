package com.example.chatplatform.service; // Adaptez le nom du package

import com.example.chatplatform.dto.RegisterRequest; // Importez votre DTO
import com.example.chatplatform.entity.User; // Importez votre entité User
import com.example.chatplatform.repository.UserRepository; // Importez votre UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Pour le hachage du mot de passe
import org.springframework.stereotype.Service;

// Importations pour la gestion XML (nous y reviendrons)
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  // Le chemin vers le fichier users.xml et le schéma user.xsd
  // Récupérez ces valeurs de application.properties si possible, sinon utilisez
  // des constantes
  private static final String USERS_XML_PATH = "src/main/resources/data/users.xml";
  private static final String USER_XSD_PATH = "src/main/resources/schema/user.xsd";

  @Autowired
  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Enregistre un nouvel utilisateur dans la base de données et dans le fichier
   * XML.
   * 
   * @param request Les données d'inscription de l'utilisateur.
   * @return L'utilisateur sauvegardé.
   * @throws RuntimeException si le nom d'utilisateur ou l'e-mail existe déjà.
   */
  public User registerUser(RegisterRequest request) {
    // 1. Vérifier l'unicité du nom d'utilisateur et de l'e-mail (Base de données)
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new RuntimeException("Le nom d'utilisateur est déjà pris.");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("L'adresse e-mail est déjà utilisée.");
    }

    // 2. Hacher le mot de passe
    String encodedPassword = passwordEncoder.encode(request.getPassword());

    // 3. Créer l'entité User
    User newUser = new User();
    newUser.setUsername(request.getUsername());
    newUser.setEmail(request.getEmail());
    newUser.setPassword(encodedPassword); // Sauvegardez le mot de passe haché

    // 4. Sauvegarder l'utilisateur dans la base de données
    User savedUser = userRepository.save(newUser);

    // 5. Sauvegarder l'utilisateur dans le fichier XML (LOGIQUE À IMPLÉMENTER EN
    // DÉTAIL)
    try {
      saveUserToXml(savedUser);
    } catch (Exception e) {
      // Gérer l'erreur de sauvegarde XML. Vous pouvez loguer ou relancer une
      // exception spécifique.
      System.err.println(
          "Erreur lors de la sauvegarde XML de l'utilisateur " + savedUser.getUsername() + ": " + e.getMessage());
      // Optionnel: vous pouvez décider de rollback la transaction DB ici si la
      // sauvegarde XML est critique
      // throw new RuntimeException("Erreur lors de la sauvegarde XML de
      // l'utilisateur.", e);
    }

    return savedUser;
  }

  /**
   * Logique pour sauvegarder un utilisateur dans le fichier users.xml.
   * 
   * @param user L'utilisateur à sauvegarder.
   * @throws Exception En cas d'erreur de parsing XML ou d'écriture.
   */
  private void saveUserToXml(User user) throws Exception {
    File xmlFile = new File(USERS_XML_PATH);
    Document doc;
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setNamespaceAware(true); // Important pour les schémas XSD
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

    // Vérifier si le fichier users.xml existe déjà
    if (xmlFile.exists() && xmlFile.length() > 0) {
      try (InputStream is = new FileInputStream(xmlFile)) {
        doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();
      }
    } else {
      // Créer un nouveau document XML avec l'élément racine
      doc = dBuilder.newDocument();
      Element rootElement = doc.createElement("users");
      doc.appendChild(rootElement);
    }

    // Valider le document avant de le modifier ou de le créer
    validateXmlFile(xmlFile, doc);

    // Créer l'élément utilisateur
    Element userElement = doc.createElement("user");
    doc.getDocumentElement().appendChild(userElement);

    Element username = doc.createElement("username");
    username.appendChild(doc.createTextNode(user.getUsername()));
    userElement.appendChild(username);

    Element email = doc.createElement("email");
    email.appendChild(doc.createTextNode(user.getEmail()));
    userElement.appendChild(email);

    // Le mot de passe HASHÉ ne doit PAS être stocké dans le XML à moins d'une
    // raison spécifique et sécurisée
    // Pour l'exemple, nous le mettons, mais réfléchissez à la sécurité de cela.
    Element password = doc.createElement("password");
    password.appendChild(doc.createTextNode(user.getPassword())); // Le mot de passe haché
    userElement.appendChild(password);

    // Écrire le contenu modifié dans le fichier XML
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes"); // Pour une meilleure lisibilité

    try (OutputStream os = new FileOutputStream(xmlFile)) {
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(os);
      transformer.transform(source, result);
    }

    // Valider le document après modification
    validateXmlFile(xmlFile, doc);
  }

  /**
   * Valide un document XML contre le schéma XSD.
   * 
   * @param xmlFile Le fichier XML (peut être non existant si c'est la première
   *                création)
   * @param doc     Le document DOM à valider.
   * @throws Exception Si la validation échoue.
   */
  private void validateXmlFile(File xmlFile, Document doc) throws Exception {
    File xsdFile = new File(USER_XSD_PATH);
    if (!xsdFile.exists()) {
      System.err.println("Attention: Schéma XSD non trouvé à l'emplacement: " + USER_XSD_PATH);
      // Si le schéma n'existe pas, nous ne pouvons pas valider. Vous pouvez choisir
      // de jeter une exception
      // ou de continuer sans validation. Pour le moment, nous continuerons mais avec
      // un avertissement.
      return;
    }

    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(xsdFile);
    Validator validator = schema.newValidator();

    // Si le fichier XML n'existe pas encore ou est vide, la validation initiale
    // peut échouer.
    // On valide le document DOM après qu'il a été mis à jour.
    validator.validate(new DOMSource(doc));
    System.out.println("Validation XML réussie contre le schéma.");
  }
}