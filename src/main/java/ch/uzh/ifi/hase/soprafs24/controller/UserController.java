package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserClientVersionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserToDisplayClientVersionDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UsernameBirthDateDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UsernamePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserClientVersionDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserClientVersionDTO> userGetDTOs = new ArrayList<>(); 

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserClientVersionDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserClientVersionDTO registrateUser(@RequestBody UsernamePasswordDTO userPostDTO) {
    // convert API user to internal representation
    // User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    log.info("Received Registration Request form client");
    log.info(userPostDTO.getUsername());
    log.info(userPostDTO.getPassword());
    
    
    User createdUser = userService.createUser(userPostDTO);
    log.info("Succesfully created new User to database");
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserClientVersionDTO(createdUser);
  }

  @GetMapping(value = "/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserToDisplayClientVersionDTO returnUserToDisplay(@PathVariable  long id) {
    // convert API user to internal representation
    // User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    log.info("Received User Request from client");
    log.info(String.valueOf(id));
    
    User userFromId = userService.findUserToDisplayById(id);
    log.info("User found: {}", userFromId.getUsername());
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntitytoUserToDisplayClientVersionDTO(userFromId);
  }

  @PostMapping("/usersLogin")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserClientVersionDTO loginUser(@RequestBody UsernamePasswordDTO userPostDTO) {
    // convert API user to internal representation
    // User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    log.info("Received Login Request form client");
    User checkDatabaseUser = userService.checkLoginCorrect(userPostDTO);
    checkDatabaseUser.setStatus(UserStatus.ONLINE);

    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserClientVersionDTO(checkDatabaseUser);
  }

  @PostMapping("/fetchByToken")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserClientVersionDTO fetchByToken(@RequestBody String token) {
    // convert API user to internal representation
    // User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    token = token.trim().replaceAll("^\"|\"$", "");
    log.info("Received token from client: {}", token);
    User userFromToken = userService.fetchUserFromToken(token);
    log.info("User found: {}", userFromToken.getUsername());
    log.info("User password: {}", userFromToken.getPassword());

    UserClientVersionDTO x = DTOMapper.INSTANCE.convertEntityToUserClientVersionDTO(userFromToken);
    log.info("Converted boy: {}", x);
    return x;
  }

  @PostMapping("/setUserOffline")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void turnUserByUsernameOffline(@RequestBody String username) {
    log.info(username);
    userService.fetchUserFromUsername(username);
  }

  @PutMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void SaveUserNameBirthDate(@RequestBody UsernameBirthDateDTO input) {
    log.info("Saving input username: {}", input.getInputUsername());
    log.info("Saving input old username: {}", input.getCurrentUsername());
    log.info("Saving input BirthDate: {}", input.getInputBirthDate());
    
    userService.saveUserNameBirthDate(input);
  }
}
