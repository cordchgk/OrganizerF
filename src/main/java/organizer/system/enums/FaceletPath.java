package organizer.system.enums;

import organizer.system.Messages;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;


/**
 * Created by cord on 12.07.2016.
 */
public enum FaceletPath {
    @NonNls INDEX("/index.xhtml"),
    @NonNls LOGIN("/login.xhtml"),
    @NonNls PASSWORD_FORGOTTEN("/password-forgotten.xhtml"),
    @NonNls RESET_PASSWORD("/reset-password.xhtml"),
    @NonNls SIGNUP("/signup.xhtml"),
    @NonNls EDIT_USER("/user/edit-user.xhtml"),
    @NonNls USER_INDEX("/user/index.xhtml"),
    @NonNls PROFILE("/user/products.xhtml"),
    @NonNls PRODUCTS("/product/products.xhtml"),
    @NonNls GROUPS("/group/groups.xhtml"),
    @NonNls GROUPMANAGER("/group/groupmanager.xhtml"),
    @NonNls VERIFICATION("/verification.xhtml"),
    @NonNls SETTINGS("/user/restaurant.xhtml"),
    @NonNls SMTP_SETTINGS("/user/smtp-settings.xhtml"),
    @NonNls NOT_FOUND("/info/404.xhtml"),
    @NonNls ABOUT("/info/about.xhtml"),
    @NonNls CONTACT("/info/contact.xhtml"),
    @NonNls FAQ_PAGE("/info/faq.xhtml"),
    @NonNls PRIVACY("/info/privacy.xhtml"),
    @NonNls TERMS("/info/terms.xhtml");
    @NonNls private static final String FACES_REDIRECT_TRUE =
            "?faces-redirect=true";
    /**
     * Represents the path to the facelet.
     */
    @NonNls private final String path;

    /**
     * Constructor for this enum.
     *
     * @param path the path to the facelet without the context path
     */
    FaceletPath(@NonNls final String path) {
        this.path = path;
    }

    /**
     * Returns the regarding FaceletPath object from the textual represenation.
     *
     * @param text the textual path
     * @return the regarding enum object
     */
    public static FaceletPath fromString(@Nullable final String text) {
        if (text != null) {
            for (final FaceletPath faceletPath : FaceletPath.values()) {
                if (text.equalsIgnoreCase(faceletPath.getPath())) {
                    return faceletPath;
                }
            }
        }
        final String notFoundMsg =
                Messages.getFormattedString("ENUM.NOT_FOUND", "FaceletPath",
                        text);
        throw new IllegalArgumentException(notFoundMsg);
    }

    /**
     * Checks if a enum with the provided {@code path} exists.
     *
     * @param path the path to check
     * @return {@code true} if an enum for the path exists, otherwise {@code
     * false}
     */
    public static boolean contains(@Nullable String path) {
        if (path != null) {
            for (final FaceletPath faceletPath : FaceletPath.values()) {
                if (path.equalsIgnoreCase(faceletPath.getPath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Getter for {@link FaceletPath#path}.
     *
     * @return the path to the facelet
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for {@link FaceletPath#path} with JSF redirection annoatation.
     *
     * @return the path to the facelet to redirect to
     */
    public String getRedirectionPath() {
        return this.path + FACES_REDIRECT_TRUE;
    }

    @Override
    public String toString() {
        final String sb = "FaceletPath{" + "path='" + this.path + '\'' +
                '}';
        return sb;
    }



}