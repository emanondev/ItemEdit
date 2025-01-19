package emanondev.itemedit.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for common reflection operations in Java.
 */
public final class ReflectionUtils {

    // Private constructor to prevent instantiation
    private ReflectionUtils() {
        throw new UnsupportedOperationException("ReflectionUtils is a utility class and cannot be instantiated.");
    }

    /**
     * Gets a declared field from a class, including private fields.
     *
     * @param clazz the class to search
     * @param fieldName the name of the field
     * @return the Field object
     * @throws RuntimeException if the field is not found
     */
    @NotNull
    public static Field getDeclaredField(@NotNull Class<?> clazz,
                                         @NotNull String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a class is present on the classpath.
     *
     * @param className the fully qualified name of the class.
     * @return true if the class is present, false otherwise.
     */
    public static boolean isClassPresent(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Gets a declared method from a class, including private methods.
     *
     * @param clazz the class to search
     * @param methodName the name of the method
     * @param parameterTypes the parameter types of the method (optional)
     * @return the Method object
     * @throws RuntimeException if the method is not found
     */
    @NotNull
    public static Method getDeclaredMethod(@NotNull Class<?> clazz,
                                           @NotNull String methodName,
                                           @Nullable Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            throw new RuntimeException("Method not found: " + methodName, e);
        }
    }

    /**
     * Gets a method from a class, including private methods.
     *
     * @param clazz the class to search
     * @param methodName the name of the method
     * @param parameterTypes the parameter types of the method (optional)
     * @return the Method object
     * @throws RuntimeException if the method is not found
     */
    @NotNull
    public static Method getMethod(@NotNull Class<?> clazz,
                                   @NotNull String methodName,
                                   @Nullable Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new RuntimeException("Method not found: " + methodName, e);
        }
    }

    /**
     * Invokes a given Method on a specified object with provided arguments.
     *
     * @param obj the object on which to invoke the method (null for static methods)
     * @param method the Method object to invoke
     * @param args the arguments to pass to the method
     * @return the result of the method invocation, or null if the method is void
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static Object invokeMethod(@Nullable Object obj,
                                      @NotNull Method method,
                                      @Nullable Object... args) {
        try {
            method.setAccessible(true); // Ensure the method is accessible
            return method.invoke(obj, args); // Invoke the method
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method: " + method.getName(), e);
        }
    }

    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj the object to invoke the method on
     * @param methodName the name of the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static Object invokeMethod(@NotNull Object obj, @NotNull String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj the object to invoke the method on
     * @param methodName the name of the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static <P1> Object invokeMethod(@NotNull Object obj, @NotNull String methodName,
                                           @NotNull Class<P1> clazzOne, @Nullable P1 paramOne) {
        return invokeMethod(obj, methodName,
                new Class[]{clazzOne},
                paramOne);
    }


    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj the object to invoke the method on
     * @param methodName the name of the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static <P1, P2> Object invokeMethod(@NotNull Object obj, @NotNull String methodName,
                                               @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                               @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo) {
        return invokeMethod(obj, methodName,
                new Class[]{clazzOne, clazzTwo},
                paramOne, paramTwo);
    }

    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj the object to invoke the method on
     * @param methodName the name of the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static <P1, P2, P3> Object invokeMethod(@NotNull Object obj, @NotNull String methodName,
                                                   @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                                   @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo,
                                                   @NotNull Class<P3> clazzThree, @Nullable P3 paramThree) {
        return invokeMethod(obj, methodName,
                new Class[]{clazzOne, clazzTwo, clazzThree},
                paramOne, paramTwo, paramThree);
    }

    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj the object to invoke the method on
     * @param methodName the name of the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static <P1, P2, P3, P4> Object invokeMethod(@NotNull Object obj, @NotNull String methodName,
                                                       @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                                       @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo,
                                                       @NotNull Class<P3> clazzThree, @Nullable P3 paramThree,
                                                       @NotNull Class<P4> clazzFour, @Nullable P4 paramFour) {
        return invokeMethod(obj, methodName,
                new Class[]{clazzOne, clazzTwo, clazzThree, clazzFour},
                paramOne, paramTwo, paramThree, paramFour);
    }

    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj the object to invoke the method on
     * @param methodName the name of the method
     * @param parameterTypes the parameter types of the method
     * @param args the arguments to pass to the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static Object invokeMethod(@NotNull Object obj, @NotNull String methodName,
                                      @NotNull Class<?>[] parameterTypes,
                                      Object... args) {
        try {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes a static method of a given class.
     *
     * @param clazz  the fully qualified name of the class.
     * @param methodName the name of the method to invoke.
     * @param args       the arguments to pass to the method.
     * @return the result of the method invocation, or null if the method is void.
     * @throws RuntimeException if the method or class cannot be found or invoked.
     */
    @Nullable
    public static Object invokeStaticMethod(@NotNull Class<?> clazz,
                                            @NotNull String methodName,
                                            Object... args) {
        try {
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            return clazz.getMethod(methodName, argTypes).invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the value of a field on an object, including private fields.
     *
     * @param obj the object to modify
     * @param fieldName the name of the field
     * @param value the new value to set
     * @throws RuntimeException if the field cannot be set
     */
    public static void setFieldValue(@NotNull Object obj,
                                     @NotNull String fieldName,
                                     @Nullable Object value) {
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the value of a field from an object, including private fields.
     *
     * @param obj the object to retrieve the value from
     * @param fieldName the name of the field
     * @return the value of the field
     * @throws RuntimeException if the field cannot be accessed
     */
    @Nullable
    public static Object getFieldValue(@NotNull Object obj,
                                       @NotNull String fieldName) {
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a class is annotated with a specific annotation.
     *
     * @param clazz the class to check
     * @param annotationClass the annotation class
     * @return true if the class is annotated with the annotation, false otherwise
     */
    public static boolean isAnnotatedWith(@NotNull Class<?> clazz,
                                          @NotNull Class<? extends Annotation> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }

    /**
     * Gets all annotations present on a class.
     *
     * @param clazz the class to search
     * @return an array of Annotations
     */
    public static @NotNull Annotation[] getClassAnnotations(@NotNull Class<?> clazz) {
        return clazz.getAnnotations();
    }
}