import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;


public class MainClass {


	static Iterator func(ArrayList mylist){
		Iterator it=mylist.iterator();
		while(it.hasNext()){
			Object element = it.next();
			if(element instanceof String)//Hints: use instanceof operator
				break;
		}
		return it;

	}
	@SuppressWarnings({ "unchecked" })
	public static void main(String []args){
		ArrayList mylist = new ArrayList();
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int m = sc.nextInt();
		for(int i = 0;i<n;i++){
			mylist.add(sc.nextInt());
		}

		mylist.add("###");
		for(int i=0;i<m;i++){
			mylist.add(sc.next());
		}

		Iterator it=func(mylist);
		while(it.hasNext()){
			Object element = it.next();
			System.out.println((String)element);
		}
	}


		/*// TODO Auto-generated method stub
		ChessFrame theFrame = new ChessFrame();

		theFrame.setVisible(true);
		theFrame.move("b2" , "b3");
		theFrame.move("b8", "c6");
		theFrame.move("b3" , "b4");
		theFrame.move("d7" , "d6");
		theFrame.move("d2" , "d4");
		theFrame.move("e7" , "e8");
		theFrame.move("f2" , "f3");
		theFrame.move("d8" , "h4");



		System.out.println(theFrame.isInCheck());

		System.out.println(theFrame.at("d1"));
		//theFrame.save("C:\\Users\\asus-\\Desktop\\game.txt");
*/
		
		
	}


