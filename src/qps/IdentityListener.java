package qps;

/**
 * @since 5/29/2016
 */
public interface IdentityListener {

    void gainedHover();

    void lostHover();

    boolean gainedSelect();

    boolean lostSelect();

}
