/*******************************************************************************
 * Copyright (c) 2011-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke - Bug 361016: Future Versions Examples
 ******************************************************************************/
package temporal.persistence;

import java.lang.reflect.Modifier;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

/**
 * Custom {@link DynamicClassWriter} used to creat edition class with additional
 * interface.
 * 
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionInterfaceClassWriter extends DynamicClassWriter implements Opcodes {

    public EditionInterfaceClassWriter(Class<?> parentInterface) {
        super(parentInterface);
    }

    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {

        Class<?> parent = getParentClass(loader);
        parentClassName = parent.getName();
        if (parent == null || parent.isPrimitive() || parent.isArray() || parent.isEnum() || !parent.isInterface() || Modifier.isFinal(parent.getModifiers())) {
            throw new IllegalArgumentException("Invalid parent interface: " + parent);
        }
        String classNameAsSlashes = className.replace('.', '/');
        String parentClassNameAsSlashes = parentClassName.replace('.', '/');

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // public class Foo extends DynamicEntityImpl {
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, classNameAsSlashes, null, "java/lang/Object", new String[] { parentClassNameAsSlashes });

        cw.visitEnd();
        return cw.toByteArray();

    }

    private Class<?> getParentClass(ClassLoader loader) throws ClassNotFoundException {
        if (parentClass == null && parentClassName != null) {
            parentClass = loader.loadClass(parentClassName);
        }
        return parentClass;
    }

}
