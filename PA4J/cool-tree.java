// -*- mode: java -*- 
//
// file: cool-tree.m4
//
// This file defines the AST
//
//////////////////////////////////////////////////////////

import java.util.Enumeration;
import java.io.PrintStream;
import java.util.Vector;


/** Defines simple phylum Program */
abstract class Program extends TreeNode {
    protected Program(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract void semant();

}


/** Defines simple phylum Class_ */
abstract class Class_ extends TreeNode {
    protected Class_(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract void semant(ClassTable classTable);
}


/** Defines list phylum Classes
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Classes extends ListNode {
    public final static Class elementClass = Class_.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Classes(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Classes" list */
    public Classes(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Class_" element to this list */
    public Classes appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Classes(lineNumber, copyElements());
    }

    public void semant(ClassTable classTable) {
	// Parse classes
	for(Enumeration<class_c> e=getElements(); e.hasMoreElements();) {
	    (e.nextElement()).semant(classTable);
	}
    }
}


/** Defines simple phylum Feature */
abstract class Feature extends TreeNode {
    protected Feature(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract void semant(SymbolTable objectTable, 
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
    public abstract AbstractSymbol get_type();
    public abstract void logFeature(SymbolTable objectTable,
				    SymbolTable methodTable,
				    ClassTable classTable,
				    class_c c);
    public abstract AbstractSymbol getName();
}


/** Defines list phylum Features
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Features extends ListNode {
    public final static Class elementClass = Feature.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Features(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Features" list */
    public Features(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Feature" element to this list */
    public Features appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Features(lineNumber, copyElements());
    }

    /**
     * Semantically analyze all features in list
     */
    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Feature> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				     methodTable,
				     classTable,
				     c);
	}
    }

    /**
     * Log all features in list to current 
     * Symbol Table scopes
     */
    public void logFeatures(SymbolTable objectTable,
			    SymbolTable methodTable,
			    ClassTable classTable,
			    class_c c) {

	for(Enumeration<Feature> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).logFeature(objectTable,
					 methodTable,
					 classTable,
					 c);
	}
    }

    /**
     * Utility method for getting instance of feature
     */
    public Feature getFeature(AbstractSymbol featureName) {
	for(Enumeration<Feature> e=getElements();
	    e.hasMoreElements();) {

	    // Compare name of feature
	    Feature feature = e.nextElement();
	    if(feature.getName().equals(featureName)) {
		return feature;
	    }
	}

	// If no feature found, return null
	return null;
    }
}


/** Defines simple phylum Formal */
abstract class Formal extends TreeNode {
    protected Formal(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
    public abstract AbstractSymbol get_type();
    public abstract void compareArg(Expression expression,
				    ClassTable classTable,
				    class_c c);
}


/** Defines list phylum Formals
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Formals extends ListNode {
    public final static Class elementClass = Formal.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Formals(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Formals" list */
    public Formals(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Formal" element to this list */
    public Formals appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Formals(lineNumber, copyElements());
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Formal> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				     methodTable,
				     classTable,
				     c);
	}
    }


    /**
     * Compare passed arguments to declared arguments
     */
    public void compareArgs(Expressions expressions,
			    ClassTable classTable,
			    class_c c) {
	if(expressions.getLength() != getLength()) {

	    classTable.semantError(c.getFilename(), c)
		.println("Wrong number of arguments passed to function: "
			 + " Expected " + getLength() + ", got " 
			 + expressions.getLength());

	    return;
	}

	// Compare all passed arguments to formal arguments
	Enumeration<Formal> e=getElements();
	Enumeration<Expression> exs=expressions.getElements();
	while(e.hasMoreElements()) {
	    (e.nextElement()).compareArg(exs.nextElement(),
					 classTable,
					 c);
	}
    }
}


/** Defines simple phylum Expression */
abstract class Expression extends TreeNode {
    protected Expression(int lineNumber) {
        super(lineNumber);
    }
    private AbstractSymbol type = null;                                 
    public AbstractSymbol get_type() { return type; }           
    public Expression set_type(AbstractSymbol s) { type = s; return this; } 
    public abstract void dump_with_types(PrintStream out, int n);
    public void dump_type(PrintStream out, int n) {
        if (type != null)
            { out.println(Utilities.pad(n) + ": " + type.getString()); }
        else
            { out.println(Utilities.pad(n) + ": _no_type"); }
    }
    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
}


/** Defines list phylum Expressions
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Expressions extends ListNode {
    public final static Class elementClass = Expression.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Expressions(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Expressions" list */
    public Expressions(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Expression" element to this list */
    public Expressions appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Expressions(lineNumber, copyElements());
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Expression> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				      methodTable,
				      classTable,
				      c);
	}
    }
}


