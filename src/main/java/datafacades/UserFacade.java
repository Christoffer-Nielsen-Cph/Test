package datafacades;

import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import errorhandling.API_Exception;
import errorhandling.NotFoundException;
import security.errorhandling.AuthenticationException;

import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User createUser(User user) throws API_Exception {
        EntityManager em = getEntityManager();
        user.addRole(new Role("user"));
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e){
            throw new API_Exception("There's already a user with the username: "+user.getUserName()+" in the system!");
        }
        finally {
            em.close();
        }
        return user;
    }
    public User updateUser(User user) {
        EntityManager em = getEntityManager();
        User updatedUser = em.find(User.class,user.getUserName());
        try {
            em.getTransaction().begin();
            updatedUser.setUserName(user.getUserName());
            updatedUser.setUserPass(user.getUserPass());
            updatedUser.setRoleList(user.getRoleList());
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return updatedUser;
    }
    public User getUserByUserName(String userName) throws NotFoundException {
        EntityManager em = getEntityManager();
        User u = em.find(User.class, userName);
        if(u == null){
            throw new NotFoundException("Can't find a user with the username: "+userName);
        }
        return u;

    }
    public List<User> getAllUsers() throws NotFoundException {
        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u",User.class);
        if(query == null){
            throw new NotFoundException("Can't find any users in the system");
        }
        return query.getResultList();
    }

    public User deleteUser(String userName) throws NotFoundException {
        EntityManager em = getEntityManager();
        User user = em.find(User.class, userName);
        if(user == null){
            throw new NotFoundException("Can't find a user with the username: "+userName);
        }
        try{
            em.getTransaction().begin();
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return user;
    }
}