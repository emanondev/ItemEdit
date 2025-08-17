package emanondev.itemedit.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for common reflection operations in Java.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a declared field from a class, including private fields.
     *
     * @param clazz     the class to search
     * @param fieldName the name of the field
     * @return the Field object
     * @throws RuntimeException if the field is not found
     */
    @NotNull
    public static Field getDeclaredField(@NotNull Class<?> clazz, @NotNull String fieldName) {
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
     * @param clazz          the class to search
     * @param methodName     the name of the method
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
     * @param clazz          the class to search
     * @param methodName     the name of the method
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
     * @param obj    the object on which to invoke the method (null for static methods)
     * @param method the Method object to invoke
     * @param args   the arguments to pass to the method
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
     * @param obj        the object to invoke the method on
     * @param methodName the name of the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static Object invokeMethod(@NotNull Object obj, @NotNull String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes a method on an object, including private methods.
     *
     * @param obj        the object to invoke the method on
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
     * @param obj        the object to invoke the method on
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
     * @param obj        the object to invoke the method on
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
     * @param obj        the object to invoke the method on
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
     * @param obj            the object to invoke the method on
     * @param methodName     the name of the method
     * @param parameterTypes the parameter types of the method
     * @param args           the arguments to pass to the method
     * @return the result of the method invocation
     * @throws RuntimeException if the method cannot be invoked
     */
    @Nullable
    public static Object invokeMethod(@NotNull Object obj, @NotNull String methodName,
                                      @NotNull Class<?>[] parameterTypes,
                                      Object... args) {
        try {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes a static method of a given class.
     *
     * @param clazz      the fully qualified name of the class.
     * @param methodName the name of the method to invoke.
     * @param args       the arguments to pass to the method.
     * @return the result of the method invocation, or null if the method is void.
     * @throws RuntimeException if the method or class cannot be found or invoked.
     */
    @Nullable
    public static Object invokeStaticMethod(@NotNull Class<?> clazz, @NotNull String methodName, Object... args) {
        try {
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            Method method = clazz.getMethod(methodName, argTypes);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the value of a field on an object, including private fields.
     *
     * @param obj       the object to modify
     * @param fieldName the name of the field
     * @param value     the new value to set
     * @throws RuntimeException if the field cannot be set
     */
    public static void setFieldValue(@NotNull Object obj,
                                     @NotNull String fieldName,
                                     @Nullable Object value) {
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the value of a field from an object, including private fields.
     *
     * @param obj       the object to retrieve the value from
     * @param fieldName the name of the field
     * @return the value of the field
     * @throws RuntimeException if the field cannot be accessed
     */
    @Nullable
    public static Object getFieldValue(@NotNull Object obj,
                                       @NotNull String fieldName) {
        try {
            Field field = getDeclaredField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an instance of the specified class using its default constructor.
     *
     * @param <T>   The type of the class.
     * @param clazz The Class object representing the class to instantiate.
     * @return An instance of the specified class.
     * @throws RuntimeException if the class has no default constructor or if instantiation fails.
     */
    public static <T> T invokeConstructor(@NotNull Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke default constructor", e);
        }
    }

    /**
     * Creates an instance of the specified class using its default constructor.
     *
     * @param <T>   The type of the class.
     * @param clazz The Class object representing the class to instantiate.
     * @return An instance of the specified class.
     * @throws RuntimeException if the class has no default constructor or if instantiation fails.
     */
    public static <T, P1> T invokeConstructor(@NotNull Class<T> clazz,
                                              @NotNull Class<P1> clazzOne, @Nullable P1 paramOne) {
        return invokeConstructor(clazz,
                new Class<?>[]{clazzOne},
                new Object[]{paramOne});
    }

    /**
     * Creates an instance of the specified class using its default constructor.
     *
     * @param <T>   The type of the class.
     * @param clazz The Class object representing the class to instantiate.
     * @return An instance of the specified class.
     * @throws RuntimeException if the class has no default constructor or if instantiation fails.
     */
    public static <T, P1, P2> T invokeConstructor(@NotNull Class<T> clazz,
                                                  @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                                  @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo) {
        return invokeConstructor(clazz,
                new Class<?>[]{clazzOne, clazzTwo},
                new Object[]{paramOne, paramTwo});
    }

    /**
     * Creates an instance of the specified class using its default constructor.
     *
     * @param <T>   The type of the class.
     * @param clazz The Class object representing the class to instantiate.
     * @return An instance of the specified class.
     * @throws RuntimeException if the class has no default constructor or if instantiation fails.
     */
    public static <T, P1, P2, P3> T invokeConstructor(@NotNull Class<T> clazz,
                                                      @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                                      @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo,
                                                      @NotNull Class<P3> clazzThree, @Nullable P3 paramThree) {
        return invokeConstructor(clazz,
                new Class<?>[]{clazzOne, clazzTwo, clazzThree},
                new Object[]{paramOne, paramTwo, paramThree});
    }

    /**
     * Creates an instance of the specified class using its default constructor.
     *
     * @param <T>   The type of the class.
     * @param clazz The Class object representing the class to instantiate.
     * @return An instance of the specified class.
     * @throws RuntimeException if the class has no default constructor or if instantiation fails.
     */
    public static <T, P1, P2, P3, P4> T invokeConstructor(@NotNull Class<T> clazz,
                                                          @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                                          @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo,
                                                          @NotNull Class<P3> clazzThree, @Nullable P3 paramThree,
                                                          @NotNull Class<P4> clazzFour, @Nullable P4 paramFour) {
        return invokeConstructor(clazz,
                new Class<?>[]{clazzOne, clazzTwo, clazzThree, clazzFour},
                new Object[]{paramOne, paramTwo, paramThree, paramFour});
    }

    /**
     * Creates an instance of the specified class using its default constructor.
     *
     * @param <T>   The type of the class.
     * @param clazz The Class object representing the class to instantiate.
     * @return An instance of the specified class.
     * @throws RuntimeException if the class has no default constructor or if instantiation fails.
     */
    public static <T, P1, P2, P3, P4, P5> T invokeConstructor(@NotNull Class<T> clazz,
                                                              @NotNull Class<P1> clazzOne, @Nullable P1 paramOne,
                                                              @NotNull Class<P2> clazzTwo, @Nullable P2 paramTwo,
                                                              @NotNull Class<P3> clazzThree, @Nullable P3 paramThree,
                                                              @NotNull Class<P4> clazzFour, @Nullable P4 paramFour,
                                                              @NotNull Class<P5> clazzFive, @Nullable P5 paramFive) {
        return invokeConstructor(clazz,
                new Class<?>[]{clazzOne, clazzTwo, clazzThree, clazzFour, clazzFive},
                new Object[]{paramOne, paramTwo, paramThree, paramFour, paramFive});
    }

    /**
     * Creates an instance of the specified class using a parameterized constructor.
     *
     * @param <T>        The type of the class.
     * @param clazz      The Class object representing the class to instantiate.
     * @param paramTypes An array of Class objects representing the parameter types of the constructor.
     * @param params     An array of Objects representing the arguments to pass to the constructor.
     * @return An instance of the specified class.
     * @throws RuntimeException if the constructor is not found or if instantiation fails.
     */
    public static <T> T invokeConstructor(@NotNull Class<T> clazz,
                                          @NotNull Class<?>[] paramTypes,
                                          @Nullable Object[] params) {
        try {
            Constructor<T> constructor = clazz.getConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke parameterized constructor", e);
        }
    }

    /**
     * Checks if a class is annotated with a specific annotation.
     *
     * @param clazz           the class to check
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