/** Defines simple phylum Case */
abstract class Case extends TreeNode {
    protected Case(int lineNumber) {
        super(lineNumber);
    }
    public abstract void dump_with_types(PrintStream out, int n);
    public abstract void semant(SymbolTable objectTable,
				SymbolTable methodTable,
				ClassTable classTable,
				class_c c);
}


/** Defines list phylum Cases
    <p>
    See <a href="ListNode.html">ListNode</a> for full documentation. */
class Cases extends ListNode {
    public final static Class elementClass = Case.class;
    /** Returns class of this lists's elements */
    public Class getElementClass() {
        return elementClass;
    }
    protected Cases(int lineNumber, Vector elements) {
        super(lineNumber, elements);
    }
    /** Creates an empty "Cases" list */
    public Cases(int lineNumber) {
        super(lineNumber);
    }
    /** Appends "Case" element to this list */
    public Cases appendElement(TreeNode elem) {
        addElement(elem);
        return this;
    }
    public TreeNode copy() {
        return new Cases(lineNumber, copyElements());
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	for(Enumeration<Case> e=getElements();
	    e.hasMoreElements();) {
	    (e.nextElement()).semant(objectTable,
				     methodTable,
				     classTable,
				     c);
	}
    }
}


/** Defines AST constructor 'programc'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class programc extends Program {
    protected Classes classes;
    /** Creates "programc" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for classes
      */
    public programc(int lineNumber, Classes a1) {
        super(lineNumber);
        classes = a1;
    }
    public TreeNode copy() {
        return new programc(lineNumber, (Classes)classes.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "programc\n");
        classes.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_program");
        for (Enumeration e = classes.getElements(); e.hasMoreElements(); ) {
            // sm: changed 'n + 1' to 'n + 2' to match changes elsewhere
	    ((Class_)e.nextElement()).dump_with_types(out, n + 2);
        }
    }
    /** This method is the entry point to the semantic checker.  You will
        need to complete it in programming assignment 4.
	<p>
        Your checker should do the following two things:
	<ol>
	<li>Check that the program is semantically correct
	<li>Decorate the abstract syntax tree with type information
        by setting the type field in each Expression node.
        (see tree.h)
	</ol>
	<p>
	You are free to first do (1) and make sure you catch all semantic
    	errors. Part (2) can be done in a second stage when you want
	to test the complete compiler.
    */
    public void semant() {

	// Checking class inheritance heirarchy is deferred to
	// ClassTable 
	ClassTable classTable = new ClassTable(classes);
	
	// Analyze class list
	classes.semant(classTable);

	if (classTable.errors()) {
	    System.err.println("Compilation halted due to static semantic errors.");
	    System.exit(1);
	}
    }

}


/** Defines AST constructor 'class_c'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class class_c extends Class_ {
    protected AbstractSymbol name;
    protected AbstractSymbol parent;
    protected Features features;
    protected AbstractSymbol filename;
    /** Creates "class_c" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for parent
      * @param a2 initial value for features
      * @param a3 initial value for filename
      */
    public class_c(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Features a3, AbstractSymbol a4) {
        super(lineNumber);
        name = a1;
        parent = a2;
        features = a3;
        filename = a4;
    }
    public TreeNode copy() {
        return new class_c(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(parent), (Features)features.copy(), copy_AbstractSymbol(filename));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "class_c\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, parent);
        features.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, filename);
    }

    
    public AbstractSymbol getFilename() { return filename; }
    public AbstractSymbol getName()     { return name; }
    public AbstractSymbol getParent()   { return parent; }

    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_class");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, parent);
        out.print(Utilities.pad(n + 2) + "\"");
        Utilities.printEscapedString(out, filename.getString());
        out.println("\"\n" + Utilities.pad(n + 2) + "(");
        for (Enumeration e = features.getElements(); e.hasMoreElements();) {
	    ((Feature)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
    }


    /**
     * Collect all inherited features in current class's 
     * inheritance heirarchy
     */
    public void propogateInheritedFeatures(SymbolTable objectTable,
					   SymbolTable methodTable,
					   ClassTable classTable,
					   AbstractSymbol a) {

	if(a.equals(TreeConstants.No_class)) {
	    // At top of inheritance heirarchy
	    return;
	} else {
	    // For every parent class, log all class
	    // features into object and method table
	    // scopes
	    class_c c = classTable.getClass_c(a);
	    propogateInheritedFeatures(objectTable,
				       methodTable,
				       classTable,
				       c.getParent());
	    objectTable.enterScope();
	    methodTable.enterScope();
	    c.logFeatures(objectTable,
			  methodTable,
			  classTable,
			  c);
	}
    }


    /**
     * Log all features in current class into 
     * Symbol table scopes
     */
    public void logFeatures(SymbolTable objectTable,
			    SymbolTable methodTable,
			    ClassTable classTable,
			    class_c c) {

	features.logFeatures(objectTable,
			     methodTable,
			     classTable,
			     c);
    }


    /**
     * Perform semantic analysis on current class
     */
    public void semant(ClassTable classTable) {

	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(getFilename(), this)
		.println("Illegal class Identifier " + name);
	}

	// Create a new item scope for holding local variables
	SymbolTable objectTable = new SymbolTable();
	SymbolTable methodTable = new SymbolTable();

	objectTable.enterScope();
	methodTable.enterScope();

	// Collect all inherited features into
	// current Symbol Table scopes
	propogateInheritedFeatures(objectTable,
				   methodTable,
				   classTable,
				   name);

	// Analyze list of features
	features.semant(objectTable,
			methodTable,
			classTable,
			this);

	methodTable.exitScope();
	objectTable.exitScope();
    }

    
    @Override
    public boolean equals(Object o) {
	return (o instanceof class_c) &&
	    ((class_c) o).getName().equals(name);
    }

    /**
     * Simple utility method for getting an instance of
     * a feature
     */
    public Feature getFeature(AbstractSymbol featureName) {
	return features.getFeature(featureName);
    }
}


