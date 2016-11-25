package mainPackage;

import components.QueueHandler;
import components.User;


public class Main {
    public static void main(String[] args){
        System.out.println("Inizio simulazione");
        int users = MyConstants.K;
        
        QueueHandler handler = new QueueHandler();
        handler.start();
        
        for(int i = 0; i < users; i++){
            User user = new User(i);
            user.start();
        }
        
    }
}
