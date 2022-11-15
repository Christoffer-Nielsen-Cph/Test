package dtos;

import entities.Role;
import entities.User;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDTO {

    private String userName;
    private String userPass;
    private List<String> roleList = new ArrayList<>();

    public UserDTO(User user){
        this.userName = user.getUserName();
        this.userPass = user.getUserPass();
        this.roleList = user.getRolesAsStrings();
    }

    public User getEntity(){
        User user = new User();
        if(this.userName != null){
            user.setUserName(this.userName);
        }
        user.setUserPass(this.userPass);
        user.getRolesAsStrings();
        return user;
    }


    public static List<UserDTO> getUserDTOs(List<User> users){
        List<UserDTO> userDTOs = new ArrayList();
        users.forEach(user->userDTOs.add(new UserDTO(user)));
        return userDTOs;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPass() {
        return userPass = BCrypt.hashpw(userName,BCrypt.gensalt());
    }

    public void setUserPass(String userPass) {
        this.userPass = BCrypt.hashpw(userName,BCrypt.gensalt());
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;
        UserDTO userDTO = (UserDTO) o;
        return getUserName().equals(userDTO.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName());
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userName='" + userName + '\'' +
                ", userPass='" + userPass + '\'' +
                ", roleList=" + roleList +
                '}';
    }
}
