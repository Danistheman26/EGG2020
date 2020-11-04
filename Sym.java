import java.util.*;

public class Sym {
    private String type;
    private String ID;
    
    public Sym(String type, String ID) {
        this.type = type;
	this.ID = ID;
    }
    
    public String getType() {
        return type;
    }
    
    public String toString() {
        return type;
    }
}

class FnSym extends Sym{
    private SymTable params;

    public FnSym(String type, SymTable params, String ID) {
	super(type, ID);
	this.params = params;
    }

    public SymTable getParams() {
	return params;
    }
}

class StructDefSym extends Sym {
    private SymTable fields;

    public StructDefSym(String type, SymTable fields, String ID) {
	super(type, ID);
	this.fields = fields;
    }

    public SymTable getFields() {
	return fields;
    }
}

class StructSym extends Sym {
    private List<Sym> params;

    public StructSym(String type, List<Sym> params, String ID) {
	super(type, ID);
	this.params = params;
    }

    public List<Sym> getParams() {
	return params;
    }

    public String toString() {
	return "FIXME";
    }
}
