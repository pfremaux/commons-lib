package commons.lib.tooling.java.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClassBlock extends CodeElement {

    private final Map<String, ClassDefinition> classDirectlyReferenced = new HashMap<>();
    private final Map<String, CodeElement> blockLines = new HashMap<>();
    private final List<MethodDeclared> constructors = new ArrayList<>();
    private final Map<String, MethodDeclared> blockMethods = new HashMap<>();

    public ClassBlock(CodeElement parent) {
        super(parent);
    }

    public Block declarePrivateMethod(ClassDefinition c, ClassDefinition returnType, String name, List<ObjectDeclaration> parameters) {
        classDirectlyReferenced.put(c.getName(), c);
        MethodDeclared methodDeclared = new MethodDeclared(this, true, Scope.PUBLIC, null, name, parameters, new ArrayList<>());
        blockMethods.put(methodDeclared.getName(), methodDeclared);
        return methodDeclared;
    }


    public Block declarePublicStaticMethod(ClassDefinition returnType, String name, List<ObjectDeclaration> parameters) {
        classDirectlyReferenced.put(returnType.getName(), returnType);
        MethodDeclared methodDeclared = new MethodDeclared(this, true, Scope.PUBLIC, returnType, name, parameters, new ArrayList<>());
        blockMethods.put(methodDeclared.getName(), methodDeclared);
        return methodDeclared;
    }

    public Block declarePublicStaticMethod(String name, List<ObjectDeclaration> parameters) {
        MethodDeclared methodDeclared = new MethodDeclared(this, true, Scope.PUBLIC, null, name, parameters, new ArrayList<>());
        blockMethods.put(methodDeclared.getName(), methodDeclared);
        return methodDeclared;
    }

    public Block declarePublicMethod(String name, List<ObjectDeclaration> parameters) {
        MethodDeclared methodDeclared = new MethodDeclared(this, false, Scope.PUBLIC, null, name, parameters, new ArrayList<>());
        blockMethods.put(methodDeclared.getName(), methodDeclared);
        return methodDeclared;
    }

    public ClassBlock declarePrivateFinalAttribute(String className, String name) {
        final ClassDefinition c = classDirectlyReferenced.get(className);
        AttributeDeclaration attributeDeclaration = new AttributeDeclaration(this, Scope.PRIVATE, false, true, c, name, null);
        blockLines.put(attributeDeclaration.getName(), attributeDeclaration);
        return this;
    }

    public ClassBlock declarePrivateFinalAttribute(ClassDefinition c, String name) {
        classDirectlyReferenced.put(c.getName(), c);
        AttributeDeclaration attributeDeclaration = new AttributeDeclaration(this, Scope.PRIVATE, false, true, c, name, null);
        blockLines.put(attributeDeclaration.getName(), attributeDeclaration);
        return this;
    }

    public ClassBlock declarePublicStaticAttribute(ClassDefinition c, String name) {
        classDirectlyReferenced.put(c.getName(), c);
        AttributeDeclaration attributeDeclaration = new AttributeDeclaration(this, Scope.PUBLIC, true, false, c, name, null);
        blockLines.put(attributeDeclaration.getName(), attributeDeclaration);
        return this;
    }

    public ClassBlock declarePublicAttribute(ClassDefinition c, String name) {
        classDirectlyReferenced.put(c.getName(), c);
        AttributeDeclaration attributeDeclaration = new AttributeDeclaration(this, Scope.PUBLIC, false, false, c, name, null);
        blockLines.put(attributeDeclaration.getName(), attributeDeclaration);
        return this;
    }

    public ClassDefinition end() {
        return (ClassDefinition) this;
    }

    public Map<String, ClassDefinition> getClassDirectlyReferenced() {
        return classDirectlyReferenced;
    }

    public Map<String, CodeElement> getBlockLines() {
        return blockLines;
    }

    public List<MethodDeclared> getConstructors() {
        return constructors;
    }

    public Map<String, MethodDeclared> getBlockMethods() {
        return blockMethods;
    }
}
