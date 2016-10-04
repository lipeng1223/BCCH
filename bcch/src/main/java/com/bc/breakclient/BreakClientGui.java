/*
 * BreakClientGuiTest.java
 *
 * Created on November 22, 2005, 8:12 PM
 */

package com.bc.breakclient;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.bc.dao.BreakClientDao;
import com.bc.orm.BreakReceived;
import com.bc.orm.BreakReceivedItem;
import com.bc.orm.BriCount;
import com.bc.orm.InventoryItem;
import com.bc.orm.Received;
import com.bc.orm.ReceivedItem;
import com.bc.orm.User;
import com.bc.orm.VendorSkidType;
import com.bc.util.DateFormat;
import com.bc.util.PrintLabel;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


/**
 *
 * @author  Tim
 */
public class BreakClientGui extends javax.swing.JFrame implements ListSelectionListener {

    public User user;
    public ArrayList<BreakReceived> breakReceivedList;
    private ArrayList<VendorSkidType> skidTypeList;
    private DefaultListModel breakListModel;
    private DefaultListModel itemListModel;
    public String printerName = "DYMO LabelWriter 400 Turbo";
    public String labelFile = "C:\\BreakRoomHorizontal.LWL";
    private Boolean finishing = false;
    private static final String UNKNOWN = "<< Unknown >>";
    
    private Boolean processing = false;


    /** Creates new form BreakClientGuiTest */
    public BreakClientGui() {
        breakReceivedList = new ArrayList<BreakReceived>();
        breakListModel = new DefaultListModel();
        itemListModel = new DefaultListModel();
        initComponents();
        receivingList.setModel(breakListModel);
        itemList.setModel(itemListModel);
        receivingList.addListSelectionListener(this);
        itemList.addListSelectionListener(this);
        status.setText("");
        isbnCombo.setModel(new DefaultComboBoxModel());

        JTextField textField = ((JTextField)isbnCombo.getEditor().getEditorComponent());
//        textField.addKeyListener(
//            new KeyListener() {
//            public void keyPressed(KeyEvent e) {
//            }
//            public void keyReleased(KeyEvent e) {
//            }
//            public void keyTyped(KeyEvent e) {
//                // try an isbn lookup
//                String isbnText = getIsbn();
//                String orig = getIsbn();
//                status.setText("Key Pressed : " + e.getKeyChar() + " current isbn : " + orig);
//                try{
//                    if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE){
//                        isbnText += e.getKeyChar();
//                    }
//                    //System.out.println("isbnText: "+isbnText);
//                    BreakClientDao dao = new BreakClientDao();
//                    List<InventoryItem> list = dao.findBunchIsbn(isbnText.trim());
//                    if (list.size() == 1){
//                        InventoryItem ii = list.get(0);
//                        inputTitle.setText(ii.getTitle());
//                        inputBin.setText(ii.getBin());
//                    }
//                    //System.out.println("list size: "+list.size());
//                    isbnCombo.removeAllItems();
//                    isbnCombo.addItem(orig);
//                    for (InventoryItem ii : list){
//                        isbnCombo.addItem(ii.getIsbn());
//                    }
//                } catch(Exception er){
//                    er.printStackTrace();
//                }
//                status.setText(isbnText);
//            }
//        }
//        );
        textField.getDocument().addDocumentListener(new DocumentListener(){

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!processing)
                    process();
                System.out.println("Insert Update");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!processing)
                    process();
                System.out.println("Remove Update");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("Change Update");
            }
            
