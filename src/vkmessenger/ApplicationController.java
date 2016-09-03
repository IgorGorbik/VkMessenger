package vkmessenger;

import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import user.User;
import user.UserUtils;

/**
 *
 * @author Игорь
 */
public class ApplicationController {

    private String access;
    private User own;
    private DefaultComboBoxModel boxModel;
    private List<User> list;

    public ApplicationController(String s) throws Exception {
        this.access = s;
        own = UserUtils.getAppUser(access);
        list = own.getFriends(access);
        Vector v = new Vector(getList());
        boxModel = new DefaultComboBoxModel(v);
    }

    /**
     * @return the boxModel
     */
    public DefaultComboBoxModel getBoxModel() {
        return boxModel;
    }

    /**
     * @return the access
     */
    public String getAccess() {
        return access;
    }

    /**
     * @return the list
     */
    public List<User> getList() {
        return list;
    }

    /**
     * @return the own
     */
    public User getOwn() {
        return own;
    }

}
