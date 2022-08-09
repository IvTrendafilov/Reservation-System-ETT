package group18.eet.reservationsystem.utils;

/**
 * Hibernate interface to helps us with lazy loading problems, when casting and checking the instance of class
 */
public interface Thisable<T> {

    default boolean instanceOf(Class<? extends T> clazz) {
        return this.getClass().isAssignableFrom(clazz);
    }

    default Thisable<T> getThis() {
        return this;
    }
}