            private void process(){
                
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        String isbnText = getIsbn();
                        try{
                            processing = true;
                            status.setText("Processing " + isbnText);
                            BreakClientDao dao = new BreakClientDao();
                            List<InventoryItem> list = dao.findBunchIsbn(isbnText.trim());
                            status.setText("Finished Processing " + isbnText);
                            if (list.size() == 1){
                                InventoryItem ii = list.get(0);
                                inputTitle.setText(ii.getTitle());
                                inputBin.setText(ii.getBin());
                            }
                            //System.out.println("list size: "+list.size());
                            isbnCombo.removeAllItems();
                            isbnCombo.addItem(isbnText);
                            processing = false;
                            for (InventoryItem ii : list){
                                isbnCombo.addItem(ii.getIsbn());
                            }
                            //isbnCombo.setEditable(true);

                            //isbnCombo.setPopupVisible(true);
                        }
                        catch(Exception er){
                            status.setText("Processing " + isbnText + " error : " + er.getMessage());
                            processing = false;
                        }
                        processing = false;
                    }
                });
            }
            
        });
        

        if (new File(labelFile).exists()){
            printMenu.setEnabled(true);
        }

    }

    private void setIsbn(String isbn){
        ((JTextField)isbnCombo.getEditor().getEditorComponent()).setText(isbn);
    }

    private String getIsbn(){
        return ((JTextField)isbnCombo.getEditor().getEditorComponent()).getText();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource().equals(receivingList)){
            updateItemList(receivingList.getSelectedIndex());
            resetInfoPanel();
            enableInput();
            deleteReceiving.setEnabled(true);
            finishReceiving.setEnabled(true);
            deleteItem.setEnabled(false);
        } else {
            loadInfoPanel(receivingList.getSelectedIndex(), itemList.getSelectedIndex());
            finishReceiving.setEnabled(true);
            deleteItem.setEnabled(true);
        }
        status.setText("");
        setFocus();
    }

    public void enableInput(){
        itemList.setEnabled(true);
        isbnCombo.setEnabled(true);
        isbnCombo.setEditable(true);
        inputReceived.setEnabled(true);
        inputReceived.setEditable(true);
        inputOrdered.setEditable(true);
        inputOrdered.setEnabled(true);
        inputTitle.setEnabled(true);
        inputTitle.setEditable(true);
        inputBin.setEnabled(true);
        inputBin.setEditable(true);
        itemAdd.setEnabled(true);
        skidCheck.setEnabled(true);
        breakRoom.setEnabled(true);
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
        loggedInAs.setText("Logged in as:  "+user.getUsername());
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        newReceiving = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        receivingList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        deleteItem = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        infoIsbn = new javax.swing.JLabel();
        infoReceived = new javax.swing.JLabel();
        infoOrdered = new javax.swing.JLabel();
        infoBin = new javax.swing.JLabel();
        infoTitle = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        skidTypeLabel = new javax.swing.JLabel();
        infoSkid = new javax.swing.JLabel();
        infoSkidType = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        infoPieceCountList = new javax.swing.JList();
        infoPieceCount = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        infoCondition = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        itemAdd = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        inputReceived = new javax.swing.JTextField();
        inputOrdered = new javax.swing.JTextField();
        inputBin = new javax.swing.JTextField();
        inputTitle = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        skidCheck = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        skidType = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        breakRoom = new javax.swing.JCheckBox();
        breakRoomIsbn = new javax.swing.JTextField();
        isbnCombo = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        conditionCombo = new javax.swing.JComboBox();
        status = new javax.swing.JTextField();
        deleteReceiving = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        recInfo = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();
        finishReceiving = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        printMenu = new javax.swing.JMenu();
        print = new javax.swing.JMenuItem();
        loggedInAs = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Break Room");

        newReceiving.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        newReceiving.setText("New");
        newReceiving.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newReceivingActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 204));
        jLabel1.setText("Receiving");

        receivingList.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        receivingList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(receivingList);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 204));
        jLabel2.setText("Items");

        itemList.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        itemList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemList.setEnabled(false);
        jScrollPane2.setViewportView(itemList);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selected Item Info", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("ISBN:");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Received:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Ordered:");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Title:");

        deleteItem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        deleteItem.setText("Delete");
        deleteItem.setEnabled(false);
        deleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Bin:");

        infoIsbn.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoIsbn.setForeground(new java.awt.Color(0, 0, 204));
        infoIsbn.setText("jLabel14");

        infoReceived.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoReceived.setForeground(new java.awt.Color(0, 0, 204));
        infoReceived.setText("jLabel15");

        infoOrdered.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoOrdered.setForeground(new java.awt.Color(0, 0, 204));
        infoOrdered.setText("jLabel16");

        infoBin.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoBin.setForeground(new java.awt.Color(0, 0, 204));
        infoBin.setText("jLabel17");

        infoTitle.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoTitle.setForeground(new java.awt.Color(0, 0, 204));
        infoTitle.setText("jLabel18");
        infoTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Skid:");

        skidTypeLabel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        skidTypeLabel.setText("Skid Type:");

        infoSkid.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoSkid.setForeground(new java.awt.Color(0, 0, 204));
        infoSkid.setText("jLabel20");

        infoSkidType.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        infoSkidType.setForeground(new java.awt.Color(0, 0, 204));
        infoSkidType.setText("jLabel20");

        infoPieceCountList.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        infoPieceCountList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        infoPieceCountList.setEnabled(false);
        jScrollPane4.setViewportView(infoPieceCountList);

        infoPieceCount.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        infoPieceCount.setText("Piece Count:");

        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Condition:");

        infoCondition.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        infoCondition.setForeground(new java.awt.Color(0, 0, 204));
        infoCondition.setText("jLabel20");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                                .add(14, 14, 14)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel11)
                                    .add(jLabel5)
                                    .add(jLabel4)
                                    .add(jLabel3))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel1Layout.createSequentialGroup()
                                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                            .add(infoBin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(infoOrdered, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, infoIsbn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                                        .add(37, 37, 37))
                                    .add(jPanel1Layout.createSequentialGroup()
                                        .add(infoReceived, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(skidTypeLabel)
                                    .add(jLabel17)
                                    .add(infoPieceCount)))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel19)
                                    .add(jLabel6))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(infoCondition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(infoTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(infoSkid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(4, 4, 4)
                                .add(infoSkidType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(deleteItem)))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(infoIsbn)
                    .add(jLabel17)
                    .add(infoSkid))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(skidTypeLabel)
                    .add(infoSkidType)
                    .add(infoReceived))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel5)
                                    .add(infoOrdered))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel11)
                                    .add(infoBin)))
                            .add(infoPieceCount))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6)
                            .add(infoTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(infoCondition))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteItem)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Receiving Item", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N

        itemAdd.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        itemAdd.setText("Add");
        itemAdd.setEnabled(false);
        itemAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAddActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("ISBN:");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Received:");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Ordered:");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Bin:");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Title:");

        inputReceived.setEditable(false);
        inputReceived.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        inputReceived.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inputReceivedKeyTyped(evt);
            }
        });

        inputOrdered.setEditable(false);
        inputOrdered.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        inputBin.setEditable(false);
        inputBin.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        inputTitle.setEditable(false);
        inputTitle.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setText("OR");

        skidCheck.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        skidCheck.setText("Skid");
        skidCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        skidCheck.setEnabled(false);
        skidCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        skidCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skidCheckActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Skid Type:");

        skidType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        skidType.setEnabled(false);
        skidType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skidTypeActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel16.setText("OR");

        breakRoom.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        breakRoom.setText("Break Room");
        breakRoom.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        breakRoom.setEnabled(false);
        breakRoom.setMargin(new java.awt.Insets(0, 0, 0, 0));
        breakRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakRoomActionPerformed(evt);
            }
        });

        breakRoomIsbn.setEnabled(false);

        isbnCombo.setEditable(true);
        isbnCombo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        isbnCombo.setEnabled(false);
        isbnCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                isbnComboItemStateChanged(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Condition:");

        conditionCombo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        conditionCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "hurt", "unjacketed", "overstock" }));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel9)
                                    .add(jLabel10)
                                    .add(jLabel12)
                                    .add(jLabel18))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(conditionCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(inputReceived)
                                        .add(inputOrdered)
                                        .add(inputBin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(jLabel15)
                                        .add(214, 214, 214))
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(jLabel16)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                                        .add(breakRoom)
                                        .add(18, 18, 18)
                                        .add(breakRoomIsbn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                                .add(26, 26, 26)
                                .add(jLabel13)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(inputTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))))
                    .add(itemAdd)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(isbnCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 182, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel7)
                        .add(29, 29, 29)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(skidCheck)
                            .add(skidType, 0, 178, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(isbnCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(skidCheck)
                    .add(jLabel7))
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel18)
                            .add(conditionCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(skidType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel15)))
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 8, Short.MAX_VALUE)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(inputReceived, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(inputOrdered, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel12)
                            .add(inputBin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(7, 7, 7))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel16)
                            .add(breakRoom)
                            .add(breakRoomIsbn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(inputTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(itemAdd)
                .add(0, 0, 0))
        );

        status.setEditable(false);
        status.setBackground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        status.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        status.setForeground(new java.awt.Color(0, 0, 204));
        status.setText("Status");

        deleteReceiving.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        deleteReceiving.setText("Delete");
        deleteReceiving.setEnabled(false);
        deleteReceiving.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteReceivingActionPerformed(evt);
            }
        });

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setEnabled(false);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(150, 60));

        recInfo.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.inactiveBackground"));
        recInfo.setColumns(20);
        recInfo.setEditable(false);
        recInfo.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        recInfo.setForeground(new java.awt.Color(0, 0, 204));
        recInfo.setRows(5);
        recInfo.setPreferredSize(new java.awt.Dimension(150, 65));
        jScrollPane3.setViewportView(recInfo);

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Receiving Info:");

        finishReceiving.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        finishReceiving.setText("Send To Inventory");
        finishReceiving.setEnabled(false);
        finishReceiving.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishReceivingActionPerformed(evt);
            }
        });

        jLabel20.setText("jLabel20");

        jLabel21.setText("jLabel21");

        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("version 3.0");

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        exitMenuItem.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exitMenuItem);

        jMenuBar1.add(jMenu1);

        printMenu.setText("Print");
        printMenu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        print.setText("Print Skid Labels");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });
        printMenu.add(print);

        jMenuBar1.add(printMenu);

        loggedInAs.setForeground(new java.awt.Color(0, 0, 204));
        loggedInAs.setText("Logged in as:");
        loggedInAs.setFocusable(false);
        loggedInAs.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        loggedInAs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        loggedInAs.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        loggedInAs.setMargin(new java.awt.Insets(2, 500, 2, 2));
        loggedInAs.setRequestFocusEnabled(false);
        jMenuBar1.add(loggedInAs);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(newReceiving)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deleteReceiving))
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel2)))
                            .add(layout.createSequentialGroup()
                                .add(30, 30, 30)
                                .add(finishReceiving)))))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(jLabel14)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 284, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 91, Short.MAX_VALUE)
                        .add(jLabel22)
                        .add(52, 52, 52))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, status, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)))
                        .addContainerGap(42, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(jLabel14))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(layout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(finishReceiving)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel2))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(newReceiving)
                                    .add(deleteReceiving))
                                .add(16, 16, 16))))
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel22)))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(246, 246, 246)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 390, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(status, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(928, 619));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void isbnComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_isbnComboItemStateChanged
        BreakClientDao dao = new BreakClientDao();
        InventoryItem ii = dao.findByIsbnCond((String)isbnCombo.getSelectedItem(), (String)conditionCombo.getSelectedItem());
        if (ii != null){
            inputTitle.setText(ii.getTitle());
            inputBin.setText(ii.getBin());
        }
    }//GEN-LAST:event_isbnComboItemStateChanged

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
//        if (new File(labelFile).exists()){
            BreakPrint bp = new BreakPrint(this);
            bp.setVisible(true);
