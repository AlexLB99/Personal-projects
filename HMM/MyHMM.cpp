// Give prob matrices, sequence and start prob.
//Trans matrix: each column is a hidden state, each row is a hidden state
//Emis matrix: Each row corresponds to a hidden state, each column is an obs. state
//Start array: each value is for a hidden state

#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <stdlib.h>     /* srand, rand */
#include <tuple>
#include <vector>
#include <iostream>
using namespace std;

static int viterbi(float start[], float emis[][3], float trans[][2], int obsSeq[], int K, int T) {
	//K=2 (#hid. states), N=3 (#ob. states), T=4 (obsSeq length)
	float T1[K][T] = {0};
	float T2[K][T] = {0};
	for (int i=0; i<K; i++) { //This calculates the first column of the trellis
		T1[i][0] = start[i] * emis[i][obsSeq[0]];
		T2[i][0] = 0;
	}

	float prob; //Initializing variables
	float max;
	int argmax;

	for (int j=1; j<T; j++) { //This calculates every other column in the trellis
		for (int i=0; i<K; i++) {
			max = 0;
			for (int k=0; k<K; k++) { //Maximize over hidden states (k)
				prob = T1[k][j-1] * trans[k][i] * emis[i][obsSeq[j]];
				if (prob > max) {
					max = prob;
					argmax = k;
				}
			}
			T1[i][j] = max;
			T2[i][j] = argmax;
		}
	}

	//Estimate the states
	int estStates[T] = {0};
	max = 0;
	for (int k=0; k<K; k++) {
		prob = T1[k][T-1];
		if (prob > max) {
			max = prob;
			argmax = k;
		}
	}
	estStates[T-1] = argmax; //NOTE: add zT if hidden states not encoded as 0,1,2,...
	for (int j = T-1; j>0; j--) {
		estStates[j-1] = T2[estStates[j]][j];
	}

	//Print results
	cout << "Viterbi \n";
	for (int i = 0; i<T; i++) {
		cout << estStates[i] << "\n";
	}


	return 5;
}

static int max(int arr[], int T) {
	int max = 0;
	for (int i=0; i<T; i++) {
		if (arr[i] > max) {
			max = arr[i];
		}
	}
	return max;
}

static vector<vector<int>> simulateSeq(float start[], float emis[][3], float trans[][2], int T) {
	int K = 2;
	int O = 3;
	vector<int> obsSeq;
	vector<int> stateSeq;

	float count = 0;
	float rando = (float)(rand() % 10000) / 10000; //get initial hidden state
	for (int i=0; i<K; i++) {
		count += start[i];
		if(rando < count) {
			stateSeq.push_back(i);
			break;
		}
	}
	rando = (float)(rand() % 10000) / 10000; //get initial observable state
	count = 0;
	for (int i=0; i<O; i++) {
		count += emis[stateSeq[0]][i];
		if(rando < count) {
			obsSeq.push_back(i);
			break;
		}
	}
	for (int t=1; t<T; t++) { //get all other states
		rando = (float)(rand() % 10000) / 10000;
		count = 0;
		for (int i=0; i<K; i++) {
			count += trans[stateSeq[t-1]][i];
			if(rando < count) {
				stateSeq.push_back(i);
				//cout << stateSeq[t] << " ";
				break;
			}
		}
		rando = (float)(rand() % 10000) / 10000;
		count = 0;
		for (int i=0; i<O; i++) {
			count += emis[stateSeq[t]][i];
			if(rando < count) {
				obsSeq.push_back(i);
				//cout << obsSeq[t] << "\n";
				break;
			}
		}
	}
	vector<vector<int>> retVec;
	retVec.push_back(obsSeq);
	retVec.push_back(stateSeq);
	return retVec;
}

