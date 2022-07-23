import java.lang.Math;
import java.util.*; 
import java.io.PrintWriter;
public class Markov {

    //
    private static double defaultProposalProb = 0.25d;

    public static double getTransProb(int stateA,int stateB,int numStates){
        double[][] probabilities = new double[numStates][numStates];
        //double steadyStateProb = 1.0d / numStates;

        int sqrt = (int)Math.sqrt(numStates);

        for (int x = 0; x < numStates; x++) {
            for (int y = 0; y < numStates; y++) {
                if (y == x + sqrt) { // NORTH
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is NORTH");
                    probabilities[x][y] = defaultProposalProb; //Math.min(1, steadyStateProb/steadyStateProb) * defaultProposalProb
                } else if (y == x - sqrt) { // SOUTH
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is SOUTH");
                    probabilities[x][y] = defaultProposalProb;
                } else if (y == x + 1 && y % sqrt != 0) { // EAST
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is EAST");
                    probabilities[x][y] = defaultProposalProb;
                } else if (y == x - 1 && x % sqrt != 0) { // WEST
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is WEST");
                    probabilities[x][y] = defaultProposalProb;
                }
                // OTHERWISE, THE PROBABILITY IS 0
            }
        }

        if(stateA != stateB) return probabilities[stateA-1][stateB-1];
        else {
            double sum = Arrays.stream(probabilities[stateA-1]).sum();
            return (sum != 1d) ? 1-sum+probabilities[stateA-1][stateB-1] : probabilities[stateA-1][stateB-1];
        }
    }

    public static double[][] getAllTransProbs(int numStates){
        double[][] probabilities = new double[numStates][numStates];
        //double steadyStateProb = 1.0d / numStates;

        int sqrt = (int)Math.sqrt(numStates);

        for (int x = 0; x < numStates; x++) {
            for (int y = 0; y < numStates; y++) {
                if (y == x + sqrt) { // NORTH
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is NORTH");
                    probabilities[x][y] = defaultProposalProb; //Math.min(1, steadyStateProb/steadyStateProb) * defaultProposalProb
                } else if (y == x - sqrt) { // SOUTH
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is SOUTH");
                    probabilities[x][y] = defaultProposalProb;
                } else if (y == x + 1 && y % sqrt != 0) { // EAST
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is EAST");
                    probabilities[x][y] = defaultProposalProb;
                } else if (y == x - 1 && x % sqrt != 0) { // WEST
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is WEST");
                    probabilities[x][y] = defaultProposalProb;
                }
                // OTHERWISE, THE PROBABILITY IS 0
            }
        }

        for (int state = 0; state < numStates; state++) {
            double sum = Arrays.stream(probabilities[state]).sum();
            if(sum != 1d) {
                probabilities[state][state] = 1 - sum + probabilities[state][state];
            }
        }

        return probabilities;
    }


    public static double getSejProb(int s1,int s2,int numStates,double TS){
        int[] count = new int[numStates+1];
        int currentState;
        int n = 10000000;
        double[][] transitionProbabilties = getAllTransProbs(numStates);

        for (int i = 0; i < n; i++) {
            currentState = s1;
            for (int j = 0; j < TS; j++) {
                // TOWER SAMPLING
                // Get transition stuffs
                currentState = towerSamplingForNextState(numStates, transitionProbabilties[currentState-1]);
                // MOVE TO NEW STATE currentState = state;
            }
            count[currentState] += 1;
        }

        return (double)count[s2] / (double)n;
    }

    private static int towerSamplingForNextState(int k, double[] transitionProbs) {
        // CAN SORT IF WANT TO
        double sum = 0d;
        int currentState = 0;
        Random rand = new Random();
        double val = rand.nextDouble();

        for(double d : transitionProbs) {
            sum += d;
            if(val <= sum) break;
            currentState++;
        }

        return currentState +1;
    }