//        }
    }//GEN-LAST:event_printActionPerformed

    private void breakRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_breakRoomActionPerformed
        if (breakRoom.isSelected()){
            BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
            breakRoomIsbn.setText(br.getVendorCode()+"BR");
            breakRoomIsbn.setEnabled(true);
            skidCheck.setSelected(false);
            inputReceived.setText("-1");
            inputOrdered.setText("-1");
            inputReceived.setEnabled(false);
            inputOrdered.setEnabled(false);
            isbnCombo.setEnabled(true);
        } else {
            isbnCombo.setEnabled(true);
            breakRoomIsbn.setText("");
            breakRoomIsbn.setEnabled(false);
            inputReceived.setText("");
            inputOrdered.setText("");
            inputReceived.setEnabled(true);
            inputOrdered.setEnabled(true);
        }
    }//GEN-LAST:event_breakRoomActionPerformed

    private void finishReceivingActionPerformed(java.awt.event.ActionEvent evt) {
        BreakProgress progress = new BreakProgress();
        try {
            BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
            int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to finish receiving: "+
                br.getDatePoListView()+
                "?\nIf you choose \"Yes\" you will not be able to edit this recieving anymore.", "Finish This Receiving?", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION){
                progress.pack(); 
                progress.setLocationRelativeTo(this); 
                progress.setVisible(true);
                
                BreakClientDao dao = new BreakClientDao();
                // send this receiving into the inventory as a pending receiving

                Received rec = new Received();
                rec.setClerk(user.getUsername());
                rec.setPoDate(br.getPoDate());
                if (rec.getPoDate() == null){
                    rec.setPoDate(Calendar.getInstance().getTime());
                }
                rec.setPoNumber(br.getPoNumber());
                rec.setVendor(br.getVendor());
                if (br.getVendor() != null) rec.setVendorCode(br.getVendor().getCode());
                rec.setComment(br.getComment());
                rec.setPosted(false);
                rec.setSkid(br.getSkid());
                rec.setSkidIsbn(br.getSkidbarcode());
                rec.setDate(br.getDate());
                rec.setCreateTime(Calendar.getInstance().getTime());
                dao.save(rec);
    
                List<BreakReceivedItem> items = br.getBreakReceivedItems();
    
                boolean includeBrItem = false;
                String brItemISBN = null;
                String brItemCondition = null;
                int brItemCount = 0;
    
                ReceivedItem brRecItem = null;
                Calendar now = Calendar.getInstance();
                for (BreakReceivedItem bri : items){
                    if (bri.getSkid()){ // skid box checked
    
                        List<BriCount> briCountItems = bri.getBriCounts();
                        int count = 0;
                        for (BriCount briCount : briCountItems){
                            ReceivedItem ri = new ReceivedItem();
                            ri.setDate(bri.getDate());
                            ri.setCreateTime(now.getTime());
                            count++;
                            ri.setIsbn(bri.getIsbn()+"_"+count+"of"+briCountItems.size());
                            if (bri.getCond() == null || bri.getCond().length() == 0){
                                ri.setCond("hurt");
                            } else {
                                ri.setCond(bri.getCond());
                            }
                            ri.setReceived(rec);
                            ri.setPoNumber(rec.getPoNumber());
                            ri.setQuantity(1);
                            ri.setOrderedQuantity(bri.getOrderedQuantity());
                            ri.setTitle(bri.getTitle());
                            ri.setPieces(bri.getPieceCount());
                            ri.setAvailable(1);
                            ri.setBreakroom(bri.getBreakRoom());
    
                            ri.setType("Skid");
                            ri.setBin("DOCK");
                            ri.setSkidPieceCount(briCount.getCountOrLbs().intValue());
                            if (!briCount.getPieces()){
                                ri.setType("Lbs");
                            }
                            // see if we have received this skid before, set the cost
                            StringTokenizer st = new StringTokenizer(bri.getIsbn(), "_", true);
                            StringBuilder sb = new StringBuilder();
                            boolean done = false;
                            while (!done){
                                String token = st.nextToken();
                                if (token.equals("SKID")){
                                    done = true;
                                    sb.append(token);
                                    break;
                                }
                                sb.append(token);
                            }
                            List<InventoryItem> list = dao.findBeginingWithIsbn(sb.toString());
                            if (list.size() > 0) {
                                InventoryItem ii = list.get(0);
                                ri.setCost(ii.getReceivedPrice());
                                ri.setSellPrice(ii.getSellingPrice());
                                ri.setSkidPieceCost(ii.getSkidPieceCost());
                                ri.setSkidPiecePrice(ii.getSkidPiecePrice());
                                ri.setSkidPrice(ii.getSellingPrice());
                                ri.setInventoryItem(ii);
                            }
                            if (ri.getType().equals("Lbs") && ri.getSkidPieceCount() != null && ri.getSkidPieceCount() > 0 &&
                                    ri.getCost() != null && ri.getCost() > 0)
                            {
                                ri.setSkidPieceCost(ri.getCost() / ri.getSkidPieceCount());
                            }
                            if (ri.getType().equals("Lbs") && ri.getSkidPieceCount() != null && ri.getSkidPieceCount() > 0 &&
                                    ri.getSellPrice() != null && ri.getSellPrice() > 0)
                            {
                                ri.setSkidPiecePrice(ri.getSellPrice() / ri.getSkidPieceCount());
                            }
                            if (ri.getSkidPieceCount() != null && ri.getSkidPieceCount() > 0 &&
                                    ri.getCost() != null && ri.getCost() > 0)
                            {
                                ri.setSkidPieceCost(ri.getCost() / ri.getSkidPieceCount());
                            }
                            if (ri.getSkidPieceCount() != null && ri.getSkidPieceCount() > 0 &&
                                    ri.getSellPrice() != null && ri.getSellPrice() > 0)
                            {
                                ri.setSkidPiecePrice(ri.getSellPrice() / ri.getSkidPieceCount());
                            }
    
                            ri.setCreateTime(Calendar.getInstance().getTime());
                            
                            // create/update inventory
                            InventoryItem ii = dao.findByIsbnCond(ri.getIsbn(), ri.getCond());
                            if (ii == null){
                                ii = new InventoryItem();
                                ii.setIsbn(ri.getIsbn());
                                ii.setCond(ri.getCond());
                                ii.setSkid(bri.getSkid());
                                ii.setTitle(ri.getTitle());
                                ii.setLastUpdateBy(user.getUsername());
                                ii.setLastUpdate(Calendar.getInstance().getTime());
                                ii.setCreateTime(Calendar.getInstance().getTime());
                                dao.save(ii);
                            }
                            ri.setInventoryItem(ii);
                            dao.save(ri);
    
    
                            ii.setBin(ri.getBin());
                            ii.setSellingPrice(ri.getSellPrice());
                            ii.setReceivedPrice(ri.getCost());
                            ii.setSkidPieceCost(ri.getSkidPieceCost());
                            ii.setSkidPiecePrice(ri.getSkidPiecePrice());
                            ii.setSellingPrice(ri.getSkidPrice());
                            ii.setSkidPieceCount(briCount.getCountOrLbs().intValue());
    
                            ii.setOnhand(ii.getOnhand()+ri.getQuantity());
                            
                            int committed = dao.getCommitted(ii.getId());
                            ii.setCommitted(committed);
                            ii.setAvailable(ii.getOnhand()-committed);
    
                            dao.update(ii);
                        }
                    } else {
                        // This is either a move to break room, or a break in the break room
                        ReceivedItem ri = new ReceivedItem();
                        ri.setCreateTime(now.getTime());
                        ri.setDate(bri.getDate());
                        ri.setIsbn(bri.getIsbn());
                        if (bri.getCond() == null || bri.getCond().length() == 0){
                            ri.setCond("hurt");
                        } else {
                            ri.setCond(bri.getCond());
                        }
                        ri.setReceived(rec);
                        ri.setPoNumber(rec.getPoNumber());
                        ri.setQuantity(bri.getQuantity());
                        ri.setOrderedQuantity(bri.getOrderedQuantity());
                        ri.setTitle(bri.getTitle());
                        ri.setPieces(bri.getPieceCount());
                        ri.setAvailable(bri.getQuantity());
                        ri.setBreakroom(false);
    
                        ri.setType("Pieces");
    
                        // create/update inventory
                        InventoryItem ii = dao.findByIsbnCond(ri.getIsbn(), ri.getCond());
                        if (bri.getBreakRoom()){ // move to break room
                            InventoryItem ib = dao.findByIsbnCond(bri.getBreakRoomIsbn(), bri.getCond());
                            if (ib == null){
                                ib = new InventoryItem();
                                ib.setIsbn(bri.getBreakRoomIsbn());
                                ib.setCond(ri.getCond());
                                ib.setBin("BKRM");
                                ib.setTitle("Break Room");
                                ib.setLastUpdateBy(user.getUsername());
                                ib.setLastUpdate(Calendar.getInstance().getTime());
                                System.out.println("created inv item: "+ib.getIsbn());
                                ib.setCreateTime(Calendar.getInstance().getTime());
                                dao.save(ib);
                            }
                            ri.setType("Skid");
                            ri.setInventoryItem(ii);
                            ri.setCost(ii.getReceivedPrice());
                            ri.setSellPrice(ii.getSellingPrice());
                            ri.setSkidPieceCost(ii.getSkidPieceCost());
                            ri.setSkidPiecePrice(ii.getSkidPiecePrice());
                            ri.setSkidPrice(ii.getSellingPrice());
                            brRecItem = new ReceivedItem();
                            brRecItem.setCreateTime(now.getTime());
                            brRecItem.setCost(ii.getReceivedPrice());
                            brRecItem.setSellPrice(ii.getSellingPrice());
                            brRecItem.setSkidPieceCost(ii.getSkidPieceCost());
                            brRecItem.setSkidPiecePrice(ii.getSkidPiecePrice());
                            brRecItem.setSkidPrice(ii.getSellingPrice());
    
                            ib.setOnhand(ib.getOnhand()+bri.getPieceCount());
    
                            ii.setBreakroomIsbn(ib.getIsbn());
                            ii.setBreakRoomCondition(ib.getCond());
                            // decreasing onhand here
                            ii.setOnhand(ii.getOnhand()-1);
                            ii.setAvailable(ii.getAvailable()-1);
                            dao.update(ii);
                            System.out.println("set break room isbn of "+ii.getIsbn()+" to "+ib.getIsbn());
    
                            int committed = dao.getCommitted(ib.getId());
                            ib.setCommitted(committed);
                            ib.setAvailable(ib.getOnhand()-committed);
                            dao.update(ib);
    
                            includeBrItem = true;
                            brItemISBN = bri.getBreakRoomIsbn();
                            brItemCondition = bri.getCond();
                            if (ii != null){
                                brItemCount += ii.getSkidPieceCount();
                            }
    
                        } else { // break in break room
                            if (ii == null){
                                ii = new InventoryItem();
                                ii.setIsbn(ri.getIsbn());
                                ii.setCond(ri.getCond());
                                ii.setSkid(bri.getSkid());
                                ii.setTitle(ri.getTitle());
                                
                                ii.setLastUpdateBy(user.getUsername());
                                ii.setLastUpdate(Calendar.getInstance().getTime());
                                ii.setCreateTime(Calendar.getInstance().getTime());
                                dao.save(ii);
                            }
                            ri.setInventoryItem(ii);
                            ii.setOnhand(ii.getOnhand()+ri.getQuantity());
                            ii.setBin(ri.getBin());
    
                            int committed = dao.getCommitted(ii.getId());
                            ii.setCommitted(committed);
                            ii.setAvailable(ii.getOnhand()-committed);
                            dao.update(ii);
                        }
    
                        // no matter what we want to save the rec item
                        if (ii != null) {
                            ri.setCost(ii.getReceivedPrice());
                            ri.setSellPrice(ii.getSellingPrice());
                        }
                        ri.setCreateTime(Calendar.getInstance().getTime());
                        dao.save(ri);
    
                        if (br.getSkid()){
                            System.out.println("br is skid, rec skid isbn: "+rec.getSkidIsbn());
                            // if it was a skid that was being broken, decrease the breakroom onhand
                            ii = dao.findByIsbnCond(rec.getSkidIsbn(), rec.getSkidCondition());
                            // decrease the breakroomISBN inv item by quantity
                            if (ii != null){
                                System.out.println(ii.getIsbn()+" break room isbn: "+ii.getBreakroomIsbn());
                                InventoryItem bi = dao.findByIsbnCond(ii.getBreakroomIsbn(), ii.getBreakRoomCondition());
                                if (bi != null){
                                    System.out.println("decreasing on hand of "+ii.getBreakroomIsbn());
                                    bi.setOnhand(bi.getOnhand()-bri.getQuantity());
                                    int committed = dao.getCommitted(bi.getId());
                                    bi.setCommitted(committed);
                                    bi.setAvailable(bi.getOnhand()-committed);
                                    dao.update(bi);
                                }
                            }
                        }
                    }
                }
                if (includeBrItem){
                    InventoryItem ii = dao.findByIsbnCond(brItemISBN, brItemCondition);
                    if (ii != null){
                        brRecItem.setDate(rec.getPoDate());
                        brRecItem.setIsbn(brItemISBN);
                        brRecItem.setCond(brItemCondition);
                        brRecItem.setReceived(rec);
                        brRecItem.setPoNumber(rec.getPoNumber());
                        brRecItem.setQuantity(brItemCount);
                        brRecItem.setOrderedQuantity(brItemCount);
                        brRecItem.setTitle(ii.getTitle());
                        brRecItem.setAvailable(brItemCount);
                        brRecItem.setBreakroom(true);
                        brRecItem.setCost(ii.getSkidPieceCost());
                        brRecItem.setSellPrice(ii.getSellingPrice());
                        brRecItem.setType("Pieces");
                        brRecItem.setCreateTime(Calendar.getInstance().getTime());
                        brRecItem.setInventoryItem(ii);
                        dao.save(brRecItem);
                        // update rec since this is a break
                        rec.setSkidBreak(true);
                        dao.update(rec);
                    }
                }
    
                // last thing, if it was a skid break, reduce the skid count by 1
                if (rec.getSkid()){
                    InventoryItem ii = dao.findByIsbnCond(rec.getSkidIsbn(), rec.getSkidCondition());
                    if (ii != null){
                        ii.setOnhand(ii.getOnhand()-1);
                        if (ii.getOnhand() < 0){
                            ii.setOnhand(0);
                        }
                        int committed = dao.getCommitted(ii.getId());
                        ii.setCommitted(committed);
                        ii.setAvailable(ii.getOnhand()-committed);
                        dao.update(ii);
                    }
                }
    
                removeReceiving(br);
    
                resetInfoPanel();
                resetInputPanel();
                finishReceiving.setEnabled(false);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (progress.isShowing()){
                progress.dispose();
            }
        }
    }

    private void skidTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skidTypeActionPerformed
        if (skidCheck.isSelected()){
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyy");
            if (skidType.getSelectedIndex() > 0){
                BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
                isbnCombo.removeAllItems();
                isbnCombo.addItem(br.getVendorCode()+"_"+skidTypeList.get(skidType.getSelectedIndex()-1).getSkidtype()+"_SKID_"+sdf.format(br.getPoDate()));
            } else {
                BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
                isbnCombo.removeAllItems();
                isbnCombo.addItem(br.getVendorCode()+"_SKID_"+sdf.format(br.getPoDate()));
            }
        }
    }//GEN-LAST:event_skidTypeActionPerformed

    private void skidCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skidCheckActionPerformed
        if (breakReceivedList != null && breakReceivedList.size() > 0 && receivingList != null && receivingList.getSelectedValue() != null){
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyy");
            inputBin.setText("");
            inputTitle.setText("");
            if (skidCheck.isSelected()){
                breakRoomIsbn.setText("");
                breakRoom.setSelected(false);
                BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
                isbnCombo.removeAllItems();
                isbnCombo.addItem(br.getVendorCode()+"_SKID_"+sdf.format(br.getPoDate()));
                skidType.setEnabled(true);
                isbnCombo.setEnabled(false);
                inputTitle.setEnabled(false);
                inputReceived.setEnabled(true);
                inputOrdered.setEnabled(true);
            } else {
                isbnCombo.removeAllItems();
                skidType.setEnabled(false);
                isbnCombo.setEnabled(true);
                inputTitle.setEnabled(true);
            }
        }
    }//GEN-LAST:event_skidCheckActionPerformed

    private void printLabel(Integer itemListIndex){
        BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
        BreakReceivedItem item = (BreakReceivedItem)br.getBreakReceivedItems().get(itemListIndex);
        if (item.getSkid()){
            StringBuilder sb = new StringBuilder();
            sb.append("PO Date: ");
            sb.append(DateFormat.format(br.getPoDate()));
            sb.append("\nPO Number: ");
            sb.append(br.getPoNumber());
            sb.append("\nVendor: ");
            if (br.getVendor() != null){
                sb.append(br.getVendor().getCodePlusName());
            } else {
                sb.append(br.getVendorCode());
            }

            String info = sb.toString();

            List<BriCount> countItems = item.getBriCounts();
            for (int i = 0; i < item.getQuantity(); i++){
                String barString = item.getIsbn()+"_"+(i+1)+"of"+countItems.size();
                String numOf = (i+1)+" of "+item.getQuantity();
                BriCount bc = countItems.get(i);
                StringBuilder bcString = new StringBuilder();
                if (bc.getPieces()){
                    bcString.append("\nPiece Count: "+bc.getCountOrLbs());
                } else {
                    bcString.append("\nSkid Lbs: "+bc.getCountOrLbs().intValue());
                }
                Map<String, String> nv = new HashMap<String, String>();
                nv.put("INFO", info+bcString.toString());
                nv.put("NUMOF", numOf);
                nv.put("BARCODETEXT", barString);
                nv.put("USER", user.getUsername());
                if (!PrintLabel.dymoPrint(printerName, labelFile, nv)){
                    JOptionPane.showMessageDialog(this,
                            "Could not print the labels to the dymo printer.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void inputReceivedKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputReceivedKeyTyped
        super.processKeyEvent(evt);
        // cannot get this to work right
        //inputOrdered.setText(inputReceived.getText());
    }//GEN-LAST:event_inputReceivedKeyTyped

    private void itemAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAddActionPerformed
        if (receivingList.getSelectedIndex() < 0){
            JOptionPane.showMessageDialog(this,
                "You must select an Open Receiving first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String isbnText = getIsbn();
        if (isbnText != null && isbnText.length() > 0){
            BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
            BreakReceivedItem bri = new BreakReceivedItem();
            bri.setIsbn(isbnText.trim());
            bri.setCond((String)conditionCombo.getSelectedItem());
            bri.setBreakReceived(br);
            bri.setSkid(false);
            bri.setBreakRoom(breakRoom.isSelected());
            if (inputReceived.getText().length() == 0){
                // put up an error
                JOptionPane.showMessageDialog(this,
                    "You must input a Received ammount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (inputOrdered.getText().length() == 0){
                inputOrdered.setText(inputReceived.getText());
            }
            try {
                bri.setQuantity(new Integer(inputReceived.getText()));
                bri.setOrderedQuantity(new Integer(inputOrdered.getText()));
            } catch (Exception e){
                JOptionPane.showMessageDialog(this,
                    "The Received Quantity and Ordered Quantity must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (skidCheck.isSelected()){
                bri.setSkid(true);
                bri.setType("Skid");
                bri.setSkidType(skidType.getSelectedItem().toString());
            }
            if (bri.getBreakRoom() && bri.getSkid()){
                // put up an error
                JOptionPane.showMessageDialog(this,
                    "You can only select Skid OR Break Room, Not both.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            bri.setTitle(inputTitle.getText());
            bri.setBin(inputBin.getText());
            if (skidCheck.isSelected()){
                bri.setBriCounts(new ArrayList());
            }

            BreakClientDao dao = new BreakClientDao();
            if (bri.getBreakRoom()){
                // check and make sure it is actually a skid
                InventoryItem ii = dao.findByIsbnCond(bri.getIsbn(), bri.getCond());
                if (ii == null || !ii.getSkid()){
                    JOptionPane.showMessageDialog(this,
                            "The ISBN you have entered is not a SKID so it cannot go to the break room.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // set the piece count of the skid
                bri.setPieceCount(ii.getSkidPieceCount());
                bri.setBreakRoomIsbn(breakRoomIsbn.getText());
                bri.setBreakRoomCondition(ii.getCond());
            }


            bri.setCreateTime(Calendar.getInstance().getTime());
            Object ob = dao.save(bri);
            if (skidCheck.isSelected()){
                PieceCount pc = new PieceCount(this, true, bri);
            }
            br.getBreakReceivedItems().add(bri);
            itemListModel.addElement(bri.getIsbnQuantityListView());
            resetInputPanel();
            itemList.setSelectedIndex(itemListModel.getSize()-1);
            status.setText("Added "+bri.getIsbn());
            inputOrdered.setEnabled(true);
            inputReceived.setEnabled(true);
            breakRoomIsbn.setText("");
            breakRoomIsbn.setEnabled(false);
        } else {
            status.setText("ERROR: ISBN must be filled in.");
        }
    }//GEN-LAST:event_itemAddActionPerformed

    private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemActionPerformed
        BreakReceived br = breakReceivedList.get(receivingList.getSelectedIndex());
        int selected = itemList.getSelectedIndex();
        BreakReceivedItem item = (BreakReceivedItem)br.getBreakReceivedItems().get(selected);
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item: "+
            item.getIsbnQuantityListView(), "Are you sure?", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION){
            BreakClientDao dao = new BreakClientDao();
            itemListModel.remove(selected);
            br.getBreakReceivedItems().remove(selected);
            List<BriCount> counts = item.getBriCounts();
            if (counts != null){
                for (BriCount bc : counts){
                    dao.delete(bc);
                }
            }
            dao.delete(item);
            resetInfoPanel();
            resetInputPanel();
            finishReceiving.setEnabled(false);
            deleteItem.setEnabled(false);
        }

    }//GEN-LAST:event_deleteItemActionPerformed

    private void deleteReceivingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteReceivingActionPerformed
        int selected = receivingList.getSelectedIndex();
        BreakReceived br = breakReceivedList.get(selected);
        int ans = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete receiving: "+
            br.getDatePoListView(), "Are you sure?", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION){
            BreakClientDao dao = new BreakClientDao();
            if (br.getBreakReceivedItems() != null){
                for (BreakReceivedItem bri : br.getBreakReceivedItems()){
                    List<BriCount> counts = bri.getBriCounts();
                    if (counts != null){
                        for (BriCount bc : counts){
                            dao.delete(bc);
                        }
                        bri.setBriCounts(new ArrayList<BriCount>());
                    }
                    dao.delete(bri);
                }
                br.setBreakReceivedItems(new ArrayList<BreakReceivedItem>());
            }
            dao.delete(br);
            itemListModel.removeAllElements();
            breakListModel.remove(selected);
            breakReceivedList.remove(selected);
            resetInfoPanel();
            resetInputPanel();
        }

    }//GEN-LAST:event_deleteReceivingActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        if (!finishing){
            System.exit(0);
        }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void newReceivingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newReceivingActionPerformed
        NewReceivingDialog nrd = new NewReceivingDialog(this, true);
        nrd.setVisible(true);
    }//GEN-LAST:event_newReceivingActionPerformed


    public void setupGui(){
        BreakClientDao dao = new BreakClientDao();
        breakReceivedList = new ArrayList(dao.findAllBreakReceived());
        for (int i = 0; i < breakReceivedList.size(); i++){
            breakListModel.addElement(((BreakReceived)breakReceivedList.get(i)).getDatePoListView());
        }
    }

    private void resetInfoPanel(){
        infoCondition.setText("");
        infoIsbn.setText("");
        infoReceived.setText("");
        infoOrdered.setText("");
        infoTitle.setText("");
        infoBin.setText("");
        infoSkid.setText("");
        infoSkidType.setText("");
        deleteItem.setEnabled(false);
        infoPieceCountList.setEnabled(false);
        infoPieceCount.setText("Piece Count:");
        skidTypeLabel.setText("Skid Type:");
        infoPieceCountList.setModel(new DefaultListModel());
        infoPieceCountList.removeAll();
        recInfo.setText("");
        setFocus();
    }

    private void resetInputPanel(){
        skidCheck.setSelected(false);
        skidType.setSelectedIndex(0);
        skidType.setEnabled(false);
        breakRoom.setSelected(false);
        isbnCombo.setEnabled(true);
        inputTitle.setEnabled(true);
        isbnCombo.removeAllItems();
        inputReceived.setText("");
        inputOrdered.setText("");
        inputTitle.setText("");
        inputBin.setText("");
        setFocus();
    }

    public void setFocus(){
        isbnCombo.requestFocusInWindow();
    }

    public void addReceiving(BreakReceived breakReceived){
        BreakClientDao dao = new BreakClientDao();
        breakReceived.setBreakReceivedItems(new ArrayList());
        breakReceived.setClerk(user.getUsername());
        breakReceivedList.add(breakReceived);
        breakReceived.setCreateTime(Calendar.getInstance().getTime());
        Object ob = dao.save(breakReceived);
        breakListModel.addElement(breakReceived.getDatePoListView());
        receivingList.setSelectedIndex(breakReceivedList.size()-1);
        updateItemList(receivingList.getSelectedIndex());
        resetInputPanel();
        status.setText("Added New Receiving: "+breakReceived.getPoNumber());
    }

    public void removeReceiving(BreakReceived breakReceived){
        BreakClientDao dao = new BreakClientDao();
        for (BreakReceivedItem bri : breakReceived.getBreakReceivedItems()){
            if (bri.getBriCounts() != null){
                for (BriCount bc : bri.getBriCounts()){
                    dao.delete(bc);
                }
                bri.getBriCounts().clear();
            }
            dao.delete(bri);
        }
        breakReceived.getBreakReceivedItems().clear();
        dao.delete(breakReceived);
        breakReceivedList.remove(breakReceived);
        breakListModel.removeElement(breakReceived.getDatePoListView());
        receivingList.setSelectedIndex(breakReceivedList.size()-1);
        updateItemList(receivingList.getSelectedIndex());
        resetInputPanel();
        resetInfoPanel();
        status.setText("Finished Receiving: "+breakReceived.getPoNumber());
    }

    private void updateItemList(int receivedSelected){
        itemListModel.clear();
        if (receivedSelected < 0){
            return;
        }
        BreakReceived br = breakReceivedList.get(receivedSelected);
        StringBuilder sb = new StringBuilder();
        if (br.getSkid()){
            sb.append("Skid Barcode: ");
            sb.append(br.getSkidbarcode());
            sb.append("\n");
        }
        sb.append("PO Number: ");
        sb.append(br.getPoNumber());
        sb.append("\nPO Date: ");
        sb.append(DateFormat.format(br.getPoDate()));
        sb.append("\nVendor: ");
        if (br.getVendor() != null){
            sb.append(br.getVendor().getCodePlusName());
        } else {
            sb.append(br.getVendorCode());
        }
        recInfo.setText(sb.toString());
        List list = br.getBreakReceivedItems();
        for (int i = 0; i < list.size(); i++){
            itemListModel.addElement(((BreakReceivedItem)list.get(i)).getIsbnQuantityListView());
        }
        if (skidTypeList != null){
            skidTypeList.clear();
        }
        skidTypeList = new ArrayList();
        if (br.getVendor() != null && br.getVendor().getVendorSkidTypes() != null){
            skidTypeList = new ArrayList(br.getVendor().getVendorSkidTypes());
        }
        skidType.removeAllItems();
        skidType.addItem(UNKNOWN);
        for (int i = 0; i < skidTypeList.size(); i++){
            skidType.addItem(skidTypeList.get(i).getSkidtype());
        }
        skidType.setSelectedIndex(0);
    }

    private void loadInfoPanel(int receivedSelected, int itemSelected){
        if (receivedSelected < 0 || itemSelected < 0){
            return;
        }
        skidTypeLabel.setText("Skid Type: ");
        infoPieceCountList.setModel(new DefaultListModel());
        infoPieceCountList.removeAll();
        BreakReceived br = breakReceivedList.get(receivedSelected);
        BreakReceivedItem item = (BreakReceivedItem)br.getBreakReceivedItems().get(itemSelected);
        infoIsbn.setText(item.getIsbn());
        infoCondition.setText(item.getCond());
        infoReceived.setText(item.getQuantity().toString());
        infoOrdered.setText(item.getOrderedQuantity().toString());
        infoTitle.setText(item.getTitle());
        infoBin.setText(item.getBin());
        infoSkid.setText("No");
        infoSkidType.setText("");
        if (item.getBreakRoom() != null && item.getBreakRoom()){
            skidTypeLabel.setText("Break Room:");
            infoSkidType.setText("Yes");
        }
        if (item.getSkid()){
            infoSkid.setText("Yes");
            infoSkidType.setText(item.getSkidType());
            infoPieceCountList.setEnabled(true);
            boolean pieces = true;
            infoPieceCountList.removeAll();
            infoPieceCountList.setModel(new DefaultListModel());
            DefaultListModel dlm = (DefaultListModel)infoPieceCountList.getModel();
            List<BriCount> counts = item.getBriCounts();
            for (int i = 0; i < counts.size(); i++){
                dlm.add(i, (i+1)+"  :  "+counts.get(i).getCountOrLbs().toString());
                pieces = counts.get(i).getPieces().booleanValue();
            }
            if (!pieces){
                infoPieceCount.setText("Lbs:");
            }
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Logger log = Logger.getLogger(BreakClientGui.class);
                    log.error("Here is something to look at");
                    new BreakClientGui().setVisible(true);
                } catch (Throwable t){
                    System.out.println("Caught Throwable: "+ t.getMessage());
                    t.printStackTrace();
                    // now try and get this somewhere for later viewing
                    Logger log = Logger.getLogger(BreakClientGui.class);
                    log.error("Caught Throwable", t);
                    System.out.println("Logged errors to C:\\BreakClientErrors.log");
                }
            }
        });
    }

    public int getReceivingListIndex(){
        return receivingList.getSelectedIndex();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox breakRoom;
    private javax.swing.JTextField breakRoomIsbn;
    private javax.swing.JComboBox conditionCombo;
    private javax.swing.JButton deleteItem;
    private javax.swing.JButton deleteReceiving;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JButton finishReceiving;
    private javax.swing.JLabel infoBin;
    private javax.swing.JLabel infoCondition;
    private javax.swing.JLabel infoIsbn;
    private javax.swing.JLabel infoOrdered;
    private javax.swing.JLabel infoPieceCount;
    private javax.swing.JList infoPieceCountList;
    private javax.swing.JLabel infoReceived;
    private javax.swing.JLabel infoSkid;
    private javax.swing.JLabel infoSkidType;
    private javax.swing.JLabel infoTitle;
    private javax.swing.JTextField inputBin;
    private javax.swing.JTextField inputOrdered;
    private javax.swing.JTextField inputReceived;
    private javax.swing.JTextField inputTitle;
    private javax.swing.JComboBox isbnCombo;
    private javax.swing.JButton itemAdd;
    private javax.swing.JList itemList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JMenu loggedInAs;
    private javax.swing.JButton newReceiving;
    private javax.swing.JMenuItem print;
    private javax.swing.JMenu printMenu;
    private javax.swing.JTextArea recInfo;
    private javax.swing.JList receivingList;
    private javax.swing.JCheckBox skidCheck;
    private javax.swing.JComboBox skidType;
    private javax.swing.JLabel skidTypeLabel;
    private javax.swing.JTextField status;
    // End of variables declaration//GEN-END:variables

}