/** Defines AST constructor 'method'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class method extends Feature {
    protected AbstractSymbol name;
    protected Formals formals;
    protected AbstractSymbol return_type;
    protected Expression expr;
    /** Creates "method" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for formals
      * @param a2 initial value for return_type
      * @param a3 initial value for expr
      */
    public method(int lineNumber, AbstractSymbol a1, Formals a2, AbstractSymbol a3, Expression a4) {
        super(lineNumber);
        name = a1;
        formals = a2;
        return_type = a3;
        expr = a4;
    }
    public TreeNode copy() {
        return new method(lineNumber, copy_AbstractSymbol(name), (Formals)formals.copy(), copy_AbstractSymbol(return_type), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "method\n");
        dump_AbstractSymbol(out, n+2, name);
        formals.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, return_type);
        expr.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_method");
        dump_AbstractSymbol(out, n + 2, name);
        for (Enumeration e = formals.getElements(); e.hasMoreElements();) {
	    ((Formal)e.nextElement()).dump_with_types(out, n + 2);
        }
        dump_AbstractSymbol(out, n + 2, return_type);
	expr.dump_with_types(out, n + 2);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	objectTable.enterScope();

	// Check for illegal method names
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal name for function");
	}
	
	// Analyze arguments
	formals.semant(objectTable,
		       methodTable,
		       classTable,
		       c);

	// Analyze method body
	expr.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	objectTable.exitScope();
    }


    /**
     * Get return type
     */
    public AbstractSymbol get_type() {
	return this.return_type;
    }

    
    /**
     * Log feature in table scopes
     */
    public void logFeature(SymbolTable objectTable,
			   SymbolTable methodTable,
			   ClassTable classTable,
			   class_c c) {
	// If method already exists in scope, report error
	if(methodTable.probe(name) != null) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier: multiply defined method "
			 + name.getString());
	} else {
	    methodTable.addId(name, this);
	}
    }


    /**
     * Get name of feature
     */
    public AbstractSymbol getName() {
	return this.name;
    }


    /**
     * Method for comparing passed arguments of
     * dispatch to declared arguments
     */
    public void compareArgs(Expressions expressions,
			    ClassTable classTable,
			    class_c c) {
	formals.compareArgs(expressions,
			    classTable,
			    c);
    }
}


/** Defines AST constructor 'attr'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class attr extends Feature {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    protected Expression init;
    /** Creates "attr" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      * @param a2 initial value for init
      */
    public attr(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        init = a3;
    }
    public TreeNode copy() {
        return new attr(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)init.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "attr\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
        init.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_attr");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
	init.dump_with_types(out, n + 2);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Check for illegal name of attribute
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal name for class member");
	}

	init.semant(objectTable,
		    methodTable,
		    classTable,
		    c);
    }

    public AbstractSymbol get_type() {
	return this.type_decl;
    }

    /**
     * Log feature into symbol table scope
     */
    public void logFeature(SymbolTable objectTable,
			   SymbolTable methodTable,
			   ClassTable classTable,
			   class_c c) {
	// Check if attr is already defined in current scope
	if(objectTable.lookup(name) != null) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier: multiply define attribute "
			 + name);
	} else {
	    objectTable.addId(name, this);
	}
    }

    /**
     * Get feature name
     */
    public AbstractSymbol getName() {
	return this.name;
    }
}


