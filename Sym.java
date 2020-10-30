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
    private List<String> params;

    public FnSym(String type, List<String> params, String ID) {
	super(type, ID);
	this.params = params;
    }

    public List<String> getParams() {
	return params;
    }

    public String toString() {
	return "FIXME";
    }
}

class StructDefSym extends Sym {
    private List<String> fields;

    public StructDefSym(String type, List<String> fields, String ID) {
	super(type, ID);
	this.fields = fields;
    }

    public List<String> getFields() {
	return fields;
    }

    public String toString() {
	return "FIXME";
    }
}

class StructSym extends Sym {
    private List<String> params;

    public StructSym(String type, List<String> params, String ID) {
	super(type, ID);
	this.params = params;
    }

    public List<String> getParams() {
	return params;
    }

    public String toString() {
	return "FIXME";
    }
}
