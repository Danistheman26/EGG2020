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
    private List<Sym> params;
    private int numParams;

    public FnSym(String type, List<Sym> params, String ID) {
	super(type, ID);
	this.params = params;
	this.numParams = params.size();
    }

    public List<Sym> getParams() {
	return params;
    }

    public int getNumParams() {
	return numParams;
    }

    public String toString() {
	return "FIXME";
    }
}

class StructDefSym extends Sym {
    private List<Sym> fields;

    public StructDefSym(String type, List<Sym> fields, String ID) {
	super(type, ID);
	this.fields = fields;
    }

    public List<Sym> getFields() {
	return fields;
    }

    public String toString() {
	return "FIXME";
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
