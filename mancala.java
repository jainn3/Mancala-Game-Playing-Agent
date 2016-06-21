import java.io.*;
import java.util.*;

/**
 * @author Nimesh
 *
 */

class Node
{
	//  3 3 3
	//0(0) 		0
	//	3(1) 3 3 
	//player one and player2
	int board[];
	int player1length;
	int player2length;
	int totalsize;
	int player1Mancala;
	int player2Mancala;
	Node (){}
	//player 1 is below and player 2 is above always.
	Node(String p2, String p1, int iniStonesp2, int iniStonesp1)
	{
		//splitting 3 3 3 into an array
		String[] player1 = p1.split(" ");
		String[] player2 = p2.split(" ");
		
		//finding p1, p2 and total length. //storing the lengths as object properties.
		int lengthP1 = player1length = player1.length;
		int lengthP2 = player2length = player2.length;
		int totalSize = totalsize = lengthP1 + lengthP2 + 2;
		
		//location of mancalas
		player2Mancala = 0;
		player1Mancala = player1length+1;
		
		//initializing the board. Note total size = N + N + 2. N can be very large.
		//Number of elements in each hole can be as large as 1000
		board = new int[totalSize];
		
		//creating initial board game.
		board[player2Mancala] = iniStonesp2;
		board[player1Mancala] = iniStonesp1;
		//creating board for player 1
		for(int i=0; i<lengthP1;i++)
		{
			board[i+1]  = Integer.parseInt(player1[i]);
		}
		
		//creating board for player 2
		for(int i=0; i<lengthP2;i++)
		{					
			board[totalSize-1-i]  = Integer.parseInt(player2[i]);		
		}
	}
	
	public Node(Node obj)
	{
		board = new int[obj.board.length];
		for(int i=0;i<obj.board.length;i++)
			board[i]=obj.board[i];
		player1length = obj.player1length;
		player2length = obj.player2length;
		totalsize = obj.totalsize;
		player1Mancala = obj.player1Mancala;
		player2Mancala = obj.player2Mancala;
	}
	public String printboard() {
		String opp2 = "";
		String opp1 = "";
		for(int i=0;i<this.player2length;i++)
		{
			opp2+=this.board[this.totalsize-1-i] + " ";
			opp1 += this.board[i+1] + " ";
			
		}
		return opp2 + "\n" + opp1 + "\n" + this.board[this.player2Mancala] + "\n" + this.board[this.player1Mancala];
	}
}

class State
{
	Node node;
	String nodeName;
	int depth;
	String value;
	//Now this is not needed. You should delete it.
	boolean playAgain;
	//Which player has to play.
	int currentPlayer;
	//If states is going to play again. Min and Min or Max and Max.
	//Use to to keep track of depth
	boolean turnAgain;
	boolean gameOver = false;
	int nextMoveIndex;
	
	int gained;
	int lost;
	public State(State obj)
	{
		node =new Node(obj.node);
		nodeName = obj.nodeName;
		depth = obj.depth;
		value = obj.value;
		playAgain = obj.playAgain;
		currentPlayer = obj.currentPlayer;	
		turnAgain = obj.turnAgain;
		gameOver = false;
		if(currentPlayer == 2)
			nextMoveIndex = node.totalsize-1;
		else
			nextMoveIndex = 1;
	}
		public State(){}
}

class game
{
	//greedy, mimmax
	private int task;
	private int cuttingDepth;
	//Initial State of my game. Not need with usage of State.
	private Node root;
	//Shouldn't be needed.
	private int agentplayer;
	
	private State currentState;

	//Children of root node or the starting state  of what is given in the game.
	//List<State> possibleMoves = new LinkedList<State>();
	State bestMove;
	
