package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UsernameBirthDateDTO {

  private String birthDate; 

  private String inputUsername;

  private String currentUsername;

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
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
