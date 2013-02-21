package it.uniroma2.disp.mpsr.helper;

import info.ferrarimarco.java.helper.math.ComputationHelper;
import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.model.Node;

import java.util.List;

public class MpsrComputationHelper {

	public static double multiplier(List<Integer> status, List<Node> nodes, double[] x){

		double result = 1.0;

		for(int i = 0; i < status.size(); i++){
			result = result * computeTerm(nodes.get(i), x[i], status.get(i));
		}

		return result;
	}

	private static double computeTerm(Node node, double x, int statusComponent){
		double result = 1.0;

		if(node.isInstantService()){
			// beta_i(n_i) = n_i! for nodes != single processor node
			result = result * (Math.pow(x, statusComponent)/ ComputationHelper.factorial(statusComponent));
		}
		else{
			result = result * Math.pow(x, statusComponent);
		}

		return result;
	}

	public static SimpleMatrix<Double> computeRelativeVisits(double y[]){

		SimpleMatrix<Double> vMatrix = new SimpleMatrix<Double>(y.length, y.length, 0.0);

		for(int i = 0; i < vMatrix.getRows(); i++){
			for(int j = 0; j < vMatrix.getColumns(); j++){
				vMatrix.setElement(i, j, y[i] / y[j]);
			}
		}
		return vMatrix;
	}

	public static double mva(List<Double> mu, SimpleMatrix<Double> visitsMatrix, List<Node> nodes, int usersCount){

		

		SimpleMatrix<Double> eNi_nMatrix = new SimpleMatrix<Double>(usersCount + 1, nodes.size() + 1, 0.0);
		SimpleMatrix<Double> eTi_nMatrix = new SimpleMatrix<Double>(usersCount + 1, nodes.size() + 1, 0.0);
		SimpleMatrix<Double> lambdai_nMatrix = new SimpleMatrix<Double>(usersCount + 1, nodes.size() + 1, 0.0);
		SimpleMatrix<Double> rho_iMatrix = new SimpleMatrix<Double>(usersCount + 1, nodes.size() + 1, 0.0);

		for(int N = 1; N <= usersCount; N++){
			for(int M = 1; M <= nodes.size(); M++){
				double eTi_n = computeEti_n(nodes.get(M-1), mu.get(M-1), eNi_nMatrix.getElement(N-1, M-1));
				eTi_nMatrix.setElement(N-1, M-1, eTi_n);
			}

			for(int M = 1; M <= nodes.size(); M++){

				double denLambda_i_n = 0.0;

				for(int j = 1; j <= nodes.size(); j++){
					denLambda_i_n += visitsMatrix.getElement(j - 1, M - 1) * eTi_nMatrix.getElement(N - 1, j - 1);
				}

				double lambdai_n = N / denLambda_i_n;
				lambdai_nMatrix.setElement(N-1, M-1, lambdai_n);

				double eni_n = lambdai_n * eTi_nMatrix.getElement(N-1, M-1);
				eNi_nMatrix.setElement(N, M-1, eni_n);

				double rhoi_n = computeRho(nodes.get(M-1), usersCount, lambdai_n, mu.get(M-1), eni_n);
				rho_iMatrix.setElement(N-1, M-1, rhoi_n);
			}
		}

		double trc1 = 0.0;
		
		for(int i = 1; i < visitsMatrix.getRows(); i++){
			trc1 += visitsMatrix.getElement(i, 0) * eTi_nMatrix.getElement(usersCount - 1, i);
		}

		return trc1;
	}

	private static double computeEti_n(Node node, double mu, double eNi_n_1){

		double result = 0.0;

		if(node.isInstantService()){
			result = 1.0/mu;
		}else{
			result = (1.0/mu) * (1 + eNi_n_1);
		}
		return result;
	}

	private static double computeRho(Node node, int usersCount, double lambda, double mu, double eni_n){
		double result = 0.0;

		if(node.isInstantService()){
			result = eni_n / usersCount;
		}else{
			result = lambda / mu;
		}

		return result;
	}
}
