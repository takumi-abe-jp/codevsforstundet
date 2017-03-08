package codevs;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    static final String AI_NAME = "bebe.java";

    static final int EMPTY = 0;

    int turn = -1;

    int pack[][][];
    int copypack[][][];

    int width;

    int height;

    int packSize;

    int summation;

    int obstacle;

    int maxTurn;

    long millitime;
    static final int TRUE = 100000;
    static final int FALSE = -100000;
    int rensaPoint = 0;
    int clearCount = 0;

    int rensa = 0;

    static final int BUILD = 0;


    int MODE = BUILD;
    int SET_CHECK = FALSE;
    int CLEARCHECK = TRUE;
    int beams_N =20;
    int beams_end = 5;
    int beams_count = 0;

    int[] NOWP;

    int FIRELINE = 1000000;

    int[][] beamsout;
    int[][][] simuboard;
    int[][][] beamsboard;
    int[][] checkboard;
    int[][] checkboard_tmp;
    int[][] checksimuboard_tmp;
    //next check

    Board my;

    Board op;

    class Board {

        int obstacleNum;

        int board[][];

        public Board(int width, int height, Scanner in) {
            obstacleNum = in.nextInt();
            board = new int[height][width];
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    board[i][j] = in.nextInt();
                }
            }
            in.next();
        }
    }

    void run() {
        println(AI_NAME);

        try (Scanner in = new Scanner(System.in)) {
            width = in.nextInt();
            height = in.nextInt();
            packSize = in.nextInt();
            summation = in.nextInt();
            obstacle = summation + 1;
            maxTurn = in.nextInt();
            pack = new int[maxTurn][packSize][packSize];
            copypack = new int[maxTurn][packSize][packSize];
            for (int i = 0; i < maxTurn; ++i) {
                for (int j = 0; j < packSize; ++j) {
                    for (int k = 0; k < packSize; ++k) {
                        pack[i][j][k] = in.nextInt();
                        copypack[i][j][k] = pack[i][j][k];
                    }
                }
                in.next();
           }
            simuboard = new int[beams_N][height+3][width];
           beamsboard = new int[(width+2)*4][height+3][width];
           checkboard_tmp = new int[height+3][width];
           checkboard = new int[height+3][width];
           beamsout = new int[beams_end+1][4];
           checksimuboard_tmp = new int[height+3][width];
           NOWP  = new int[4];
           int[][] simuboard = new int[height+3][width];
           int[] res = new int[4];

           while (true) {
                turn = in.nextInt();
                millitime = in.nextLong();
                my = new Board(width, height, in);
                op = new Board(width, height, in);

                debug("turn : " + turn);

                int rot = 0;

                int[][] pack = fillObstaclePack(this.pack[turn], my.obstacleNum);
                pack = packRotate(pack, rot);
                int left = 0, right = width - packSize;

                bad:
                for (int i = 0; i < packSize; ++i) {
                    for (int j = 0; j < packSize; ++j) {
                        if (pack[j][i] != EMPTY)
                            break bad;
                    }
                    --left;
                }
                bad:
                for (int i = 0; i < packSize; ++i) {
                    for (int j = packSize - 1; j >= 0; --j) {
                        if (pack[j][i] != EMPTY)
                            break bad;
                    }
                    ++right;
                }
                //ozyama keisan
            	int tmp = 0;
            	tmp = my.obstacleNum;
            	for(int i=0; i<5; i++){
            		copypack[turn+i] = fillObstaclePack(this.pack[turn+i], tmp);
            	}

            	//cleaer
            	for(int i=0; i<4; i++){
            		NOWP[i] = -1;
            	}
                for (int i = 0; i < (width+2)*4; i++) {
                    for (int j = 0; j < height+3; j++) {
                        for (int k = 0; k < width; k++) {
                        	beamsboard[i][j][k] = EMPTY;
                        }
                    }
               }
                simuboard = setSimuboard(my.board);
    	    	//hakakten



    	    	int count = 0;
    	    	for(int i=0; i<height+3; i++){
    	    		for(int j=0; j<width; j++){
    	    			if(simuboard[i][j] != EMPTY) count++;
    	    		}
    	    	}

    	    	res = bestfire(simuboard);
    	    	//printboard("simu2",simuboard);
    	    	//printboard("check2",checkboard);
    	    	debug("f :"+res[0]+" r :"+res[1]+" p :"+res[2]+" t :"+res[3]);
    	    	if(turn == 0){
    	    		println(4 + " " + 0);
    	    	}else{
	            	if(20000 > millitime){
	            		beams_N = 10;
	            		beams_end = 2;
	            		FIRELINE = 500000;
	            		beams(simuboard);
	            	}else{
	            		beams(simuboard);
	            	}
	                debug(" "+(beamsout[0][0]-2)+" : "+beamsout[0][1]);
	                if( NOWP[3] == 0 && count > 40 ){
	                	 debug("aa "+NOWP[0]+" : "+NOWP[1]+" rensaP"+NOWP[2]+" beams "+NOWP[3]);
	                	println(NOWP[0] + " " + NOWP[1]);
	                }else{
	                	println((beamsout[0][0]-2) + " " + beamsout[0][1]);
	                }
    	    	}
            }
        }
    }
    //add
    int[] bestfire(int[][] simuboard){

    	int[] res = new int[4];
    	res[0] = 0;
    	res[1] = 0;
    	res[2] = 0;
    	res[3] = 0;


    	//0 h 1 w 2 score
    	int[] tmpheight = new int [width];
		tmpheight = boardheight(simuboard);

		for(int j = 0; j<width; j++){
			int i = tmpheight[j];
        	if(0<j && j <width && 0<i &&  i < height+3){
        		if(simuboard[i][j] == EMPTY){
        			for(int t = 1; t<10; t++){
                		int num = fire_check(simuboard,i,j,t);
                		if(res[2] <= num){

            				res[0] = i;
            				res[1] = j;
            				res[2] = num;
            				res[3] = t;

            				for(int bi=0; bi<height+3; bi++){
            					for(int bj=0; bj<width; bj++){
            						int tmp = checksimuboard_tmp[bi][bj];
            						checkboard[bi][bj] = tmp;
            					}
            				}

                		}
        			}
                }
        	}
		}

		return res;
    }

    int[] beams(int[][] board){

    	int[] res = new int[4];

    	beams_count = 0;
    	for(int k=0; k<beams_N; k++){
	    	for(int i=0; i<height+3; i++){
	    		for(int j=0; j<width; j++){
	    			simuboard[k][i][j] = 11;
	    		}
	    	}
    	}

    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			simuboard[0][i][j] = board[i][j];
    		}
    	}



    	//rank []  fall [0] rote[1] rensaP[2]
    	//all get
    	int[][] point = new int[width+2][3];
    	int[][][] bboard = new int[4 * point.length][height+3][width];
    	int[][] first = new int[(width+2) * 4][4];
    	int[][] beams = new int[beams_N][4];

		int tmp = 0;

		MODE = BUILD;
		point = Simu(copypack[turn],simuboard[0]);
    	for(int i=0; i<point.length; i++){
    		for(int j=0; j<4; j++){
				first[tmp][0] = i;
				first[tmp][1] = j;
				first[tmp][2] = point[i][j];
				//bboard[tmp] = beamsboard[tmp];

		    	for(int bi=0; bi<height+3; bi++){
		    		for(int bj=0; bj<width; bj++){
		    			bboard[tmp][bi][bj] = beamsboard[tmp][bi][bj];
		    		}
		    	}

				tmp++;
    		}
    	}

    	//best beams
    	for(int k=0; k<beams_N; k++){
    		beams[k][2] = -1;
    		tmp = 0;
    		int count = 0;
	    	for(int i=0; i<first.length; i++){
	    		if( first[i][2] >=0 && beams[k][2] < first[i][2]){
					beams[k][0] = first[i][0];
					beams[k][1] = first[i][1];
					beams[k][2] = first[i][2];
					beams[k][3] = first[i][3];
					tmp = i;
					count = 1;
				}
	    	}
	    	if(count == 1){
				first[ tmp ][ 2 ] = FALSE;

				//simuboard[k] = bboard[tmp];
		    	for(int i=0; i<height+3; i++){
		    		for(int j=0; j<width; j++){
		    			simuboard[k][i][j] = bboard[tmp][i][j];
		    		}
		    	}
	    	}
	    }
    	beams_count++;
    	if(beams[0][2] > FIRELINE){
    		res = beams[0];
    	}else if(beams[0][2] < FIRELINE){
    		res = beamsMain(copypack[turn+beams_count],simuboard,beams);
    	}
    	beams_count--;
		beamsout[beams_count][0] = beams[res[3]][0];
		beamsout[beams_count][1] = beams[res[3]][1];
		beamsout[beams_count][2] = beams[res[3]][2];
		beamsout[beams_count][3] = beams[res[3]][3];

    	debug("                 "+beams_count+"  beamscheck     fall :"+beamsout[beams_count][0]+" rote :"+beamsout[beams_count][1]+"  rensaP : "+beamsout[beams_count][2]);

    	return beams[res[3]];
    }

    int[] beamsMain(int[][] pack,int[][][] simuboard,int[][] b_best){


    	int[][] point = new int[width+2][3];
    	int[][] beams_best = new int[beams_N][4];


    	int[][] res = new int[beams_N * (width+2) * 4][4];
    	int[][] beams = new int[beams_N][4];
    	int[][][] bboard = new int[beams_N * point.length * 4][height+3][width];
    	int[][][] resboard = new int[beams_N][height+3][width];

    	for(int i=0; i<beams_N; i++){
    		for(int j=0; j<4; j++){
    			beams_best[i][j] =b_best[i][j];

    		}
    		//debug("b : " +beams_count+"f : "+b_best[i][0]+" r : "+b_best[i][1]+" s : "+b_best[i][2] +" a :"+b_best[i][3]);
    	}
    	//rank []  fall [0] rote[1] rensaP[2]
    	//all get
    	int bcount=0;
    	for(int bb =0; bb<beams_N; bb++){
    		int tmp = 0;
    		//if(beams_best[bb][2] >= 0){
	    		point = Simu(pack,simuboard[bb]);
		    	for(int i=0; i<point.length; i++){
		    		for(int j=0; j<4; j++){
						res[bcount][0] = i;
						res[bcount][1] = j;
						res[bcount][2] = point[i][j] + beams_best[bb][2];
				    	for(int bi=0; bi<height+3; bi++){
				    		for(int bj=0; bj<width; bj++){
				    			bboard[bcount][bi][bj] = beamsboard[tmp][bi][bj];
				    		}
				    	}
						res[bcount][3] = bb;
						bcount++;
						tmp++;
		    		}
		    	}
    		//}

    	}
    	//best beams
    	for(int k=0; k<beams_N; k++){
    		int tmp = 0;
    		int count = 0;
    		beams[k][2] = -1;
	    	for(int i=0; i<res.length; i++){
	    		if(res[i][2] >= 0 && beams[k][2] < res[i][2]){
					beams[k][0] = res[i][0];
					beams[k][1] = res[i][1];
					beams[k][2] = res[i][2];
					beams[k][3] = res[i][3];
					resboard[k] = bboard[i];
					tmp = i;
					count = 1;
				}
	    	}
	    	if(count == 1)res[ tmp ][ 2 ] = FALSE;

	    }

    	beams_count++;
    	int[] out = new int[4];
    	if(beams[0][2] < 0 || beams[0][2] > FIRELINE){
    		out = beams[0];
    	}else if(beams_end > beams_count && beams[0][2] < FIRELINE){
    		out = beamsMain(copypack[turn+beams_count],resboard,beams);
    	}else if(beams[0][2] > FIRELINE){
    		out = beams[0];
    	}
    	beams_count--;

		beamsout[beams_count][0] = 0;
		beamsout[beams_count][1] = 0;
		beamsout[beams_count][2] = 0;
		beamsout[beams_count][3] = 0;
		//big
		if(beams_count == beams_end || out[2] == 0){
	    	for(int i=0; i<beams_N; i++){
	    		if(beamsout[beams_count][2] < beams[i][2]){
			    	beamsout[beams_count][0] = beams[i][0];
			        beamsout[beams_count][1] = beams[i][1];
			        beamsout[beams_count][2] = beams[i][2];
			        beamsout[beams_count][3] = beams[i][3];

	    			out[0] = beams_best[beams[i][3]][0];
	    			out[1] = beams_best[beams[i][3]][1];
	    			out[2] = beams_best[beams[i][3]][2];
	    			out[3] = beams_best[beams[i][3]][3];
	    		}
	    	}
		}else{
	    			beamsout[beams_count][0] = beams[out[3]][0];
	    			beamsout[beams_count][1] = beams[out[3]][1];
	    			beamsout[beams_count][2] = beams[out[3]][2];
	    			beamsout[beams_count][3] = beams[out[3]][3];

	    			out[0] = beams_best[out[3]][0];
	    			out[1] = beams_best[out[3]][1];
	    			out[2] = beams_best[out[3]][2];
	    			out[3] = beams_best[out[3]][3];
		}




    	debug("                 "+beams_count+"  beamscheck     fall :"+beamsout[beams_count][0]+" rote :"+beamsout[beams_count][1]+"  rensaP : "+beamsout[beams_count][2]);

    	//0 fall 1 rote 2 score


    	return out;
    }

    int[] getBigger(int[][] point){
    	int res[] = new int[3];
    	res[0] = 6;
    	res[1] = 0;
    	res[2] = 0;
    	// j fall 0 score 1 rote
    	for(int i=0; i<point.length; i++){
    		for(int j=0; j<4; j++){
	    		if(res[2] < point[i][j]){
					res[0] = i;
					res[1] = j;
					res[2] = point[i][j];
				}
    		}

    	}

    	//res[0]= fall best [1]= then rote [2] then score
    	return res;
    }
    //Simurate
    int[][] Simu(int[][] pack, int[][] boardbefore){
    	int[][] board = new int[height+3][width];
    	//clear
    	for(int k=0; k<height+3; k++){
    		for(int j=0; j<width; j++){
    			board[k][j] = boardbefore[k][j];
    		}
    	}

    	//point[i][] fall [][i] rote [] score
    	int point[][] = new int[width+2][4];
    	for(int i=0; i<width+2; i++){
    		point[i][0] = 0;
    		point[i][1] = 0;
    		point[i][2] = 0;
    		point[i][3] = 0;
    	}
    	int tmp = 0;
		for(int k=0; k<width+2; k++){
			for(int i=0; i<4; i++){
    			point[k][i] = nextSimu(pack,board,k-2,i,tmp);
    			tmp++;
			}
		}

    	return point;
    }

    int[][] packFallSimu(int[][] pack){
    	int count = 0;
    	while(count==0){
    		count=1;
	    	for(int i=0; i<packSize-1; i++){
	        	for(int j=0; j<packSize; j++){
	                if (pack[i][j] != EMPTY && pack[i+1][j] == EMPTY) {
	                    count=0;
	                    pack[i+1][j] = pack[i][j];
	                    pack[i][j] = EMPTY;
	                }
	        	}
	    	}
    	}

    	return pack;
    }

    int[][] set_pack(int[][] pack ,int[][] simuboard ,int k){
    	SET_CHECK = FALSE;
    	int[] boardheight = boardheight(simuboard);

		if(k == -2){
			if(pack[2][0] == EMPTY && pack[2][1] == EMPTY){
		    	for(int i=0; i<packSize; i++){
		    		for(int j=2; j<packSize; j++){
		        		if(boardheight[k+j]-2+i >= 0){
		        			simuboard[boardheight[k+j]-2+i][k+j] = pack[i][j];
		        		}
		    		}
		    	}
		    	SET_CHECK = TRUE;
			}else{
				SET_CHECK = FALSE;
			}
		}else if(k == -1){
			if(pack[2][0] == EMPTY){

		    	for(int i=0; i<packSize; i++){
		        	for(int j=1; j<packSize; j++){
		        		if(boardheight[k+j]-2+i >= 0){
		        			simuboard[boardheight[k+j]-2+i][k+j] = pack[i][j];
		        		}
		        	}
		    	}
		    	SET_CHECK = TRUE;
			}else{
				SET_CHECK = FALSE;
			}
		}else if(k == 8){
			if(pack[2][2] == EMPTY){
		    	for(int i=0; i<packSize; i++){
		        	for(int j=0; j<packSize-1; j++){
		        		if(boardheight[k+j]-2+i >= 0){
		        			simuboard[boardheight[k+j]-2+i][k+j] = pack[i][j];
		        		}
		        	}
		    	}
		    	SET_CHECK = TRUE;
			}else{
				SET_CHECK = FALSE;
			}
		}else if(k == 9){
			if(pack[2][2] == EMPTY && pack[2][1] == EMPTY){
		    	for(int i=0; i<packSize; i++){
		        	for(int j=0; j<packSize-2; j++){
		        		if(boardheight[k+j]-2+i >= 0){
		        			simuboard[boardheight[k+j]-2+i][k+j] = pack[i][j];
		        		}
		        	}
		    	}
		    	SET_CHECK = TRUE;
			}else{
				SET_CHECK = FALSE;
			}
		}else{
	    	for(int i=0; i<packSize; i++){
	        	for(int j=0; j<packSize; j++){
	        		if(boardheight[k+j]-2+i >= 0){
	        			simuboard[boardheight[k+j]-2+i][k+j] = pack[i][j];
	        		}
	        	}
	    	}
	    	SET_CHECK = TRUE;
	    }
		return simuboard;
    }

    int nextSimu(int[][] pack,int[][] board,int k,int rote,int c){
    	int[][] simuboard = new int[height+3][width];
    	int[][] simupack = new int[packSize][packSize];

    	int score = 0;
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			simuboard[i][j] = board[i][j];
    		}
    	}
    	simupack = packFallSimu(packRotate(pack,rote));
		simuboard = set_pack(simupack,simuboard,k);


		if(SET_CHECK == TRUE){
			score =rensaPointSimu(simuboard,k,rote,c);
		}else{
			score =FALSE;
		}

    	// rensa score return
    	return score;
    }

	int rensaPointSimu(int[][] board,int fall,int rote, int c){
		rensaPoint =0;

		int score = FALSE;
    	int[][] simuboard = new int[height+3][width];
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			simuboard[i][j] = board[i][j];
    		}
    	}
    	for(int bi=0; bi<height+3; bi++){
    		for(int bj=0; bj<width; bj++){
    			beamsboard[c][bi][bj] = 11;
    		}
    	}

		simuboard = scoreSimu(simuboard);


        int sum = 0;
        for(int i=0; i<width; i++){
        	if(simuboard[2][i] == EMPTY){
        		sum ++;
        	}
        }
        if(sum == 10){
        	if(NOWP[2] < rensaPoint){
        		NOWP[0] = fall;
        		NOWP[1] = rote;
        		NOWP[2] = rensaPoint;
        		NOWP[3] = beams_count;
        	}
	    	for(int bi=0; bi<height+3; bi++){
	    		for(int bj=0; bj<width; bj++){
	    			beamsboard[c][bi][bj] = simuboard[bi][bj];
	    		}
	    	}
    		score =volSimu(rensaPoint,simuboard);
        }else{
        	score = FALSE;
        }



        return score;
	}

	int volSimu(int p,int[][] simuboard){
		if(p < 0)return p;
		if(rensa > 1)return 0;

    	int[] bheight = new int[width];
    	bheight = boardheight(simuboard);
		int score = 0;
		/*if(p > 400){
			score +=  FIRELINE;
			return score;
		}*/


    	//rensa
    	//if(rensa == 0)score += 30000;

    	//rensa kitaiti
    	int[] f = new int[4];
		f = bestfire(simuboard);
		score += f[2];
		score += pat(checkboard);
/*
		int[] heightscore = new int[height+3];
		for(int i=0; i<height+3; i++){
			if(i < 4)heightscore[i] = 0;
			if(i >= 4)heightscore[i] = i*10;
		}
*/
		int tmp = 20;
		int ss = 0;
		for(int i=0; i<width; i++){
			if(bheight[i] < tmp){
				ss = i;
				tmp = bheight[i];
			}
		}

		int ss2 = 0;
		tmp = 20;
		for(int i=0; i<width; i++){
			if(bheight[i] < tmp && ss != i){
				ss2 = i;
				tmp = bheight[i];
			}
		}
/*
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			if(bheight[j] < i){
    				if(simuboard[i][j]!=EMPTY && checkboard[i][j] == EMPTY){
    					score += 100;
        	    		if(ss == j)score += 200;
        	    		if(ss2 == j)score += 200;
    				}
    			}
    		}
    	}
*/
    	/*
		for(int i=0; i<width; i++){
			if(simuboard[18][i] == EMPTY)score += 0;
		}*/

		//f = bestfire(checkboard);
/*
		score += (f[2]/2);
		score += (pat(checkboard)/2);
*/
    	//int[] f1 = new int[4];
		//f1 = bestfire(checkboard);

		//score += pat(checkboard);


/*
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			if(simuboard[i][j]!=EMPTY && checkboard[i][j] == EMPTY)score+= 500;
    		}
    	}
*/

    	//height


		/*
    	for(int i=0; i<width-3; i++){
    		for(int j=bheight[i]; j<bheight[i+1]; j++){
    			score -= 1000;
    		}
    	}*/

    	/*
    	for(int i=width-1; i>5; i--){
    		for(int j=bheight[i-1]; j<bheight[i]; j++){
    			score -= 1000;
    		}
    	}*/


    	for(int i=1; i<width-1; i++){
    		int s = bheight[i]+1;
    		if(s < 19){
    			if(checkboard[s][i+1] != EMPTY)score += 500;
    			if(checkboard[s][i-1] != EMPTY)score += 500;
    		}
    	}

/*
    	for(int i=0; i<height+3; i++){
    		for(int j=1; j<width-1; j++){
    			if(simuboard[i][j-1] != EMPTY)score += 50;
    			if(simuboard[i][j+1] != EMPTY)score += 50;
    		}
    	}
*/
    	/*
    	for(int i=0; i<height+3; i++){
    		if(simuboard[i][0]== EMPTY)score += 100;
    		if(simuboard[i][1]== EMPTY)score += 40;
    		if(simuboard[i][6]!= EMPTY)score += 40;
    		if(simuboard[i][7]!= EMPTY)score += 40;
    		if(simuboard[i][8]!= EMPTY)score += 20;
    	}
*/
		//if(checkboard[FIREPOINT_H][FIREPOINT_W] == check_keima2) score +=10000;

		return score;

	}

	int pat(int[][] simuboard){

		int[][] checkboard = new int[height+3][width];
		int score =0;
		int check_keima = 5;
		int check_keima2 = 10;
		int check =50;
		int keima = 50;
		int yoko = 50;
		int tate = 30;
		int left = 1;

    	//katati no score
    	for(int i=height+2; i>0; i--){
    		for(int j=0; j<width; j++){
    			//keima
    			if(i <= height && j < width-1){
    				//migi
    				if(simuboard[i][j] + simuboard[i+2][j+1] == 10 ){
    					score += keima;
    					checkboard[i][j] = check_keima;
    					checkboard[i+2][j+1] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
				if(i <= height && j > left){
    				if(simuboard[i][j] + simuboard[i+2][j-1] == 10 ){
    					score += keima;
    					checkboard[i][j] = check_keima;
    					checkboard[i+2][j-1] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			//yoko
    			if(i < height+2 && j < width-3){
    				if(simuboard[i][j] + simuboard[i+1][j+1] + simuboard[i+1][j+2] + simuboard[i+1][j+3] == 10 ) {
    					score += yoko;
    					checkboard[i][j] = check_keima;
    					checkboard[i+1][j+1] = check_keima2;
    					checkboard[i+1][j+2] = check_keima2;
    					checkboard[i+1][j+3] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			if(i < height+2 && j > left+2){
    				if(simuboard[i][j] + simuboard[i+1][j-1] + simuboard[i+1][j-2] + simuboard[i+1][j-3] == 10 ){
    					score += yoko;
    					checkboard[i][j] = check_keima;
    					checkboard[i+1][j-1] =check_keima2;
    					checkboard[i+1][j-2] = check_keima2;
    					checkboard[i+1][j-3] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			//yoko
    			if(i < height+2 && j < width-2){
    				if(simuboard[i][j] + simuboard[i+1][j+1] + simuboard[i+1][j+2] == 10) {
    					score += yoko;
    					checkboard[i][j] = check_keima;
    					checkboard[i+1][j+1] =check_keima2;
    					checkboard[i+1][j+2] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			if(i < height+2 && j > left+1){
    				if(simuboard[i][j] + simuboard[i+1][j-1] + simuboard[i+1][j-2] == 10 ){
    					score += yoko;
    					checkboard[i][j] = check_keima;
    					checkboard[i+1][j-1] = check_keima2;
    					checkboard[i+1][j-2] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			if(i < height-1 && j < width-2){
    				//migi
    				if(simuboard[i][j] + simuboard[i+2][j+1] + simuboard[i+3][j+2] == 10){
    					score += keima;
    					checkboard[i][j] = check_keima;
    					checkboard[i+2][j+1] = check_keima2;
    					checkboard[i+3][j+2] = check_keima2;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
				//hidari
				if(i < height-1 && j > left+1 ){
    				if(simuboard[i][j] + simuboard[i+2][j-1] + simuboard[i+3][j-2] == 10){
    					score += keima;
    					checkboard[i][j] = check_keima;
    					checkboard[i+2][j-1] = check_keima2;
    					checkboard[i+3][j-2] = check_keima2;
    				}
    			}
    		}
    	}

    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			//tate
    			if(i <= height ){
    				if(simuboard[i][j] + simuboard[i+2][j] == 10){
    					score += tate;
    					checkboard[i][j] = check_keima;
    					checkboard[i+2][j] = check_keima2;
    				}
    			}
    		}
    	}
/*
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			if(i < height){
    				if(checkboard[i][j] + checkboard[i+2][j] + checkboard[i+3][j]== 10){
    					score += tate;
    				}
    			}
    		}
    	}
    	for(int i=height+3; i>0; i--){
    		for(int j=0; j<width; j++){
    			if(i < height-1 ){
    				if(checkboard[i][j] + checkboard[i+1][j] + checkboard[i+3][j] == 10 ){
    					score += tate;
    				}
    			}
    		}
    	}
*/
/*
    	for(int i=1; i<height+2; i++){
    		for(int j=1; j<width-1; j++)
				if(checkboard[i][j] != EMPTY){
					if(checkboard[i-1][j-1] ==  check_keima || checkboard[i-1][j] ==  check_keima2)score += check;
					if(checkboard[i-1][j] ==  check_keima || checkboard[i-1][j] ==  check_keima2)score += check;
					if(checkboard[i-1][j+1] ==  check_keima || checkboard[i-1][j+1] ==  check_keima2)score += check;
					if(checkboard[i][j-1] ==  check_keima || checkboard[i][j-1] ==  check_keima2)score += check;
					if(checkboard[i][j+1] ==  check_keima || checkboard[i][j+1] ==  check_keima2)score += check;
					if(checkboard[i+1][j-1] ==  check_keima || checkboard[i+1][j-1] ==  check_keima2)score += check;
					if(checkboard[i+1][j] ==  check_keima || checkboard[i+1][j] ==  check_keima2)score += check;
					if(checkboard[i+1][j+1] ==  check_keima || checkboard[i+1][j+1] ==  check_keima2)score += check;
				}
    	}*/
/*
    	for(int i=1; i<height+2; i++){
    		for(int j=1; j<width-1; j++){
    			int a =checkboard[i-1][j-1] + checkboard[i-1][j] + checkboard[i-1][j+1] +
    			checkboard[i][j-1] + checkboard[i][j] + checkboard[i][j+1] +
    			checkboard[i+1][j-1] + checkboard[i+1][j] + checkboard[i+1][j+1];

    			if(36 <= a && a <= 54 )score += check;

    		}
    	}
    	*/
/*
    	for(int i=height+2; i>0; i--){
    		for(int j=0; j<width; j++){
				if(checkboard[i][j] == check_keima2){
					score += keimascore2;
				}

    		}
    	}*/
		return score;
	}

	int fire_check(int[][] board,int h,int w, int n){
		int[][] simuboard = new int[height+3][width];
		for(int i=0; i<height+3; i++){
			for(int j=0; j<width; j++){
				simuboard[i][j] = board[i][j];
			}
		}

		int score = 0;
		if(simuboard[h][w] == 11){
			return score;
		}
		simuboard[h][w] = n;

		for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			checkboard_tmp[i][j] = EMPTY;
    			checksimuboard_tmp[i][j] = EMPTY;
    		}

    	}

		checksimuboard_tmp = scoreSimu(simuboard);
		if(CLEARCHECK == FALSE && rensa <15)return FALSE;
		 /*
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			int tmp = checkboard_tmp[i][j];
    			if(tmp != EMPTY){
    				checksimuboard_tmp[i-tmp][j] = checksimuboard_tmp[i][j];
    				checksimuboard_tmp[i][j] = EMPTY;
    			}
    		}
    	}*/
    	score +=  rensaPoint*1000;

		return score;
	}
    int[][] setSimuboard(int[][] board){
    	int[][] simuboard = new int[height+3][width];
    	for(int i=0; i<3; i++){
    		for(int j=0; j<width; j++){
    			simuboard[i][j] = EMPTY;
    		}
    	}
    	for(int i=0; i<height; i++){
    		for(int j=0; j<width; j++){
    			simuboard[i+3][j] = board[i][j];
    		}
    	}
    	return simuboard;
    }
    int[][] scoreSimu(int[][] board){
    	int[][] simuboard = new int[height+3][width];
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			simuboard[i][j] = board[i][j];
    		}
    	}
    	rensaPoint = 0;
    	rensa = 0;
    	CLEARCHECK = TRUE;
    	while(true){
    		int[][] scoreboard= SimuMain(simuboard);
	        simuboard = erase(scoreboard,simuboard);
	        if(clearCount > 0)rensa++;
	        rensaPoint  += ( ((int)Math.pow(1.3,rensa)) * ((int)(clearCount/2)) );
	        if(clearCount == 0)break;
    	}
    	return simuboard;
    }

    int[][] SimuMain(int[][] board){
    	int[][] scoreboard = new int[height+3][width];
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			scoreboard[i][j] = EMPTY;
    		}
    	}
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
        		int sum = 0;
        		//yoko
        		for(int k=j; k<width; k++){
        			sum += board[i][k];
        			if(board[i][k] == EMPTY || board[i][k] == 11){
        				break;
        			}
            		if(sum == 10){
            			for(int n=j; n<=k; n++){
            				scoreboard[i][n] += 1;
            			}
            			break;
            		}
        		}
        		sum = 0;
        		int tmp = i;
        		//naname ue
        		for(int k=j; k<width; k++){
        			sum += board[tmp][k];
        			if(board[tmp][k] == EMPTY || board[tmp][k] == 11){
        				break;
        			}
            		if(sum == 10){
            			int m = i;
            			for(int n=j; n<=k; n++){
            				scoreboard[m][n] += 1;
            				m--;
            			}
            			break;
            		}
            		tmp--;
            		if(tmp < 0){
            			break;
            		}
        		}
        		sum = 0;
        		tmp = i;
        		//naname sita
        		for(int k=j; k<width; k++){
        			sum += board[tmp][k];
        			if(board[tmp][k] == EMPTY || board[tmp][k] == 11){
        				break;
        			}
            		if(sum == 10){
            			int m = i;
            			for(int n=j; n<=k; n++){
            				scoreboard[m][n] += 1;
            				m++;
            			}
            			break;
            		}
            		tmp++;
            		if(tmp > height+2){
            			break;
            		}
        		}
        		sum = 0;
        		//tate
        		for(int k=i; k<height+3; k++){
        			sum += board[k][j];
        			if(board[k][j] == EMPTY || board[k][j] == 11){
        				break;
        			}
            		if(sum == 10){
            			for(int n=i; n<=k; n++){
            				scoreboard[n][j] += 1;
            			}
            			break;
            		}
        		}
    		}
    	}
    	return scoreboard;
    }

    int[][] erase(int[][] scoreboard ,int[][] simuboard){
    	clearCount = 0;
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			if(scoreboard[i][j]  != EMPTY){
    				clearCount += scoreboard[i][j];
    				scoreboard[i][j] = EMPTY;
    				simuboard[i][j] = EMPTY;
    			}
    		}
    	}
    	//if(clearCount > 3)CLEARCHECK = FALSE;
    	//seiri
    	while(true){
    		int count = 0;
        	for(int i=0; i<height+2; i++){
        		for(int j=0; j<width; j++){
                	if(simuboard[i+1][j] == EMPTY && simuboard[i][j] != EMPTY){
                		simuboard[i+1][j] = simuboard[i][j];
                		simuboard[i][j] = EMPTY;
                		count = 1;
                		int tmp = checkboard_tmp[i][j] + 1;
                		checkboard_tmp[i+1][j] = tmp;
                		checkboard_tmp[i][j] = EMPTY;
                	}
        		}
        	}
        	if(count == 0){
        		break;
        	}
    	}
    	return simuboard;
    }

    int[] boardheight(int[][] board){
    	int boardheight[] = new int[width];
    	for(int i=0; i<height+3; i++){
    		for(int j=0; j<width; j++){
    			if(board[i][j] == EMPTY ){
    				boardheight[j] = i;
    			}
    		}
    	}
    	return boardheight;
    }

    void printboard(String a,int[][] board){
    	debug(a);
    	for(int i=0; i<height+3; i++){
    		String sum ="";
    		for(int j=0; j<width; j++){
    			sum += " " + board[i][j];
    		}
    		debug(sum);
    	}
    }

    void printpack(String a,int[][] pack){
    	debug(a);
    	for(int i=0; i<packSize; i++){
    		String sum ="";
    		for(int j=0; j<packSize; j++){
    			sum += " " + pack[i][j];
    		}
    		debug(sum);
    	}
    }

    int[][] packRotate(int[][] pack, int rot) {
        int[][] res = copyPack(pack);
        for (int i = 0; i < rot; ++i) {
            res = rot1(res);
        }
        return res;
    }

    int[][] rot1(int[][] pack) {
        int[][] res = copyPack(pack);
        for (int i = 0; i < packSize; ++i) {
            for (int j = 0; j < packSize; ++j) {
                res[j][packSize - i - 1] = pack[i][j];
            }
        }
        return res;
    }

    int[][] fillObstaclePack(int[][] pack, int obstacleNum) {
        int[][] res = copyPack(pack);
        for (int i = 0; i < packSize; ++i) {
            for (int j = 0; j < packSize; ++j) {
                if (obstacleNum > 0 && res[i][j] == EMPTY) {
                    --obstacleNum;
                    res[i][j] = obstacle;
                }
            }
        }
        return res;
    }

    int[][] copyPack(int[][] pack) {
        int[][] res = new int[packSize][];
        for (int i = 0; i < packSize; ++i) {
            res[i] = Arrays.copyOf(pack[i], packSize);
        }
        return res;
    }

    void println(String msg) {
        System.out.println(msg);
        System.out.flush();
    }

    void debug(String msg) {
        System.err.println(msg);
        System.err.flush();
    }
}
