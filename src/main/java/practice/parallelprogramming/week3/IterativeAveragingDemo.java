package practice.parallelprogramming.week3;

import edu.rice.pcdp.PCDP;

public class IterativeAveragingDemo {
	
	public static int n=100000;
	public static double myNew[] = new double[100000];
	public static double myVal[] = new double[100000];

	public static void runSequential(int iterations) {
		for(int iter=0;iter<iterations;iter++) {
			for(int j=1;j<n-1;j++) {
				myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
			}
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
		}
	}
	
	public static void runForAll(final int iterations) {
		for(int iter=0;iter<iterations;iter++) {
			PCDP.forall(1, n, j-> {
				myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
			});
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
		}
	}
	
	public static void runForAllGrouped(final int iterations,final int tasks) {
		for(int iter=0;iter<iterations;iter++) {
			PCDP.forall(0, tasks-1, i-> {
				for(int j=i*(n/tasks)+1;j<=(i+1)*(n/tasks);j++)
					myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
					
			});
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
		}
	}
	
//	public void runForAllGroupedBarrier(final int iterations,final int tasks) {
//		PCDP.forall(0,tasks-1,i->{
//			double myVal[]=this.myVal;
//			double myNew[]=this.myNew;
//			for(int iter=0;iter<iterations;iter++) {
//				for(int j=i*(n/tasks)+1;j<=(i+1)*(n/tasks);j++)
//					myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
//				
//			PCDP.next();//BARRIER
//				
//			double[] temp=myNew;
//			myNew=myVal;
//			myVal=temp;
//			}
//		});
//			
//			
//		
//	}

	

	

	private static void printResults(String name, long timeInNanos, double sum) {
		System.out.printf("%s completed in %8.3f milliseconds , with value =%8.5f \n", name, timeInNanos / 1e6, sum);

	}

	public static void main(String[] args) {
		// StudentStreamsUsageDemo[] array = new StudentStreamsUsageDemo[10000000];

		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
	    n=100000;
		PCDP.forseq(0, n-1, i -> {
			myVal[i] = i ;
		});

		

		for (int numRun = 0; numRun < 5; numRun++) {
			System.out.printf("Run %d \n", numRun);
			runSequential(1000);
			runForAll(1000);
			runForAllGrouped(1000, 4);
			
		}
	}

}
