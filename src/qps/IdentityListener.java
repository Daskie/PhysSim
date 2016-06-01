package qps;

/**
 * @since 5/29/2016
 */
public interface IdentityListener {

    void gainedHover(int id);

    void lostHover(int id);

    boolean gainedSelect(int id);

    boolean lostSelect(int id);

}
