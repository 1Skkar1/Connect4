import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.concurrent.ThreadLocalRandom;

public class Connect4 {

	private static Scanner in = new Scanner(System.in);
	private static final char playerChip = 'O'; //player is always O
	private static final char cpuChip = 'X';    //cpu is always X
	public static char[][] iniBoard = new char[6][7];
	static int aiMove = -1;
	static int genNodes = 0;
	static int visNodes = 0;
	static double epsilon = 1e-6;
	//public static int genNodes=0, visNodes=0, moves=0;

	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();
	} 

	public static void showLogo(){
		System.out.println("\n\n\n\n");
		System.out.println("     ██████╗ ██████╗ ███╗   ██╗███╗   ██╗███████╗ ██████╗████████╗   ██╗  ██╗");
		System.out.println("    ██╔════╝██╔═══██╗████╗  ██║████╗  ██║██╔════╝██╔════╝╚══██╔══╝   ██║  ██║");
		System.out.println("    ██║     ██║   ██║██╔██╗ ██║██╔██╗ ██║█████╗  ██║        ██║█████╗███████║");
		System.out.println("    ██║     ██║   ██║██║╚██╗██║██║╚██╗██║██╔══╝  ██║        ██║╚════╝╚════██║");
		System.out.println("    ╚██████╗╚██████╔╝██║ ╚████║██║ ╚████║███████╗╚██████╗   ██║           ██║");
		System.out.println("     ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝ ╚═════╝   ╚═╝           ╚═╝");
        System.out.println("\n\n\n\n"); 
	}

	public static void mkBoard(char[][] board){
		for(int i=0; i<6; i++){
			for(int j=0; j<7; j++){
				board[i][j] = '-';
			}
		}
	}

	public static void printBoard(Node nd, int resStrat, int maxDepth){
		System.out.println("+---------------------------+");
		for(int i=0; i<6; i++){
			System.out.print("| ");
			for(int j=0; j<7; j++){
				System.out.print(nd.board[i][j] + " | ");
			}
			if(i == 0)
				System.out.print("\tINFO:");
			else if(i == 1){
				if(resStrat == 1)
					System.out.print("\tALGORITHM: Minimax");
				else if(resStrat == 2)
					System.out.print("\tALGORITHM: Alpha-Beta");
				else
					System.out.print("\tALGORITHM: MCTS");
			}
			else if(i == 2)
				System.out.print("\tMAX DEPTH: " + maxDepth);
			else if(i == 3)
				System.out.print("\tYOU: 'O'");
			else if(i == 4)
				System.out.print("\tCPU: 'X'");
			System.out.println();
		}
		System.out.println("+---+---+---+---+---+---+---+");
		System.out.println("| 0 | 1 | 2 | 3 | 4 | 5 | 6 |");
	}

	public static void mkMove(Node nd, int col, char chip){
		int i=0;
		for(i=5; i>=0; i--){
			if(nd.board[i][col] == '-')
				break;
		}
		if(i<0)
			return;
		//System.out.println("i: "+i);
		//System.out.println("chip: "+chip);
		nd.board[i][col] = chip;
	}

	public static boolean validMove(Node nd, int col){
		if(nd.board[0][col] != '-')
			return false;
		else
			return true;
	}

	public static char[][] mkClone(char[][] board){
		char[][] clone = new char[6][7];

		for(int i=0; i<6; i++){
			for(int j=0; j<7; j++){
				clone[i][j] = board[i][j];
			}
		}
		return clone;
	}

	public static boolean fullBoard(Node nd){
		for(int i=0; i<6; i++){
			for(int j=0; j<7; j++){
				if(nd.board[i][j] == '-')
					return false;
			}
		}
		return true;
	}


