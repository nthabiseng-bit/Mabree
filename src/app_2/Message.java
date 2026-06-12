/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

//====================================
// Message - stores one Message with full details
//=====================================
class Message{
    private static int counter = 1;
       
    String messageID;
    String messageHash;
    String recipient;
    String messageText;
    
    public Message(String recipient,String messageText){
        this.messageID = String.format("MSG%03d",counter++);
        this.messageHash = generateHash(messageText);
        this.recipient = recipient;
        this.messageText = messageText;
         
    }
    
    public String getMessageID(){return messageID;}
    public String getMessageHash(){return messageHash;}
    public String getMessageRecipient(){return recipient;}
    public String getMessageText(){return messageText;}
    
    private String generateHash(String text){
        int hash = 0;
        for(int i = 0;i<text.length();i++){
            hash =(hash*31 + text.charAt(i))%100000;
            
        }
        return String.format("Hash%05d",Math.abs(hash));
    }
    
    public void printDetails(){
        System.out.println("Message ID :" + messageID);
        System.out.println("Message Hash :" + messageHash);
        System.out.println("Recipient :" + recipient);
        System.out.println("Message :" + messageText);
        
    }
    
    @Override
    public String toString(){
        return String.format("Message{id=%s, recipient=%s}",messageID, recipient);
    }
    
    //-----------convert Message to JSON format--------------
    public String toJSON(){
        
        String line1=" \"messageID\" :\""+messageID +"\",\n";
        String line2= " \"messageHash\":\""+messageHash+"\",\n";
        String line3= " \"recipient\":\""+recipient+"\",\n";
        String line4= " \"messageText\":\""+messageText+"\"\n";
        
        return "{\n"+ line1 +line2 + line3 + line4 +"},\n";
    }
}

//======================================
// MessageValidator - validates recipient and Message
//=======================================
class MessageValidator{
    
    public static ValidationResult validateRecipient(String recipient){
        recipient = recipient.trim();
        if (!recipient.startsWith("+27"))
            return new ValidationResult(false," Recipient number must start with +27.");
        String localPart = recipient.substring(3);
        if (!localPart.matches("\\d+"))
            return new ValidationResult(false, "Only digits allowed after the country code.");
        if (localPart.length()<8)
            return new ValidationResult(false,"Number after +27 is too short(minimum 8 digits).");
        if (localPart.length()>9)
            return new ValidationResult(false,"Numbers after +27 must be no more than 9 digits.");
        return new ValidationResult(true,"ok");
            
    }
    public static ValidationResult validateMessage(String message){
        if (message.trim().isEmpty())
            return new ValidationResult(false,"Message cannot be empty.");
        if (message.length()>250)
            return new ValidationResult(false,"Please enter a message of less than 250 characters.");
        return new ValidationResult(true,"Message sent");
            
    }
}

//==========================
// JSONSaver - saves messages to a JSON file
//==========================
class JSONSaver{
    
    public static void save(Message msg,String filename){
        try{
            FileWriter fw = new FileWriter(filename,true);
            fw.write(msg.toJSON());
            fw.close();
            System.out.println(" Saved to "+ filename);
        }catch (IOException e){
            System.out.println("Error: Could not save.");
        }
    }
}
 //============================
// MessageStore -stores send and stored messages
//=============================
class MessageStore{

    private final List<Message> sentMessages =
            new ArrayList<>();

    private final List<Message> storedMessages =
            new ArrayList<>();

    private int maxMessages;
    private int sentCount;

    public void setMaxMessages(int max){
        this.maxMessages = max;
        this.sentCount = 0;
    }

    public boolean canSendMore(){
        return sentCount < maxMessages;
    }

    public int getRemaining(){
        return maxMessages - sentCount;
    }

    public void addSent(Message m){
        sentMessages.add(m);
        sentCount++;
        JSONSaver.save(m, "Sent_messages.json");
    }

    public void addStored(Message m){
        storedMessages.add(m);
        JSONSaver.save(m, "stored_messages.json");
    }

    public List<Message> getSentMessages(){
        return sentMessages;
    }

    public List<Message> getStoredMessages(){
        return storedMessages;
    }

    public boolean deleteByHash(String hash){

        for(int i = 0; i < sentMessages.size(); i++){

            if(sentMessages.get(i)
                    .getMessageHash()
                    .equals(hash)){

                sentMessages.remove(i);

                return true;
            }
        }

        return false;
    }
    
}

