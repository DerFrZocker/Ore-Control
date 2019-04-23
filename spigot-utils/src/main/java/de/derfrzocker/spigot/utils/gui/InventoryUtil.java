package de.derfrzocker.spigot.utils.gui;

public class InventoryUtil {

    public static int calculateSlot(int current, int start) { // No Idea if this work never tested //EDIT: It works (first try)
        final int rowSize = 9 - (2 * start);

        if (rowSize <= 0)
            throw new IllegalStateException();

        int row = current / rowSize;

        int slot = current + start;

        if (slot == 0)
            return current + start;

        return slot + (2 * start * row);
    }

    public static int calculateSlots(int rows, int gap) {
        return (9 - 2 * gap) * rows;
    }

    public static int calculatePages(int slots, int amount) {
        int rest = amount % slots;
        int pages = amount / slots;

        if (rest != 0)
            pages++;

        return pages;
    }

}