/** Defines AST constructor 'formalc'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class formalc extends Formal {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    /** Creates "formalc" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      */
    public formalc(int lineNumber, AbstractSymbol a1, AbstractSymbol a2) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
    }
    public TreeNode copy() {
        return new formalc(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "formalc\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_formal");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	// Check for illegal name
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal name for formal argument "
			 + name.getString());
	}

	// If item already exists, throw multiply defined error
	if(objectTable.probe(name) != null) {
	    classTable.semantError(c.getFilename(), this)
		.println("Multiply defined argument "
			 + name.getString());
	    return;
	}

	objectTable.addId(name, this);
    }

    public AbstractSymbol get_type() {
	return type_decl;
    }

    
    /**
     * Compare an expression to declared argument
     */
    public void compareArg(Expression expression,
			   ClassTable classTable,
			   class_c c) {
	// Check if passed argument is a SELF_TYPE
	if(expression.get_type().equals(TreeConstants.self) ||
	   expression.get_type().equals(TreeConstants.SELF_TYPE)) {
	    
	    expression.set_type(c.getName());
	}

	// Compare types of argument
	if(!classTable.isSubtypeOf(expression.get_type(), type_decl)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal argument: "
			 + "Expected " + type_decl.getString()
			 + ", Found " + expression.get_type().getString());
	}
    }
}


/** Defines AST constructor 'branch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class branch extends Case {
    protected AbstractSymbol name;
    protected AbstractSymbol type_decl;
    protected Expression expr;
    /** Creates "branch" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for type_decl
      * @param a2 initial value for expr
      */
    public branch(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3) {
        super(lineNumber);
        name = a1;
        type_decl = a2;
        expr = a3;
    }
    public TreeNode copy() {
        return new branch(lineNumber, copy_AbstractSymbol(name), copy_AbstractSymbol(type_decl), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "branch\n");
        dump_AbstractSymbol(out, n+2, name);
        dump_AbstractSymbol(out, n+2, type_decl);
        expr.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_branch");
        dump_AbstractSymbol(out, n + 2, name);
        dump_AbstractSymbol(out, n + 2, type_decl);
	expr.dump_with_types(out, n + 2);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c);
	}

	// Enter a new scope to evaluate expression
	objectTable.enterScope();

	// Bind expression to identifier of case branch
	objectTable.addId(name, expr);

	// Analyze expression object
	expr.semant(objectTable, methodTable, classTable, c);

	objectTable.exitScope();
    }

    public AbstractSymbol get_type() {
	return type_decl;
    }
}


/** Defines AST constructor 'assign'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class assign extends Expression {
    protected AbstractSymbol name;
    protected Expression expr;
    /** Creates "assign" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      * @param a1 initial value for expr
      */
    public assign(int lineNumber, AbstractSymbol a1, Expression a2) {
        super(lineNumber);
        name = a1;
        expr = a2;
    }
    public TreeNode copy() {
        return new assign(lineNumber, copy_AbstractSymbol(name), (Expression)expr.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "assign\n");
        dump_AbstractSymbol(out, n+2, name);
        expr.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_assign");
        dump_AbstractSymbol(out, n + 2, name);
	expr.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	// Cannot assign to illegal identifier
	if(classTable.checkIllegalIdentifier(name)) {
	    classTable.semantError(c.getFilename(), c);
	}

	// Evaluate expression first
	expr.semant(objectTable, methodTable, classTable, c);

	// Check that identifier exists
	Object a = objectTable.lookup(name);
	
	if(a == null) {
	    classTable.semantError(c.getFilename(), c)
		.println("Assignment to non-existent variable "
			 + name);

	    set_type(TreeConstants.Object_);

	} else if(!classTable.isSupertypeOf(classTable.getTypeOf(a), 
					    expr.get_type())) {

	    classTable.semantError(c.getFilename(), c)
		.println("Assignment to illegal type: "
			 + classTable.getTypeOf(a).getString() + ", "
			 + expr.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(classTable.getTypeOf(a));
	}
    }
}


