package comunicationObj;

import java.io.Serializable;
import java.util.ArrayList;

public class Pacchetto implements Serializable{

	private static final long serialVersionUID = 8412015884579218320L;
	private Object firstParam;	
	private Object secondParam;	
	private ArrayList<?> thirdParam;	//
	private Object lastParam;
	private boolean result;		// Risultato dell'operazione
	
	public Pacchetto(Object firstParam, Object secondParam, ArrayList<?> thirdParam, boolean result) {
		this(firstParam,secondParam, thirdParam, null,result);
	}
	
	public Pacchetto(Object firstParam, Object secondParam, ArrayList<?> thirdParam, Object lastParam, boolean result) {
		this.firstParam = firstParam;
		this.secondParam = secondParam;
		this.thirdParam = thirdParam;
		this.lastParam = lastParam;
		this.result = result;
	}

	public Object getFirstParam() {
		return firstParam;
	}

	public void setFirstParam(Object firstParam) {
		this.firstParam = firstParam;
	}

	public Object getSecondParam() {
		return secondParam;
	}

	public void setSecondParam(Object secondParam) {
		this.secondParam = secondParam;
	}

	public ArrayList<?> getThirdParam() {
		return thirdParam;
	}

	public void setThirdParam(ArrayList<Object> thirdParam) {
		this.thirdParam = thirdParam;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
	@Override
	public String toString(){
		
		String resultTmp = (result==false) ? "false" : "true";
		String thirdParam = (this.thirdParam != null) ? "(0)-> "+this.thirdParam.get(0) : "null";
	    return "[FP: " + firstParam.toString() + " SP: " + secondParam.toString() + " TP: " + thirdParam + " RESULT: " + resultTmp + "]";
	    
	}
	
	@Override
	public boolean equals(Object other){
		
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Pacchetto))return false;
	    
	    Pacchetto tmpOther = (Pacchetto)other;
	    
	    if(tmpOther.toString().equals(this.toString())) 
	    	return true;
	    else
	    	return false;
	    
	    
	}

	public Object getLastParam() {
		return lastParam;
	}

	public void setLastParam(Object lastParam) {
		this.lastParam = lastParam;
	}
	
}
