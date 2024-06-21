package frontend.thisdesignisfortheteacher;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class hello {

	public hello() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(calculatePrice(90.145464, new BigDecimal(1, mat)));
	}
	
	private static final MathContext mat = new  MathContext(2, RoundingMode.HALF_EVEN);
	
	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	public static double calculatePrice(double price, BigDecimal discount) {
		if(discount == null || discount.compareTo(BigDecimal.ZERO) == 0)
			return price;
		return ONE_HUNDRED.subtract(discount)
				.divide(ONE_HUNDRED)
				.multiply(new BigDecimal(price)).doubleValue();
		/*if(discount == 0) {
			
		}
		rentDiscount.*/
	}

}
