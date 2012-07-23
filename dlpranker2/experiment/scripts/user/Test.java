package scripts.user;

import java.util.List;

import scripts.AbstractRankExperiment;

public class Test {
public static void main (String[] args) throws Exception
{
	List<Number> i = AbstractRankExperiment.getParam(1, 20, 2);
	System.out.println(i);
	
	List<Number> e = AbstractRankExperiment.getParam(1, 20, 3);
	System.out.println(e);
	
	
}
}
