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
import org.eclipse.persistence.internal.libraries.asm.*;
import org.eclipse.persistence.internal.libraries.asm.commons.EmptyVisitor;

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
    public byte[] writeClass(DynamicClassLoader loader, String className)
        throws ClassNotFoundException {
        return super.writeClass(loader, className);
        /*
        ClassReader cr = new ClassReader(originalClassBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cr.accept(new EditionClassAdapter(new EmptyVisitor()), ClassReader.SKIP_DEBUG);
        return cw.toByteArray();
        */
    }

    @Override
    protected DynamicClassWriter createCopy(Class<?> parentClass) {
        return new EditionClassWriter(parentClass);
    }

    class EditionClassAdapter extends ClassAdapter implements Opcodes {

        public EditionClassAdapter(ClassVisitor cv) {
            super(cv);
            // TODO Auto-generated constructor stub
        }
        
    }
}
