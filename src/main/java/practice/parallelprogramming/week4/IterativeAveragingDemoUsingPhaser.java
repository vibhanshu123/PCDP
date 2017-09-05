package practice.parallelprogramming.week4;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

import edu.rice.pcdp.PCDP;

public class IterativeAveragingDemoUsingPhaser {
	
	public static int n=100000;
	public static double myNew[] = new double[100000];
	public static double myVal[] = new double[100000];

	public static void runSequential(int iterations) {
		long startTime =System.nanoTime();
		for(int iter=0;iter<iterations;iter++) {
			for(int j=1;j<n-1;j++) {
				myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
			}
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
		}
		long timeInNanos=System.nanoTime()-startTime;
		printResults("runSequential", timeInNanos,myNew.length);
	}
	
	public static void runForAll(final int iterations) {
		long startTime =System.nanoTime();
		for(int iter=0;iter<iterations;iter++) {
			PCDP.forall(1, n-2, j-> {
				myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
			});
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
		}
		long timeInNanos=System.nanoTime()-startTime;
		printResults("runForAll", timeInNanos, myNew.length);
	}
	
	public static void runForAllGrouped(final int iterations,final int tasks) {
		long startTime =System.nanoTime();
		for(int iter=0;iter<iterations;iter++) {
			PCDP.forall(0, tasks-1, i-> {
				for(int j=i*(n/tasks)+1;j<=(i+1)*(n/tasks);j++)
					myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
					
			});
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
		}
		long timeInNanos=System.nanoTime()-startTime;
		printResults("runForAllGrouped", timeInNanos, myNew.length);
	}
	
	public static void runForAllGroupedBarrier(final int iterations,final int tasks) {
		long startTime =System.nanoTime();
        CyclicBarrier barrier = new CyclicBarrier(tasks);		
		PCDP.forall(0,tasks-1,i->{
			for(int iter=0;iter<iterations;iter++) {
				for(int j=i*(n/tasks)+1;j<=(i+1)*(n/tasks);j++)
					myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
				
		//	PCDP.next();//BARRIER
				 try {
			           barrier.await();
			         } catch (InterruptedException ex) {
			           return;
			         } catch (BrokenBarrierException ex) {
			           return;
			         }
				
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
			}
		});	
		long timeInNanos=System.nanoTime()-startTime;
		printResults("runForAllGroupedBarrier", timeInNanos, myNew.length);
	}
	
	public static void runForAllGroupedBarrierPhaser(final int iterations,final int tasks) {
		Phaser ph = new Phaser(0);
		ph.bulkRegister(tasks);
		Thread threads[] = new Thread[tasks];
		long startTime =System.nanoTime();
		for(int ii=0;ii<tasks;ii++){
			int i=ii;
			threads[ii]=new Thread(() ->{
			for(int iter=0;iter<iterations;iter++) {
			
				int left = i*(n/tasks)+1;
				
				int right =(i+1)*(n/tasks);
				
				for(int j=left;j<=right;j++){
					myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
				}
				
//				for(int j=i*(n/tasks)+1;j<=(i+1)*(n/tasks);j++)
//					myNew[j]=(myVal[j-1]+myVal[j+1])/2.0;
				
		ph.arriveAndAwaitAdvance();
				
			double[] temp=myNew;
			myNew=myVal;
			myVal=temp;
			}
		});	
			
		threads[ii].start();	
		
	}
		
		for(int ii=0;ii<tasks;ii++){
			try{
				threads[ii].join();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		long timeInNanos=System.nanoTime()-startTime;
		printResults("runForAllGroupedBarrier", timeInNanos, myNew.length);
	}

	

	

	private static void printResults(String name, long timeInNanos, double sum) {
		System.out.printf("%s completed in %8.3f milliseconds , with value =%8.5f \n", name, timeInNanos / 1e6, sum);
		myNew = new double[100000];

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
			//runForAllGrouped(1000, 4);
			runForAllGroupedBarrier(1000, 4);
			
		}
	}


}
