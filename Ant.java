package ACO;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import utils.Calculator;
/**
 *Ants
 *@author Gavrila
 */
 public class Ant{
	public class position{
		public int vm;
		public int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	} 
	public double[][] delta;//Increased pheromone at each node
	public int Q = 100;
	public List<position> tour;//Paths obtained by ants (solution, division of tasks assigned to virtual machines)
	public double tourLength;//The length of the path obtained by the ant (after allocating, the total time spent)
	public long[] TL_task;//Total tasks per virtual machine
	public List<Integer> tabu;//Taboo table
	private int VMs;//Number of cities (equivalent to the number of virtual machines)
	private int tasks;//
	private List<? extends Cloudlet> cloudletList;	//Cloud task list
	private List<? extends Vm> vmList;				//Virtual machine list
	/**
	 *Randomly assign ants to a node, and complete the initial trial of the ants containing fields
* @ param list1 Task list
* @ param list2 virtual machine list
	 */
	public void RandomSelectVM(List<? extends Cloudlet> list1, List<? extends Vm> list2){
		cloudletList = list1;
		vmList = list2;
		VMs = vmList.size();
		tasks = cloudletList.size();
		delta = new double[VMs][tasks];
		TL_task = new long[VMs];
		for(int i=0; i<VMs; i++)TL_task[i] = 0;
		tabu = new ArrayList<Integer>();
		tour=new ArrayList<position>();
		
		//Randomly select the position of ants
		int firstVM = (int)(VMs*Math.random());
		int firstExecute = (int)(tasks*Math.random());
		tour.add(new position(firstVM, firstExecute));
		tabu.add(new Integer(firstExecute));
		TL_task[firstVM] += cloudletList.get(firstExecute).getCloudletLength();
	}
	/**
	  * calculate the expected execution time and transfer time of the task on vm
* @param vm virtual machine serial number
* @param task task number
	  */
	public double Dij(int vm, int task){
		double d;
		double s = TL_task[vm];
		double s1 = vmList.get(vm).getMips();
		double s2 = cloudletList.get(task).getCloudletLength();
		double s3 = vmList.get(vm).getBw();
		double r1 = Calculator.div(TL_task[vm],vmList.get(vm).getMips(),1);
		double r2 = Calculator.div(cloudletList.get(task).getCloudletLength(),vmList.get(vm).getBw(),1);
	    //d = TL_task[vm]/vmList.get(vm).getMips() + cloudletList.get(task).getCloudletLength()/vmList.get(vm).getBw();
	    d = r1+r2;
		return d;
	}
	 /**
	  * Select the next node
* @param pheromone global pheromone information
	  */
	  public void SelectNextVM(double[][] pheromone){
		  double[][] p;//Probability of each node being selected
		  p = new double[VMs][tasks];
		  double alpha = 1.0;
		  double beta = 1.0;
		  double sum = 0;//Denominator
		  //  Denominator part of calculation formula
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  if(tabu.contains(new Integer(j))) continue;
				  double x = Math.pow(pheromone[i][j],alpha);
				  double y = Math.pow(1/Dij(i,j),beta);
				  sum+= x*y;
				  //sum += Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta);
			  }
		  }
		  //Calculate the probability of each node being selected
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  p[i][j] = Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta)/sum;
				  if(tabu.contains(new Integer(j)))p[i][j] = 0;
			  }
		  }
		double selectp = Math.random();
        //Roulette choose a VM
        double sumselect = 0;
        int selectVM = -1;
        int selectTask = -1;
        boolean flag=true;
        for(int i=0; i<VMs&&flag==true; i++){
        	for(int j=0; j<tasks; j++){
        		sumselect += p[i][j];
        		if(sumselect>=selectp){
        			selectVM = i;
        			selectTask = j;
        			flag=false;
        			break;
        		}
        	}
        }
        if (selectVM==-1 | selectTask == -1)  
            System.out.println("Selecting the next virtual machine was unsuccessful！");
    		tabu.add(new Integer(selectTask));
		tour.add(new position(selectVM, selectTask));
		TL_task[selectVM] += cloudletList.get(selectTask).getCloudletLength();  		
	  }
	  
	  
	  
	public void CalTourLength(){
		System.out.println();
		double[] max;
		max = new double[VMs];
		for(int i=0; i<tour.size(); i++){
			max[tour.get(i).vm] += cloudletList.get(tour.get(i).task).getCloudletLength()/vmList.get(tour.get(i).vm).getMips(); 
		}		
		tourLength = max[0];
		for(int i=0; i<VMs; i++){
			if(max[i]>tourLength)tourLength = max[i];
			System.out.println("#"+i+"Execution time of two virtual machines："+max[i]);
		}
		return;
	}
	/**
	 * Calculate the pheromone increment matrix
	 */
    public void CalDelta(){
    	for(int i=0; i<VMs; i++){
    		for(int j=0; j<tasks; j++){
    			if(i==tour.get(j).vm&&tour.get(j).task==j)delta[i][j] = Q/tourLength;
    			else delta[i][j] = 0;
    		}
    	}
    }
 }