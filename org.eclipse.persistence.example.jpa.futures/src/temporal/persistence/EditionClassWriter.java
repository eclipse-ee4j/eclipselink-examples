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

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;

/**
 * TODO
 *
 * @author dclarke
 * @since EclipseLink 2.3.1
 */
public class EditionClassWriter extends DynamicClassWriter {

    public EditionClassWriter(Class<?> parentClass) {
        super(parentClass);
    }

    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {
        return super.writeClass(loader, className);
        /*
        EnumInfo enumInfo = loader.enumInfoRegistry.get(className);
        if (enumInfo != null) {
            return createEnum(enumInfo);
        }

        Class<?> parent = getParentClass(loader);
        parentClassName = parent.getName();
        if (parent == null || parent.isPrimitive() || parent.isArray() || parent.isEnum() || parent.isInterface() || Modifier.isFinal(parent.getModifiers())) {
            throw new IllegalArgumentException("Invalid parent class: " + parent);
        }
        String classNameAsSlashes = className.replace('.', '/');
        String parentClassNameAsSlashes = parentClassName.replace('.', '/');

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // public class Foo extends DynamicEntityImpl {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, null, parentClassNameAsSlashes, null);

        // public static DynamicPropertiesManager DPM = new
        // DynamicPropertiesManager();
        cw.visitField(ACC_PUBLIC + ACC_STATIC, PROPERTIES_MANAGER_FIELD, "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        MethodVisitor mv = cw.visitMethod(ACC_STATIC, CLINIT, "()V", null, null);
        mv.visitTypeInsn(NEW, DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES, INIT, "()V");
        mv.visitFieldInsn(PUTSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);

        // public Foo() {
        // super();
        // }
        mv = cw.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, parentClassNameAsSlashes, INIT, "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);

        mv = cw.visitMethod(ACC_PUBLIC, "fetchPropertiesManager", "()L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";", null, null);
        mv.visitFieldInsn(GETSTATIC, classNameAsSlashes, PROPERTIES_MANAGER_FIELD, "L" + DYNAMIC_PROPERTIES_MANAGER_CLASSNAME_SLASHES + ";");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);

        cw.visitEnd();
        return cw.toByteArray();
*/
    }

    @Override
    protected DynamicClassWriter createCopy(Class<?> parentClass) {
        return new EditionClassWriter(parentClass);
    }

}