//===============================
// QuickChat- main chat controller
//===============================
class QuickChat{
    
    private final Scanner scanner= new Scanner(System.in);
    private final MessageStore messageStore= new MessageStore();
    private final String loggedInUser;
    
    public QuickChat(String username){
        this.loggedInUser= username;
    }
    
    private void divider(char ch,int width){
   System.out.println(new String(new char[width]).replace('\0',ch));
    }
    
    private void header(String title){
        divider('=',45);
        System.out.println(" " + title);
        divider('=',45);
    }
    
    private void pause(){
        System.out.println(" Press Enter to continue....");
        scanner.nextLine();
    }
    
    //=======send Message flow===============
    
    private void sendMessage(){
        if(!messageStore.canSendMore()){
            System.out.println(" Error: you have used all your messages.");
            pause();
            return;
        }
        header(" Send Message");
        System.out.println("Message remaining: " + messageStore.getRemaining());
        divider('-',45);
        
        //Step 1- Get recipient number
        String recipient="";
        String messageText="";
        while(true){
            System.out.print("Enter recipient number(e.g. +27733501410)");
            recipient = scanner.nextLine().trim();
            ValidationResult r= MessageValidator.validateRecipient(recipient);
            if (r.success){
                break;
            }
            System.out.println("Error:" + r.message);
        }
        // step 2 - Get Message text
        while(true){
            System.out.print(" Enter message(max 250 characters):");
            messageText=scanner.nextLine();
            ValidationResult r= MessageValidator.validateMessage(messageText);
            if(r.success){
                System.out.println(""+ r.message);
                break;
            }
            System.out.println("Error:" +r.message);
        }
        
        // step 3- Create Message object
        Message msg = new Message(recipient,messageText);
        
        // step 4- Ask what to do with the Message
        divider('-',45);
        System.out.println("What would you like to do?");
        System.out.println("1.Send Messages");
        System.out.println("2.Disregard Message");
        System.out.println("3.Store Messages to send later");
        divider('-',45);
        System.out.print("Select an option from 1-3:");
        String action = scanner.nextLine().trim();
        System.out.println();
        
        switch (action) {
            case "1" -> {
                messageStore.addSent(msg);
                divider('-',45);
                System.out.println(" Message successfully sent");
                divider('-',45);
                System.out.println(" MESSAGE DETAILS:");
                divider('-',45);
                msg.printDetails();
                divider('-',45);
            }
            case "2" -> {
                divider('-',45);
                System.out.println("Press 0 to delete the message");
                String confirm = scanner.nextLine().trim();
                if (confirm.equals("0")){
                    System.out.println("Message deleted.");
                }else{
                    System.out.println(" Message was kept but not sent.");
                }
            }
            case "3" -> {
                messageStore.addStored(msg);
                divider('-',45);
                System.out.println("Message successfully stored");
                divider('-',45);
            }
            default -> System.out.println("Error: invalid option.Message discarded.");
        }
        divider('-',45);
        pause();
    }
    private void displaySentMessages(){

    header("ALL SENT MESSAGES");

    if(messageStore.getSentMessages().isEmpty()){

        System.out.println("No sent messages found.");

    }else{

        for(Message msg :
                messageStore.getSentMessages()){

            divider('-',45);

            System.out.println("Recipient: "
                    + msg.getMessageRecipient());

            System.out.println("Message: "
                    + msg.getMessageText());
        }
    }

    divider('-',45);
    pause();
}
    private void displayLongestMessage(){

    header("LONGEST MESSAGE");

    if(messageStore.getSentMessages().isEmpty()){

        System.out.println("No messages found.");

    }else{

        Message longest =
                messageStore.getSentMessages().get(0);

        for(Message msg :
                messageStore.getSentMessages()){

            if(msg.getMessageText().length() >
               longest.getMessageText().length()){

                longest = msg;
            }
        }

        System.out.println(
                longest.getMessageText());
    }

    divider('-',45);
    pause();
}
    private void searchMessageID(){

    header("SEARCH MESSAGE ID");

    System.out.print("Enter Message ID: ");

    String searchID =
            scanner.nextLine();

    boolean found = false;

    for(Message msg :
            messageStore.getSentMessages()){

        if(msg.getMessageID()
                .equals(searchID)){

            System.out.println(
                    "\nMESSAGE FOUND");

            System.out.println(
                    "Recipient: "
                    + msg.getMessageRecipient());

            System.out.println(
                    "Message: "
                    + msg.getMessageText());

            found = true;
        }
    }

    if(!found){

        System.out.println(
                "Message ID not found.");
    }

    divider('-',45);
    pause();
}
    private void searchRecipient(){

    header("SEARCH RECIPIENT");

    System.out.print(
            "Enter recipient number: ");

    String recipient =
            scanner.nextLine();

    boolean found = false;

    for(Message msg :
            messageStore.getSentMessages()){

        if(msg.getMessageRecipient()
                .equals(recipient)){

            System.out.println(
                    msg.getMessageText());

            found = true;
        }
    }

    if(!found){

        System.out.println(
                "No messages found.");
    }

    divider('-',45);
    pause();

}
    private void deleteMessage(){

    header("DELETE MESSAGE");

    System.out.print(
            "Enter Message Hash: ");

    String hash =
            scanner.nextLine();

    boolean deleted =
            messageStore.deleteByHash(hash);

    if(deleted){

        System.out.println(
                "Message deleted.");

    }else{

        System.out.println(
                "Hash not found.");
    }

    divider('-',45);
    pause();
}
    private void messageReport(){

    header("MESSAGE REPORT");

    if(messageStore.getSentMessages()
            .isEmpty()){

        System.out.println(
                "No messages available.");

    }else{

        for(Message msg :
                messageStore.getSentMessages()){

            divider('-',45);

            System.out.println(
                    "Recipient: "
                    + msg.getMessageRecipient());

            System.out.println(
                    "Message: "
                    + msg.getMessageText());

            System.out.println(
                    "Message ID: "
                    + msg.getMessageID());

            System.out.println(
                    "Hash: "
                    + msg.getMessageHash());
        }
    }

    divider('-',45);
    pause();
}
    
