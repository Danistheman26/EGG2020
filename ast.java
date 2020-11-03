import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a egg program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// <<<ASTnode class (base class for all other kinds of nodes)>>>
// **********************************************************************

abstract class ASTnode { 

    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndent(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++) p.print(" ");
    }
}

// **********************************************************************
// <<<ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode>>>
// **********************************************************************

class ProgramNode extends ASTnode {
    SymTable myStmtTable;

    public ProgramNode(DeclListNode L) {
        myDeclList = L;
        myStmtTable = new SymTable();
    }
    
    public void nameAnalysis(){
    	myDeclList.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode myDeclList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
    	Iterator it = myDecls.iterator();
        while (it.hasNext()) {
            ((DeclNode)it.next()).nameAnalysis(myStmtTable);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
	Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().nameAnalysis(myStmtTable);
            while (it.hasNext()) {  // print the rest of the list
                it.next().nameAnalysis(myStmtTable);
            }
        } 
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
    	myDeclList.nameAnalysis(myStmtTable);
	myStmtList.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
   	Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            ((StmtNode)it.next()).nameAnalysis(myStmtTable);
        }
 
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
   	Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            ((ExpNode)it.next()).nameAnalysis(myStmtTable);
            while (it.hasNext()) {  // print the rest of the list
                ((ExpNode)it.next()).nameAnalysis(myStmtTable);
            }
        } 
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// <<<DeclNode and its subclasses>>>
// **********************************************************************

abstract class DeclNode extends ASTnode {
	public void nameAnalysis(SymTable myStmtTable) {}
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
	if(myStmtTable.lookupLocal(myId.myStrVal()) == null) {
	    System.out.println("ERROR var decl node");
	} else {
	    // make symbol
	    Sym newVar = new Sym(myType.getType(), myId.myStrVal());

	    // add to table
	    try {
	    	myStmtTable.addDecl(myId.myStrVal(), newVar);
	    } catch (DuplicateSymException e) {
	    } catch (EmptySymTableException f) {
	    } catch ( WrongArgumentException g) {
		System.out.println("FAIL");
		System.exit(-1);
	    }
	}
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.println(";");
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// first check if this function already exists in the symbol table before creating it
		if(myStmtTable.lookupLocal(myId.myStrVal()) == null) {
			System.out.println("ERROR function decl node");
		} else {
			// make symbol
			Sym newVar = new Sym(myType.getType(), myId.myStrVal());

			// add to table
			try {
				myStmtTable.addDecl(myId.myStrVal(), newVar);
			} catch (DuplicateSymException e) {
			} catch (EmptySymTableException f) {
			} catch ( WrongArgumentException g) {
			System.out.println("FAIL");
			System.exit(-1);
			}
		}
		
		// then unparse its formals and body inside a new scope
    	myStmtTable.addScope();
        myFormalsList.nameAnalysis(myStmtTable);
        myBody.nameAnalysis(myStmtTable);
		try {
			myStmtTable.removeScope();
		} catch (EmptySymTableException f) {
		}
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
    	if(myStmtTable.lookupLocal(myId.myStrVal()) == null) {
			System.out.println("ERROR formal decl node");
		} else {
			// make symbol
			Sym newVar = new Sym(myType.getType(), myId.myStrVal());

			// add to table
			try {
				myStmtTable.addDecl(myId.myStrVal(), newVar);
			} catch (DuplicateSymException e) {
			} catch (EmptySymTableException f) {
			} catch ( WrongArgumentException g) {
			System.out.println("FAIL");
			System.exit(-1);
			}
		}
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        myId.unparse(p, 0);
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// check if already in the statement table
		// if(myStmtTable.lookupLocal(myId.myStrVal()) == null) {
			// System.out.println("ERROR struct decl node");
		// } else {
			//make symbol
			// Sym newVar = new Sym(myType.getType(), myId.myStrVal());

			//add to table
			// try {
				// myStmtTable.addDecl(myId.myStrVal(), newVar);
			// } catch (DuplicateSymException e) {
			// } catch (EmptySymTableException f) {
			// } catch ( WrongArgumentException g) {
			// System.out.println("FAIL");
			// System.exit(-1);
			// }
		// }
		
		// then add its sub fields.. this might be more difficult than expected
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("struct ");
        myId.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("};\n");

    }

    // 2 kids
    private IdNode myId;
    private DeclListNode myDeclList;
}

// **********************************************************************
// <<<TypeNode and its Subclasses>>>
// **********************************************************************

abstract class TypeNode extends ASTnode {
    public abstract String getType();
}

