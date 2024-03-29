package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UsernameBirthDateDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UsernamePasswordDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setCreationDate(new Date(1));
    user.setBirthDate(null);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setPassword("123");
    user.setUsername("FirstName LastName");
    user.setToken("1");
    user.setStatus(UserStatus.OFFLINE);
    user.setCreationDate(new Date(1));
    user.setBirthDate(null);

    UsernamePasswordDTO userPostDTO = new UsernamePasswordDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void givenId_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setCreationDate(new Date(1));
    user.setBirthDate(null);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.findUserToDisplayById(Mockito.anyLong())).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/1");

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id", is(user.getId())))
    .andExpect(jsonPath("$.username", is(user.getUsername())))
    .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void givenId_whenPutUsers_thenReturnJsonArray() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setPassword("Firstname Lastname");
      user.setUsername("firstname lastname");
      user.setStatus(UserStatus.OFFLINE);
      user.setCreationDate(new Date(1));
      user.setBirthDate(null);

      // Mocking the UserService to define what it should return when saveUserNameBirthDate is called
       doNothing().when(userService).saveUserNameBirthDate(Mockito.any(), Mockito.anyLong());

      // when
      MockHttpServletRequestBuilder putRequest = put("/users/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"inputUsername\":\"newUsername\", \"inputBirthDate\":\"2000-01-01\", \"currentUsername\":\"currentUsername\"}");

      // then
      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent());
  }


  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"password": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}