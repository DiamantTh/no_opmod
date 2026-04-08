package systems.diath.visotaris_opmod.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

/**
 * Übersetzt englische API-Item-Keys (z.B. {@code "acacia_leaves"}) in den
 * lokalisierten Anzeigenamen der aktuellen Minecraft-Spielsprache.
 *
 * <p>Funktionsweise:
 * <ol>
 *   <li>API-Key → Minecraft-Registry-ID ({@code minecraft:acacia_leaves})</li>
 *   <li>Registry-Lookup → {@link Item}</li>
 *   <li>{@link Item#getTranslationKey()} → Übersetzungs-Key ({@code block.minecraft.acacia_leaves})</li>
 *   <li>{@link Language#getInstance()} → lokalisierter String in der aktiven Spielsprache</li>
 * </ol>
 *
 * <p>Unterstützt auch CMD-Compound-Keys wie {@code "paper#626"}: in diesem Fall
 * wird nur der Basis-Teil ({@code "paper"}) zur Lokalisierung verwendet.
 *
 * <p>Thread-safe: Nur lesender Zugriff auf Registries und Language-Singleton.
 * Darf auf dem Render-Thread aufgerufen werden.
 */
public final class ItemNameResolver {

    private ItemNameResolver() {}

    /**
     * Gibt den lokalisierten Item-Namen für den gegebenen API-Key zurück.
     *
     * @param apiKey  API-Item-Key in Kleinbuchstaben, z.B. {@code "acacia_leaves"}
     *                oder CMD-Compound {@code "paper#626"}
     * @return  Lokalisierter Name (z.B. "Akazienblätter" auf Deutsch), oder
     *          den rohen {@code apiKey} wenn kein Registry-Eintrag gefunden wird
     */
    public static String resolve(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) return apiKey;

        // CMD-Compound-Key: "paper#626" → nur "paper" zur Lokalisierung, Suffix anhängen
        String suffix = "";
        String baseKey = apiKey;
        int hashIdx = apiKey.indexOf('#');
        if (hashIdx >= 0) {
            baseKey = apiKey.substring(0, hashIdx);
            suffix  = " (" + apiKey.substring(hashIdx + 1) + ")";
        }

        // minecraft-Namespace versuchen; bei Vanilla-Items immer korrekt
        Identifier id = Identifier.of("minecraft", baseKey);
        Item item = Registries.ITEM.get(id);

        // Items.AIR ist der Fallback wenn nichts gefunden – in dem Fall den Key zurückgeben
        if (item == Items.AIR && !"air".equals(baseKey)) {
            return apiKey;   // unbekanntes Item → Roh-Key beibehalten
        }

        Language lang = Language.getInstance();
        String translationKey = item.getTranslationKey();
        String localizedName  = lang.get(translationKey, null);

        // Wenn Language den Key nicht kennt, roh zurückgeben
        if (localizedName == null || localizedName.equals(translationKey)) {
            return apiKey + suffix;
        }
        return localizedName + suffix;
    }

    /**
     * Gibt den lokalisierten Namen zurück, oder {@code fallback} wenn die
     * Auflösung keinen bekannten Eintrag liefert.
     *
     * @param apiKey    API-Item-Key (wie in {@link #resolve(String)})
     * @param fallback  Rückgabewert bei unbekanntem Item
     */
    public static String resolveOrFallback(String apiKey, String fallback) {
        String result = resolve(apiKey);
        return result.equals(apiKey) ? fallback : result;
    }
}