/** Defines AST constructor 'static_dispatch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class static_dispatch extends Expression {
    protected Expression expr;
    protected AbstractSymbol type_name;
    protected AbstractSymbol name;
    protected Expressions actual;
    /** Creates "static_dispatch" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for type_name
      * @param a2 initial value for name
      * @param a3 initial value for actual
      */
    public static_dispatch(int lineNumber, Expression a1, AbstractSymbol a2, AbstractSymbol a3, Expressions a4) {
        super(lineNumber);
        expr = a1;
        type_name = a2;
        name = a3;
        actual = a4;
    }
    public TreeNode copy() {
        return new static_dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(type_name), copy_AbstractSymbol(name), (Expressions)actual.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "static_dispatch\n");
        expr.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, type_name);
        dump_AbstractSymbol(out, n+2, name);
        actual.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_static_dispatch");
	expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, type_name);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Remember: <expr>@<type>.<id>(<actual>);
	// Evaluate expression that static method is called on
	expr.semant(objectTable, 
		    methodTable, 
		    classTable, 
		    c);
	
	// Evaluate list of arguments
	actual.semant(objectTable,
		      methodTable,
		      classTable,
		      c);

	if(expr.get_type().equals(TreeConstants.self) ||
	   expr.get_type().equals(TreeConstants.SELF_TYPE)) {
	    expr.set_type(c.getName());
	}

	// Check if method exists in scope of referenced object
	if(classTable.getClass_c(type_name) instanceof class_c) {
	    Feature feature = classTable.getFeature
		(type_name, name);

	    if(!classTable.isSubtypeOf(expr.get_type(), type_name)) {
		// Check if calling class is a legal subtype
		// of specified class
		classTable.semantError(c.getFilename(), c)
		    .println("Illegal static dispatch: Referenced type " 
			     + expr.get_type().getString()
			     + " is not of specified type "
			     + type_name.getString());

	    } else if(feature == null) {
		// Check if called method exists
		classTable.semantError(c.getFilename(), c)
		    .println("Call to non-existent static method "
			     + name);

	    } else if(feature instanceof method) {
		// Compare passed arguments of dispatch to
		// declared arguments of method
		method m = (method) feature;
		m.compareArgs(actual,
			      classTable,
			      c);

	    } else {
		// Calling non-method feature
		classTable.semantError(c.getFilename(), c)
		    .println("Illegal dispatch to attribute: "
			     + name.getString());
	    }
	} else {
	    // Item which method was called on is not a class
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal static dispatch to non-class object "
			 + type_name);
	}
    }
}


/** Defines AST constructor 'dispatch'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class dispatch extends Expression {
    protected Expression expr;
    protected AbstractSymbol name;
    protected Expressions actual;
    /** Creates "dispatch" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for name
      * @param a2 initial value for actual
      */
    public dispatch(int lineNumber, Expression a1, AbstractSymbol a2, Expressions a3) {
        super(lineNumber);
        expr = a1;
        name = a2;
        actual = a3;
    }
    public TreeNode copy() {
        return new dispatch(lineNumber, (Expression)expr.copy(), copy_AbstractSymbol(name), (Expressions)actual.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "dispatch\n");
        expr.dump(out, n+2);
        dump_AbstractSymbol(out, n+2, name);
        actual.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_dispatch");
	expr.dump_with_types(out, n + 2);
        dump_AbstractSymbol(out, n + 2, name);
        out.println(Utilities.pad(n + 2) + "(");
        for (Enumeration e = actual.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
        out.println(Utilities.pad(n + 2) + ")");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Evaluate Expression
	expr.semant(objectTable, 
		    methodTable, 
		    classTable, 
		    c);

	actual.semant(objectTable,
		      methodTable,
		      classTable,
		      c);


	// Set type to return type
	Feature feature = null;
	if(expr.get_type().equals(TreeConstants.self) ||
	   expr.get_type().equals(TreeConstants.SELF_TYPE)) {

	    feature = classTable.getFeature(c.getName(), name);
	} else {
	    feature = classTable.getFeature(expr.get_type(), name);
	}

	// Check if called referenced feature exists
	if(feature == null) {

	    classTable.semantError(c.getFilename(), c)
		.println("Call to non-existant method " + 
			 name + " of class " + 
			 expr.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(feature.get_type());

	    method m = (method) feature;
	    m.compareArgs(actual, classTable, c);
	}
    }
}


/** Defines AST constructor 'cond'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class cond extends Expression {
    protected Expression pred;
    protected Expression then_exp;
    protected Expression else_exp;
    /** Creates "cond" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for pred
      * @param a1 initial value for then_exp
      * @param a2 initial value for else_exp
      */
    public cond(int lineNumber, Expression a1, Expression a2, Expression a3) {
        super(lineNumber);
        pred = a1;
        then_exp = a2;
        else_exp = a3;
    }
    public TreeNode copy() {
        return new cond(lineNumber, (Expression)pred.copy(), (Expression)then_exp.copy(), (Expression)else_exp.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "cond\n");
        pred.dump(out, n+2);
        then_exp.dump(out, n+2);
        else_exp.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_cond");
	pred.dump_with_types(out, n + 2);
	then_exp.dump_with_types(out, n + 2);
	else_exp.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Evaluate component expressions
	pred.semant(objectTable, methodTable, classTable, c);
	then_exp.semant(objectTable, methodTable, classTable, c);
	else_exp.semant(objectTable, methodTable, classTable, c);

	// Check type of bool expression
	if(!pred.get_type().equals(TreeConstants.Bool)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Expected boolean type in if statement; got "
			 + pred.get_type().getString());
	}

	// Set type to least upper bound of then and else
	set_type(classTable.lub(then_exp.get_type(), 
				else_exp.get_type()));
    }
}


