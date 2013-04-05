package example.asm;

import org.eclipse.persistence.internal.libraries.asm.util.ASMifierClassVisitor;

public class ASMifier {

    public static void main(String[] args) throws Exception {
        ASMifierClassVisitor.main(new String[] {EmpSummary.class.getName()});
    }
}