    public static double getBiasTransProb(int s1, int s2,double[] ssprob)
    {
        int numStates = 9;
        double[][] probabilities = new double[numStates][numStates];

        int sqrt = (int)Math.sqrt(numStates);

        for (int x = 0; x < numStates; x++) {
            for (int y = 0; y < numStates; y++) {
                if (y == x + sqrt) { // NORTH
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is NORTH");
                    probabilities[x][y] = Math.min(1, ssprob[y]/ssprob[x]) * defaultProposalProb;
                } else if (y == x - sqrt) { // SOUTH
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is SOUTH");
                    probabilities[x][y] = Math.min(1, ssprob[y]/ssprob[x]) * defaultProposalProb;
                } else if (y == x + 1 && y % sqrt != 0) { // EAST
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is EAST");
                    probabilities[x][y] = Math.min(1, ssprob[y]/ssprob[x]) * defaultProposalProb;
                } else if (y == x - 1 && x % sqrt != 0) { // WEST
                    //System.out.println("State " + (x+1) + "to state " + (y+1) +" is WEST");
                    probabilities[x][y] = Math.min(1, ssprob[y]/ssprob[x]) * defaultProposalProb;
                }
                // OTHERWISE, THE PROBABILITY IS 0
            }
        }

        if(s1 != s2) return probabilities[s1-1][s2-1];
        else {
            double sum = Arrays.stream(probabilities[s1-1]).sum();
            return (sum != 1d) ? 1-sum+probabilities[s1-1][s2-1] : probabilities[s1-1][s2-1];
        }
    }

    public static double getContTransProb(int s1,int s2,double[] rates){
        // No self transitions
        if (s1 == s2) return 0d;

        int numStates = 3;
        double[][] probabilities = new double[numStates][numStates];
        double sum;
        int firstStateRate;

        for (int x = 0; x < numStates; x++) {
            firstStateRate = x * 2;
            sum = rates[firstStateRate] + rates[firstStateRate+1];
            for (int y = 0; y < numStates; y++) {

                if (y < x) {
                    probabilities[x][y] = (y == 0) ? rates[firstStateRate]/sum : rates[firstStateRate+1]/sum;
                } else if (y > x) {
                    probabilities[x][y] = (y == 2) ? rates[firstStateRate+1]/sum : rates[firstStateRate]/sum;
                }

            }
        }
        return probabilities[s1-1][s2-1];

    }

    public static double[][] getAllContTransProb(double[] rates){

        int numStates = 3;
        double[][] probabilities = new double[numStates][numStates];
        double sum;
        int firstStateRate;

        for (int x = 0; x < numStates; x++) {
            firstStateRate = x * 2;
            sum = rates[firstStateRate] + rates[firstStateRate+1];
            for (int y = 0; y < numStates; y++) {

                if (y < x) {
                    probabilities[x][y] = (y == 0) ? rates[firstStateRate]/sum : rates[firstStateRate+1]/sum;
                } else if (y > x) {
                    probabilities[x][y] = (y == 2) ? rates[firstStateRate+1]/sum : rates[firstStateRate]/sum;
                }

            }
        }
        return probabilities;

    }

    public static double getContSejProb(int s1,int s2,double[] rates,double TSC){
        double time, waitingTime, propensity;
        int currentState, nextState, n;
        double[][] transitionProbabilities = getAllContTransProb(rates);
        int[] count = new int[3];

        n = 1000000;

        for (int i = 0; i < n; i++) {
            time = 0d;
            nextState = s1-1;
            currentState = nextState;
            while (time < TSC) {
                currentState = nextState;
                //System.out.println(currentState);
                propensity = rates[currentState*2] + rates[(currentState*2) +1];
                Random rand = new Random();
                waitingTime = - (1d / propensity) * Math.log(rand.nextDouble());
                time += waitingTime;
                nextState = towerSamplingForNextState(3, transitionProbabilities[currentState])-1;
            }
            count[currentState] += 1;
        }

        return (double)count[s2-1] / (double)n;
    }

}