	public game()
	{
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("traverse_log.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	    System.setOut(out);
	}
	
	
	public void readInput(String fileName)
	{
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			task = Integer.parseInt(br.readLine());
			agentplayer = Integer.parseInt(br.readLine());
			cuttingDepth = Integer.parseInt(br.readLine());
	
			String boardp2 = br.readLine();
			String boardp1 = br.readLine();
			int inistonesp2 = Integer.parseInt(br.readLine());
			int inistonesp1 = Integer.parseInt(br.readLine());
			root = new Node(boardp2, boardp1, inistonesp2, inistonesp1);
			
			currentState = new State();
			currentState.node = root;
			currentState.depth = 0;
			currentState.nodeName = "root";
			currentState.value = "-Infinity";
			//the root says which player to play first. Oops it says which player you are.
			currentState.currentPlayer = agentplayer;

			if(agentplayer == 2)
				currentState.nextMoveIndex = currentState.node.totalsize-1;
			else
				currentState.nextMoveIndex = 1;
			if(task == 2)
			    System.out.println("Node,Depth,Value");
			if(task==3)
				System.out.println("Node,Depth,Value,Alpha,Beta");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	public void MinMaxDecision()
	{
		int maximum;
		switch(task)
		{
		case 1: 
				//Greedy
				cuttingDepth = 1;	
				maximum = Max(currentState, Integer.MIN_VALUE, Integer.MAX_VALUE);
				printNextState();
				break;
		case 2: 
				//MinMax
			    maximum = Max(currentState, Integer.MIN_VALUE, Integer.MAX_VALUE);
				printNextState();			
				break;
		case 3: 
				//AlphaBeta
				maximum = Maximum(currentState, Integer.MIN_VALUE, Integer.MAX_VALUE);
				printNextState();
				break;
		
		}
	}
	private void printNextState() {
		State move = bestMove;//getBestState(maxElement);
		String nextState = move.node.printboard();
		outputToFile(nextState,"next_state.txt");
	}

	private void outputToFile(String output,String fileName) {
		Writer writer = null;
		try
		{
			File file = new File(fileName);
		    writer = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(file))));
			writer.write(output);
			writer.close();
		}
		catch(Exception e){}
		
