package mainPackage;

import components.User;


public class Main {
    public static void main(String[] args){
        System.out.println("Inizio simulazione");
        for(int i = 0; i<2; i++){
            User user = new User(i);
            user.start();
        }
        
    }
}
