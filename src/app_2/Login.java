/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app_2;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

public class Login {

    private String username;
    private String password;
    private String phoneNumber;

    public boolean checkUsername(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPassword(String password) {
        return Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$")
                .matcher(password).matches();
    }

    public boolean checkPhoneNumber(String phone) {
        return phone.startsWith("+27") && phone.length() == 12;
    }

    public String registerUser(String username, String password, String phone) {

        if (!checkUsername(username))
            return "Username is not correctly formatted.";

        if (!checkPassword(password))
            return "Password is not correctly formatted.";

        if (!checkPhoneNumber(phone))
            return "Phone number is incorrectly formatted.";

        this.username = username;
        this.password = password;
        this.phoneNumber = phone;

        return "User successfully registered.";
    }

    public boolean loginUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String returnLoginStatus(boolean status) {
        if (status)
            return "Welcome " + username + ", it is great to see you again.";
        else
            return "Username or password incorrect.";
    }
}

//*******************
// Validator class - all validation logic
//********************
class Validator{
    public static ValidationResult validateUsername(String username){
        if (username.length()>5)
            return new ValidationResult(false, " Username must not be more than 5 characters long.");
        if (!username.contains("_"))
            return new ValidationResult(false, " Username must contain an underscore(_)");
        return new ValidationResult(true, "OK");
    }
     
    public static ValidationResult validatePassword(String password){
        if (password.length()<8)
            return new ValidationResult(false, " Password must be at least 8 characters long.");
        if (!Pattern.compile("[A-Z]").matcher(password).find())
            return new ValidationResult(false, " Password must contain at least one capital letter.");
        if (!Pattern.compile("[0-9]").matcher(password).find())
            return new ValidationResult(false, " Password must contain at least one number.");
        if (!Pattern.compile("[@#$%&!*~^]").matcher(password).find())
            return new ValidationResult(false, " Password  must contain at least one special character.");
        return new ValidationResult(true, "OK");
            
    }
    
    public static ValidationResult validatePhone(String phone){
        phone = phone.trim();
        if (!phone.startsWith("+27"))
            return new ValidationResult(false, " Cell number must start with SA country code +27.");
        String localPart = phone.substring(3);
        if (!localPart.matches("\\d+"))
            return new ValidationResult(false, " Only digits are allowed after the country code.");
        if (localPart.length()<8)
            return new ValidationResult(false, " The number after +27 is too short(minimum 8 digits).");
        if (localPart.length()>9)
            return new ValidationResult(false, " The number after +27 must be no more than 9 digits.");
        return new ValidationResult(true, "Ok");
    }
}
//*****************
// ValidationResult - must be declared first
// since Validator depends on it
//*****************
class ValidationResult{
    boolean success;
    String message;
    
    public ValidationResult(boolean sucess,String message){
        this.success = sucess;
        this.message = message;
       
    }
}

//****************
// User class - reepresents a registered user
//****************
class User{
    String username;
    String password;
    String phone;
    
    public User(String username,String password,String phone){
        this.username = username;
        this.password = password;
        this.phone = phone;
                
    }
    
    @Override
    public String toString(){
        return String.format("User{username=%s, phone=%s}",username,phone);
    }
}   

//-----------------
// UserStore - in-memory user " database "
//-----------------

class UserStore{
    private final Map<String,User>users = new HashMap<>();
    
    public ValidationResult register(String username,String password,String phone){
        if (users.containsKey(username))
            return new ValidationResult(false, " Username" + username + " is already taken.");
        users.put(username, new User(username, password, phone));
        return new ValidationResult(true,"Registration successful!");
    }
    
    public ValidationResult login(String username,String password){
        User user = users.get(username);
        if(user == null)
            return new ValidationResult(false, "Username not found. Please  register first.");
        if(!user.password.equals(password))
            return new ValidationResult(false, "Incorrect password.");
        return new ValidationResult(true, " welcome" + username + "!");
    }
}

//--------------------
// App- main application controller & entry point
//--------------------
class App{
    
    private final Scanner scanner = new Scanner(System.in);
    private final UserStore store = new UserStore();
    
    //-------display helpers------------------
    
    private void divider(char ch, int width){
        System.out.println(String.valueOf(ch).repeat(width));
    }
    
    private void header(String title){
        divider('=',45);
        System.out.println(""+ title);
        divider('=',45);
    }
    
    private void pause(){
        System.out.print(" Press Enter to continue...");
        scanner.nextLine();
    }
    
    //----------validated input helpers----------
    
    private String getUsername(){
        while(true){
            System.out.print(" Username(max 5 chars, must include '_'):");
            String input = scanner.nextLine().trim();
            ValidationResult result = Validator.validateUsername(input);
            if(result.success)return input;
            System.out.println("x" + result.message);
        }
    }
    
    private String getPassword(){
        while (true){
            System.out.print("Password:");
            String input = scanner.nextLine().trim();
            ValidationResult result= Validator.validatePassword(input);
            if(result.success)return input;
            System.out.println("x" + result.message);
        }
    }
    
    private String getPhone(){
        while(true){
            System.out.print(" SA Cell number(e.g. +278212345678):");
            String input = scanner.nextLine().trim();
            ValidationResult result = Validator.validatePhone(input);
            if(result.success)return input;
            System.out.println("x" + result.message);
        }
    }
    
    //-------register flow---------
    
    private void register(){
        header("REGISTER");
        String username = getUsername();
        String password = getPassword();
        String phone = getPhone();
        
        ValidationResult result = store.register(username,password,phone);
        divider('-',45);
        System.out.println(result.success ? "!" + result.message:"x"+ result.message );
        divider('-',45);
        pause();
    }
    
    //----login flow-------------
    
    private void login(){
        header("LOGIN");
        System.out.print("Username");
        String username = scanner.nextLine().trim();
        System.out.print("Password:");
        String password = scanner.nextLine().trim();
        
        ValidationResult result = store.login(username,password);
        divider('-',45);
        if (result.success){
          System.out.println("SUCCESS:"+ result.message);
          divider('-',45);
          pause();
          QuickChat chat= new QuickChat(username);
          chat.run();
        }else{
            System.out.println("Error:"+ result.message);
            divider('-',45);
            pause();
        }
    }
    
    //-----main menu loop-------
    
    private void run(){
        while(true){
            header("USER ACCOUNT SYSTEM");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            divider('-',45);
            System.out.print(" Select an option from (1-3):");
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            switch (choice){
                case "1"-> register();
                case "2"-> login();
                case "3"->{
                    System.out.println("Goodbye!");
                    divider('=',45);
                    return;
                }
                default->{
                    System.out.println("x Invalid choice.Please enter 1,2 or 3.");
                    pause();
                }
            }
        }
    }
    
    //-------entry point---------
    
    public static void main(String[]args){
        new App().run();
    }
}
