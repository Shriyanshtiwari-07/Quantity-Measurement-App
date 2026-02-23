package quantitymeasurementapp;

public class QuantityMeasurementApp {
	public static  class FeetEquality {
	     private final double value;
	     public FeetEquality() {
			this.value = 0;
			}
	     public FeetEquality(double value) {
	    	 if(Double.isNaN(value)) {
	    		 throw new IllegalArgumentException("Invalid Feet value");
	    	 }
	    	 this.value = value;
	     }
	     
	     
	     public double getValue() {
			return value;
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
	
	public static class Inches{
		private final  double inche;
		public Inches(double inche) {
			if(Double.isNaN(inche)) {
				throw new IllegalArgumentException("Invalid Inche value");
			}
			this.inche = inche;
		}
		
		public Inches() {
			this.inche = 0;
		}
		public double getInche() {
			return inche;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this==obj) {
	    		return true;
	    	}
	    	if(obj==null||this.getClass()!=obj.getClass()) {
	    		return false;
	    	}
	    	return Double.compare(this.inche, ((Inches) obj).getInche())==0;
		 }
	}
	
	  public static boolean demonstrateFeetEquality(double v1, double v2) {
	        FeetEquality f1 = new FeetEquality(v1);
	        FeetEquality f2 = new FeetEquality(v2);
	        return f1.equals(f2);
	    }

	    public static boolean demonstrateInchesEquality(double v1, double v2) {
	        Inches i1 = new Inches(v1);
	        Inches i2 = new Inches(v2);
	        return i1.equals(i2);
	    }
	
      public static void main(String[] args) {
	   FeetEquality feet1 = new FeetEquality(1);
	   FeetEquality feet2 = new FeetEquality();
	   System.out.println(feet1.equals(feet2));
	   
    }
      
}