class IntNode extends TypeNode {
    public IntNode() {
    }
    
    public String getType(){
    	return "int";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }
    
    public String getType(){
    	return "bool";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }
    
    public String getType(){
    	return "void";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        myId = id;
    }
    
    public String getType(){
    	return "struct";
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        myId.unparse(p, 0);
    }
    
    // 1 kid
    private IdNode myId;
}

// **********************************************************************
// <<<StmtNode and its subclasses>>>
// **********************************************************************

abstract class StmtNode extends ASTnode {
    public void nameAnalysis(SymTable myStmtTable) {}
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// check if all of the statements inside this node are accessing valid items
		myAssign.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    // 1 kid
    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    // 1 kid
    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    // 1 kid
    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// check if valid if statement inputs
		myExp.nameAnalysis(myStmtTable);
		// then unparse its formals and body inside a new scope
    	myStmtTable.addScope();
        myDeclList.nameAnalysis(myStmtTable);
        myStmtList.nameAnalysis(myStmtTable);
		try {
			myStmtTable.removeScope();
		} catch (EmptySymTableException f) {
		}
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// check if valid if statement inputs
		myExp.nameAnalysis(myStmtTable);
		
		// then unparse its formals and body inside a new scope
    	myStmtTable.addScope();
        myThenDeclList.nameAnalysis(myStmtTable);
        myThenStmtList.nameAnalysis(myStmtTable);
		try {
			myStmtTable.removeScope();
		} catch (EmptySymTableException f) {
		}
		
		// have to do the same for the else statement
		myStmtTable.addScope();
        myElseDeclList.nameAnalysis(myStmtTable);
        myElseStmtList.nameAnalysis(myStmtTable);
		try {
			myStmtTable.removeScope();
		} catch (EmptySymTableException f) {
		}
		
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
        addIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");        
    }

    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// check if valid if statement inputs
		myExp.nameAnalysis(myStmtTable);
		// then unparse its formals and body inside a new scope
    	myStmtTable.addScope();
        myDeclList.nameAnalysis(myStmtTable);
        myStmtList.nameAnalysis(myStmtTable);
		try {
			myStmtTable.removeScope();
		} catch (EmptySymTableException f) {
		}
    }
    
    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// check if valid if statement inputs
		myExp.nameAnalysis(myStmtTable);
		// then unparse its formals and body inside a new scope
    	myStmtTable.addScope();
        myDeclList.nameAnalysis(myStmtTable);
        myStmtList.nameAnalysis(myStmtTable);
		try {
			myStmtTable.removeScope();
		} catch (EmptySymTableException f) {
		}
    }
	
    public void unparse(PrintWriter p, int indent) {
	addIndent(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        addIndent(p, indent);
        p.println("}");
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// FIXME I think this might be valid.. not sure if this is just a call to a function?
		myCall.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    // 1 kid
    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		if (myExp != null) {
			myExp.nameAnalysis(myStmtTable);
        }
    }

    public void unparse(PrintWriter p, int indent) {
        addIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    // 1 kid
    private ExpNode myExp; // possibly null
}

// **********************************************************************
// <<<ExpNode and its subclasses>>>
// **********************************************************************

abstract class ExpNode extends ASTnode {
    public void nameAnalysis(SymTable myStmtTable) {}
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// this is an int lit, there is nothing to check here until we do type checking
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// this is an String lit, there is nothing to check here until we do type checking
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// this is an true node, there is nothing to check here until we do type checking
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// this is a false node, there is nothing to check here until we do type checking
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// we need to check if this ID was found anywhere in the SymTable otherwise print Undeclared identifier
		if(myStmtTable.lookupLocal(myStrVal) != null) {
			// add a link to the table
		} else if(myStmtTable.lookupGlobal(myStrVal) != null) {
			// add a link to the table in that scope
		} else {
			System.out.println("Undeclared identifier");
		}
    }

    public String myStrVal() {
	return myStrVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;    
        myId = id;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// not sure how we will go about this section, might be tough tbh
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        myId.unparse(p, 0);
    }

    // 2 kids
    private ExpNode myLoc;    
    private IdNode myId;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myLhs.nameAnalysis(myStmtTable);
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)  p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)  p.print(")");
    }

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		// FIXME, not sure what this is, probably calling a function?
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
        myId.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    // one child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// <<<Subclasses of UnaryExpNode>>>
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// <<<Subclasses of BinaryExpNode>>>
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }
    
    public void nameAnalysis(SymTable myStmtTable){
		myExp1.nameAnalysis(myStmtTable);
		myExp2.nameAnalysis(myStmtTable);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}
