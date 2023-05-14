package cop2805;

public class Main {//main

	public static void main(String[] args) {
		
		double salesAmount = 1000;
		
			System.out.println("Sales\t\t\tIncome");
			System.out.println("----------------------------------");
		
		for(int i=0; i<20000 ; i++)
		{
			System.out.printf("$ %.2f\t\t",salesAmount);
			System.out.printf("$ %.2f", computeIncome(salesAmount));
		
			System.out.println();

			salesAmount+=1000;
		}
		
			System.out.println("----------------------------------");

	}
	
	public static double computeIncome(double salesAmount) {//method
		double income = 5000;
		
		while (income<=salesAmount); {//loop
			
			if (salesAmount <= 5000) {//tier 1
				income+= salesAmount * .08;
				salesAmount-=salesAmount;
				
				}
			
			else if (salesAmount>5000 && salesAmount<=10000) { //tier 2
				income+= salesAmount-5000.01 *0.1;
				salesAmount-=(salesAmount-5000);
			}
			
			else if (salesAmount > 10000) {//tier 3
				income+= salesAmount-10000.01* .12;
				salesAmount-=salesAmount-10000;
			}
		}
		return income;
	}
}
