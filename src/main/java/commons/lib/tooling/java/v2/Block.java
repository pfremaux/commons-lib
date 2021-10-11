package commons.lib.tooling.java.v2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Block extends CodeElement {

    private final Map<String, ClassDefinition> classReferenced = new HashMap<>();
    private final Map<String, ClassDefinition> classUsed = new HashMap<>();
    private final Map<String, ObjectDeclaration> objectDirectlyReferenced = new HashMap<>();
    private final List<CodeElement> blockLines = new ArrayList<>();

    public Block(CodeElement parent) {
        super(parent);
    }


    public Block declare(ClassDefinition c, String name) {
        classReferenced.put(c.getName(), c);
        ObjectDeclaration attributeDeclaration = new ObjectDeclaration(this, c, name, (String) null);
        objectDirectlyReferenced.put(attributeDeclaration.getName(), attributeDeclaration);
        blockLines.add(attributeDeclaration);
        return this;
    }

    public Block bulkLine(String v) {
        blockLines.add(new BulkLine(this, v));
        return this;
    }

    public Block storeLastResultIn(ClassDefinition classDefinition, String name) {
        CodeElement element = blockLines.get(blockLines.size() - 1);
        ObjectDeclaration newReceiver = new ObjectDeclaration(parent, classDefinition, name);
        Assign assign = new Assign(this, newReceiver, null, element);
        objectDirectlyReferenced.put(newReceiver.getName(), newReceiver);
        blockLines.remove(blockLines.size() - 1);
        blockLines.add(assign);
        return this;
    }

    public Block storeLastResultInTry() {
        final CodeElement element = blockLines.get(blockLines.size() - 1);
        Block block = new TryBlock(this, element);
        blockLines.remove(blockLines.size() - 1);
        blockLines.add(block);
        return block;
    }

    public Block declare(ClassDefinition c, String name, ObjectUsage assignmentValue) {
        classReferenced.put(c.getName(), c);
        ObjectDeclaration attributeDeclaration = new ObjectDeclaration(this, c, name, assignmentValue);
        objectDirectlyReferenced.put(attributeDeclaration.getName(), attributeDeclaration);
        blockLines.add(attributeDeclaration);
        return this;
    }

    public Block return_(String s) {
        ObjectUsage use = null;
        ObjectDeclaration objectDeclaration = objectDirectlyReferenced.get(s);
        if (objectDeclaration == null) {
            CodeElement parent = getParent();
            if (parent instanceof ClassBlock) {
                ClassBlock classBlock = (ClassBlock) parent;
                AttributeDeclaration attribute = (AttributeDeclaration) classBlock.getBlockLines().get(s);
                if (attribute != null) {
                    use = attribute.use();
                }
            } else if (parent instanceof Block) {
                Block block = (Block) parent;
                Map<String, ObjectDeclaration> declarations = block.getBlockLines().stream().filter(line -> line instanceof ObjectDeclaration)
                        .map(line -> (ObjectDeclaration) line)
                        .collect(Collectors.toMap(ObjectDeclaration::getName, Function.identity()));
                use = declarations.get(s).use();
            }
        } else {
            use = objectDeclaration.use();
        }
        blockLines.add(new Return(this, use));
        return this;
    }

    public Block return_(ObjectUsage objectUsage) {
        blockLines.add(new Return(this, objectUsage));
        return this;
    }

    public Block try_() {
        Block block = new TryBlock(this);
        blockLines.add(block);
        return block;
    }

    public Block assign(ObjectUsage receive, CodeElement value) {
        blockLines.add(new Assign(this, null, receive, value));
        return this;
    }

    public Block assign(ObjectDeclaration receive, CodeElement value) {
        blockLines.add(new Assign(this, receive, null, value));
        return this;
    }


    public Block assign(String name, CodeElement value) {
        ObjectDeclaration objectDeclaration = objectDirectlyReferenced.get(name);
        return assign(objectDeclaration.use(), value);
    }

    public Block catch_(ClassDefinition c, String name) {
        Catch e = new Catch(this, c, name);
        blockLines.add(e);
        return e;
    }

    public Block callBetter(String o, String methodName, String... parameters) {
        final ObjectDeclaration objectDeclaration;
        final MethodDeclared methodDeclared;
        final Call call;
        List<ObjectUsage> objectDeclarations = new ArrayList<>();
        for (String parameter : parameters) {
            ObjectDeclaration declaredObject = getDeclaredObject(parameter);
            objectDeclarations.add(declaredObject.use());
        }
        if (o == null) {
            final ClassDefinition enclosingClass = getEnclosingClass();
            methodDeclared = enclosingClass.getBlockMethods().get(methodName);
            // methodDeclared = getEnclosingMethod();
            call = new Call(this, null, null, methodDeclared, null, objectDeclarations);
        } else {
            // alternative : objectDeclaration = getObjectDirectlyReferenced().get(o);
            objectDeclaration = getDeclaredObject(o);
            Map<String, MethodDeclared> blockMethods = objectDeclaration.getClassDefinition().getBlockMethods();
            methodDeclared = blockMethods.get(methodName);
            call = new Call(this, null, objectDeclaration.use(), methodDeclared, null, objectDeclarations);
        }

        getBlockLines().add(call);
        return this;
    }

    public Block call(String o, String methodName, ObjectUsage... parameters) {
        final ObjectDeclaration objectDeclaration;
        final MethodDeclared methodDeclared;
        final Call call;
        if (o == null) {
            final ClassDefinition enclosingClass = getEnclosingClass();
            methodDeclared = enclosingClass.getBlockMethods().get(methodName);
            // methodDeclared = getEnclosingMethod();
            call = new Call(this, null, null, methodDeclared, null, Arrays.asList(parameters));
        } else {
            // alternative : objectDeclaration = getObjectDirectlyReferenced().get(o);
            objectDeclaration = getDeclaredObject(o);
            Map<String, MethodDeclared> blockMethods = objectDeclaration.getClassDefinition().getBlockMethods();
            methodDeclared = blockMethods.get(methodName);
            call = new Call(this, null, objectDeclaration.use(), methodDeclared, null, Arrays.asList(parameters));
        }

        getBlockLines().add(call);
        return this;
    }


    public Block call(ObjectUsage o, MethodDeclared m, ObjectUsage... parameters) {
        getBlockLines().add(new Call(this, null, o, m, null, Arrays.asList(parameters)));
        return this;
    }

    public Block call(ClassDefinition c, String m, ObjectUsage... parameters) {
        getBlockLines().add(new Call(this, c, null, null, m, Arrays.asList(parameters)));
        return this;
    }


    public static class Catch extends Block {
        private final ClassDefinition c;
        private final String name;

        public Catch(CodeElement parent, ClassDefinition c, String name) {
            super(parent);
            this.c = c;
            this.name = name;
        }


        @Override
        public Block end() {
            return (Block) parent;
        }

        @Override
        protected void build(StringBuilder b, String baseTab, int indentationLevel) {
            b.append(baseTab);
            b.append("catch (");
            b.append(c.getName());
            b.append(" ");
            b.append(name);
            b.append(") {\n");
            getBlockLines().forEach(line -> line.build(b, indentationLevel + 1));
            b.append(baseTab);
            b.append("}\n");
        }
    }


    public abstract CodeElement end();

    public ObjectDeclaration getDeclaredObject(String name) {
        if (this instanceof MethodDeclared) {
            MethodDeclared currentMethod = (MethodDeclared) this;
            for (ObjectDeclaration parameter : currentMethod.getParameters()) {
                if (name.equals(parameter.getName())) {
                    return parameter;
                }
            }
        }
        for (CodeElement blockLine : getBlockLines()) {
            if (blockLine instanceof ObjectDeclaration) {
                ObjectDeclaration d = (ObjectDeclaration) blockLine;
                if (name.equals(d.getName())) {
                    return d;
                }
            } else if (blockLine instanceof Assign) {
                final Assign assign = (Assign) blockLine;
                final ObjectDeclaration newReceiver = assign.getNewReceiver();
                if (name.equals(newReceiver.getName())) {
                    return newReceiver;
                }
            } else if (blockLine instanceof TryBlock) {
                final TryBlock tryBlock = (TryBlock) blockLine;
                final CodeElement closeableElement = tryBlock.getCloseableElement();
                if (closeableElement instanceof Assign) {
                    final Assign assign = (Assign) closeableElement;
                    final ObjectDeclaration newReceiver = assign.getNewReceiver();
                    if (name.equals(newReceiver.getName())) {
                        return newReceiver;
                    }
                    final ObjectDeclaration declaredObject = tryBlock.getDeclaredObject(name);
                    if (declaredObject != null) {
                        return declaredObject;
                    }
                }
            } else if (blockLine instanceof Block) {
                final Block block = (Block) blockLine;
                final ObjectDeclaration declaredObject = block.getDeclaredObject(name);
                if (declaredObject != null) {
                    return declaredObject;
                }
            }
        }
        final CodeElement parent = getParent();
        if (parent instanceof Block) {
            final Block block = (Block) parent;
            final ObjectDeclaration declaredObject = block.getDeclaredObject(name);
            if (declaredObject != null) {
                return declaredObject;
            }
        }
        return null;
    }

    public MethodDeclared getEnclosingMethod() {
        if (this instanceof MethodDeclared) {
            return (MethodDeclared) this;
        }
        CodeElement p = getParent();
        while (p != null && !(p instanceof MethodDeclared)) {
            p = p.getParent();
        }
        return (MethodDeclared) p;
    }

    public ClassDefinition getEnclosingClass() {

        CodeElement p = getParent();
        while (p != null && !(p instanceof ClassDefinition)) {
            p = p.getParent();
        }
        return (ClassDefinition) p;
    }

    public Map<String, ClassDefinition> getClassReferenced() {
        return classReferenced;
    }

    public Map<String, ObjectDeclaration> getObjectDirectlyReferenced() {
        return objectDirectlyReferenced;
    }

    public List<CodeElement> getBlockLines() {
        return blockLines;
    }
}
