package quantitymeasurementapp;

public class QuantityMeasurementApp {
	public static  class FeetEquality {
	     private double value;
	     public FeetEquality() {};
	     public FeetEquality(double value) {
	    	 this.value = value;
	     }
	     
	     
	     public double getValue() {
			return value;
		}


		 public void setValue(double value) {
			 this.value = value;
		 }


		 @Override
	    public boolean equals(Object obj) {
	    	if(this==obj) {
	    		return true;
	    	}
	    	if(obj==null||this.getClass()!=obj.getClass()) {
	    		return false;
	    	}
	    	return Double.compare(this.value, ((FeetEquality) obj).getValue())==0;
		 }
		 
	}
      public static void main(String[] args) {
	   FeetEquality feet1 = new FeetEquality(1);
	   FeetEquality feet2 = new FeetEquality(1);
	   
    }}