/*
-50 for three Os, no Xs,
-10 for two Os, no Xs,
-1 for one O, no Xs,
0 for no tokens, or mixed Xs and Os,
1 for one X, no Os,
10 for two Xs, no Os,
50 for three Xs, no Os.
*/

	public static int utilVert(Node nd, int a, int b){
		int nPlayer = 0; //O
		int nCPU = 0;    //X
		int nSquares = 0;

		for(int i=0; i<4; i++){
			if((a+i) < 6){
				if(nd.board[a+i][b] == cpuChip)
					nCPU++;
				else if(nd.board[a+i][b] == playerChip)
					nPlayer++;
				nSquares++;
			}
		}

		if(nSquares == 4){
			if(nCPU > 0 && nPlayer == 0){
				if(nCPU == 1)
					return 1;
				else if(nCPU == 2)
					return 10;
				else if(nCPU == 3)
					return 50;
				else if(nCPU == 4)
					return 512;
			}
			if(nCPU == 0 && nPlayer > 0){
				if(nPlayer == 1)
					return -1;
				else if(nPlayer == 2)
					return -10;
				else if(nPlayer == 3)
					return -50;
				else if(nPlayer == 4)
					return -512;
			}
		}
		return 0;
	}

	public static int utilHori(Node nd, int a, int b){
		int nPlayer = 0; //O
		int nCPU = 0;    //X
		int nSquares = 0;

		for(int i=0; i<4; i++){
			if((b+i) < 7){
				if(nd.board[a][b+i] == cpuChip)
					nCPU++;
				else if(nd.board[a][b+i] == playerChip)
					nPlayer++;
				nSquares++;
			}
		}

		if(nSquares == 4){
			if(nCPU > 0 && nPlayer == 0){
				if(nCPU == 1)
					return 1;
				else if(nCPU == 2)
					return 10;
				else if(nCPU == 3)
					return 50;
				else if(nCPU == 4)
					return 512;
			}
			if(nCPU == 0 && nPlayer > 0){
				if(nPlayer == 1)
					return -1;
				else if(nPlayer == 2)
					return -10;
				else if(nPlayer == 3)
					return -50;
				else if(nPlayer == 4)
					return -512;
			}
		}
		return 0;
	}

	public static int utilRDiag(Node nd, int a, int b){
		int nPlayer = 0; //O
		int nCPU = 0;    //X
		int nSquares = 0;

		for(int i=0; i<4; i++){
			if((a-i) >= 0 && (b-i) >= 0){
				if(nd.board[a-i][b-i] == cpuChip)
					nCPU++;
				else if(nd.board[a-i][b-i] == playerChip)
					nPlayer++;
				nSquares++;
			}
		}

		if(nSquares == 4){
			if(nCPU > 0 && nPlayer == 0){
				if(nCPU == 1)
					return 1;
				else if(nCPU == 2)
					return 10;
				else if(nCPU == 3)
					return 50;
				else if(nCPU == 4)
					return 512;
			}
			if(nCPU == 0 && nPlayer > 0){
				if(nPlayer == 1)
					return -1;
				else if(nPlayer == 2)
					return -10;
				else if(nPlayer == 3)
					return -50;
				else if(nPlayer == 4)
					return -512;
			}
		}
		return 0;
	}

	public static int utilLDiag(Node nd, int a, int b){
		int nPlayer = 0; //O
		int nCPU = 0;    //X
		int nSquares = 0;

		for(int i=0; i<4; i++){
			if((a-i) >= 0 && (b+i) < 7){
				if(nd.board[a-i][b+i] == cpuChip)
					nCPU++;
				else if(nd.board[a-i][b+i] == playerChip)
					nPlayer++;
				nSquares++;
			}
		}

		if(nSquares == 4){
			if(nCPU > 0 && nPlayer == 0){
				if(nCPU == 1)
					return 1;
				else if(nCPU == 2)
					return 10;
				else if(nCPU == 3)
					return 50;
				else if(nCPU == 4)
					return 512;
			}
			if(nCPU == 0 && nPlayer > 0){
				if(nPlayer == 1)
					return -1;
				else if(nPlayer == 2)
					return -10;
				else if(nPlayer == 3)
					return -50;
				else if(nPlayer == 4)
					return -512;
			}
		}
		return 0;
	}

	public static int gameOver(Node nd){
		//    0 -> not over
		//    1 -> draw
		// -512 -> player wins
		//  512 -> cpu wins
		int win;
		for(int i=0; i<6; i++){
			for(int j=0; j<7; j++){
				if((win = utilVert(nd,i,j)) != 0){
					if(win == -512)
						return -512;
					else if(win == 512)
						return 512;
				}

				if((win = utilHori(nd,i,j)) != 0){
					if(win == -512)
						return -512;
					else if(win == 512)
						return 512;
				}

				if((win = utilRDiag(nd,i,j)) != 0){
					if(win == -512)
						return -512;
					else if(win == 512)
						return 512;
				}

				if((win = utilLDiag(nd,i,j)) != 0){
					if(win == -512)
						return -512;
					else if(win == 512)
						return 512;
				}
			}
		}

		if(fullBoard(nd))
			return 1;

		return 0;
	}

	public static int boardScore(Node nd){
		int totScore = 0;
		int gameOver = gameOver(nd);

		for(int i=0; i<6; i++){
			for(int j=0; j<7; j++){
				totScore += utilVert(nd,i,j);
				totScore += utilHori(nd,i,j);
				totScore += utilRDiag(nd,i,j);
				totScore += utilLDiag(nd,i,j);
			}
		}
		return totScore;	
	}

	public static boolean terminal(Node nd, int maxDepth){
		if(gameOver(nd) != 0)
			return true;
		else if(nd.depth == maxDepth)
			return true;
		else
			return false;
	}

	public static boolean terminal(Node nd){
		if(gameOver(nd) != 0)
			return true;
		else
			return false;
	}



	public static void minimax(Node nd, int maxDepth){
		int val;
		val = MM_max(nd,maxDepth);
	}

	public static int MM_max(Node nd, int maxDepth){
		visNodes++;
		genNodes += 7;

		if(terminal(nd,maxDepth))
			return boardScore(nd);

		int val = Integer.MIN_VALUE;
		int max = Integer.MIN_VALUE;

		for(int i=0; i<7; i++){
			genNodes++;
			if(!validMove(nd,i))
				continue;

			Node n = new Node(mkClone(nd.board), nd.depth+1, 0);
			mkMove(n, i, cpuChip);
			val = Math.max(val, MM_min(n,maxDepth));

			if(val > max){
				max = val;
				if(n.depth == 1){
					aiMove = i;
				}
			}
			//System.out.println("aimove:"+aiMove);
		}
		return val;
	}

	public static int MM_min(Node nd, int maxDepth){
		visNodes++;
		genNodes += 7;

		if(terminal(nd,maxDepth))
			return boardScore(nd);

		int val = Integer.MAX_VALUE;

		for(int i=0; i<7; i++){
			if(!validMove(nd,i))
				continue;

			Node n = new Node(mkClone(nd.board), nd.depth+1, 0);
			mkMove(n, i, playerChip);
			val = Math.min(val, MM_max(n,maxDepth));
		}
		return val;
	}

	public static void alphabeta(Node nd, int maxDepth){
		int val;
		val = AB_max(nd,maxDepth,Integer.MIN_VALUE,Integer.MAX_VALUE);
	}

	public static int AB_max(Node nd, int maxDepth, int alpha, int beta){
		visNodes++;
		genNodes += 7;

		if(terminal(nd,maxDepth))
			return boardScore(nd);

		int val = Integer.MIN_VALUE;
		int max = Integer.MIN_VALUE;

		for(int i=0; i<7; i++){
			genNodes++;
			if(!validMove(nd,i))
				continue;

			Node n = new Node(mkClone(nd.board), nd.depth+1, 0);
			mkMove(n, i, cpuChip);
			val = Math.max(val, AB_min(n,maxDepth,alpha,beta));

			if(val > max){
				max = val;
				if(n.depth == 1)
					aiMove = i;
			}

			if(val >= beta)
				return val;

			alpha = Math.max(alpha,val);
		}
		return val;
	}

	public static int AB_min(Node nd, int maxDepth, int alpha, int beta){
		visNodes++;
		genNodes += 7;

		if(terminal(nd,maxDepth))
			return boardScore(nd);

		int val = Integer.MAX_VALUE;

		for(int i=0; i<7; i++){
			if(!validMove(nd,i))
				continue;

			Node n = new Node(mkClone(nd.board), nd.depth+1, 0);
			mkMove(n, i, playerChip);
			val = Math.min(val, AB_max(n,maxDepth,alpha,beta));

			if(val <= alpha)
				return val;

			beta = Math.min(beta,val);
		}
		return val;
	}

	public static int montecarloTS(Node nd, int maxDepth){
		double totValue=0;
		double val=0;
		int bestMove=0;
		int moves = 0;
		int maxVal = Integer.MIN_VALUE;

		visNodes++;
		genNodes += 7;

		Node root = new Node(mkClone(nd.board), nd.depth, 0, 1, null);

		for(int i=0; i<100; i++){
			LinkedList<Node> visPath = new LinkedList<Node>();
			visPath.add(root);
		
			Node aux = root;
			while(aux.children.size() != 0){
				aux = selectUCT(aux,aux.children);
				visPath.add(aux);
			}

			Node newChild = new Node(mkClone(root.board), root.depth+1, 0, 1, root);
			visPath.add(newChild);

			val = rollOut(newChild,maxDepth);

			for(Node nn : visPath)
				nn.updateStats(val);
			
			for(Node nn : visPath){
				if(nn.depth == 1){
					if(nn.score > maxVal){
						maxVal = nn.score;
						bestMove = nn.move;
					}
				}
			}
		}
		return bestMove;
	}

	public static Node selectUCT(Node nd, LinkedList<Node> children){
		double val = Double.MIN_VALUE;
		int vScore;
		Node selected = null;

		for(Node n : children){
			vScore = boardScore(n);
			double valUCT = vScore + Math.sqrt(Math.log(nd.nVisits) / (n.nVisits));

			if(valUCT > val){
				selected = n;
				val = valUCT;
			}
		}
		return selected;
	}

	public static double rollOut(Node nd, int maxDepth){
		int moves = 0;
		Node myBoard = new Node(mkClone(nd.board), 0, 0, 0, null);

		while(!terminal(myBoard)){
			int randomNum = ThreadLocalRandom.current().nextInt(0, 6+1);
			while(!validMove(nd,randomNum))
				randomNum = ThreadLocalRandom.current().nextInt(0, 6+1);
			
			//System.out.println("rand "+randomNum);
			if(moves % 2 == 0)
				mkMove(myBoard, randomNum, cpuChip);
			else
				mkMove(myBoard, randomNum, playerChip);

			moves++;
		}
		return boardScore(myBoard);
	}





	public static void main(String[] args){
		//----------INTRO SCREEN---------------------------
		clearScreen();
		showLogo();
		                                                      
		//----------MAIN MENU---------------------------
		System.out.println("CHOOSE ALGORITHM FOR CPU OPPONENT:");
		System.out.println(" 1) Minimax");
		System.out.println(" 2) Alpha-Beta");
		System.out.println(" 3) Monte Carlo Tree Search");
		int resStrat = in.nextInt();

		clearScreen();
		showLogo();

		System.out.println("INSERT MAX DEPTH:");
		int maxDepth = in.nextInt();

		clearScreen();
		showLogo();

		System.out.println("WHO PLAYS FIRST?");
		System.out.println(" 1) Player ( O )");
		System.out.println(" 2) CPU    ( X )");
		int firstPlayer = in.nextInt();

		clearScreen();
		showLogo();
		
		mkBoard(iniBoard);
		Node board = new Node(iniBoard,0,0);
		
		int whoWin=0, play=0, fin=0, moves=0;

		while(fin==0){
			printBoard(board,resStrat,maxDepth);
			long iniClock=0;

			if((firstPlayer == 1 && moves%2 == 0) || (firstPlayer == 2 && moves%2 != 0)){ //player is first
				System.out.println("CHOOSE YOUR MOVE:");
				play = in.nextInt();
				mkMove(board,play,playerChip);
				fin = gameOver(board);
				clearScreen();
				moves++;
			}

			else{ //cpu plays
				genNodes = visNodes = 0;
				iniClock = System.nanoTime();

				switch(resStrat){
					case 1 :
						minimax(board,maxDepth);
						System.out.println("score: "+ boardScore(board));
						mkMove(board,aiMove,cpuChip);
						break;
					case 2 :
						alphabeta(board,maxDepth);
						mkMove(board,aiMove,cpuChip);
						break;
					case 3 :
						play = montecarloTS(board,maxDepth);
						break;
					default :
						System.out.println("Invalid Difficulty!");
				}

				long finClock = System.nanoTime() - iniClock;
				float finClockSecs = (float) finClock/1000000000;
				clearScreen();
				System.out.println("PLAY STATS:");
				System.out.println("CPU played on column: " + aiMove);
				System.out.printf("Running Time: %.4f seconds\n" , finClockSecs);
				System.out.println("Generated Nodes: " + genNodes);
				System.out.println("Visited Nodes: " + visNodes);
				moves++;
			}
			fin = gameOver(board);
			if (fin != 0)
				printBoard(board,resStrat,maxDepth);
		}

		if(fin == 2)
			System.out.println("DRAW!");

		else if(fin == -512)
			System.out.println("YOU WIN!");

		else if(fin == 512)
			System.out.println("YOU LOSE!");
	}
}

class Node{
	char[][] board = new char[6][7];
	int depth;
	int score;
	int nVisits;
	int move;
	Node parent;
	LinkedList<Node> children;

	public Node(char[][] board, int depth, int score){
		this.board = board;
		this.depth = depth;
		this.score = score;
	}
	
	public Node(char[][] board, int depth, int score, int nVisits, Node nd){
		this.board = board;
		this.depth = depth;
		this.score = score;
		this.nVisits = nVisits;
		this.parent = nd;
		this.children = new LinkedList<Node>();
	}

	public void updateStats(double totValue){
		this.nVisits++;
		this.score += totValue;
	}

	public void setMove(int move){
		this.move = move;
	}

	public char[][] getBoard(){
		return board;
	}

	public int getDepth(){
		return depth;
	}

	public int getScore(){
		return score;
	}
}


	
