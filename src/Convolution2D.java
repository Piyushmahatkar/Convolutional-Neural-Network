import java.util.Scanner;

public class Convolution2D {

	int inputFeatureMapDatatype, featureCoefficientDataType, No, Ni;
	
	int lr,lc, fr,fc, ur,uc, pl,pr,pt,pb;
	int inputFeatureMapSize[] = new int[3];
	int inputFeatureZeroPadding[] = new int[4];
	int inputfeatureUpSamplingfactor[] = new int[2];
	int input[][][], output[][][], filter[][][][];
	int filterCoefficientMapSizes[] = new int[4];
	int filterCoefficientUpSamplingfactor[] = new int[2];
	int outputFeatureDownSamplingFactor[] = new int [2];
//	int inputFeatureMapSize[] = new int[3];
	public void dataGeneration()
	{
			Ni = inputFeatureMapSize[0];
			lr = inputFeatureMapSize[1];
			lc = inputFeatureMapSize[2];
			
			input = new int [Ni][lr][lc];
			
			int head = -1;
			for(int k=0;k<Ni;k++) 
			for(int i=0;i<lr;i++)
			for(int j=0;j<lc;j++)
				if(inputFeatureMapDatatype == 1) 
				{
					input[k][i][j] = (int) (Math.random()*lr*lc*Ni); 
				}
				else 
				{
					head +=1;
					input[k][i][j] = head;
				}
		}	
	public void dataGeneration2()
	{
			No = filterCoefficientMapSizes[0];
			Ni = filterCoefficientMapSizes[1];
			fr = filterCoefficientMapSizes[2];
			fc = filterCoefficientMapSizes[3];
			filter = new int [No][Ni][fr][fc];
			int head = -1;
			for(int i=0;i<No;i++) 
			for(int j=0;j<Ni;j++) 
			for(int k=0;k<fr;k++) 
			for(int l=0;l<fc;l++) 
				if(featureCoefficientDataType == 1) 
				{
					filter[i][j][k][l] = (int) (Math.random()*No*Ni*fr*fc); 
				}
				else
				{
					head +=1;
					filter[i][j][k][l] = head;
				}
	
	}
	
	public static void Display(int arr[][][]) {
		for(int k=0; k<arr.length;k++) {
		for(int i=0;i<arr[0].length;i++) {
			for(int j=0;j<arr[0][0].length;j++) {
				System.out.print(arr[k][i][j]+"\t");
			}
			System.out.print("\n");
		}
		System.out.println();
		}
		System.out.print("\n");
	}
	
	public static void Display(int arr[][][][]) {
		for(int i=0; i<arr.length;i++) {
		for(int j=0;j<arr[0].length;j++) {
		for(int k=0;k<arr[0][0].length;k++) {
		for(int l=0;l<arr[0][0][0].length;l++) {
				System.out.print(arr[i][j][k][l]+"\t");
			}
			System.out.print("\n");
			}
		System.out.println();
			}
		}
		System.out.print("\n");
	}

	public int[][][] preprocess(int newInput[][][],int UpSamplingfactor[]) {
		//Up sampling
		
		 ur = UpSamplingfactor[0] + 1;
		 uc = UpSamplingfactor[1] + 1;
		
		lr = ur * newInput[0].length;
		lc = uc * newInput[0][0].length;
		
		int answer[][][] = new int [Ni][lr][lc];
		
		for(int i=0;i<Ni;i++) {
		for(int j=0;j<newInput[0].length;j++) {
			for(int k=0;k<newInput[0][0].length;k++) {
				try{
					answer[i][j*ur][k*uc] = newInput[i][j][k];
				}catch(Exception e){
					answer[i][j][k] = 0;
				}
			}
		 }
		}
		return answer;
	}
	

	
	public int[][][] addZeroPadding(int passedinput[][][], int passedInputFeatureZeroPadding[])
	{
		 pl = passedInputFeatureZeroPadding[0];
		 pr = passedInputFeatureZeroPadding[1];
		 pt = passedInputFeatureZeroPadding[2];
		 pb = passedInputFeatureZeroPadding[3];
		 lr = pl + passedinput[0].length + pr ;
		 lc = pt + passedinput[0][0].length + pb;
		 int zeroPadding[][][] = new int [Ni][lr][lc];
		 for(int i=0;i<passedinput.length;i++) {
			 for(int j=0;j<passedinput[0].length; j++) {
				for(int k=0;k<passedinput[0][0].length;k++) {
					try{
						zeroPadding[i][j+pl][k+pt] = passedinput[i][j][k];
					}catch(Exception e){
						zeroPadding[i][j][k] = 0;
					}
				}
			}
			}
			return zeroPadding;
	}
	
