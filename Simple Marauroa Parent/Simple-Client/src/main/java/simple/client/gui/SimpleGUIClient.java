package simple.client.gui;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import simple.client.ClientFrameworkProvider;
import simple.client.LoginProvider;
import simple.client.api.IWorldManager;
import simple.client.api.SelfChangeListener;
import simple.client.event.listener.ClientRPEventNotifier;
import simple.common.NotificationType;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.action.chat.PublicChatAction;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProviders({
    @ServiceProvider(service = SelfChangeListener.class)})
public class SimpleGUIClient extends javax.swing.JFrame
        implements SelfChangeListener {

    private final ClientFrameworkProvider client
            = Lookup.getDefault().lookup(ClientFrameworkProvider.class);
    private static final Logger LOG
            = Logger.getLogger(SimpleGUIClient.class.getName());
    private static final StyleContext CONTEXT = new StyleContext();
    private static final StyledDocument DOC
            = new DefaultStyledDocument(CONTEXT);
    private RPObject myself;

    /**
     * Creates new form SimpleGUICLient
     */
    public SimpleGUIClient() {
        initComponents();
        LoginProvider lp = Lookup.getDefault().lookup(LoginProvider.class);
        if (lp != null) {
            lp.displayLoginDialog();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lobbyPane = new javax.swing.JPanel();
        chatPane = new javax.swing.JScrollPane();
        history = new JTextArea(DOC);
        roomListPane = new javax.swing.JScrollPane();
        roomList = new javax.swing.JList();
        playerPane = new javax.swing.JScrollPane();
        playerList = new javax.swing.JList<>();
        playerInfoPane = new javax.swing.JPanel();
        sendTextButton = new javax.swing.JButton();
        chatField = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lobbyPane.setDoubleBuffered(false);

        history.setEditable(false);
        history.setColumns(20);
        history.setLineWrap(true);
        history.setRows(5);
        chatPane.setViewportView(history);

        roomList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        roomListPane.setViewportView(roomList);

        playerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        playerPane.setViewportView(playerList);

        javax.swing.GroupLayout playerInfoPaneLayout = new javax.swing.GroupLayout(playerInfoPane);
        playerInfoPane.setLayout(playerInfoPaneLayout);
        playerInfoPaneLayout.setHorizontalGroup(
            playerInfoPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 244, Short.MAX_VALUE)
        );
        playerInfoPaneLayout.setVerticalGroup(
            playerInfoPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout lobbyPaneLayout = new javax.swing.GroupLayout(lobbyPane);
        lobbyPane.setLayout(lobbyPaneLayout);
        lobbyPaneLayout.setHorizontalGroup(
            lobbyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lobbyPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lobbyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chatPane, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                    .addComponent(roomListPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lobbyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(playerInfoPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(playerPane, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        lobbyPaneLayout.setVerticalGroup(
            lobbyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lobbyPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lobbyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lobbyPaneLayout.createSequentialGroup()
                        .addComponent(roomListPane, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chatPane))
                    .addGroup(lobbyPaneLayout.createSequentialGroup()
                        .addComponent(playerPane, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playerInfoPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        sendTextButton.setText("Send");
        sendTextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendTextButtonActionPerformed(evt);
            }
        });

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chatField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendTextButton)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lobbyPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 831, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendTextButton)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(lobbyPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(26, 26, 26)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void sendTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendTextButtonActionPerformed
        if (!chatField.getText().trim().isEmpty()) {
            RPAction action = new RPAction();
            action.put("type", PublicChatAction.CHAT);
            action.put("text", chatField.getText());
            client.getClientManager().send(action);
        }
        //Reset field
        chatField.setText("");
    }//GEN-LAST:event_sendTextButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        ((SimpleGUIClient) Lookup.getDefault().lookup(SelfChangeListener.class))
                .setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JTextField chatField;
    private javax.swing.JScrollPane chatPane;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private static javax.swing.JTextArea history;
    private javax.swing.JPanel lobbyPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JPanel playerInfoPane;
    private javax.swing.JList<RPObject> playerList;
    private javax.swing.JScrollPane playerPane;
    private static javax.swing.JList roomList;
    private javax.swing.JScrollPane roomListPane;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JButton sendTextButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public boolean onMyRPObject(RPObject added, RPObject deleted) {
        RPObject.ID id = null;
        IWorldManager worldManager
                = Lookup.getDefault().lookup(IWorldManager.class);
        if (added != null) {
            id = added.getID();
        }
        if (deleted != null) {
            id = deleted.getID();
        }
        if (id == null) {
            // Unchanged.
            return true;
        }
        final RPObject object = worldManager.get(id);
        if (object != null) {
            object.applyDifferences(added, deleted);
            myself = object;
            //Get the object's events
            object.events().forEach((event) -> {
                LOG.info(event.toString());
                processEvent(event);
            });
        }
        LOG.info(myself.toString());
        return true;
    }

    @Override
    public RPObject getMyObject() {
        return myself;
    }

    private void processEvent(RPEvent event) {
        LOG.info(event.toString());
        if (event.has("event_id")) {
            ClientRPEventNotifier.get().logic(Arrays.asList(event));
            LOG.log(Level.FINE, "Processing event: {0}", event.getName());
            switch (event.getName()) {
                case TextEvent.RPCLASS_NAME:
                    processChat(event);
                    break;
                case PrivateTextEvent.RPCLASS_NAME:
                    processChat(event);
                    break;
                default:
                    //TODO: Handle other events
                    LOG.log(Level.WARNING, "Received the following "
                            + "event but didn't know how to handle "
                            + "it: \n{0}", event);
                    break;
            }
        } else {
            LOG.log(Level.SEVERE, "Invalid event:\n{0}", event);
        }
    }

    void processChat(RPEvent event) {
        NotificationType type = NotificationType.NORMAL;
        if (event.has(TextEvent.TEXT_TYPE)) {
            try {
                type = NotificationType.valueOf(event.get(TextEvent.TEXT_TYPE));
            } catch (IllegalArgumentException ex) {
                LOG.log(Level.WARNING, "Unable to provide format: {0}",
                        event.get(TextEvent.TEXT_TYPE));
            }
        }
        String text = "";
        switch (event.getName()) {
            case TextEvent.RPCLASS_NAME:
            case PrivateTextEvent.RPCLASS_NAME:
                text = (event.get(WellKnownActionConstant.FROM) == null ? "System"
                        : (event.get(WellKnownActionConstant.FROM)))
                        + " (" + event.get(WellKnownActionConstant.TIME) + "): ";
                text += event.get(WellKnownActionConstant.TEXT);
                break;
        }
        writeChat(text.replaceAll("_", " "), type);
    }

    /**
     * Display text in chat.
     *
     * @param text text to display.
     * @param type chat type.
     */
    public void writeChat(String text, NotificationType type) {
        writeChat(history, text, type);
    }

    /**
     * Display text in chat.
     *
     * @param text text to display.
     */
    public void writeChat(String text) {
        writeChat(history, text, NotificationType.NORMAL);
    }

    /**
     * Display text in chat.
     *
     * @param area Text Area to write to.
     * @param text text to display.
     * @param type chat type.
     */
    public void writeChat(JTextArea area, String text, NotificationType type) {
        try {
            int len = area.getDocument().getLength();
            Style style;
            if (CONTEXT.getStyle(type.name()) == null) {
                style = CONTEXT.getStyle(StyleContext.DEFAULT_STYLE);
            } else {
                style = CONTEXT.getStyle(type.name());
            }
            area.getDocument().insertString(len, text + "\n", style);
            // Convert the new end location
            // to view co-ordinates
            Rectangle r = area.modelToView(len);

            // Finally, scroll so that the new text is visible
            if (r != null) {
                area.scrollRectToVisible(r);
            }
        } catch (BadLocationException e) {
            LOG.log(Level.WARNING, "Failed to append text: {0}", e);
        }
    }
}