    // show recently sent messages
    
private void showRecentMessages(){

    header("RECENTLY SENT MESSAGES");

    if(messageStore.getSentMessages().isEmpty()){

        System.out.println("No messages have been sent.");

    }else{

        List<Message> messages =
                messageStore.getSentMessages();

        int start =
                Math.max(0,
                messages.size() - 5);

        for(int i = messages.size() - 1;
            i >= start;
            i--){

            Message msg =
                    messages.get(i);

            divider('-',45);

            System.out.println(
                    "Message ID: "
                    + msg.getMessageID());

            System.out.println(
                    "Recipient: "
                    + msg.getMessageRecipient());

            System.out.println(
                    "Message: "
                    + msg.getMessageText());
        }
    }

    divider('-',45);
    pause();
}
    // --------Main QuickChat menu-----------
    
    public void run(){
        header("Welcome to QuickChat");
        System.out.println(" Hello,"+ loggedInUser+"!");
        divider('-',45);
        
        // Ask how many messages the user wants to send 
        int maxMessages=0;
        while(true){
            System.out.print("How many messages would you like to send?");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()){
                System.out.println("Error: input cannot be empty.Please enter a number.");
            }
            try{
                maxMessages = Integer.parseInt(input);
                if (maxMessages<=0){
                    System.out.println("Error: Please enter a number greater than 0.");
                }else{
                    break;
                }
            }catch(NumberFormatException e){
                System.out.println("Error: Please enter a valid number.");
            }
        }
        messageStore.setMaxMessages(maxMessages);
        System.out.println("You can send"+ maxMessages+ "message(s)");
        divider('-',45);
        
        boolean running=true;
        while(running){
            header("QUICKCHAT MENU");
            System.out.println("1. Send Messages");
            System.out.println("2. Show Recently Sent Messages");
            System.out.println("3. Display Sent Messages");
            System.out.println("4. Display Longest Message");
            System.out.println("5. Search Message ID");
            System.out.println("6. Search Recipient");
            System.out.println("7. Delete Message");
            System.out.println("8. Message Report");
            System.out.println("9. Quit");
            divider('-',45);
            System.out.println("Select an option from 1-3:");
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            switch (choice) {
case "1" -> sendMessage();

case "2" -> showRecentMessages();

case "3" -> displaySentMessages();

case "4" -> displayLongestMessage();

case "5" -> searchMessageID();

case "6" -> searchRecipient();

case "7" -> deleteMessage();

case "8" -> messageReport();

case "9" -> {
    System.out.println(
            "Goodbye,"
            + loggedInUser + "!");
    running = false;

                }
                default -> {
                    System.out.println(" Error: invalid choice. Please enter 1,2 or 3.");
                    pause();
                }
            }
        }
    }
}