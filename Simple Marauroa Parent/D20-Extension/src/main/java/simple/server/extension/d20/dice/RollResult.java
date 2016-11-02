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

public class RollResult {

    private int total;
    int modifier;
    List<Integer> rolls;

    private RollResult(int total,
            int modifier,
            List<Integer> rolls) {
        this.total = total;
        this.modifier = modifier;
        this.rolls = rolls;
    }

    public RollResult(int bonus) {
        this.total = bonus;
        this.modifier = bonus;
        rolls = new ArrayList<>();
    }

    public void addResult(int res) {
        total += res;
        rolls.add(res);
    }

    public RollResult andThen(RollResult r2) {
        int temptotal = this.getTotal() + r2.getTotal();
        List<Integer> temp = new ArrayList<>();
        temp.addAll(this.rolls);
        temp.addAll(r2.rolls);
        return new RollResult(temptotal,
                this.modifier + r2.modifier,
                temp);
    }

    @Override
    public String toString() {
        return getTotal() + "  <= " + rolls.toString()
                + (modifier > 0 ? ("+" + modifier)
                        : modifier < 0 ? modifier : "");
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }
}
