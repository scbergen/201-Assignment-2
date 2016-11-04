import java.util.*;
public class StackCalculator {
	private Stack<Integer> opStack;
	private Stack<Long> postfixStack;
	StringTokenizer str;
	public final int EOL =0;
	public final int VALUE =1;
	public final int OPAREN =2;
	public final int CPAREN =3;
	public final int EXP =4;
	public final int MULT =5;
	public final int DIV =6;
	public final int PLUS =7;
	public final int MINUS =8;
	public final int OSQPAREN=9;
	public final int CSQPAREN=10;
	public final int OBRAC=11;
	public final int CBRAC =12;
	public final int MOD=13;

	//****First Class within StackCalculator*****
	public class Precedence{
		public int inputSymbol;
		public int topOfStack;
		//public Precedence(){}
		public Precedence(int inSymbol, int topSymbol){
			inputSymbol = inSymbol;
			topOfStack = topSymbol;
		}

	}
	//****Second Class within StackCalculator*****
	public class EvalTokenizer{

		public  EvalTokenizer(StringTokenizer is)
		{str = is;}


		public Token getToken()
		{
			long theValue;
			while(str.hasMoreTokens()){
				
			
				
			String s = str.nextToken();

			
			if(s.equals(" ")) return getToken();
			if(s.equals("**"))return new Token(EXP);
			if(s.equals("/")) return new Token(DIV);
			if(s.equals("*")) return new Token(MULT);
			if(s.equals("(")) return new Token(OPAREN);
			if(s.equals(")")) return new Token(CPAREN);
			if(s.equals("+")) return new Token(PLUS);
			if(s.equals("-")) return new Token(MINUS);
			if(s.equals("{")) return new Token(OBRAC);
			if(s.equals("}")) return new Token(CBRAC);
			if(s.equals("[")) return new Token(OSQPAREN);
			if(s.equals("]")) return new Token(CSQPAREN);
			if(s.equals("%")) return new Token(MOD);
			try{
				theValue=Long.parseLong(s);

			}catch(NumberFormatException e)
			{
				//System.err.println("Parse error");
				return new Token();
			}
			return new Token(VALUE, theValue);
		}
			return new Token();
		}
		
	}

	//****Third Class within StackCalculator*****
	public class Token{
		public Token()
		{this(EOL);}
		public Token (int t)
		{this(t, 0);}
		public Token(int t, long v)
		{type = t; value = v;}

		public int getType()
		{return type;}
		public long getValue()
		{return value;}
		private int type = EOL;
		private long value = 0; 
	}
	//CONSTRUCTOR
	public StackCalculator(){
		opStack= new Stack<Integer>(); 
		opStack.push(EOL);
		postfixStack = new Stack<Long>();
	}
	//METHOD AS SPECIFIED BY ALAN
	public void processInput(String s){

		str = new StringTokenizer(s, "+*/**(){}[]% ", true);

		EvalTokenizer tok = new EvalTokenizer(str);
		Token lastToken;
		do{
			lastToken = tok.getToken();
			processToken(lastToken);

		}while(lastToken.getType()!=EOL);
		if(postfixStack.isEmpty())
		{
			//System.err.println("Missing operand!");
			//return 0;
		}else{

		try{
			long theResult = postfixStack.pop();
			if(!postfixStack.isEmpty())
				System.err.println("Warning: missing operators!");

			System.out.println(theResult);
		}catch(EmptyStackException e){
			//System.out.println("0");
		}
		}

	}
	public void processToken(Token lastToken){
		int topOp;
		int lastType = lastToken.getType();
		switch(lastType){
		case VALUE:
			postfixStack.push((long) lastToken.getValue());
			return;
		case CPAREN:
			while((topOp=opStack.peek())!=OPAREN && topOp != EOL)
				binaryOp(topOp);
			if(topOp==OPAREN)
				opStack.pop();
			else
				System.err.println("Mismatched Parentheses");
			break;
		case CSQPAREN:
			while((topOp=opStack.peek())!=OSQPAREN && topOp != EOL)
				binaryOp(topOp);
			if(topOp==OSQPAREN)
				opStack.pop();
			else
				System.err.println("Mismatched Parentheses");
			break;
		case CBRAC:
			while((topOp=opStack.peek())!=OBRAC && topOp != EOL)
				binaryOp(topOp);
			if(topOp==OBRAC)
				opStack.pop();
			else
				System.err.println("Mismatched Parentheses");
			break;
		default: //General case operator
			while(precTable[lastType].inputSymbol<=
			precTable[topOp=opStack.peek()].topOfStack)
				binaryOp(topOp);
			if(lastType!=EOL)
				opStack.push(lastType);
			break;
		}
	}
	public long postfixPop()
	{
		if(postfixStack.isEmpty()){
			System.err.println("Nonsensical Input");
			return 0; 
		}
		return postfixStack.pop();
	}
	public void binaryOp(int topOp){
		if (topOp==OPAREN){
			System.err.println("Unbalanced Parentheses Error, Too Many Left Parentheses");
			opStack.pop();
			return;
		}else{
			if(topOp==OSQPAREN){
				System.err.println("Unbalanced Parentheses Error");
				opStack.pop();	
				return;
			}else{
				if(topOp==OBRAC){
					System.err.println("Unbalanced Parentheses Error");
					opStack.pop();
					return;
				}
			}
		}
		
		
		long rhs = postfixPop();
		long lhs = postfixPop();
		
		if(topOp==EXP)
			postfixStack.push((long) Math.pow(rhs, lhs));
		else if (topOp==PLUS)
			postfixStack.push(lhs+rhs);
		else if(topOp==MINUS)
			postfixStack.push(Math.abs(lhs-rhs));
		else if (topOp==MULT)
			postfixStack.push(lhs*rhs);
		else if (topOp==DIV)
			if(rhs!=0){
				postfixStack.push(lhs/rhs);
			}
			else
			{
				System.err.println("Division by zero");
				postfixStack.push(lhs);
			}
		else if(topOp==MOD)
			if(rhs!=0){
				postfixStack.push(lhs%rhs);
			}
			else
			{
				System.err.println("Division by zero");
				//postfixStack.push(lhs);
			}
		
		opStack.pop();}

	Precedence [] precTable = 
		{
				new Precedence (0, -1),//EOL
				new Precedence (0, 0),//VALUE
				new Precedence (100, 0),//OPAREN
				new Precedence (0, 99),//CPAREN
				new Precedence (6, 5),//EXP
				new Precedence (3, 4),//MULT
				new Precedence (3, 4),//DIV
				new Precedence (1, 2),//PLUS
				new Precedence (1, 2),//MINUS
				new Precedence(100, 0),
				new Precedence(0, 99),
				new Precedence(100, 0),
				new Precedence(0, 99),
				new Precedence(3, 4),


		};		

}


