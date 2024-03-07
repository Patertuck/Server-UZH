package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UsernameBirthDateDTO {

  private String inputBirthDate = null; 

  private String inputUsername = null;

  private String currentUsername;

  public String getInputBirthDate() {
    return inputBirthDate;
  }

  public void setInputBirthDate(String inputBirthDate) {
    this.inputBirthDate = inputBirthDate;
  }

  public String getInputUsername() {
    return inputUsername;
  }

  public void setInputUsername(String inputUsername) {
    this.inputUsername = inputUsername;
  }

  public String getCurrentUsername() {
    return currentUsername;
  }
}
