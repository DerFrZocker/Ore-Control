package de.derfrzocker.ore.control.inventorygui;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
public class InventoryButton<V> {

    @Getter
    @NonNull
    private final int slot;

    @Getter
    @Setter
    private V value = null;

}
