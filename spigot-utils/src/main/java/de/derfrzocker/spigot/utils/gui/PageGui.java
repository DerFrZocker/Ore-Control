package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class PageGui<T> implements InventoryGui {

    private final Map<Integer, SubPageGui> guis = new HashMap<>();

    private final Map<Integer, Consumer<InventoryClickEvent>> button = new HashMap<>();

    private BiConsumer<T, InventoryClickEvent> eventBiConsumer;

    private Function<T, ItemStack> itemStackFunction;

    private PageSettings pageSettings;

    private int pages;

    private int nextPage;

    private int previousPage;

    private boolean init = false;

    public void init(final T[] values, final IntFunction<T[]> function, final PageSettings pageSettings, final Function<T, ItemStack> itemStackFunction, BiConsumer<T, InventoryClickEvent> eventBiConsumer) {
        if (this.init)
            return;

        this.init = true;

        this.eventBiConsumer = eventBiConsumer;
        this.itemStackFunction = itemStackFunction;
        this.pageSettings = pageSettings;
        this.nextPage = pageSettings.getNextPageSlot();
        this.previousPage = pageSettings.getPreviousPageSlot();

        final int slots = InventoryUtil.calculateSlots(pageSettings.getRows() - pageSettings.getEmptyRows(), pageSettings.getGap());

        this.pages = InventoryUtil.calculatePages(slots, values.length);
        for (int i = 0; i < pages; i++) {
            final T[] subValues;

            if (i == pages - 1)
                subValues = function.apply(values.length - i * slots);
            else
                subValues = function.apply(slots);

            System.arraycopy(values, i * slots, subValues, 0, subValues.length);

            guis.put(i, new SubPageGui(subValues, i));
        }
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack, final @NonNull Consumer<InventoryClickEvent> consumer) {
        button.put(slot, consumer);
        guis.forEach((key, value) -> value.getInventory().setItem(slot, itemStack));
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack) {
        guis.forEach((key, value) -> value.getInventory().setItem(slot, itemStack));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Inventory getInventory() {
        return guis.get(0).getInventory();
    }

    private final class SubPageGui implements InventoryGui {

        @Getter
        private final Inventory inventory;

        private final int page;

        private final Map<Integer, T> values = new HashMap<>();

        private SubPageGui(final T[] values, final int page) {
            this.page = page;

            final MessageValue[] messageValues = new MessageValue[]{new MessageValue("page", String.valueOf(page)), new MessageValue("pages", String.valueOf(pages))};

            this.inventory = Bukkit.createInventory(this, pageSettings.getRows() * 9,
                    MessageUtil.replacePlaceHolder(pageSettings.getInventoryName(), messageValues));

            if (page + 1 != pages)
                inventory.setItem(nextPage, MessageUtil.replaceItemStack(pageSettings.getNextPageItemStack(), messageValues));

            if (page != 0)
                inventory.setItem(previousPage, MessageUtil.replaceItemStack(pageSettings.getPreviousPageItemStack(), messageValues));

            for (int i = 0; i < values.length; i++) {
                final T value = values[i];

                final int slot = InventoryUtil.calculateSlot(i, pageSettings.getGap());

                inventory.setItem(slot, itemStackFunction.apply(value));

                this.values.put(slot, value);
            }

        }

        @Override
        public void onInventoryClick(final InventoryClickEvent event) {
            if (event.getRawSlot() == previousPage && page != 0) {
                openSync(event.getWhoClicked(), guis.get(page - 1).getInventory());
                return;
            }

            if (event.getRawSlot() == nextPage && page + 1 != pages) {
                openSync(event.getWhoClicked(), guis.get(page + 1).getInventory());
                return;
            }

            final Consumer<InventoryClickEvent> consumer = button.get(event.getRawSlot());

            if (consumer != null) {
                consumer.accept(event);
                return;
            }

            final T value = values.get(event.getRawSlot());

            if (value == null)
                return;

            eventBiConsumer.accept(value, event);
        }

    }

}