		//System.out.println("Final Output is: " + "\n" + output);		
	}
	
	/*
	 * alpha beta pruning.
	 */
	public int Maximum(State currentGame, int maxalpha, int minbeta)
	{
		//System.out.println("Start of Max: " +currentGame.nodeName + "," + currentGame.depth + "," + maxalpha + "," + minbeta);
		if(cutOffTest(currentGame))
		{
			 int evalValue = Eval(currentGame);
			 //gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",evalValue+"", maxalpha +"", minbeta+"");
			 currentGame.value = evalValue + "";
			 return evalValue;
		}
		
		//List<State> Successor = GenerateMoves(currentGame);
		int v = Integer.MIN_VALUE;
		currentGame.value = v + "";
//		gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",currentGame.value, maxalpha +"", minbeta+"");
		State move = null;
		while((move = getNextMove(currentGame))!=null) 
		{	
			currentGame.value=v+"";
		
			int maxvalues;
			if(move.playAgain)
			{
				move.playAgain = false;
				maxvalues=Maximum(move, maxalpha, minbeta);
			}
			else
			{
				maxvalues=Minimum(move, maxalpha, minbeta);
			}
			v = Math.max(v, maxvalues);	
			
			if(currentGame.depth<=1)
			{
				if(bestMove == null)
				{
					bestMove = move;				
				}
				else
				{
					if(v>Integer.parseInt(bestMove.value))
					{
						bestMove = move;
					}
				}
			}
			if(v>=minbeta)
			{
				gathertraverseLogs(currentGame.nodeName,currentGame.depth + "", v +"", maxalpha +"", minbeta+"");
				return v;
			}
			maxalpha = Math.max(maxalpha,v);
			gathertraverseLogs(currentGame.nodeName,currentGame.depth + "", v +"", maxalpha +"", minbeta+"");	
		}
		
		currentGame.value = v + "";
		return v;
	}
	public int Minimum(State currentGame, int maxalpha, int minbeta)
	{
		if(cutOffTest(currentGame))
			{
			 int evalValue = Eval(currentGame);
		//	 gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",evalValue+"", maxalpha +"", minbeta+"");
			 currentGame.value = evalValue + "";
			 return evalValue;
			}

		int v = Integer.MAX_VALUE;
		currentGame.value = v + "";
	//	gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",currentGame.value, maxalpha +"", minbeta+"");

		State node = null;
		while((node = getNextMove(currentGame))!=null)  
		{
			currentGame.value=v+"";

			int minvalues;
			if(node.playAgain)
				{
				node.playAgain = false;  
				minvalues = Minimum(node, maxalpha, minbeta);
				
				}
			else
				{
				minvalues =Maximum(node, maxalpha, minbeta);
				}
			v = Math.min(v, minvalues);
			if(v<=maxalpha)
			{
			//	gathertraverseLogs(currentGame.nodeName,currentGame.depth + "", v +"", maxalpha +"", minbeta+"");
				return v;
			}
			minbeta = Math.min(minbeta, v);
		//	gathertraverseLogs(currentGame.nodeName,currentGame.depth + "", v +"", maxalpha +"", minbeta+"");

		}
		currentGame.value = v + "";

		return v;
	}


	//Call Max with depth of the root. I am increasing depth on each call to max and min.
	public int Max(State currentGame, int maxalpha, int minbeta)
	{
		if(cutOffTest(currentGame))
		{
			 int evalValue = Eval(currentGame);
			 gathertraverseLogs(currentGame.nodeName,currentGame.depth+"",evalValue+"");
			 currentGame.value = evalValue + "";
			 return evalValue;
		}
		
		int v = Integer.MIN_VALUE;
		currentGame.value = v + "";
		List<State> Successor = GenerateMoves(currentGame);

		gathertraverseLogs(currentGame.nodeName,currentGame.depth+"",currentGame.value+"");

		for (State move : Successor) 
		{	
			
			currentGame.value=v+"";
		
			int maxvalues;
			if(move.playAgain)
			{
				move.playAgain = false;
				maxvalues=Max(move, v, minbeta);
			}
			else
			{
				maxvalues=Min(move, v, minbeta);
			}
			v = Math.max(v, maxvalues);		
					
			if(currentGame.depth<=1)
			{
				if(bestMove == null)
				{
					bestMove = move;
				}
				else
				{
					if(v>Integer.parseInt(bestMove.value))
					{
							bestMove = move;
					}
				}
			}
			gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",v+"");
		}
		
		currentGame.value = v + "";

		return v;		
	}
	
	
	int player1Index;
	int player2Index;
	private State getNextMove(State state)
	{
		int player = state.currentPlayer;
		//I am assuming player1 length and player2 length is same.	
		if(player == 1 && state.node.player1length < state.nextMoveIndex)
		return null;

		if(player == 2 && state.nextMoveIndex < state.node.totalsize-state.node.player2length)
		return null;
			List<State> nextState = GenerateMoves(state);	
			if(player == 1)
				state.nextMoveIndex++;
			else
				state.nextMoveIndex--;
		return nextState.get(0);
	}
	
	private List<State> GenerateMoves(State currentGame) {
		
		List<State> Moves = new LinkedList<State>();
		//Assign each of the next move to this reference. Also use the Node.
		State nextMove = null;
		Node nextMovenode = null;
		boolean playAgain = false;

		if(currentGame.currentPlayer == 1)
		{		
			int noOfMoves = currentGame.node.player1length;
			//Iterating for player1 for number of holes excluding mancala
			for(int i=currentGame.nextMoveIndex; i<=currentGame.nextMoveIndex;i++)
			{
				nextMove = new State(currentGame); 
				//nextMove=(State)currentGame.clone();
				nextMovenode = nextMove.node;
				
				//Number of stones in ith hole.
				int noOfstones = nextMovenode.board[i];
				
				//Not a valid legal move.
				if(noOfstones<=0)
					continue;
				
				//Making the stones of ith hole as 0. This is what Mancala says.
				nextMovenode.board[i] = 0;
				
				//I am iterating by adding 1 stone to each hole.
				for(int j=1; j<=noOfstones;j++)
				{
					//if the hole suppose to be player2 Mancala then increase the stones.
					if((i+j)%nextMovenode.totalsize==nextMovenode.player2Mancala)
					{
						noOfstones++;
						continue;
					}
					//Add one mancala to all holes.
					else
					{
						int temp = i+j;
						if(0<temp && temp>=nextMovenode.player1length && temp!=nextMovenode.player1Mancala)
							nextMove.gained++;
						else 
							nextMove.lost++;
					nextMovenode.board[(i+j)%nextMovenode.totalsize]++;
					}
					
					//Last stone inserted. 2 condition:
					//1) Replay if it is in player1 Mancalas. I don't care currently.
					//2)If last stone is the first stone inserted in the players area.
					
					if(j==noOfstones)
					{
						int lastStoneloc = (i+j)%nextMovenode.totalsize;
						//last stone loc has to at player1 area
						if(lastStoneloc<=nextMovenode.player1length)
						{
							if(nextMovenode.board[lastStoneloc] == 1)							
							{
								//formula to get opp hole. 2x(N  + 1 - i) + i...i is current loc, N number of holes in player1
								//simplifies to 2(N+1) - i
								//get opposite hole and put them all in the mancala
								int opphole = 2 * (nextMovenode.player1length + 1) - lastStoneloc;
								//moving all the stones to Mancala.
								nextMovenode.board[nextMovenode.player1Mancala] += nextMovenode.board[opphole] + nextMovenode.board[lastStoneloc]; 
								//Marking current and opposite node as 0
								nextMovenode.board[opphole] = nextMovenode.board[lastStoneloc]=0;
								//All gems to mancala.
							}
						}
						//Play again condition not needed any more.
					
						if(lastStoneloc == nextMovenode.player1Mancala)
						{
							//another chance
							playAgain = true;
						}	
					}
				}
				nextMove.nodeName = "B" + (i + 1);
				
				if(currentGame.turnAgain)
				{
					nextMove.depth = currentGame.depth;
				}
				else
				{
					nextMove.depth = currentGame.depth + 1;
				}
				if(!playAgain)
				{
					//nextMove.depth = currentGame.depth + 1;
					nextMove.currentPlayer = 2;
					nextMove.playAgain = false;
					nextMove.turnAgain = false;
				}
				else
				{
					nextMove.currentPlayer = currentGame.currentPlayer;
					//nextMove.depth = currentGame.depth;
					nextMove.playAgain = true;
					nextMove.turnAgain = true;
				}
				
				Moves.add(nextMove);
				playAgain = false;
			}
		}
		
		//For player two
		if(currentGame.currentPlayer==2)
		{			
			for(int i = currentGame.nextMoveIndex;i>=currentGame.nextMoveIndex;i--)
			{
				nextMove = new State(currentGame); 
				//nextMove=(State)currentGame.clone();
				nextMovenode = nextMove.node;
				
				//Number of stones in ith hole.
				int noOfstones = nextMovenode.board[i];
				
				//Not a valid legal move.
				if(noOfstones<=0)
					continue;
				
				//Making the stones of ith hole as 0. This is what Mancala says.
				nextMovenode.board[i] = 0;
				for(int j=1; j<=noOfstones;j++)
				{
					//if the hole suppose to be player2 Mancala then increase the stones.
					if((i+j)%nextMovenode.totalsize==nextMovenode.player1Mancala)
					{
						noOfstones++;
						continue;
					}
					//Add one mancala to all holes.
					else
					{
						int temp = i+j;
						if(0<temp && temp>=nextMovenode.player1length)
							nextMove.lost++;
						else if(temp!=nextMovenode.player2Mancala)
							nextMove.gained++;
							nextMovenode.board[(i+j)%nextMovenode.totalsize]++;
					}				
					
					if(j==noOfstones)
					{
						int lastStoneloc = (i+j)%nextMovenode.totalsize;
						//last stone loc has to at player2 area
						if((lastStoneloc<=(nextMovenode.totalsize-1)) && (lastStoneloc>=(nextMovenode.totalsize - nextMovenode.player2length)))
						{
							//Well this logic is similar to player 1....Works out to be the same.
							//2(N+1) - i....N number of holes in first player. i is the index.
							if(nextMovenode.board[lastStoneloc] == 1)							
							{
								//formula to get opp hole. 2x(N  + 1 - i) + i...i is current loc, N number of holes in player1
								//simplifies to 2(N+1) - i
								//get opposite hole and put them all in the mancala
								int opphole = 2 * (nextMovenode.player2length + 1) - lastStoneloc;
								//moving all the stones to Mancala.
								nextMovenode.board[nextMovenode.player2Mancala] += nextMovenode.board[opphole] + nextMovenode.board[lastStoneloc]; 
								//Marking current and opposite node as 0
								nextMovenode.board[opphole] = nextMovenode.board[lastStoneloc]=0;
								//All gems to mancala.
							}
						}
												
						if(lastStoneloc == nextMovenode.player2Mancala)
						{
							//another chance
							playAgain = true;
						}					
					}
				}
				nextMove.nodeName = "A" + ((2 * (nextMovenode.player2length + 1) - i) + 1);
				if(currentGame.turnAgain)
				{
					nextMove.depth = currentGame.depth;
				}
				else
				{
					nextMove.depth = currentGame.depth + 1;
				}
				
				if(!playAgain)
				{
					//nextMove.depth = currentGame.depth + 1;
					nextMove.currentPlayer = 1;
					nextMove.playAgain = false;
					nextMove.turnAgain = false;

				}
				else
				{
					//nextMove.depth = currentGame.depth;
					nextMove.currentPlayer = currentGame.currentPlayer;
					nextMove.playAgain = true;
					nextMove.turnAgain = true;
				}
				Moves.add(nextMove);
				playAgain = false;
			}
		}

		return Moves;
	}

	
	public void IsGameOver(State current)
	{
		Node currentboard = current.node;
		boolean p1empty = true;
		boolean p2empty = true;

		//I am checking if either side is 0
		for(int i=0;i<currentboard.player1length;i++)
		{
			if(currentboard.board[i+1]!=0 && p1empty)
			{
				p1empty = false;
			}
			if(currentboard.board[currentboard.totalsize-1-i]!=0 &&p2empty)
			{
				p2empty = false;
			}
			if(!p1empty && !p2empty)
			{
				return;
				//break;
			}
		}
		int summOfStones = 0;
		
		if(p1empty || p2empty)
			current.gameOver = true;
		if(p1empty)
		{			
			for(int i=0;i<currentboard.player2length;i++)
			{
				summOfStones += currentboard.board[currentboard.totalsize-1-i];
				currentboard.board[currentboard.totalsize-1-i] = 0;
			}
			currentboard.board[currentboard.player2Mancala] += summOfStones;
		}
		
		if(p2empty)
		{
		    summOfStones = 0;
			for(int i=0;i<currentboard.player1length;i++)
			{
				summOfStones += currentboard.board[i+1];
				currentboard.board[i+1]=0;
			}
			currentboard.board[currentboard.player1Mancala] += summOfStones;
		}
		
	}
	public int Min(State currentGame, int maxalpha, int minbeta)
	{
		if(cutOffTest(currentGame))
			{
			 int evalValue = Eval(currentGame);
			 gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",evalValue+"");
			 currentGame.value = evalValue + "";
			 return evalValue;
			}
		ArrayList<Integer> arr = new ArrayList<Integer>();
		int v = Integer.MAX_VALUE;
		currentGame.value = v + "";
		gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",currentGame.value);
	
		List<State> Successor = GenerateMoves(currentGame);
		for (State node : Successor) 
		{
			currentGame.value=v+"";
	
			int minvalues;
			if(node.playAgain)
				{
				node.playAgain = false;  
				minvalues = Min(node, maxalpha, v);
				
				}
			else
				{
				minvalues =Max(node, maxalpha, v);
				}
			v = Math.min(v, minvalues);
			gathertraverseLogs(currentGame.nodeName,currentGame.depth + "",v+"");
		
		}
		currentGame.value = v + "";
		return v;	
	}


	//If root is null or it has value of cutting edge then return
	public boolean cutOffTest(State currentState)
	{
		IsGameOver(currentState);
		if(currentState == null||currentState.gameOver)
			return true;
		/*
		if(currentState.gameOver)
		{
			if(currentState.turnAgain)
			{	
				String temp= "";
				if(currentState.depth%2 == 1)
					temp = "-Infinity";
				else
					temp = "Infinity";
				gathertraverseLogs(currentState.nodeName, currentState.depth+"", temp);
			}
			else if(currentState.depth!=cuttingDepth)
			{
				String temp= currentState.value;
				if(currentState.depth%2 == 0)
					temp = "-Infinity";
				else
					temp = "Infinity";
				gathertraverseLogs(currentState.nodeName, currentState.depth+"", temp);
			}
			return true;
		}
		*/
		if(currentState.turnAgain)
		{
			return false;
		}
		return currentState.depth>=cuttingDepth?true:false;
	}
	
	public int Eval(State currentState)
	{
		/*
		  3 3 3
		0		2 
		  3 3 3
		  if agent is player 1
		  return 2-0 = 2;
		  if agent is player 2
		  return 0-2 = -2
		  
		*/
		
	//	int value = currentState.node.board[currentState.node.player1length+1] - currentState.node.board[0];
		if(agentplayer == 1)
		{
			return (int) ((currentState.node.board[currentState.node.player1Mancala] - currentState.node.board[currentState.node.player2Mancala])-currentState.lost*0.3*2+currentState.gained);
		}
		if(agentplayer == 2)
		{
			return (int) ((currentState.node.board[currentState.node.player2Mancala] - currentState.node.board[currentState.node.player1Mancala])-currentState.lost*0.3*2+currentState.gained);

		}
		return 0;
	}	
    
	public void gathertraverseLogs(String nodeName, String depth, String value)
	{
		if(task==1)
			return;
		if(value.contains("2147483648"))
		{
			value = "-Infinity";
		}
		if(value.contains("2147483647"))
		{
			value = "Infinity";
		}
		//System.out.println(nodeName + "," + depth + "," + value + "," + count++);
		String traversedLog = nodeName + "," + depth + "," + value;		
		
		System.out.println(traversedLog);
	}

	public void gathertraverseLogs(String nodeName, String depth, String value, String alpha, String beta)
	{
		if(value.contains("2147483648"))
		{
			value = "-Infinity";
		}
		if(value.contains("2147483647"))
		{
			value = "Infinity";
		}
		
		if(alpha.contains("2147483648"))
		{
			alpha = "-Infinity";
		}
		
		if(alpha.contains("2147483647"))
		{
			alpha = "Infinity";
		}
		
		if(beta.contains("2147483648"))
		{
			beta = "-Infinity";
		}
		
		if(beta.contains("2147483647"))
		{
			beta = "Infinity";
		}
		
		//System.out.println(nodeName + "," + depth + "," + value + "," + alpha + "," + beta);
		String traversedLog = nodeName + "," + depth + "," + value + "," + alpha + "," + beta;
		System.out.println(traversedLog);
	}
}

public class mancala {
	
	public static void main(String[] args) {
		game gameMancala = new game();
		gameMancala.readInput(args[1]);
		gameMancala.MinMaxDecision();	
	}
}
