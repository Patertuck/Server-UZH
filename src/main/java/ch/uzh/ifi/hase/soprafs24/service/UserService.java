package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserToDisplayClientVersionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UsernameBirthDateDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UsernamePasswordDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(UsernamePasswordDTO userInput) {
    User newUser = new User();
    newUser.setUsername(userInput.getUsername());
    newUser.setPassword(userInput.getPassword());
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(new Date(System.currentTimeMillis()));
    newUser.setBirthDate(null);
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.info("Created Information for User: {}", newUser.getCreationDate());
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      //stopps function when this error is thrown
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the password", "are"));
    } 
  }
  
  public User checkLoginCorrect(UsernamePasswordDTO userInput) {
    
    User userByUsername = userRepository.findByUsername(userInput.getUsername());
    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername == null) {
      log.info("User doesnt exist in database");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the password", "are"));
    } 

    if(Objects.equals(userInput.getPassword().trim(), userByUsername.getPassword().trim())){
      log.info("Found User");
      return userByUsername;
    }

    log.info("No Correct password for user found. Pasword Database: {} Password client: {}", userByUsername.getPassword(), userInput.getPassword());
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the password", "are"));

  }

  public User findUserToDisplayById(long id){
    try {
      log.info("Attempting to fetch user from Id: '{}'", id);
      User user = userRepository.findById(id);
      log.info("User found: '{}'", user);
      return user;
  } catch (Exception e) {
      log.error("Error fetching user from Id: '{}'", id, e);
      // Handle the exception or rethrow it based on your requirements
      return null;
    }
  }

  public void fetchUserFromUsername(String username){
    User user = userRepository.findByUsername(username.trim().replaceAll("^\"|\"$", ""));
    user.setStatus(UserStatus.OFFLINE);
    user = userRepository.save(user);
    userRepository.flush();
  }

  public User fetchUserFromToken(String token){
    try {
      log.info("Attempting to fetch user for token: '{}'", token);
      User user = userRepository.findByToken(token.trim());
      log.info("User found: '{}'", user);
      return user;
  } catch (Exception e) {
      log.error("Error fetching user from token: '{}'", token, e);
      // Handle the exception or rethrow it based on your requirements
      return null;
    }
  }

  public void saveUserNameBirthDate(UsernameBirthDateDTO input, long id){
    User updatedUser = userRepository.findById(id);
    if (input.getInputUsername() != null){
      log.info("Updating username!");
      updatedUser.setUsername(input.getInputUsername());
     }
     
     if (input.getInputBirthDate() != null){
       Date date = Date.valueOf(input.getInputBirthDate());
       log.info("Updating BirthDate: {}", date);
      updatedUser.setBirthDate(date);
     }

    updatedUser = userRepository.save(updatedUser);
    userRepository.flush();
  }
}