	public void matrixMultiplication()
	{
		int answer[][][] = new int [No][lr-fr+1][lc-fc+1];
		
		for(int a=0;a<= No-1;a++) {
			for(int mr=0;mr <= lr-fr;mr++) {
				for(int mc=0;mc<=lc-fc; mc++) {
					for(int i=0;i<=Ni-1;i++) {
						for(int j=0;j<fr;j++) {
							for(int k=0;k<fc;k++) {
								answer[a][mr][mc] += filter[a][i][j][k]*input[i][mr+j][mc+k];
							}
						}
					}
				}
			}
		}
		output = answer;
	}
	public void postprocess()
	{
		int Matrixr = output[0].length -  pt - pb;
		int Matrixc = output[0][0].length - pl - pr;
		int Sampr = outputFeatureDownSamplingFactor[0];
		int Sampc = outputFeatureDownSamplingFactor[1];
		if(Sampr == 0) {
			Sampr = 1;
		}
		if(Sampc == 0) {
			Sampc = 1;
		}
		
		int answer[][][] = new int [No][Matrixr/Sampr][Matrixc/Sampc];
		for(int i =0;i<No;i++) {
			for(int j=0;j<Matrixr/Sampr;j++) {
				for(int k=0;k<Matrixc/Sampc;k++) {
					answer[i][j][k] = output[i][j*Sampr][k*Sampc];
				}
			}
		}
		output = answer;
	}
	public void preprocessfilter(int newInput[][][][], int UpSamplingfactor[]) {
		
		 ur = UpSamplingfactor[0] + 1;
		 uc = UpSamplingfactor[1] + 1;
		
		fr = ur * newInput[0][0].length;
		fc = uc * newInput[0][0][0].length;
		
		int answer[][][][] = new int [No][Ni][fr][fc];
		
		for(int i=0;i<No;i++) {
		for(int j=0;j<Ni;j++) {	
		for(int k=0;k<newInput[0][0].length;k++) {
			for(int l=0;l<newInput[0][0][0].length;l++) {
				try{
					answer[i][j][k*ur][l*uc] = newInput[i][j][k][l];
				}catch(Exception e){
					answer[i][j][k][l] = 0;
				}
			}
		 }
		}
		}
		filter = answer;
	}	

	
	public static void main(String[] args) {
		Scanner inp = new Scanner (System.in);
		Convolution2D example = new Convolution2D();
		System.out.println("2D Convolutional Neural Network");
		System.out.println("Please select appropriate options:");
		System.out.println("Input Feature map data type\n 1. Random \n 2. Sequential ");
		example.inputFeatureMapDatatype = inp.nextInt();
		System.out.println("Input Feature Map Size");
		System.out.print("Format order: ni, lr, lc\n");
		for(int i=0;i<3;i++) 
			example.inputFeatureMapSize[i]=inp.nextInt();
		example.dataGeneration();
		Display(example.input);
		
		System.out.println("Input feature UpSampling factor:");
		System.out.print("Format order: U-row U-col\n");
		for(int i=0;i<2;i++) 
			example.inputfeatureUpSamplingfactor[i]= inp.nextInt();
		example.input = example.preprocess( example.input , example.inputfeatureUpSamplingfactor);
		Display(example.input);
		System.out.println("Input zero padding on sides (0 for no padding)");
		System.out.print("Format order: left, right, top, bottom\n");
		for(int i=0;i<4;i++) 
			example.inputFeatureZeroPadding[i] = inp.nextInt();
		example.input = example.addZeroPadding(example.input,example.inputFeatureZeroPadding);
		Display(example.input);
		// done with padding
		System.out.println("Filter coefficients Data:");
		System.out.println("Filter coefficient data type:\n 1. Random \n 2. Sequential \n");
		example.featureCoefficientDataType = inp.nextInt();
		System.out.println("Filter coefficient sizes:");
		System.out.println("Format Order: No, Fr, Fc");
		example.filterCoefficientMapSizes[1] = example.Ni;
		example.filterCoefficientMapSizes[0] = inp.nextInt();
		example.filterCoefficientMapSizes[2] = inp.nextInt();
		example.filterCoefficientMapSizes[3] = inp.nextInt();
		
		example.dataGeneration2();
		Display(example.filter);
		System.out.println("Filter coefficient UpSampling factor\n");
		System.out.println("Format order: D-row, Dc-col");
		for(int i=0;i<2;i++) 
			example.filterCoefficientUpSamplingfactor[i] = inp.nextInt();
		example.preprocessfilter(example.filter, example.filterCoefficientUpSamplingfactor);
		Display(example.filter);
		// filter coefficients done
		System.out.println("Now Working on output coefficients");
	
		
		example.matrixMultiplication();
		Display(example.output);
		System.out.println("Enter Output feature down-sampling factor:");
		System.out.print("Format Order: S-row, S-col\n");
		for(int i=0;i<2;i++) 
			example.outputFeatureDownSamplingFactor[i] = inp.nextInt();
		example.postprocess();
		Display(example.output);
	}

}