/** Defines AST constructor 'loop'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class loop extends Expression {
    protected Expression pred;
    protected Expression body;
    /** Creates "loop" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for pred
      * @param a1 initial value for body
      */
    public loop(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        pred = a1;
        body = a2;
    }
    public TreeNode copy() {
        return new loop(lineNumber, (Expression)pred.copy(), (Expression)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "loop\n");
        pred.dump(out, n+2);
        body.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_loop");
	pred.dump_with_types(out, n + 2);
	body.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Evaluate component expressions
	pred.semant(objectTable, methodTable, classTable, c);
	body.semant(objectTable, methodTable, classTable, c);

	// Check that predicate expression is boolean type
	if(!pred.get_type().equals(TreeConstants.Bool)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Expected boolean expression; Found "
			 + pred.get_type().getString());
	}

	// Set type to the type that body evaluates to
	set_type(body.get_type());
    }
}


/** Defines AST constructor 'typcase'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class typcase extends Expression {
    protected Expression expr;
    protected Cases cases;
    /** Creates "typcase" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for expr
      * @param a1 initial value for cases
      */
    public typcase(int lineNumber, Expression a1, Cases a2) {
        super(lineNumber);
        expr = a1;
        cases = a2;
    }
    public TreeNode copy() {
        return new typcase(lineNumber, (Expression)expr.copy(), (Cases)cases.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "typcase\n");
        expr.dump(out, n+2);
        cases.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_typcase");
	expr.dump_with_types(out, n + 2);
        for (Enumeration e = cases.getElements(); e.hasMoreElements();) {
	    ((Case)e.nextElement()).dump_with_types(out, n + 2);
        }
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Evaluate component expressions
	expr.semant(objectTable, 
		    methodTable, 
		    classTable, 
		    c);

	// Evaluate case expressions
	cases.semant(objectTable,
		     methodTable,
		     classTable,
		     c);

	// Type is the least upper bound of all expression
	// branches in list
	//set_type();
    }
}


/** Defines AST constructor 'block'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class block extends Expression {
    protected Expressions body;
    /** Creates "block" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for body
      */
    public block(int lineNumber, Expressions a1) {
        super(lineNumber);
        body = a1;
    }
    public TreeNode copy() {
        return new block(lineNumber, (Expressions)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "block\n");
        body.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_block");
        for (Enumeration e = body.getElements(); e.hasMoreElements();) {
	    ((Expression)e.nextElement()).dump_with_types(out, n + 2);
        }
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	objectTable.enterScope();
	
	// Evaluate all expressions in body list
	body.semant(objectTable,
		    methodTable,
		    classTable,
		    c);

	// Set type to type of body expression
	Expression e = (Expression) body
	    .getNth(body.getLength() - 1);
	set_type(e.get_type());

	objectTable.exitScope();
    }
}


