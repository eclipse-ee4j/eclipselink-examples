package eclipselink.example.mysports.admin.model.persistence;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.factories.DescriptorCustomizer;

import eclipselink.example.mysports.admin.model.Extension;
import eclipselink.example.mysports.admin.model.HostedLeague;

public class ExtensionMappingsCustomizer implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        OneToManyMapping playerMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("playerExtensions");
        ExpressionBuilder eb = new ExpressionBuilder(Extension.class);
        Expression fkExp = eb.getField(descriptor.getMappingForAttributeName("league").getField()).equal(eb.getParameter("id"));
        Expression typeExp = eb.getField(descriptor.getMappingForAttributeName("type").getField()).equal(HostedLeague.PLAYER);
        playerMapping.setSelectionCriteria(fkExp.and(typeExp));

        OneToManyMapping teamMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("teamExtensions");
        eb = new ExpressionBuilder(Extension.class);
        fkExp = eb.getField(descriptor.getMappingForAttributeName("league").getField()).equal(eb.getParameter("id"));
        typeExp = eb.getField(descriptor.getMappingForAttributeName("type").getField()).equal(HostedLeague.TEAM);
        teamMapping.setSelectionCriteria(fkExp.and(typeExp));

        OneToManyMapping divisionMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("divisionExtensions");
        eb = new ExpressionBuilder(Extension.class);
        fkExp = eb.getField(descriptor.getMappingForAttributeName("league").getField()).equal(eb.getParameter("id"));
        typeExp = eb.getField(descriptor.getMappingForAttributeName("type").getField()).equal(HostedLeague.DIVISION);
        divisionMapping.setSelectionCriteria(fkExp.and(typeExp));
    }

}
