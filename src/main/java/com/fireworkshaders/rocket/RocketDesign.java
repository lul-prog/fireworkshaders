package com.fireworkshaders.rocket;

import com.fireworkshaders.FireworkShaders;
import com.fireworkshaders.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.Optional;

/**
 * Validated, portable description of a player-designed rocket.
 * The compact tag is stored on the rocket item and copied to the projectile
 * when it is launched; the client never trusts arbitrary NBT values.
 */
public final class RocketDesign {
    public static final String DEFAULT_NAME = "Custom Firework";
    private static final String ROOT_TAG = "RocketDesign";

    public enum Shape {
        SPHERE("sphere", "shape_sphere"),
        RING("ring", "shape_ring"),
        CASCADE("cascade", "shape_cascade");

        private final String id;
        private final String ingredientId;

        Shape(String id, String ingredientId) {
            this.id = id;
            this.ingredientId = ingredientId;
        }

        public String id() {
            return this.id;
        }

        public static Shape byId(String id) {
            for (Shape shape : values()) {
                if (shape.id.equals(id)) return shape;
            }
            return SPHERE;
        }

        public static Shape byIndex(int index) {
            Shape[] values = values();
            return index >= 0 && index < values.length ? values[index] : SPHERE;
        }
    }

    public enum Modifier {
        TRAIL("modifier_trail", 1),
        CRACKLE("modifier_crackle", 1 << 1),
        SMOKE("modifier_smoke", 1 << 2);

        private final String ingredientId;
        private final int bit;

        Modifier(String ingredientId, int bit) {
            this.ingredientId = ingredientId;
            this.bit = bit;
        }

        public int bit() {
            return this.bit;
        }
    }

    public enum Powder {
        STRONTIUM_RED("powder_strontium", 0xFF4B52),
        COPPER_BLUE("powder_copper", 0x42A5FF),
        BARIUM_GREEN("powder_barium", 0x6CFF9A),
        SODIUM_GOLD("powder_sodium", 0xFFD34D),
        POTASSIUM_VIOLET("powder_potassium", 0xC77DFF),
        MAGNESIUM_WHITE("powder_magnesium", 0xFFF4E2);

        private final String ingredientId;
        private final int color;

        Powder(String ingredientId, int color) {
            this.ingredientId = ingredientId;
            this.color = color;
        }

        public int color() {
            return this.color;
        }
    }

    private final int color;
    private final Shape shape;
    private final int modifierMask;
    private final String name;

    public RocketDesign(int color, Shape shape, int modifierMask, String name) {
        this.color = color & 0xFFFFFF;
        this.shape = shape == null ? Shape.SPHERE : shape;
        this.modifierMask = modifierMask & 0x7;
        this.name = sanitizeName(name);
    }

    public int color() {
        return this.color;
    }

    public Shape shape() {
        return this.shape;
    }

    public int modifierMask() {
        return this.modifierMask;
    }

    public boolean hasModifier(Modifier modifier) {
        return (this.modifierMask & modifier.bit()) != 0;
    }

    public String name() {
        return this.name;
    }

    public static RocketDesign defaultDesign() {
        return new RocketDesign(0xFFD34D, Shape.SPHERE, 0, DEFAULT_NAME);
    }

    /** Color powder and shape are required; the three modifier slots are independent and optional. */
    public static Optional<RocketDesign> fromIngredients(ItemStack powderStack,
                                                           ItemStack shapeStack,
                                                           ItemStack trailStack,
                                                           ItemStack crackleStack,
                                                           ItemStack smokeStack,
                                                           String name) {
        Powder powder = powderOf(powderStack);
        Shape shape = shapeOf(shapeStack);
        if (powder == null || shape == null) return Optional.empty();

        int modifierMask = 0;
        ItemStack[] modifierStacks = {trailStack, crackleStack, smokeStack};
        for (ItemStack stack : modifierStacks) {
            if (stack.isEmpty()) continue;
            Modifier modifier = modifierOf(stack);
            if (modifier == null) return Optional.empty();
            modifierMask |= modifier.bit();
        }
        return Optional.of(new RocketDesign(powder.color(), shape, modifierMask, name));
    }

    public ItemStack createRocketStack() {
        ItemStack stack = new ItemStack(ModItems.CUSTOM_ROCKET.get());
        CompoundTag designTag = new CompoundTag();
        designTag.putInt("Color", this.color);
        designTag.putString("Shape", this.shape.id());
        designTag.putInt("ModifierMask", this.modifierMask);
        designTag.putString("Name", this.name);
        stack.getOrCreateTag().put(ROOT_TAG, designTag);
        stack.setHoverName(Component.literal(this.name));
        return stack;
    }

    public static Optional<RocketDesign> readFromStack(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key == null || !FireworkShaders.MOD_ID.equals(key.getNamespace())
                || !"custom_rocket".equals(key.getPath())) {
            return Optional.empty();
        }
        CompoundTag root = stack.getTag();
        if (root == null || !root.contains(ROOT_TAG, Tag.TAG_COMPOUND)) return Optional.empty();
        CompoundTag tag = root.getCompound(ROOT_TAG);
        String shapeId = tag.getString("Shape").toLowerCase(Locale.ROOT);
        return Optional.of(new RocketDesign(tag.getInt("Color"), Shape.byId(shapeId),
                tag.getInt("ModifierMask"), tag.getString("Name")));
    }

    public static boolean isColorPowder(ItemStack stack) {
        return powderOf(stack) != null;
    }

    public static boolean isShapeModule(ItemStack stack) {
        return shapeOf(stack) != null;
    }

    public static boolean isModifier(ItemStack stack, Modifier expected) {
        return !stack.isEmpty() && modifierOf(stack) == expected;
    }

    private static Powder powderOf(ItemStack stack) {
        String path = itemPath(stack);
        for (Powder powder : Powder.values()) {
            if (powder.ingredientId.equals(path)) return powder;
        }
        return null;
    }

    private static Shape shapeOf(ItemStack stack) {
        String path = itemPath(stack);
        for (Shape shape : Shape.values()) {
            if (shape.ingredientId.equals(path)) return shape;
        }
        return null;
    }

    private static Modifier modifierOf(ItemStack stack) {
        String path = itemPath(stack);
        for (Modifier modifier : Modifier.values()) {
            if (modifier.ingredientId.equals(path)) return modifier;
        }
        return null;
    }

    private static String itemPath(ItemStack stack) {
        if (stack.isEmpty()) return "";
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null && FireworkShaders.MOD_ID.equals(key.getNamespace()) ? key.getPath() : "";
    }

    /** Prevent formatting/control characters and keep names short enough for menus and packets. */
    public static String sanitizeName(String input) {
        if (input == null || input.isBlank()) return DEFAULT_NAME;
        StringBuilder clean = new StringBuilder();
        boolean skipFormattingCode = false;
        for (int offset = 0; offset < input.length();) {
            int codePoint = input.codePointAt(offset);
            offset += Character.charCount(codePoint);
            if (skipFormattingCode) {
                skipFormattingCode = false;
                continue;
            }
            if (codePoint == '\u00A7') {
                skipFormattingCode = true;
                continue;
            }
            if (Character.isISOControl(codePoint)) continue;
            clean.appendCodePoint(codePoint);
            if (clean.codePointCount(0, clean.length()) >= 32) break;
        }
        String result = clean.toString().trim();
        return result.isEmpty() ? DEFAULT_NAME : result;
    }
}
