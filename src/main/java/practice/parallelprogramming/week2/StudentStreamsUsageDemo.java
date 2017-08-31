package practice.parallelprogramming.week2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StudentStreamsUsageDemo {
	
	int id;
	boolean isCurrent;
	double age;
	public StudentStreamsUsageDemo(int id, boolean isCurrent, double age) {
		super();
		this.id = id;
		this.isCurrent = isCurrent;
		this.age = age;
	}
	
	
	public static double seqIteration(StudentStreamsUsageDemo[] students){
		long startTime = System.nanoTime();
		
		List<StudentStreamsUsageDemo> activeStudents = new ArrayList<StudentStreamsUsageDemo>();
				for(StudentStreamsUsageDemo s:students){
					if(s.isCurrent) activeStudents.add(s);
				}
		
		long ageSum=0;
		for(StudentStreamsUsageDemo a: activeStudents) ageSum+=a.age;
		
		double retVal =ageSum/ activeStudents.size();
		long timeInNanos = System.nanoTime()-startTime;
		printResults("seqIteration", timeInNanos, retVal);
		return retVal;
			
	}
	
	public static double parStream(StudentStreamsUsageDemo[] students){
		long startTime = System.nanoTime();
		
		double retVal = Stream.of(students).parallel()
				        .filter(s->s.isCurrent)
				        .mapToDouble(s->s.age)
				        .average().getAsDouble();
		long timeInNanos = System.nanoTime()-startTime;
		printResults("parStream", timeInNanos, retVal);
		return retVal;
			
	}
	
	
	private static void printResults(String name, long timeInNanos, double sum) {
		System.out.printf("%s completed in %8.3f milliseconds , with value =%8.5f \n",name,timeInNanos / 1e6, sum);
		
	}
	
	public static void main(String[] args) {
		StudentStreamsUsageDemo[] array = new StudentStreamsUsageDemo[10000000];

		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
        boolean isCurrent =true;
		for (int i = 0; i < 10000000; i++) {
			array[i] = new StudentStreamsUsageDemo(i, isCurrent, 20+i);
			isCurrent = !isCurrent;
		}

		for (int numRun = 0; numRun < 5; numRun++) {
			System.out.printf("Run %d \n", numRun);
			seqIteration(array);
			parStream(array);
		}
	}
	

}
