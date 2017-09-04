package practice.parallelprogramming.week3;

import edu.rice.pcdp.PCDP;
import practice.parallelprogramming.week2.StudentStreamsUsageDemo;

public class MatrixMultiplicationDemo {
	
	public static void seqMatrixMultiply(double[][] A,double [][] B,double [][] C,int n) {
		long startTime =System.nanoTime();
		PCDP.forseq2d(0,n-1,0,n-1,(i,j)->
		{
			C[i][j]=0;
			for(int k=0;k<n;k++) {
				C[i][j]+=A[i][k]*B[k][j];
			}
		});
		
		long timeInNanos=System.nanoTime()-startTime;
		printResults("seqMatrixMultiply", timeInNanos, C[n-1][n-1]);
	}
	
	public static void parMatrixMultiply(double[][] A,double [][] B,double [][] C,int n) {
		long startTime =System.nanoTime();
		PCDP.forall2d(0,n-1,0,n-1,(i,j)->
		{
			C[i][j]=0;
			for(int k=0;k<n;k++) {
				C[i][j]+=A[i][k]*B[k][j];
			}
		});
		
		long timeInNanos=System.nanoTime()-startTime;
		printResults("parMatrixMultiply", timeInNanos, C[n-1][n-1]);
	}
	
	public static void parMatrixMultiplyChunked(double[][] A,double [][] B,double [][] C,int n) {
		long startTime =System.nanoTime();
		PCDP.forall2dChunked(0,n-1,0,n-1,4,(i,j)->
		{
			C[i][j]=0;
			for(int k=0;k<n;k++) {
				C[i][j]+=A[i][k]*B[k][j];
			}
		});
		
		long timeInNanos=System.nanoTime()-startTime;
		printResults("parMatrixMultiply", timeInNanos, C[n-1][n-1]);
	}
	
	private static void printResults(String name, long timeInNanos, double sum) {
		System.out.printf("%s completed in %8.3f milliseconds , with value =%8.5f \n",name,timeInNanos / 1e6, sum);
		
	}
	
	public static void main(String[] args) {
		//StudentStreamsUsageDemo[] array = new StudentStreamsUsageDemo[10000000];

		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        double A[][]= new double[500][500];
        double B[][]= new double[500][500];
        double C[][]= new double[500][500];
		PCDP.forseq2d(0, 499, 0, 499, (i,j)->{
			A[i][j]=i*j;
		});
		
		PCDP.forseq2d(0, 499, 0, 499, (i,j)->{
		  B[i][j]=(i+1)/(j+1);	
		});

		for (int numRun = 0; numRun < 5; numRun++) {
			System.out.printf("Run %d \n", numRun);
			seqMatrixMultiply(A, B, C, 500);
			parMatrixMultiply(A, B, C, 500);
			parMatrixMultiplyChunked(A, B, C, 500);
		}
	}
	
	

}
