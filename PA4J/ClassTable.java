import java.io.PrintStream;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {

    // Current class; used for self-type completions
    private AbstractSymbol currentClass;

    private int semantErrors;
    private PrintStream errorStream;

    private AbstractSymbol object_class;
    private AbstractSymbol io_class;
    private AbstractSymbol int_class;
    private AbstractSymbol bool_class;
    private AbstractSymbol string_class;

    private Vector<class_c> classes;
    private HashMap<AbstractSymbol, class_c> classMap;

    // Flag for debugging purposes
    public static final boolean DEBUG = false;

    // List of all illegal identifiers
    private  Vector<AbstractSymbol> illegalIdentifiers;
    private static final AbstractSymbol[] illegalIdentifierSymbols = 
    new AbstractSymbol[] {
	TreeConstants.self,
	TreeConstants.SELF_TYPE
    };

    {
	// Initialize illegal identifiers
	illegalIdentifiers = new Vector<AbstractSymbol>();
	for(AbstractSymbol a: ClassTable.illegalIdentifierSymbols) {
	    illegalIdentifiers.add(a);
	}
    }


    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make it do what
     * you want.
     * */
    private void installBasicClasses() {
	AbstractSymbol filename 
	    = AbstractTable.stringtable.addString("<basic class>");
	
	// The following demonstrates how to create dummy parse trees to
	// refer to basic Cool classes.  There's no need for method
	// bodies -- these are already built into the runtime system.

	// IMPORTANT: The results of the following expressions are
	// stored in local variables.  You will want to do something
	// with those variables at the end of this method to make this
	// code meaningful.

	// The Object class has no parent class. Its methods are
	//        cool_abort() : Object    aborts the program
	//        type_name() : Str        returns a string representation 
	//                                 of class name
	//        copy() : SELF_TYPE       returns a copy of the object

	class_c Object_class = 
	    new class_c(0, 
		       TreeConstants.Object_, 
		       TreeConstants.No_class,
		       new Features(0)
			   .appendElement(new method(0, 
					      TreeConstants.cool_abort, 
					      new Formals(0), 
					      TreeConstants.Object_, 
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.type_name,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.copy,
					      new Formals(0),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0))),
		       filename);
	
	// The IO class inherits from Object. Its methods are
	//        out_string(Str) : SELF_TYPE  writes a string to the output
	//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
	//        in_string() : Str            reads a string from the input
	//        in_int() : Int                "   an int     "  "     "

	class_c IO_class = 
	    new class_c(0,
		       TreeConstants.IO,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new method(0,
					      TreeConstants.out_string,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Str)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.out_int,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_string,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_int,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0))),
		       filename);

	// The Int class has no methods and only a single attribute, the
	// "val" for the integer.

	class_c Int_class = 
	    new class_c(0,
		       TreeConstants.Int,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	// Bool also has only the "val" slot.
	class_c Bool_class = 
	    new class_c(0,
		       TreeConstants.Bool,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	// The class Str has a number of slots and operations:
	//       val                              the length of the string
	//       str_field                        the string itself
	//       length() : Int                   returns length of the string
	//       concat(arg: Str) : Str           performs string concatenation
	//       substr(arg: Int, arg2: Int): Str substring selection

	class_c Str_class =
	    new class_c(0,
		       TreeConstants.Str,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.Int,
					    new no_expr(0)))
			   .appendElement(new attr(0,
					    TreeConstants.str_field,
					    TreeConstants.prim_slot,
					    new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.length,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.concat,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg, 
								     TreeConstants.Str)),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.substr,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int))
						  .appendElement(new formalc(0,
								     TreeConstants.arg2,
								     TreeConstants.Int)),
					      TreeConstants.Str,
					      new no_expr(0))),
		       filename);


	classes.add(Object_class);
	classes.add(IO_class);
	classes.add(Int_class);
	classes.add(Bool_class);
	classes.add(Str_class);

	classMap.put(TreeConstants.Object_, Object_class);
	classMap.put(TreeConstants.IO,      IO_class);
	classMap.put(TreeConstants.Int,     Int_class);
	classMap.put(TreeConstants.Bool,    Bool_class);
	classMap.put(TreeConstants.Str,     Str_class);

	Object_class.semant(this);
	IO_class.semant(this);
	Int_class.semant(this);
	Bool_class.semant(this);
	Str_class.semant(this);

	illegalIdentifiers.add(TreeConstants.Object_);
	illegalIdentifiers.add(TreeConstants.IO);
	illegalIdentifiers.add(TreeConstants.Int);
	illegalIdentifiers.add(TreeConstants.Bool);
	illegalIdentifiers.add(TreeConstants.Str);
	
    }
	


    public ClassTable(Classes cls) {
	semantErrors = 0;
	errorStream = System.err;
       
	classes = new Vector();
	classMap = new HashMap<AbstractSymbol, class_c>();

	// Create all basic classes
	installBasicClasses();

	// Iterate through Classes list and log all
	// classes in class table
	for(Enumeration<class_c> e=cls.getElements(); 
	    e.hasMoreElements();) {

	    class_c c = e.nextElement();
	    classes.add(c);
	    Object o = classMap.put(c.getName(), c);

	    if(o != null) {
		semantError(c.getFilename(), c)
		    .println("Class " + c.getName().getString() + 
			     " was previously defined.");
	    }
	}


	for(Enumeration<class_c> e=cls.getElements();
	    e.hasMoreElements();) {
	    class_c c = e.nextElement();
	    checkInheritance(c.getName());
	}


	if(errors()) {
	    System.err.println("Compilation halted due to static semantic errors.");
	    System.exit(1);
	}
    }


    /**
     * Check Inheritance Heirarchy for loops
     */
    public void checkInheritance(AbstractSymbol a) {
	checkInheritance(a, new Vector<AbstractSymbol>(), a);
    }

    /**
     * Helper method for checking inheritance heirarchy
     */
    public void checkInheritance(AbstractSymbol a, 
				 Vector<AbstractSymbol> v,
				 AbstractSymbol superClass) {

	class_c c = getClass_c(a);
	if(a.equals(TreeConstants.No_class)) {
	    // At the top of heirarchy
	    return;
	} else if(a.equals(TreeConstants.Int) ||
		  a.equals(TreeConstants.Bool) ||
		  a.equals(TreeConstants.Str) ||
		  a.equals(TreeConstants.self) ||
		  a.equals(TreeConstants.SELF_TYPE)) {
	    // Object inherits from illegal type
	    class_c parent = getClass_c(superClass);
	    semantError(parent.getFilename(), parent)
		.println("Illegal inheritance from fundamental type: "
			 + superClass.getString()
			 + " inherits "
			 + a.getString());
	    
	} else if(v.contains(a)) {
	    // An illegal heirarchy has been found
	    semantError(c.getFilename(), c)
		.println("Illegal cyclic inheritance at " + c);
	} else if(c == null) {
	    
	    class_c s = getClass_c(superClass);
	    semantError(s.getFilename(), s)
		.println("");

	} else {
	    // Continue checking heirarchy
	    v.add(a);
	    checkInheritance(c.getParent(), v, superClass);
	}
    }

    /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(class_c c) {
	return semantError(c.getFilename(), c);
    }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
	errorStream.print(filename + ":" + t.getLineNumber() + ": ");
	return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
	semantErrors++;
	return errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
	return semantErrors != 0;
    }
    

    /**
     * Utility method for getting class from name
     */
    public class_c getClass_c(AbstractSymbol name) {
	return classMap.get(name);
    }


    /**
     * Utility method for getting parent name from class
     */ 
    public AbstractSymbol getParent(AbstractSymbol a) {
	return getClass_c(a).getParent();
    }

    public class_c getParentClass(AbstractSymbol a) {
	return getParentClass(getClass_c(a));
    }

    public class_c getParentClass(class_c c) {
	return getClass_c(c.getParent());
    }


    /**
     * Function for checking validity of an identifier
     * 
     * @param name Name of identifier to check
     * @return Boolean value; true if id is illegal
     */
    public boolean checkIllegalIdentifier(AbstractSymbol name) {
	return illegalIdentifiers.contains(name);
    }


    public boolean isSubtypeOf(AbstractSymbol c1, AbstractSymbol c2) {

	if(DEBUG) {
	    System.out.println(c1.getString() + ", " + 
			       c2.getString());
	}

	if(c1.equals(c2)) {
	    // Found class
	    return true;
	} else if(c1.equals(TreeConstants.No_type)) {
	    // No_type extends all other classes
	    return true;
	} else if(c2.equals(TreeConstants.No_type)) {
	    return false;
	} else if(c1.equals(TreeConstants.No_class)) {
	    // Did not find class
	    return false;
	} else if(isSelfType(c1)) {
	    // Replace class if it is a self type
	    return isSubtypeOf(getCurrentClass(), c2);
	    //	} else if(isSelfType(c2)) {
	    // Replace class if it is a self type
	    //return isSubtypeOf(c1, getCurrentClass());
	} else {
	    return isSubtypeOf(getParent(c1), c2);
	}
    }

    public boolean isSubtypeOf(class_c c1, class_c c2) {
	return isSubtypeOf(c1.getName(), c2.getName());
    }

    public boolean isSupertypeOf(AbstractSymbol c1, AbstractSymbol c2) {
	return isSubtypeOf(c2, c1);
    }

    public boolean isSupertypeOf(class_c c1, class_c c2) {
	return isSupertypeOf(c1.getName(), c2.getName());
    }

    public AbstractSymbol lub(AbstractSymbol a1, AbstractSymbol a2) {

	if(isSelfType(a1)) {
	    return lub(getCurrentClass(), a2);
	} else if(isSelfType(a2)) {
	    return lub(a1, getCurrentClass());
	} if(isSupertypeOf(a1, a2)) {
	    return a1;
	} else {
	    return lub(getClass_c(a1).getParent(),
		       a2);
	}
    }

    public AbstractSymbol lub(class_c c1, class_c c2) {
	return lub(c1.getName(), c2.getName());
    }


    /**
     * Get feature from class or inherited classes;
     * return null if feature not found
     */
    public Feature getFeature(AbstractSymbol className,
			      AbstractSymbol featureName) {

	if(className.equals(TreeConstants.No_class)) {
	    return null;
	} else if(isSelfType(className)) {
	    return getFeature(getCurrentClass(), 
			      featureName);
	} else {
	    class_c c = getClass_c(className);
	    Feature feature = c.getFeature(featureName);

	    if(feature == null) {
		return getFeature(c.getParent(), featureName);
	    } else {
		return feature;
	    }
	}
    }


    public boolean validComparisonTypes(Expression e1, Expression e2) {
	if(e1.get_type().equals(TreeConstants.Int) ||
	   e2.get_type().equals(TreeConstants.Int)) {

	    return e1.get_type().equals(TreeConstants.Int) && 
		e2.get_type().equals(TreeConstants.Int);

	} else if(e1.get_type().equals(TreeConstants.Bool) ||
	   e2.get_type().equals(TreeConstants.Bool)) {

	    return e1.get_type().equals(TreeConstants.Bool) && 
		e2.get_type().equals(TreeConstants.Bool);

	} else if(e1.get_type().equals(TreeConstants.Str) ||
	   e2.get_type().equals(TreeConstants.Str)) {

	    return e1.get_type().equals(TreeConstants.Str) && 
		e2.get_type().equals(TreeConstants.Str);

	} else {
	    return true;
	}
    }

    public boolean isSelfType(AbstractSymbol symbol) {
	return TreeConstants.self.equals(symbol) ||
	    TreeConstants.SELF_TYPE.equals(symbol);
    }

    public void setCurrentClass(AbstractSymbol symbol) {
	this.currentClass = symbol;
    }

    public AbstractSymbol getCurrentClass() {
	return this.currentClass;
    }
}


