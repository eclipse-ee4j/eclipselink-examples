package tests.internal.asm;

import org.eclipse.persistence.internal.libraries.asm.util.ASMifierClassVisitor;

public class ASMifyPersonEdition {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ASMifierClassVisitor.main(new String[] { PersonEdition.class.getName()});
    }

}