static void BaumWelch(vector<int> obsSeq, int K, int O, int T) {
	//K = #hidden states, O = #observable states, T = #observations
	cout << "Baum-Welch starting: \n";
	vector<vector<float>> emis(K, vector<float> (O,0));
	vector<vector<float>> trans(K, vector<float> (K,0));
	vector<float> start(K);

	float startsum = 0;
	for (int i=0; i<K; i++) { //Initialize trans, emis and start matrices with default values
		float rando = (float)(rand() % 10000) / 10000;
		start[i] = rando;
		startsum += start[i];
		float transsum = 0;
		for (int j=0; j<K; j++) {
			float rando = (float)(rand() % 10000) / 10000;
			trans[i][j] = rando;
			transsum += trans[i][j];
		}
		float emissum = 0;
		for (int j=0; j<O; j++) {
			float rando = (float)(rand() % 10000) / 10000;
			emis[i][j] = rando;
			emissum += emis[i][j];
		}
		//Make sure rows sum to 1
		for (int j=0; j<K; j++) {
			trans[i][j] = trans[i][j] / transsum;
		}
		for (int j=0; j<O; j++) {
			emis[i][j] = emis[i][j] / emissum;
		}
	}
	for (int i=0; i<K; i++) {
		start[i] = start[i] / startsum;
	}

	float c[T] = {0}; //initialize scalar vector
	float alpha[K][T] = {0};
	float beta[K][T] = {0};
	float gamma[K][T] = {0};
	float xi[K][K][T-1] = {0};
	for (int bw_iters=0; bw_iters<1000; bw_iters++) {
		c[0] = 0;

		//CALCULATE ALPHA MATRIX
		for (int i=0; i<K; i++) { //Initialize alpha and beta matrices with initial values
			alpha[i][0] = start[i] * emis[i][obsSeq[0]];
			c[0] += alpha[i][0];
		}
		c[0] = 1/c[0];
		for (int i=0; i<K; i++) { //Scale initial alphas
			alpha[i][0] = c[0] * alpha[i][0];
		}
		for (int t=1; t<T; t++) {
			c[t] = 0;
			for (int i=0; i<K; i++) {
				float alphasum = 0;
				for (int k=0; k<K; k++) { //calculate summation used for alpha_i(t)
					alphasum += alpha[k][t-1] * trans[k][i];
				}
				alpha[i][t] = emis[i][obsSeq[t]] * alphasum;
				c[t] += alpha[i][t];
			}
			c[t] = 1/c[t];
			for (int i=0; i<K; i++) { //Scale alphas at time t
				alpha[i][t] = c[t] * alpha[i][t];
			}
		}

		//CALCULATE BETA MATRIX
		for (int i=0; i<K; i++) { //Initialize alpha and beta matrices with initial values
			beta[i][T-1] = c[T-1];
		}
		for (int t=T-2; t>=0; t--) {
			for (int i=0; i<K; i++) {
				float betasum = 0;
				for (int k=0; k<K; k++) { //calculate summation used for beta_i(j)
					betasum += beta[k][t+1] * trans[i][k] * emis[k][obsSeq[t+1]];
				}
				beta[i][t] = c[t] * betasum;
			}
		}

		//CALCULATE XI AND GAMMA MATRICES
		for (int t=0; t<T-1; t++) {
			for (int i=0; i<K; i++) { //calculate xi and gamma for all i,j,t
				gamma[i][t] = 0;
				for (int j=0; j<K; j++) {
					xi[i][j][t] = alpha[i][t] * trans[i][j] * beta[j][t+1] * emis[j][obsSeq[t+1]];
					gamma[i][t] += xi[i][j][t];
				}
			}
		}
		for (int i=0; i<K; i++) {
			gamma[i][T-1] = alpha[i][T-1];
		}

		//COMPUTE NEW ESTIMATIONS
		for (int i=0; i<K; i++) { //Calculate start array
			start[i] = gamma[i][0];
		}
		for (int i=0; i<K; i++) { //Calculate transmission matrix
			float denom = 0;
			for (int t=0; t<T-1; t++) { //sum of gamma_i over t=0:T-1
				denom += gamma[i][t];
			}
			for (int j=0; j<K; j++) {
				float num = 0;
				for (int t=0; t<T-1; t++) { //sum of xi_ij over t=0:T-1
					num += xi[i][j][t];
				}
				trans[i][j] = num / denom;
			}
		}
		for (int i=0; i<K;  i++) { //Calculate emission matrix
			float denom = 0;
			for (int t=0; t<T; t++) { //sum of gamma_i over all t
				denom += gamma[i][t];
			}
			for (int j=0; j<O; j++) {
				float num = 0;
				for (int t=0; t<T; t++) { //sum of gamma_i over all t where yt=j
					if (obsSeq[t] == j) {
						num += gamma[i][t];
					}
				}
				emis[i][j] = num / denom;
				cout << i << " " << j << " " << emis[i][j] << " " << num << " " << denom << "\n";
			}
		}
	}
}

int main() {
	cout << "Starting program \n";
	float start[] = {0.6, 0.4};   /*  initializers for row indexed by 0 */

	float trans[2][2] = {
	   {0.4, 0.6},   /*  initializers for row indexed by 0 */
	   {0.7, 0.3}   /*  initializers for row indexed by 1 */
	};

	float emis[2][3] = {
		   {0.1, 0.1, 0.8},   /*  initializers for row indexed by 0 */
		   {0.3, 0.6, 0.1}   /*  initializers for row indexed by 1 */
	};

	vector<vector<int>> seqs = simulateSeq(start, emis, trans, 20000);
	BaumWelch(seqs[0],2,3,20000);
	vector<vector<float>> tr(2,vector<float>(2,0));
	vector<vector<float>> em(2,vector<float>(3,0));

	for (int i=0; i<2; i++) {
		int count = 0;
		for (int t=0; t<20000-1; t++) {
			if (seqs[1][t] == i) {
				count += 1;
			}
		}
		for (int j=0; j<2; j++) {
			for (int t=0; t<20000-1; t++) {
				if (seqs[1][t] == i && seqs[1][t+1] == j) {
					tr[i][j] += 1;
				}
			}
			tr[i][j] = tr[i][j] / (float)count;
		}
		for (int j=0; j<3; j++) {
			for (int t=0; t<20000-1; t++) {
				if (seqs[1][t] == i && seqs[0][t] == j) {
					em[i][j] += 1;
				}
			}
			em[i][j] = em[i][j] / (float)count;
		}
	}
	for (int i=0; i<2; i++) {
		for (int j=0; j<2; j++) {
		}
		for (int j=0; j<3; j++) {
			cout << em[i][j] << "\n";
		}
	}

	int obsSeq[] =   {0,2,2,0,0,1,1,0,0,0,0,0,2};
	int stateSeq[] = {1,0,0,1,1,0,1,1,1,1,1,1,1};
	int T = sizeof(obsSeq)/sizeof(obsSeq[0]);
	std::cout << "Execution complete";
	//Currently The last 6 values are the real emission matrix values for the model, whereas the 6 values before that (in the third column) are the emission matrix values computed by Baum-Welch
	return 0;
}
