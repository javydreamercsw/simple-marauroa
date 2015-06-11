package simple.server.extension.d20.dice;

import java.util.Random;

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

public class DieRoll {

    private int ndice;
    private int nsides;
    private int bonus;
    private static Random rnd;

    static {
        rnd = new Random();
    }

    public DieRoll(int ndice,
            int nsides,
            int bonus) {
        this.ndice = ndice;
        this.nsides = nsides;
        this.bonus = bonus;
    }

    public RollResult makeRoll() {
        RollResult r = new RollResult(bonus);
        for (int i = 0; i < ndice; i++) {
            int roll = rnd.nextInt(nsides) + 1;
            r.addResult(roll);
        }
        return r;
    }

    @Override
    public String toString() {
        String ans = ndice + "d" + nsides;
        if (bonus > 0) {
            ans = ans + "+" + bonus;
        } else if (bonus < 0) {
            ans = ans + bonus;
        }
        return ans;
    }
}
