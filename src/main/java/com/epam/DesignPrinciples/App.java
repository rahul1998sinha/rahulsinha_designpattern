package com.epam.DesignPrinciples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Hello world!
 *
 */

interface ImageReader {
    DecodedImage getDecodeImage();
}

class DecodedImage {
    private String image;

    public DecodedImage(String image) {
        this.image = image;
    }

    public String toString() {
        return image + ": is decoded";
    }
}

class GifReader implements ImageReader {
    private DecodedImage decodedImage;

    public GifReader(String image) {
        this.decodedImage = new DecodedImage(image);
    }

    public DecodedImage getDecodeImage() {
        return decodedImage;
    }
}

class JpegReader implements ImageReader {
    private DecodedImage decodedImage;

    public JpegReader(String image) {
        decodedImage = new DecodedImage(image);
    }

    public DecodedImage getDecodeImage() {
        return decodedImage;
    }
}
class SquarePeg {
    private double width;

    public SquarePeg(double width) {
        this.width = width;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}

/* The NEW */
class RoundHole {
    private final int radius;

    public RoundHole(int radius) {
        this.radius = radius;
        System.out.println("RoundHole: max SquarePeg is " + radius * Math.sqrt(2));
    }

    public int getRadius() {
        return radius;
    }
}

// Design a "wrapper" class that can "impedance match" the old to the new
class SquarePegAdapter {
    // The adapter/wrapper class "has a" instance of the legacy class
    private final SquarePeg squarePeg;

    public SquarePegAdapter(double w) {
        squarePeg = new SquarePeg(w);
    }

    // Identify the desired interface
    public void makeFit(RoundHole roundHole) {
        // The adapter/wrapper class delegates to the legacy object
        double amount = squarePeg.getWidth() - roundHole.getRadius() * Math.sqrt(2);
        System.out.println( "reducing SquarePeg " + squarePeg.getWidth() + " by " + ((amount < 0) ? 0 : amount) + " amount");
        if (amount > 0) {
            squarePeg.setWidth(squarePeg.getWidth() - amount);
            System.out.println("   width is now " + squarePeg.getWidth());
        }
    }
}
interface SocketInterface {
    String readLine();
    void  writeLine(String str);
    void  dispose();
}

class SocketProxy implements SocketInterface {
    // 1. Create a "wrapper" for a remote,
    // or expensive, or sensitive target
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public SocketProxy(String host, int port, boolean wait) {
        try {
            if (wait) {
                // 2. Encapsulate the complexity/overhead of the target in the wrapper
                ServerSocket server = new ServerSocket(port);
                socket = server.accept();
            } else {
                socket = new Socket(host, port);
            }
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() {
        String str = null;
        try {
            str = in.readLine();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        return str;
    }

    public void writeLine(String str) {
        // 4. The wrapper delegates to the target
        out.println(str);
    }

    public void dispose() {
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
class Button {
    // 2. The "current" state object
    private State current;

    public Button() {
        current = OFF.instance();
    }

    public void setCurrent(State s) {
        current = s;
    }

    // 3. The "wrapper" always delegates to the "wrappee"
    public void push() {
        current.push(this);
    }
}

// 4. The "wrappee" hierarchy
class State {
    // 5. Default behavior can go in the base class
    public void push(Button b) {
        b.setCurrent(OFF.instance());
        System.out.println("   turning OFF");
    }
}

class ON extends State {
    private static ON instance = new ON();

    private ON() {}

    public static State instance() {
        return instance;
    }
}

class OFF extends State {
    private static OFF instance = new OFF();
    private OFF() { }

    public static State instance() {
        return instance;
    }
    // 6. Override only the necessary methods
    public void push(Button b) {
        // 7. The "wrappee" may callback to the "wrapper"
        b.setCurrent(ON.instance());
        System.out.println("   turning ON");
    }
}
abstract class OrderProcessTemplate 
{ 
	public boolean isGift; 

	public abstract void doSelect(); 

	public abstract void doPayment(); 

	public final void giftWrap() 
	{ 
		try
		{ 
			System.out.println("Gift wrap successfull"); 
		} 
		catch (Exception e) 
		{ 
			System.out.println("Gift wrap unsuccessful"); 
		} 
	} 

	public abstract void doDelivery(); 

	public final void processOrder(boolean isGift) 
	{ 
		doSelect(); 
		doPayment(); 
		if (isGift) { 
			giftWrap(); 
		} 
		doDelivery(); 
	} 
} 


class NetOrder extends OrderProcessTemplate 
{ 
	@Override
	public void doSelect() 
	{ 
		System.out.println("Item added to online shopping cart"); 
		System.out.println("Get gift wrap preference"); 
		System.out.println("Get delivery address."); 
	} 

	@Override
	public void doPayment() 
	{ 
		System.out.println 
				("Online Payment through Netbanking, card or Paytm"); 
	} 

	@Override
	public void doDelivery() 
	{ 
		System.out.println 
					("Ship the item through post to delivery address"); 
	} 

} 

class StoreOrder extends OrderProcessTemplate 
{ 

	@Override
	public void doSelect() 
	{ 
		System.out.println("Customer chooses the item from shelf."); 
	} 

	@Override
	public void doPayment() 
	{ 
		System.out.println("Pays at counter through cash/POS"); 
	} 

	@Override
	public void doDelivery() 
	{ 
		System.out.println("Item deliverd to in delivery counter."); 
	} 

} 

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        //Factory Method
    	DecodedImage decodedImage;
        ImageReader reader = null;
        Scanner sc =new Scanner(System.in);
        sc.nextLine();
        System.out.println("Enter the name of image with format");
        String image = sc.nextLine();
        String format = image.substring(image.indexOf('.') + 1, (image.length()));
        if (format.equals("gif")) {
            reader = new GifReader(image);
        }
        if (format.equals("jpeg")) {
            reader = new JpegReader(image);
        }
        assert reader != null;
        decodedImage = reader.getDecodeImage();
        System.out.println(decodedImage);
        //Adapter
        RoundHole roundHole = new RoundHole( 5 );
        SquarePegAdapter squarePegAdapter;
        for (int i = 6; i < 10; i++) {
            squarePegAdapter = new SquarePegAdapter((double)i);
            
            squarePegAdapter.makeFit(roundHole);
        }
    
    //Proxy
    SocketInterface socket = new SocketProxy( "127.0.0.1", 8080, args[0].equals("first") ? true : false );
    String  str;
    boolean skip = true;
    while (true) {
        if (args[0].equals("second") && skip) {
            skip = !skip;
        } else {
            str = socket.readLine();
            System.out.println("Receive - " + str);
            if (str.equals(null)) {
                break;
            }
        }
        System.out.print( "Send ---- " );
        str = new Scanner(System.in).nextLine();
        socket.writeLine( str );
        if (str.equals("quit")) {
            break;
        }
    }
    socket.dispose();
    OrderProcessTemplate netOrder = new NetOrder(); 
	netOrder.processOrder(true); 
	System.out.println(); 
	OrderProcessTemplate storeOrder = new StoreOrder(); 
	storeOrder.processOrder(true);
    
    //State
    InputStreamReader is = new InputStreamReader( System.in );
    Button btn = new Button();
    while (true) {
        System.out.print("Press 'Enter'");
        is.read();
        btn.push();
        break;
    }
    
}
}