/** Defines AST constructor 'let'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class let extends Expression {
    protected AbstractSymbol identifier;
    protected AbstractSymbol type_decl;
    protected Expression init;
    protected Expression body;
    /** Creates "let" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for identifier
      * @param a1 initial value for type_decl
      * @param a2 initial value for init
      * @param a3 initial value for body
      */
    public let(int lineNumber, AbstractSymbol a1, AbstractSymbol a2, Expression a3, Expression a4) {
        super(lineNumber);
        identifier = a1;
        type_decl = a2;
        init = a3;
        body = a4;
    }
    public TreeNode copy() {
        return new let(lineNumber, copy_AbstractSymbol(identifier), copy_AbstractSymbol(type_decl), (Expression)init.copy(), (Expression)body.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "let\n");
        dump_AbstractSymbol(out, n+2, identifier);
        dump_AbstractSymbol(out, n+2, type_decl);
        init.dump(out, n+2);
        body.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_let");
	dump_AbstractSymbol(out, n + 2, identifier);
	dump_AbstractSymbol(out, n + 2, type_decl);
	init.dump_with_types(out, n + 2);
	body.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	objectTable.enterScope();

	// Analyze initialization body
	init.semant(objectTable, methodTable, classTable, c);

	if(classTable.checkIllegalIdentifier(identifier)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Illegal identifier in let assignment: "
			 + identifier);
	} else {

	    // Check that type for declared argument matches
	    // initialization expression
	    if(init.get_type().equals(TreeConstants.No_type)) {
		
		init.set_type(type_decl);
		objectTable.addId(identifier, init);

	    } else if(!classTable.isSupertypeOf(type_decl, init.get_type())) {
		classTable.semantError(c.getFilename(), c)
		    .println("Illegal assignment to type: "
			     + type_decl + ", "
			     + init.get_type());
	    } else {
		// Set type of init to supertype
		init.set_type(type_decl);

		// Add identifier to object scope
		objectTable.addId(identifier, init);
	    }
	}

	body.semant(objectTable, methodTable, classTable, c);

	// Set type of let to Least Upper Bound of expression body
	// and type declaration
	//set_type(classTable.lub(type_decl, body.get_type()));
	set_type(body.get_type());

	objectTable.exitScope();
    }
}


/** Defines AST constructor 'plus'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class plus extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "plus" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public plus(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new plus(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "plus\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_plus");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Invalid types for integer expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Int);
	}
    }
}


/** Defines AST constructor 'sub'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class sub extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "sub" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public sub(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new sub(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "sub\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_sub");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected types in integer expression: "
			 + e1.get_type().getString() + ", " 
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Int);
	}
    }
}


/** Defines AST constructor 'mul'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class mul extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "mul" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public mul(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new mul(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "mul\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_mul");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected types in integer expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Int);
	}
    }
}


/** Defines AST constructor 'divide'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class divide extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "divide" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public divide(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new divide(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "divide\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_divide");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected types in integer expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());
	} else {
	    set_type(TreeConstants.Int);
	}
    }
}


/** Defines AST constructor 'neg'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class neg extends Expression {
    protected Expression e1;
    /** Creates "neg" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public neg(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new neg(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "neg\n");
        e1.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_neg");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int)) {
	    
	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in integer expression: "
			 + e1.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Int);
	}
    }
}


/** Defines AST constructor 'lt'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class lt extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "lt" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public lt(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new lt(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "lt\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_lt");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in comparison expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


/** Defines AST constructor 'eq'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class eq extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "eq" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public eq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new eq(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "eq\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_eq");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!classTable.validComparisonTypes(e1, e2)) {
	    classTable.semantError(c.getFilename(), c)
		.println("Invalid comparison between types: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());
	}
	set_type(TreeConstants.Bool);
    }
}


/** Defines AST constructor 'leq'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class leq extends Expression {
    protected Expression e1;
    protected Expression e2;
    /** Creates "leq" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      * @param a1 initial value for e2
      */
    public leq(int lineNumber, Expression a1, Expression a2) {
        super(lineNumber);
        e1 = a1;
        e2 = a2;
    }
    public TreeNode copy() {
        return new leq(lineNumber, (Expression)e1.copy(), (Expression)e2.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "leq\n");
        e1.dump(out, n+2);
        e2.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_leq");
	e1.dump_with_types(out, n + 2);
	e2.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);
	e2.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Int) ||
	   !e2.get_type().equals(TreeConstants.Int)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in integer expression: "
			 + e1.get_type().getString() + ", "
			 + e2.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


/** Defines AST constructor 'comp'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class comp extends Expression {
    protected Expression e1;
    /** Creates "comp" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public comp(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new comp(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "comp\n");
        e1.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_comp");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Bool)) {

	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in boolean expression: "
			 + e1.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


/** Defines AST constructor 'int_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class int_const extends Expression {
    protected AbstractSymbol token;
    /** Creates "int_const" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for token
      */
    public int_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }
    public TreeNode copy() {
        return new int_const(lineNumber, copy_AbstractSymbol(token));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "int_const\n");
        dump_AbstractSymbol(out, n+2, token);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_int");
	dump_AbstractSymbol(out, n + 2, token);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	set_type(TreeConstants.Int);
    }
}


