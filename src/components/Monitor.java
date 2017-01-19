package components;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author andrea
 */
public class Monitor {
    private static Map<Thread, FEL> FELs = new HashMap<>();
    
    private static Map<Thread, Channel> CHANNELs = new HashMap<>();
    
    public synchronized static void addFEL (Thread t){
        FELs.put(t, new FEL());
        System.out.println("banane");
                
    }
    
    public synchronized static void addCHANNEL (Thread t) {
        CHANNELs.put(t, new Channel(t));
        System.out.println("dureeeeeeeee");
    }
    
    public synchronized static FEL getFEL (Thread t) {
        return FELs.get(t);
    }
    
    public synchronized static Channel getCHANNEL (Thread t) {
        return CHANNELs.get(t);
    }
    
}
