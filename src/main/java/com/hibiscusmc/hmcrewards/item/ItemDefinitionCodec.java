package com.hibiscusmc.hmcrewards.item;

import com.hibiscusmc.hmcrewards.data.serialize.DnCodec;
import com.hibiscusmc.hmcrewards.data.serialize.DnReader;
import com.hibiscusmc.hmcrewards.data.serialize.DnType;
import com.hibiscusmc.hmcrewards.data.serialize.DnWriter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ItemDefinitionCodec implements DnCodec<ItemDefinition> {
    private static final DnCodec<ItemDefinition> INSTANCE = new ItemDefinitionCodec();

    private ItemDefinitionCodec() {
    }

    @Override
    public @NotNull Class<ItemDefinition> type() {
        return ItemDefinition.class;
    }

    @Override
    public @NotNull ItemDefinition decode(final @NotNull DnReader reader) {
        if (reader.readType() == DnType.VALUE) {
            // material only
            return ItemDefinition.of(reader.readStringValue());
        }

        // material & more properties
        reader.readObjectStart();
        String material = null;
        String displayName = null;
        final List<String> lore = new ArrayList<>();

        while (reader.hasMoreValuesOrEntries()) {
            final String name = reader.readName();
            switch (name) {
                case "material" -> material = reader.readStringValue();
                case "name" -> displayName = reader.readStringValue();
                case "lore" -> {
                    reader.readArrayStart();
                    while (reader.hasMoreValuesOrEntries()) {
                        lore.add(reader.readStringValue());
                    }
                    reader.readArrayEnd();
                }
                default -> reader.skipValue();
            }
        }

        if (material == null) {
            throw new IllegalStateException("Missing required property 'material'.");
        }

        reader.readObjectEnd();
        return ItemDefinition.of(material, displayName, lore);
    }

    @Override
    public void encode(final @NotNull DnWriter writer, final @NotNull ItemDefinition value) {
        final String material = value.material();
        final String displayName = value.name();
        final List<String> lore = value.lore();

        if (lore.isEmpty() && displayName == null) {
            // material only
            writer.writeStringValue(material);
            return;
        }

        // material & more properties
        writer.writeObjectStart();
        writer.writeStringValue("material", material);
        if (displayName != null) {
            writer.writeStringValue("name", displayName);
        }
        if (!lore.isEmpty()) {
            writer.writeArrayStart("lore");
            for (final String line : lore) {
                writer.writeStringValue(line);
            }
            writer.writeArrayEnd();
        }
        writer.writeObjectEnd();
    }

    public static @NotNull DnCodec<ItemDefinition> instance() {
        return INSTANCE;
    }
}
