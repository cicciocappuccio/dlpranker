package kernels;

import java.util.Map;

public class ParamsScore implements Comparable<ParamsScore> {

    private Map<String, Double> params;

    private double score;
    private double complexity;

    public ParamsScore(Map<String, Double> params, double score, double complexity) {
            this.params = params;
            this.score = score;
            this.complexity = complexity;
    }

    private static final double EPS = 1e-12;

    @Override
    public int compareTo(ParamsScore o) {
            int ret = 0;
            if (Math.abs(score - o.score) > EPS) // o1.score != o2.score
                    ret = (score > o.score ? -1 : 1); // better -> first
            if (ret == 0)
                    ret = (complexity < o.complexity ? -1 : 1);
            return ret;
    }

    public Map<String, Double> getParams() {
            return this.params;
    }

    public double getScore() {
            return score;
    }

    public double getComplexity() {
            return complexity;
    }

    @Override
    public String toString() {
            return "ParamsScore [model=" + params
                            + ", score=" + score + (score == Double.NEGATIVE_INFINITY ? " (INFEASIBLE)" : "")
                            + ", complexity=" + complexity + "]";
    }

}

