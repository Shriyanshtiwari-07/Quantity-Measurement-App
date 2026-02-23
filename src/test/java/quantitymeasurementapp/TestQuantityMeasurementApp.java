package quantitymeasurementapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import quantitymeasurementapp.QuantityMeasurementApp;
import quantitymeasurementapp.QuantityMeasurementApp.FeetEquality;
import quantitymeasurementapp.QuantityMeasurementApp.Inches;

public class TestQuantityMeasurementApp {
		
		QuantityMeasurementApp.FeetEquality feet1;
		QuantityMeasurementApp.FeetEquality feet2;
		QuantityMeasurementApp.Inches inche1;
		QuantityMeasurementApp.Inches inche2;
		
	    @Test
	    public void testFeetEquals() {
	    	feet1 = new FeetEquality(1);
	    	feet2 = new FeetEquality(1);
	    	assertTrue(feet1.equals(feet2));
	    }
	    
	    @Test
	    public void testFeetDifferent() {
	    	feet1 = new FeetEquality(1);
	    	feet2 = new FeetEquality(2);
	    	assertTrue(!feet1.equals(feet2));
	    }
	    
	    @Test
	    public void testFeetNull() {
	    	feet1 = new FeetEquality(1);
	    	feet2 = null;
	    	assertTrue(!feet1.equals(feet2));
	    }
	    
	    @Test
	    public void testFeetSameRefferencet() {
	    	feet1 = new FeetEquality(1);
	    	
	    	assertTrue(feet1.equals(feet1));
	    }
	    
	    @Test
	    public void testFeetEmptyVariable() {
	    	feet1 = new FeetEquality(1);
	    	feet2 = new FeetEquality();
	    	assertTrue(!feet1.equals(feet2));
	    }
	    
	    @Test
	    public void testIncheEmptyVariable() {
	    	inche1 = new Inches(1);
	    	inche2 = new Inches();
	    	assertTrue(!inche1.equals(inche2));
	    }
	    
	    @Test
	    public void testIncheSameRefference() {
	    	inche1 = new Inches(1);
	    	
	    	assertTrue(inche1.equals(inche1));
	    }
	
	    @Test
	    public void testIncheNull() {
	    	inche1 = new Inches(1);
	    	inche2 = null;
	    	assertTrue(!inche1.equals(inche2));
	    }
	    
	    @Test
	    public void testIncheDifferent() {
	    	inche1 = new Inches(1);
	    	inche2 = new Inches(2);
	    	assertTrue(!inche1.equals(inche2));
	    }
	    
	    @Test
	    public void testInchesEqual() {
	    	inche1 = new Inches(1);
	    	inche2 = new Inches(1);
	    	assertTrue(inche1.equals(inche2));
	    }

}