import processing.core.PApplet;

public class Main extends PApplet {

	public static void main(String[] args) {
		 PApplet.main("Main");
		 		 
		 XmlFileParser.parseFile("data\\Concept Car.ana");
		 GraphHandler graphHandler = GraphHandler.getInstance();
		 int nodeCount = graphHandler.getNodeCount();
		 int edgeCount = graphHandler.getEdgeCount();
		 
		 int dummy = 0;
		 dummy++;
	}

	
	// processing callbacks
	// -----------------------------------------------------
	 public void settings(){
		 size(300,300);
	 }

	 public void setup(){
		 //fill(120,50,240);
	 }

	 public void draw(){
		 ellipse(width/2,height/2,second(),second());
		 
		 line(10, 10, 100, 150);
		 noFill();
		 rect(10, 20, 50, 25);
	 }
	 // -----------------------------------------------------
}
