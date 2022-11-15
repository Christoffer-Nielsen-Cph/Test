package dtos;

import entities.Role;
import entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoleDTO {
    private String roleName;
    private List<UserDTO> userList = new ArrayList<>();

    public RoleDTO(Role role){
        if(role.getRoleName() != null){
            this.roleName = role.getRoleName();
        }
    }
    public Role getEntity(){
        Role r = new Role(this.roleName);
        this.userList.forEach(user->r.addUser(user.getEntity()));
        return r;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UserDTO> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDTO> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "roleName='" + roleName + '\'' +
                ", userList=" + userList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleDTO)) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return getRoleName().equals(roleDTO.getRoleName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleName());
    }
}
