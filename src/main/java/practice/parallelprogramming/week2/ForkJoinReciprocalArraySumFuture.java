package practice.parallelprogramming.week2;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ForkJoinReciprocalArraySumFuture {

   public static final String ERROR_MSG="Incorrect arguement for array size (should be >0), assuming n= 25,00,000";	
	
	public static double seqArraySum(double[] x){
		long startTime = System.nanoTime();
		double sum =0;
		
		for(int i=0;i<x.length;i++){
			sum+=1/x[i];
		}
	
		long timeInNanos= System.nanoTime()-startTime;
		printResults("seqArraySum", timeInNanos,sum);
		return sum;
	}
	
	
	public static double parArraySumFuture(double[] x){
		long startTime = System.nanoTime();
		double sum =0;
		SumArray arrSum = new SumArray(x, 0, x.length);
	    sum= ForkJoinPool.commonPool().invoke(arrSum);
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

		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");

		for (int i = 0; i < 100000000; i++) {
			array[i] = (i + 1);
		}

		for (int numRun = 0; numRun < 5; numRun++) {
			System.out.printf("Run %d \n", numRun);
			seqArraySum(array);
			parArraySumFuture(array);
		}
	}
	 
	private static void printResults(String name, long timeInNanos, double sum) {
		System.out.printf("%s completed in %8.3f milliseconds , with sum =%8.5f \n",name,timeInNanos / 1e6, sum);
		
	}
	
	private static class SumArray extends RecursiveTask<Double>{
		
		static int SEQUENTIAL_THRESHOLD = 5000000;
		int lo;
		int hi;
		double [] arr;
		
		public SumArray(double[] a, int l, int h) {
			lo=l;
			hi=h;
			arr=a;
		}

		@Override
		protected Double compute() {
			if(hi-lo<=SEQUENTIAL_THRESHOLD){
				double sum=0;
				for(int i=lo;i<hi;++i)
					sum+=1/arr[i];
					return sum;
				
			}else{
				SumArray left = new SumArray(arr, lo, (hi+lo)/2);
				SumArray right = new SumArray(arr, (lo+hi)/2, hi);
				left.fork(); //future async
				double rightsum=right.compute();
				double leftsum=left.join();
			   return rightsum+leftsum;
			}
			
		}
		
	}

}