/** Defines AST constructor 'bool_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class bool_const extends Expression {
    protected Boolean val;
    /** Creates "bool_const" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for val
      */
    public bool_const(int lineNumber, Boolean a1) {
        super(lineNumber);
        val = a1;
    }
    public TreeNode copy() {
        return new bool_const(lineNumber, copy_Boolean(val));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "bool_const\n");
        dump_Boolean(out, n+2, val);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_bool");
	dump_Boolean(out, n + 2, val);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	set_type(TreeConstants.Bool);
    }
}


/** Defines AST constructor 'string_const'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class string_const extends Expression {
    protected AbstractSymbol token;
    /** Creates "string_const" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for token
      */
    public string_const(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        token = a1;
    }
    public TreeNode copy() {
        return new string_const(lineNumber, copy_AbstractSymbol(token));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "string_const\n");
        dump_AbstractSymbol(out, n+2, token);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_string");
	out.print(Utilities.pad(n + 2) + "\"");
	Utilities.printEscapedString(out, token.getString());
	out.println("\"");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	set_type(TreeConstants.Str);
    }
}


/** Defines AST constructor 'new_'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class new_ extends Expression {
    protected AbstractSymbol type_name;
    /** Creates "new_" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for type_name
      */
    public new_(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        type_name = a1;
    }
    public TreeNode copy() {
        return new new_(lineNumber, copy_AbstractSymbol(type_name));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "new_\n");
        dump_AbstractSymbol(out, n+2, type_name);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_new");
	dump_AbstractSymbol(out, n + 2, type_name);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {

	if(classTable.getClass_c(type_name) == null) {
	    
	    classTable.semantError(c.getFilename(), c)
		.println("Invalid class type: " 
			 + type_name.getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(type_name);
	}
    }
}


/** Defines AST constructor 'isvoid'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class isvoid extends Expression {
    protected Expression e1;
    /** Creates "isvoid" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for e1
      */
    public isvoid(int lineNumber, Expression a1) {
        super(lineNumber);
        e1 = a1;
    }
    public TreeNode copy() {
        return new isvoid(lineNumber, (Expression)e1.copy());
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "isvoid\n");
        e1.dump(out, n+2);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_isvoid");
	e1.dump_with_types(out, n + 2);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	e1.semant(objectTable, methodTable, classTable, c);

	if(!e1.get_type().equals(TreeConstants.Bool)) {
	    
	    classTable.semantError(c.getFilename(), c)
		.println("Unexpected type in isvoid expression: "
			 + e1.get_type().getString());

	    set_type(TreeConstants.Object_);
	} else {
	    set_type(TreeConstants.Bool);
	}
    }
}


/** Defines AST constructor 'no_expr'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class no_expr extends Expression {
    /** Creates "no_expr" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      */
    public no_expr(int lineNumber) {
        super(lineNumber);
    }
    public TreeNode copy() {
        return new no_expr(lineNumber);
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "no_expr\n");
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_no_expr");
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	set_type(TreeConstants.No_type);
    }
}


/** Defines AST constructor 'object'.
    <p>
    See <a href="TreeNode.html">TreeNode</a> for full documentation. */
class object extends Expression {
    protected AbstractSymbol name;
    /** Creates "object" AST node. 
      *
      * @param lineNumber the line in the source file from which this node came.
      * @param a0 initial value for name
      */
    public object(int lineNumber, AbstractSymbol a1) {
        super(lineNumber);
        name = a1;
    }
    public TreeNode copy() {
        return new object(lineNumber, copy_AbstractSymbol(name));
    }
    public void dump(PrintStream out, int n) {
        out.print(Utilities.pad(n) + "object\n");
        dump_AbstractSymbol(out, n+2, name);
    }

    
    public void dump_with_types(PrintStream out, int n) {
        dump_line(out, n);
        out.println(Utilities.pad(n) + "_object");
	dump_AbstractSymbol(out, n + 2, name);
	dump_type(out, n);
    }

    public void semant(SymbolTable objectTable,
		       SymbolTable methodTable,
		       ClassTable classTable,
		       class_c c) {
	// Get reference to identifier in object scope
	Object o = objectTable.lookup(name);
	
	if(name.equals(TreeConstants.self) ||
	   name.equals(TreeConstants.SELF_TYPE)) {
	    
	    set_type(c.getName());
	} else if(o == null) {
	    // Refernced object does not exist
	    classTable.semantError(c.getFilename(), c)
		.println("Reference to non-existant object: " + name);
	} else {
	    set_type(classTable.getTypeOf(o));
	}
    }
}

