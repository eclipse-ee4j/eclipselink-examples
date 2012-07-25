/*******************************************************************************
 * Copyright (c) 2010-2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - TODO
 ******************************************************************************/
package example.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.FieldVisitor;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * Example {@link SessionCustomizer} which identifies constructor queries where
 * the class does not exist and create it dynamically.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class DynamicCustomizer implements SessionCustomizer {

    @Override
    public void customize(Session session) throws Exception {
        DynamicClassLoader dcl = (DynamicClassLoader) session.getProperty(PersistenceUnitProperties.CLASSLOADER);

        for (DatabaseQuery q : session.getJPAQueries()) {
            ConstructorExpression constructor = getConstructor(((JPAQuery) q).getJPQLString());

            if (constructor != null) {
                try {
                    dcl.loadClass(constructor.getClassName());
                } catch (ClassNotFoundException cnfe) {
                    dcl.addClass(constructor.getClassName(), createWriter(session, constructor));
                }
            }
        }
    }

    private ConstructorExpression getConstructor(String jpql) {
        JPQLGrammar grammar = DefaultEclipseLinkJPQLGrammar.instance();
        JPQLExpression jpqlExpression = new JPQLExpression(jpql, grammar, false);

        Expression queryStatement = jpqlExpression.getQueryStatement();
        if (queryStatement instanceof SelectStatement) {
            SelectStatement select = (SelectStatement) queryStatement;
            if (select.getSelectClause().getSelectExpression() instanceof ConstructorExpression) {
                return (ConstructorExpression) select.getSelectClause().getSelectExpression();
            }
        }
        return null;
    }

    /**
     * TODO: This method needs to take the items being passed to the result
     * constructor and using the FROM information and the mappings to get the
     * type of each.
     */
    private DynamicClassWriter createWriter(Session session, ConstructorExpression constructor) {
        DynamicQueryResultWriter writer = new DynamicQueryResultWriter();

        // TODO: Replace this static code with a dynamic solution using the
        // Hermes query definition and the mappings.
        writer.getAttributes().add(new QueryAttribute("id", Integer.class.getName()));
        writer.getAttributes().add(new QueryAttribute("firstName", String.class.getName()));
        writer.getAttributes().add(new QueryAttribute("lastName", String.class.getName()));
        writer.getAttributes().add(new QueryAttribute("city", String.class.getName()));

        return writer;
    }

    static class DynamicQueryResultWriter extends DynamicClassWriter implements Opcodes {

        private List<QueryAttribute> attributes;

        private static String OBJECT_TYPE = "java/lang/Object";

        DynamicQueryResultWriter() {
            super(Object.class);
            this.attributes = new ArrayList<DynamicCustomizer.QueryAttribute>();
        }

        public List<QueryAttribute> getAttributes() {
            return attributes;
        }

        @Override
        public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException {
            ClassWriter cw = new ClassWriter(0);
            FieldVisitor fv;
            MethodVisitor mv;
            AnnotationVisitor av0;
            String classNameAsSlashes = className.replace('.', '/');

            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, classNameAsSlashes, null, OBJECT_TYPE, null);
            av0 = cw.visitAnnotation(Type.getDescriptor(XmlRootElement.class), true);
            av0.visitEnd();

            for (QueryAttribute attr : getAttributes()) {
                fv = cw.visitField(ACC_PRIVATE, attr.getName(), attr.getType().getDescriptor(), null, null);
                fv.visitEnd();

            }

            // Zero Arg constructor
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, OBJECT_TYPE, "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();

            // Query Constructor
            StringWriter writer = new StringWriter();
            writer.write("(");
            for (QueryAttribute attr : getAttributes()) {
                writer.write(attr.getType().getDescriptor());
            }
            writer.write(")V");
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", writer.toString(), null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, OBJECT_TYPE, "<init>", "()V");

            int index = 1;
            for (QueryAttribute attr : getAttributes()) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, index++);
                mv.visitFieldInsn(PUTFIELD, classNameAsSlashes, attr.getName(), attr.getType().getDescriptor());
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, getAttributes().size() + 1);
            mv.visitEnd();

            for (QueryAttribute attr : getAttributes()) {
                attr.writeAccessors(cw, classNameAsSlashes);
            }
            cw.visitEnd();

            return cw.toByteArray();
        }
    }

    static class QueryAttribute implements Opcodes {
        private String name;
        private Type type;

        public QueryAttribute(String name, String className) {
            super();
            this.name = name;
            this.type = Type.getObjectType(className.replace('.', '/'));
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return this.type;
        }

        public void writeAccessors(ClassWriter cw, String classInternalName) {
            String upperAttr = getName().substring(0, 1).toUpperCase() + getName().substring(1);
            MethodVisitor mv;

            mv = cw.visitMethod(ACC_PUBLIC, "get" + upperAttr, "()" + type, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, classInternalName, getName(), getType().getDescriptor());
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();

            mv = cw.visitMethod(ACC_PUBLIC, "set" + upperAttr, "(" + type + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, classInternalName, getName(), getType().getDescriptor());
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
    }
}
