/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package cluelesscoders.clueless;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CCUI {

    
    
    enum BUTTON_OPTION{
        MOVE, ACCUSE, SUGGEST, END_TURN
    }
    
    enum S_INDEX{
        SUSPECT(0), WEAPON(1), LOCATION(2);
        private final int num;
        S_INDEX(int i){this.num = i;}
        public int getValue(){return num;}
    }
    
    public JFrame cWindow;
    public JPanel p0;
    public JPanel p1;
    public JPanel p2;
    public JPanel p3;
    public JPanel p4;
    public JPanel p5;
    public JPanel p6;
    public JPanel p7;
    public JTextArea LOG;
    public JLabel myPos;
    public JLabel TurnIdx;
    public BUTTON_OPTION  opt;
    public JButton Move;
    public JButton Accuse;
    public JButton Suggest;
    public JButton EndTurn;
    public JButton Submit;
    public BufferedImage cluelessBoard;
    public DrawPanel gameBoard;
    
    public ArrayList<AllRoom> player_locations;
    private Map <AllRoom,Dimension> drawLoc;
    
    class DrawPanel extends JLabel{
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if (player_locations.isEmpty()){
                return;
            }
            int r = 30;
            Color c;
            int index;
            ArrayList<AllRoom> prev = new ArrayList<AllRoom>();


            for(PlayerName p : PlayerName.values()){
                index = p.ordinal();
                switch(p){
                    case PlayerName.Miss_Scarlet:
                        c = Color.RED;
                        break;
                    case PlayerName.Colonel_Mustard:
                        c = Color.YELLOW;
                        break;
                    case PlayerName.Mrs_White:
                        c = Color.LIGHT_GRAY;
                        break;
                    case PlayerName.Mrs_Peacock:
                        c = Color.BLUE;
                        break;
                    case PlayerName.Professor_Plum:
                        c = Color.MAGENTA;
                        break;
                    case PlayerName.Reverend_Green:
                        c = Color.GREEN;
                        break;
                    default:
                        c = Color.BLACK;
                }
                AllRoom currRoom = player_locations.get(index);
                Dimension coord = drawLoc.get(currRoom);
                int x = coord.width;
                int y = coord.height;
                if(prev.contains(currRoom)){
                    x = coord.width + 10*Collections.frequency(prev, currRoom);
                }
                prev.add(currRoom);
                g.setColor(c);
                g.fillOval(x,y, r, r);
            }
        }
    };
    
    
    public CCUI(){
        
        cWindow = new JFrame();
        cWindow.setLayout(new GridBagLayout());
        
        GridBagConstraints sz = new GridBagConstraints();
        sz.fill = GridBagConstraints.HORIZONTAL;
        
        p0 = new JPanel();
        p1 = new JPanel();
        p2 = new JPanel();
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        p6 = new JPanel();
        p7 = new JPanel();
        
        p0.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        p1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        p2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        p3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        p4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        p5.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        p6.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        
        
        
        JLabel thisPlayer = new JLabel("Your Character: ");
        p0.setLayout(new FlowLayout(FlowLayout.LEFT));
        p0.add(thisPlayer);
        
        /** Panel 1 **/
        
        JLabel playerLine = new JLabel("Players: ");
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        p1.add(playerLine);
        
        /** Panel 2 **/
        
        JLabel turnLine = new JLabel("Turn: ");
        TurnIdx = new JLabel("");
        p2.setLayout(new FlowLayout(FlowLayout.LEFT));
        p2.add(turnLine);
        p2.add(TurnIdx);
        
        /** Panel 3 **/
        
        JLabel actionLine = new JLabel("Actions :");        
        Move = new JButton("Move");
        Accuse = new JButton("Accuse");
        Suggest = new JButton("Suggest");
        EndTurn = new JButton("End Turn");
        
        Move.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                opt = BUTTON_OPTION.MOVE;
                synchronized(CCUI.class){
                    CCUI.class.notify();
                }
            }
        
        });
        
        Suggest.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                opt = BUTTON_OPTION.SUGGEST;
                synchronized(CCUI.class){
                    CCUI.class.notify();
                }
            }
        
        });
        
        Accuse.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                opt = BUTTON_OPTION.ACCUSE;
                synchronized(CCUI.class){
                    CCUI.class.notify();
                }
            }
        
        });
        
        EndTurn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                opt = BUTTON_OPTION.END_TURN;
                synchronized(CCUI.class){
                    CCUI.class.notify();
                }
            }
        
        });
        
        
        Move.setFocusPainted(false);
        Accuse.setFocusPainted(false);
        Suggest.setFocusPainted(false);
        EndTurn.setFocusPainted(false);
        
        disableAllButtons();
        
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        p3.add(actionLine);
        p3.add(Move);
        p3.add(Accuse);
        p3.add(Suggest);
        p3.add(EndTurn);
        
        /** Panel 4 **/
        
        JLabel myHandLine = new JLabel("Your Hand: ");
        p4.setLayout(new FlowLayout(FlowLayout.LEFT));
        p4.add(myHandLine);
        
        
        /** Panel 5 **/
        
        JLabel posText = new JLabel("Your Position: ");
        myPos = new JLabel(" ");
        p5.setLayout(new FlowLayout(FlowLayout.LEFT));
        p5.add(posText);
        p5.add(myPos);
        
        /** Panel 6 **/
        
        JLabel logTitle = new JLabel("Game Log: ");
        p6.setLayout(new GridBagLayout());
        GridBagConstraints logSz = new GridBagConstraints();
        LOG = new JTextArea(10,100);
        LOG.setEditable(false);
        LOG.setBackground(Color.LIGHT_GRAY);
        LOG.setForeground(Color.BLUE);
        JScrollPane textScroll = new JScrollPane(LOG);
        logSz.gridx = 0;
        logSz.gridy = 0;
        p6.add(logTitle);        
        logSz.gridx = 1;
        logSz.gridy = 0;
        p6.add(textScroll);
        
        
        /**************/
        cluelessBoard = null;
        try{
            cluelessBoard = ImageIO.read(new File("graphics/CluelessBoard.png"));
        }
        catch(IOException e){
            LOG.append(e.toString());
        }
        gameBoard = new DrawPanel();
        p7.setSize(cluelessBoard.getWidth(),cluelessBoard.getHeight());
        gameBoard.setIcon(new ImageIcon(cluelessBoard));
        p7.add(gameBoard);
        player_locations = new ArrayList<>();
        initializeBoardLocation();
       
                
                
        sz.gridx = 0;
        sz.gridy = 0;
        cWindow.add(p0,sz);
        
        sz.gridx = 0;
        sz.gridy = 1;
        cWindow.add(p1,sz);
        
        sz.gridx = 0;
        sz.gridy = 2;
        cWindow.add(p2,sz);
        
        sz.gridx = 0;
        sz.gridy = 3;
        cWindow.add(p3,sz);
        
        sz.gridx = 0;
        sz.gridy = 4;
        cWindow.add(p4,sz);
        
        sz.gridx = 0;
        sz.gridy = 5;
        cWindow.add(p5,sz);
        
        sz.gridx = 0;
        sz.gridy = 6;
        cWindow.add(p6,sz);
        
        sz.gridx = 1;
        sz.gridy = 0;
        sz.gridheight= 7;
        cWindow.add(p7,sz);
        
        cWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cWindow.setMinimumSize(new Dimension(1700,200));
        cWindow.setTitle("Clue-less Board");
        cWindow.pack();
        
    }
    
    private void initializeBoardLocation() {
        drawLoc = new HashMap<>();
        drawLoc.put(AllRoom.Study,new Dimension(75,100));
        drawLoc.put(AllRoom.Study2Hall,new Dimension(200,65));
        drawLoc.put(AllRoom.Hall, new Dimension(325,100));
        drawLoc.put(AllRoom.Hall2Lounge,new Dimension(420,65));
        drawLoc.put(AllRoom.Lounge, new Dimension(550,85));
        drawLoc.put(AllRoom.Study2Library,new Dimension(75,175));
        drawLoc.put(AllRoom.Hall2BillardRoom,new Dimension(325,175));
        drawLoc.put(AllRoom.Lounge2DiningRoom,new Dimension(525,175));
        drawLoc.put(AllRoom.Library,new Dimension(75,350));
        drawLoc.put(AllRoom.Library2Billardroom,new Dimension(200,325));
        drawLoc.put(AllRoom.Billiard_Room,new Dimension(325,350));
        drawLoc.put(AllRoom.BilliardRoom2Diningroom,new Dimension(420,325));
        drawLoc.put(AllRoom.Dining_Room,new Dimension(525,350));
        drawLoc.put(AllRoom.Library2Conservatory,new Dimension(75,425));
        drawLoc.put(AllRoom.BilliardRoom2Ballroom,new Dimension(325,425));
        drawLoc.put(AllRoom.DiningRoom2Kitchen,new Dimension(525,425));   
        drawLoc.put(AllRoom.Conservatory,new Dimension(75,500));
        drawLoc.put(AllRoom.Conservatory2Ballroom,new Dimension(200,550));
        drawLoc.put(AllRoom.Ballroom,new Dimension(325,575));
        drawLoc.put(AllRoom.Ballroom2Kitchen,new Dimension(420,550));
        drawLoc.put(AllRoom.Kitchen,new Dimension(550,500));
        
        
    }
    
    public void show(){
        cWindow.setVisible(true);
    }
    
    public void printlnUI(String message){
        LOG.append( message + '\n');
    }
    
    public void addPlayer(String message){
        JLabel playerName = new JLabel(message + " ");
        p1.add(playerName);
        p1.validate();
    }
    
    public void addCards(ArrayList<Card> cards){
        for(Card c: cards){
            JLabel cardName = new JLabel("[ "+ c.getName() + " ]");
            p4.add(cardName);
        }
        p4.validate();
    }
    
    public void setCharacter(String message){
        JLabel myName = new JLabel(message);
        p0.add(myName);
        p0.validate();
    }
    
    public void updatePosition(String message){
        myPos.setText(message);
        p5.validate();
    }
    
    public void updateTurn(String name){
        TurnIdx.setText(name);
        p2.validate();
    }
    
    public void updateBoard(ArrayList<AllRoom> player_locations){
        this.player_locations = player_locations;        
        p7.revalidate();
        gameBoard.repaint();
    }
    
    public String GetButtonInput(){
        
        synchronized(CCUI.class){  
            try{
                CCUI.class.wait();
            }
            catch(InterruptedException e){
                LOG.append("Something went wrong: " + e);
            }
        }        
        
        return switch (opt) {
            case MOVE -> "m";
            case SUGGEST -> "s";
            case ACCUSE -> "a";
            case END_TURN -> "e";
            default -> "x";
        };
    }
    
    public void toggleButton(CCUI.BUTTON_OPTION b, boolean config){
        switch(b){
            case MOVE -> Move.setEnabled(config);
            case SUGGEST -> Suggest.setEnabled(config);
            case ACCUSE -> Accuse.setEnabled(config);
            case END_TURN -> EndTurn.setEnabled(config);
        }
    }
    
    public final void disableAllButtons(){
        Move.setEnabled(false);
        Accuse.setEnabled(false);
        Suggest.setEnabled(false);
        EndTurn.setEnabled(false);
    }
    
    public AllRoom movePopup(ArrayList<AllRoom> valid_rooms){
        AllRoom roomChoice = (AllRoom)JOptionPane.showInputDialog(null,"Select a room to move to: ", 
                "Move Selected", JOptionPane.QUESTION_MESSAGE, null, valid_rooms.toArray(), valid_rooms.get(0));
        return roomChoice;
    }
    
    public ArrayList<String> SuggestionPopup(){
        JDialog sWindow = new JDialog(cWindow, "Make Suggestion",true);
        JPanel sPanel = new JPanel();
        sPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints sSz = new GridBagConstraints();
        sSz.fill = GridBagConstraints.HORIZONTAL;  
        
        sPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        
        JLabel susLabel = new JLabel("Suspect: ");
        JComboBox<String> susDD = new JComboBox(PlayerName.values());
        sSz.gridx = 0;
        sSz.gridy = 0;
        
        sPanel.add(susLabel,sSz);
        
        sSz.gridx = 1;
        sSz.gridy = 0;
        sSz.gridwidth = 2;
        sPanel.add(susDD,sSz);
        
        JLabel wpLabel = new JLabel("Weapon: ");
        JComboBox<String> wpDD = new JComboBox(Weapon.values());
        
        sSz.gridx = 0;
        sSz.gridy = 1;
        sSz.gridwidth = 1;
         sSz.gridwidth = 1;
        sPanel.add(wpLabel,sSz);
        
        sSz.gridx = 1;
        sSz.gridy = 1;
        sSz.gridwidth = 2;
        sPanel.add(wpDD,sSz);
        
        JLabel rmLabel = new JLabel("Room: ");
        JTextArea rmTxt = new JTextArea(2,15);
        rmTxt.setEditable(false);
        rmTxt.setText(myPos.getText());
        sSz.gridx = 0;
        sSz.gridy = 2;
         sSz.gridwidth = 1;
        sPanel.add(rmLabel,sSz);
        
        sSz.gridx = 1;
        sSz.gridy = 2;
        sSz.gridwidth = 2;
        sPanel.add(rmTxt,sSz);
        
       
        
        Submit = new JButton("Submit");
        Submit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                sWindow.setVisible(false);
            }
        
        });
        
        Submit.setFocusPainted(false);        
        
        sSz.gridx = 1;
        sSz.gridy = 3;
        sSz.gridwidth = 1;
        sPanel.add(Submit,sSz);
        
        sWindow.add(sPanel);
        
        sWindow.pack();
        
        sWindow.setVisible(true);
        
        ArrayList<String> rSugg = new ArrayList<String>(); 
        rSugg.add(susDD.getSelectedItem().toString());
        rSugg.add(wpDD.getSelectedItem().toString());
        rSugg.add(myPos.getText());
        
        sWindow.dispose();
        
        return rSugg;
        
    }
    
    public ArrayList<String> AccusationPopup(){
        JDialog aWindow = new JDialog(cWindow, "Make Accusation",true);
        JPanel aPanel = new JPanel();
        aPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints aSz = new GridBagConstraints();
        aSz.fill = GridBagConstraints.HORIZONTAL;  
        
        aPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5)
                , BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        
        JLabel susLabel = new JLabel("Suspect: ");
        JComboBox<String> susDD = new JComboBox(PlayerName.values());
        aSz.gridx = 0;
        aSz.gridy = 0;
        
        aPanel.add(susLabel,aSz);
        
        aSz.gridx = 1;
        aSz.gridy = 0;
        aSz.gridwidth = 2;
        aPanel.add(susDD,aSz);
        
        JLabel wpLabel = new JLabel("Weapon: ");
        JComboBox<String> wpDD = new JComboBox(Weapon.values());
        
        aSz.gridx = 0;
        aSz.gridy = 1;
        aSz.gridwidth = 1;
        aSz.gridwidth = 1;
        aPanel.add(wpLabel,aSz);
        
        aSz.gridx = 1;
        aSz.gridy = 1;
        aSz.gridwidth = 2;
        aPanel.add(wpDD,aSz);
        
        JLabel rmLabel = new JLabel("Room: ");
        JComboBox<String> rmDD = new JComboBox(Room.values());
        
        aSz.gridx = 0;
        aSz.gridy = 2;
        aSz.gridwidth = 1;
        aPanel.add(rmLabel,aSz);
        
        aSz.gridx = 1;
        aSz.gridy = 2;
        aSz.gridwidth = 2;
        aPanel.add(rmDD,aSz);
        
       
        
        Submit = new JButton("Submit");
        Submit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                aWindow.setVisible(false);
            }
        
        });
        
        Submit.setFocusPainted(false);        
        
        aSz.gridx = 1;
        aSz.gridy = 3;
        aSz.gridwidth = 1;
        aPanel.add(Submit,aSz);
        
        aWindow.add(aPanel);
        
        aWindow.pack();
        
        aWindow.setVisible(true);
        
        ArrayList<String> rAcc = new ArrayList<String>(); 
        rAcc.add(susDD.getSelectedItem().toString());
        rAcc.add(wpDD.getSelectedItem().toString());
        rAcc.add(rmDD.getSelectedItem().toString());
        
        aWindow.dispose();
        
        return rAcc;
        
    }
    
    public String DisprovePopup(ArrayList<Card> cards){
         String cardChoice = ((Card)JOptionPane.showInputDialog(null,"Which card would you like to disprove with?", 
                "Disprove Suggestion", JOptionPane.QUESTION_MESSAGE, null, cards.toArray(), cards.get(0))).toString();
        return cardChoice;
    }
}
