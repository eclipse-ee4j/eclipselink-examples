package org.eclipse.persistence.dynamic;

import java.lang.reflect.Method;

public class ReflectiveDynamicClassLoader extends DynamicClassLoader {

    private Method defineClassMethod;

    public ReflectiveDynamicClassLoader(ClassLoader delegate) {
        super(delegate);
    }

    protected Method getDefineClassMethod() {
        if (this.defineClassMethod == null) {
            try {
                this.defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", new Class<?>[] {String.class, byte[].class, int.class, int.class});
                this.defineClassMethod.setAccessible(true);
            } catch (Exception e) {
               throw new RuntimeException("ReflectiveDynamicClassLoader could not access defineClass method", e);
            }
        }
        return this.defineClassMethod;
    }

    @Override
    protected Class<?> defineDynamicClass(String name, byte[] b) {
        try {
            return (Class<?>) getDefineClassMethod().invoke(getParent(), new Object[] { name, b , 0, b.length});
        } catch (Exception e) {
            throw new RuntimeException("ReflectiveDynamicClassLoader falied to create class: " + name, e);
        }
    }

}
