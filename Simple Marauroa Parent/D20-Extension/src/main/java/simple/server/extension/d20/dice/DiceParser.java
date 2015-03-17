package simple.server.extension.d20.dice;

import java.util.ArrayList;
import java.util.List;

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
public class DiceParser {
    /* this is a helper class to manage the input "stream"*/

    private static class StringStream {

        StringBuffer buff;

        public StringStream(String s) {
            buff = new StringBuffer(s);
        }

        private void munchWhiteSpace() {
            int index = 0;
            char curr;
            while (index < buff.length()) {
                curr = buff.charAt(index);
                if (!Character.isWhitespace(curr)) {
                    break;
                }
                index++;
            }
            buff = buff.delete(0, index);
        }

        public boolean isEmpty() {
            munchWhiteSpace();
            return buff.toString().equals("");
        }

        public Integer getInt() {
            return readInt();
        }

        public Integer readInt() {
            int index = 0;
            char curr;
            munchWhiteSpace();
            while (index < buff.length()) {
                curr = buff.charAt(index);
                if (!Character.isDigit(curr)) {
                    break;
                }
                index++;
            }
            try {
                Integer ans;
                ans = Integer.parseInt(buff.substring(0,
                        index));
                buff = buff.delete(0, index);
                return ans;
            } catch (Exception e) {
                return null;
            }
        }

        public Integer readSgnInt() {
            munchWhiteSpace();
            StringStream state = save();
            if (checkAndEat("+")) {
                Integer ans = readInt();
                if (ans != null) {
                    return ans;
                }
                restore(state);
                return null;
            }
            if (checkAndEat("-")) {
                Integer ans = readInt();
                if (ans != null) {
                    return -ans;
                }
                restore(state);
                return null;
            }
            return readInt();
        }

        public boolean checkAndEat(String s) {
            munchWhiteSpace();
            if (buff.indexOf(s) == 0) {
                buff = buff.delete(0, s.length());
                return true;
            }
            return false;
        }

        public StringStream save() {
            return new StringStream(buff.toString());
        }

        public void restore(StringStream ss) {
            this.buff = new StringBuffer(ss.buff);
        }

        @Override
        public String toString() {
            return buff.toString();
        }

    }

    /**
     * roll::= ndice ; roll | ndice xdice::= dice | N X dice dice::= die bonus?
     * dtail XXXX| FA(die,bonus,N) dtail dtail::= & dice | <nothing>
     * die::= (N)? dN bonus::= + N | -N
     *
     */
    public static List<DieRoll> parseRoll(String s) {
        StringStream ss = new StringStream(s.toLowerCase());
        List<DieRoll> v = parseRollInner(ss, new ArrayList<>());
        if (ss.isEmpty()) {
            return v;
        }
        return null;
    }

    private static List<DieRoll> parseRollInner(StringStream ss,
            List<DieRoll> v) {
        List<DieRoll> r = parseXDice(ss);
        if (r == null) {
            return null;
        }
        v.addAll(r);
        if (ss.checkAndEat(";")) {
            return parseRollInner(ss, v);
        }
        return v;
    }

    private static List<DieRoll> parseXDice(StringStream ss) {
        StringStream saved = ss.save();
        Integer x = ss.getInt();
        int num;
        if (x == null) {
            num = 1;
        } else {
            if (ss.checkAndEat("x")) {
                num = x;
            } else {
                num = 1;
                ss.restore(saved);
            }
        }
        DieRoll dr = parseDice(ss);
        if (dr == null) {
            return null;
        }
        List<DieRoll> ans = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ans.add(dr);
        }
        return ans;
    }

    /**
     * dice::= die (bonus?) dtail XXXX| FA(die,bonus,N) dtail
     */
    private static DieRoll parseDice(StringStream ss) {
        return parseDTail(parseDiceInner(ss), ss);
    }

    private static DieRoll parseDiceInner(StringStream ss) {
        Integer num = ss.getInt();
        int dsides;
        int ndice;
        if (num == null) {
            ndice = 1;
        } else {
            ndice = num;
        }
        if (ss.checkAndEat("d")) {
            num = ss.getInt();
            if (num == null) {
                return null;
            }
            dsides = num;
        } else {
            return null;
        }
        num = ss.readSgnInt();
        int bonus;
        if (num == null) {
            bonus = 0;
        } else {
            bonus = num;
        }
        return new DieRoll(ndice,
                dsides,
                bonus);

    }

    private static DieRoll parseDTail(DieRoll r1,
            StringStream ss) {
        if (r1 == null) {
            return null;
        }
        if (ss.checkAndEat("&")) {
            DieRoll d2 = parseDice(ss);
            return parseDTail(new DiceSum(r1, d2), ss);
        } else {
            return r1;
        }
    }

    private static void test(String s) {
        List<DieRoll> v = parseRoll(s);
        int i;
        if (v == null) {
            System.out.println("Failure:" + s);
        } else {
            System.out.println("Results for " + s + ":");
            for (i = 0; i < v.size(); i++) {
                DieRoll dr = v.get(i);
                System.out.print(v.get(i));
                System.out.print(": ");
                System.out.println(dr.makeRoll());
            }
        }
    }

    public static void main(String[] args) {
        test("d6");
        test("2d6");
        test("d6+5");
        test("4X3d8-5");
        test("12d10+5 & 4d6+2");
        test("d6 ; 2d4+3");
        test("4d6+3 ; 8d12 -15 ; 9d10 & 3d6 & 4d12 +17");
        test("4d6 + xyzzy");
        test("hi");
        test("4d4d4");
    }

}
