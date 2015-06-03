package simple.server.extension.d20.dice;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

/*
 JDice: Java Dice Rolling Program
 Copyright (C) 2006 Andrew D. Hilton  (adhilton@cis.upenn.edu)


 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 */
public class JDice {

    static final String CLEAR = "Clear";
    static final String ROLL = "Roll Selection";

    static void showError(String s) {
    }

    private static class JDiceListener implements ActionListener {

        List<String> listItems;
        JList resultList;
        JComboBox inputBox;
        long lastEvent; /* hack to prevent double events with text
         entry */


        public JDiceListener(JList resultList,
                JComboBox inputBox) {

            this.listItems = new ArrayList<>();
            this.resultList = resultList;
            this.inputBox = inputBox;
            lastEvent = 0;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getWhen() == lastEvent) {
                return;
            }
            lastEvent = e.getWhen();
            if (e.getSource() instanceof JComboBox
                    || e.getActionCommand().equals(ROLL)) {
                if (inputBox.getSelectedItem() != null) {
                    String s = inputBox.getSelectedItem().toString();
                    String[] arr = s.split("=");
                    String name = "";
                    for (int i = 0; i < arr.length - 2; i++) {
                        name = arr[i] + "=";
                    }
                    if (arr.length >= 2) {
                        name = name + arr[arr.length - 2];
                    }
                    doRoll(name, arr[arr.length - 1]);
                }
            } else if (e.getActionCommand().equals(CLEAR)) {
                doClear();
            } else {
                doRoll(null, e.getActionCommand());
            }
        }

        private void doClear() {
            resultList.clearSelection();
            listItems.clear();
            resultList.setListData(listItems.toArray());
        }

        private void doRoll(String name,
                String diceString) {
            String prepend = "";
            int start = 0;
            int i;
            List<DieRoll> v = DiceParser.parseRoll(diceString);
            if (v == null) {
                showError("Invalid dice string" + diceString);
                return;
            }
            if (name != null) {
                listItems.add(0, name);
                start = 1;
                prepend = "  ";
            }
            int[] selectionIndices = new int[start + v.size()];
            for (i = 0; i < v.size(); i++) {
                DieRoll dr = v.get(i);
                RollResult rr = dr.makeRoll();
                String toAdd = prepend + dr + "  =>  " + rr;
                listItems.add(i + start, toAdd);
            }
            for (i = 0; i < selectionIndices.length; i++) {
                selectionIndices[i] = i;
            }
            resultList.setListData(listItems.toArray());
            resultList.setSelectedIndices(selectionIndices);
        }

    }

    public static void main(String[] args) {
        List<String> v = new ArrayList<>();
        if (args.length >= 1) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(args[0]));
                String s;
                while ((s = br.readLine()) != null) {
                    v.add(s);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.err.println("***********\n**********\n");
                System.err.println("Could not read input file: " + args[0]);
                System.err.println("***********\n**********\n");
            }
        }
        JFrame jf = new JFrame("Dice Roller");
        Container c = jf.getContentPane();
        c.setLayout(new BorderLayout());
        JList jl = new JList();
        c.add(jl, BorderLayout.CENTER);
        JComboBox jcb = new JComboBox(v.toArray());
        jcb.setEditable(true);
        c.add(jcb, BorderLayout.NORTH);
        JDiceListener jdl = new JDiceListener(jl, jcb);
        jcb.addActionListener(jdl);
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide,
                BoxLayout.Y_AXIS));
        String[] buttons = {ROLL,
            "d4",
            "d6",
            "d8",
            "d10",
            "d12",
            "d20",
            "d100",
            CLEAR};
        for (String button : buttons) {
            JButton newButton = new JButton(button);
            rightSide.add(newButton);
            newButton.addActionListener(jdl);
        }
        c.add(rightSide, BorderLayout.EAST);
        jf.setSize(450, 500);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
