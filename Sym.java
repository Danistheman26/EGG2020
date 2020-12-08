import java.util.*;

/**
 * The Sym class defines a symbol-table entry. 
 * Each Sym contains a type (a Type).
 */
public class Sym {
    private Type type;
	private int offset = 0;
	private boolean global;
    
    public Sym(Type type, int offset, boolean global) {
        this.type = type;
		this.offset = offset;
		this.global = global;
    }
	
	public boolean isGlobal() {
        return global;
    }
    
    public int getOffset() {
        return offset;
    }
	
	public Type getType() {
        return type;
    }
    
    public void setOffset(int newOffset){
    	offset = newOffset;
    }
    
    public String toString() {
        return type.toString() + ", " + global + ", " + offset;
    }
}

/**
 * The FnSym class is a subclass of the Sym class just for functions.
 * The returnType field holds the return type and there are fields to hold
 * information about the parameters.
 */
class FnSym extends Sym {
    // new fields
    private Type returnType;
    private int numParams;
    private List<Type> paramTypes;
    private int formalsOffset;
    private int localsOffset;
    
    public FnSym(Type type, int numparams, int offset, boolean global) {
        super(new FnType(), offset, global);
        returnType = type;
        numParams = numparams;
    }

    public void addFormals(List<Type> L) {
        paramTypes = L;
    }
    
    public Type getReturnType() {
        return returnType;
    }

    public int getNumParams() {
        return numParams;
    }

    public List<Type> getParamTypes() {
        return paramTypes;
    }
    
    public void setFormalsOffset(int newOffset){
    	formalsOffset = newOffset;
    }
    
    public int getformalsOffset(){
    	return formalsOffset;
    }
    
    public void setLocalsOffset(int newOffset){
    	localsOffset = newOffset;
    }
    
    public int getLocalsOffset(){
    	return localsOffset;
    }
    
    
    public String toString() {
        // make list of formals
        String str = "";
        boolean notfirst = false;
        for (Type type : paramTypes) {
            if (notfirst)
                str += ",";
            else
                notfirst = true;
            str += type.toString();
        }

        str += "->" + returnType.toString();
        str += ", " + localsOffset + ", " + formalsOffset;
        return str;
    }
}

/**
 * The StructSym class is a subclass of the Sym class just for variables 
 * declared to be a struct type. 
 * Each StructSym contains a symbol table to hold information about its 
 * fields.
 */
class StructSym extends Sym {
    // new fields
    private IdNode structType;  // name of the struct type
    
    public StructSym(IdNode id, int offset, boolean global) {
        super(new StructType(id), offset, global);
        structType = id;
    }

    public IdNode getStructType() {
        return structType;
    }    
}

/**
 * The StructDefSym class is a subclass of the Sym class just for the 
 * definition of a struct type. 
 * Each StructDefSym contains a symbol table to hold information about its 
 * fields.
 */
class StructDefSym extends Sym {
    // new fields
    private SymTable symTab;
    
    public StructDefSym(SymTable table, int offset, boolean global) {
        super(new StructDefType(), offset, global);
        symTab = table;
    }

    public SymTable getSymTable() {
        return symTab;
    }
}
