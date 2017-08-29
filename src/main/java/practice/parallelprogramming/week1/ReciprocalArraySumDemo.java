package practice.parallelprogramming.week1;

import java.util.Random;

import edu.rice.pcdp.PCDP;

public class ReciprocalArraySumDemo {
	
	public static double sum1,sum2;
	
	public static double seqArraySum(double[] x){
		long startTime = System.nanoTime();
		sum1=0;
		sum2 =0;
		for(int i=0;i<x.length/2;i++){
			sum1+=1/x[i];
		}
		
		for(int i=x.length/2;i<x.length;i++){
			sum2+=1/x[i];
		}
		
		double sum = sum1+sum2;
		long timeInNanos= System.nanoTime()-startTime;
		printResults("seqArraySum", timeInNanos,sum);
		return sum;
	}
	
	
	public static double parArraySum(double[] x){
		long startTime = System.nanoTime();
		sum1= 0;
		sum2 =0;
		PCDP.finish(() -> {

			PCDP.async(() -> {
				for (int i = 0; i < x.length / 2; i++) {
					sum1 += 1 / x[i];
				}
			});

			for (int i = x.length / 2; i < x.length; i++) {
				sum2 += 1 / x[i];
			}
		});
		
		double sum = sum1+sum2;
		long timeInNanos= System.nanoTime()-startTime;
		printResults("parArraySum", timeInNanos,sum);
		return sum;
	}
	
	 private static double[] createArray(final int N) {
	        final double[] input = new double[N];
	        final Random rand = new Random(314);

	        for (int i = 0; i < N; i++) {
	            input[i] = rand.nextInt(100);
	            // Don't allow zero values in the input array to prevent divide-by-zero
	            if (input[i] == 0.0) {
	                i--;
	            }
	        }

	        return input;
	    }
	


		public static void main(String[] args) {
			double[] array = new double[100000000];

			System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");

			for (int i = 0; i < 100000000; i++) {
				array[i] = (i + 1);
			}

			for (int numRun = 0; numRun < 5; numRun++) {
				System.out.printf("Run %d \n", numRun);
				seqArraySum(array);
				parArraySum(array);
			}
		}
	private static void printResults(String name, long timeInNanos, double sum) {
		System.out.printf("%s completed in %8.3f milliseconds , with sum =%8.5f \n",name,timeInNanos / 1e6, sum);
		
	}

}
