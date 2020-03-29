package utils;

import java.util.Random;

public class ChaosStrategy
{
    
	//Use static inner classes to implement the current best singleton pattern thread-safe implementation I use

    /** It can be seen that in this way we have not explicitly performed any synchronization operations, so how does he ensure thread safety?
     * Like the hungry mode, the JVM guarantees that static members of the class can only be loaded once.，
     * This guarantees from the JVM level that there will be only one instance object.
     * So the question is, what is the difference between this method and the hungry man model? Doesn't it load immediately?
     *In fact, when a class is loaded, its inner classes are not loaded at the same time.
     * A class is loaded if and only if one of its static members (static domain, constructor, static method, etc.) is called.
     * */
    private ChaosStrategy()
    {
        chaosvalue = 0.0;
        u=3.9999;// Complete chaos
    }

    private double u;

    private static class ChaosStrategyInner
    {
        private static ChaosStrategy instance= new ChaosStrategy();
    }

    public static ChaosStrategy getInstance()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ChaosStrategyInner.instance;
    }

    private double chaosvalue;
    public double getChaosValue()
    {
        return chaosvalue;
    }

    public void setChaosValue(double value)
    {
        this.chaosvalue = value;
    }

    //Calculate the impact of the initial value, iterate 500 times to get the chaotic state value, which is generated based on Logistic mapping
    public void CalChaos()
    {
        Random rd = new Random();
        double chaosvalue = LM(500,rd.nextDouble());
        setChaosValue(chaosvalue);
    }

    public double LM(int n,double x0)
    {
        double result = x0;//Initial value of iteration
        for(int i=0;i<n;i++)//N iterations
        {
            result = Calculator.mul(Calculator.mul(Calculator.sub(1.0, result), result), u);
        }
        setChaosValue(result);
        return result;
    }

    public double PLM(int n,double x0)
    {
        double result = x0;//Initial value of iteration
        for(int i=0;i<n;i++)//N iterations
        {
            if(result>=0.0&&result<=0.5)
                result = Calculator.mul(Calculator.mul(Calculator.sub(0.5, result), result), u);
            if(result>=0.5&&result<=1.0)
                result = 1.0-Calculator.mul(Calculator.mul(Calculator.sub(1.0, result), Calculator.sub(result,0.5)), u);
        }
        setChaosValue(result);
        return result;
    }
}